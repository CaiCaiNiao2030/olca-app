package org.openlca.core.editors.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.openlca.core.application.Messages;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessGroup;
import org.openlca.core.model.ProcessGroupSet;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.results.AnalysisResult;
import org.openlca.core.model.results.ProcessGrouping;
import org.openlca.core.resources.ImageType;
import org.openlca.ui.Question;
import org.openlca.ui.UI;
import org.openlca.ui.Viewers;
import org.openlca.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The page of the analysis editor with the grouping function. */
class GroupPage extends FormPage {

	List<ProcessGrouping> groups;
	ProcessGroupSet groupSet;
	AnalyzeEditor editor;
	AnalysisResult result;

	private TableViewer groupViewer;
	private TableViewer processViewer;
	private Menu groupMoveMenu;
	private GroupResultSection resultSection;
	private Section groupingSection;

	public GroupPage(AnalyzeEditor editor, AnalysisResult result) {
		super(editor, "analysis.GroupPage", Messages.Common_Grouping);
		this.editor = editor;
		this.result = result;
		initGroups(result);
	}

	private void initGroups(AnalysisResult result) {
		groups = new ArrayList<>();
		ProductSystem system = result.getSetup().getProductSystem();
		ProcessGrouping restGroup = new ProcessGrouping();
		restGroup.setName(Messages.Common_Rest);
		restGroup.setRest(true);
		for (Process p : system.getProcesses())
			restGroup.getProcesses().add(p);
		groups.add(restGroup);
	}

	public void applyGrouping(ProcessGroupSet groupSet) {
		if (groupSet == null || result == null
				|| result.getSetup().getProductSystem() == null)
			return;
		this.groupSet = groupSet;
		List<Process> processes = new ArrayList<>();
		for (Process p : result.getSetup().getProductSystem().getProcesses())
			processes.add(p);
		List<ProcessGrouping> newGroups = ProcessGrouping.applyOn(processes,
				groupSet, Messages.Common_Rest);
		groups.clear();
		groups.addAll(newGroups);
		updateViewers();
		updateTitle();
	}

	private void updateViewers() {
		if (groupViewer != null && processViewer != null
				&& resultSection != null) {
			groupViewer.refresh(true);
			processViewer.setInput(Collections.emptyList());
			resultSection.update();
		}
	}

	/** Add the current group set name to the section title, if it is not null. */
	void updateTitle() {
		if (groupingSection == null)
			return;
		if (groupSet == null)
			groupingSection.setText(Messages.Common_Groups);
		else
			groupingSection.setText(Messages.Common_Groups + " ("
					+ groupSet.getName() + ")");
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = UI
				.formHeader(managedForm, Messages.Common_Grouping);
		FormToolkit toolkit = managedForm.getToolkit();
		Composite body = UI.formBody(form, toolkit);
		createGroupingSection(toolkit, body);
		resultSection = new GroupResultSection(groups, result,
				editor.getDatabase());
		resultSection.render(body, toolkit);
		form.reflow(true);
	}

	private void createGroupingSection(FormToolkit toolkit, Composite body) {
		groupingSection = UI.section(body, toolkit, Messages.Common_Groups);
		Composite composite = UI.sectionClient(groupingSection, toolkit);
		UI.gridLayout(composite, 2);
		UI.bindActions(groupingSection, new AddGroupAction(),
				new SaveGroupSetAction(this), new OpenGroupSetAction(this));
		createGroupViewer(composite);
		processViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI);
		UI.gridData(processViewer.getControl(), true, false).heightHint = 200;
		configureViewer(processViewer);
		createMoveMenu();
	}

	private void createGroupViewer(Composite composite) {
		groupViewer = new TableViewer(composite, SWT.BORDER);
		GridData groupData = UI
				.gridData(groupViewer.getControl(), false, false);
		groupData.heightHint = 200;
		groupData.widthHint = 250;
		configureViewer(groupViewer);
		groupViewer.setInput(groups);
		UI.bindActions(groupViewer, new DeleteGroupAction());
		groupViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						ProcessGrouping g = Viewers.getFirst(event
								.getSelection());
						if (g != null)
							processViewer.setInput(g.getProcesses());
					}
				});
	}

	private void configureViewer(TableViewer viewer) {
		viewer.setLabelProvider(new GroupPageLabel());
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(new GroupPageSorter());
	}

	private void createMoveMenu() {
		Menu menu = new Menu(processViewer.getTable());
		processViewer.getTable().setMenu(menu);
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText(Messages.Common_Move);
		groupMoveMenu = new Menu(item);
		item.setMenu(groupMoveMenu);
		groupMoveMenu.addListener(SWT.Show, new MenuGroupListener());
	}

	private class AddGroupAction extends Action {

		public AddGroupAction() {
			setImageDescriptor(ImageType.ADD_ICON.getDescriptor());
			setToolTipText(Messages.Common_Add);
		}

		@Override
		public void run() {
			String m = Messages.Common_PleaseEnterName;
			InputDialog dialog = new InputDialog(getSite().getShell(), m, m,
					"", null);
			int code = dialog.open();
			if (code == Window.OK) {
				String name = dialog.getValue();
				ProcessGrouping group = new ProcessGrouping();
				group.setName(name);
				groups.add(group);
				groupViewer.add(group);
				resultSection.update();
			}
		}
	}

	private class DeleteGroupAction extends Action {

		public DeleteGroupAction() {
			setImageDescriptor(ImageType.DELETE_ICON.getDescriptor());
			setText(Messages.Common_Delete);
		}

		@Override
		public void run() {
			ProcessGrouping grouping = Viewers.getFirstSelected(groupViewer);
			if (grouping == null || grouping.isRest())
				return;
			ProcessGrouping rest = findRest();
			if (rest == null)
				return;
			groups.remove(grouping);
			rest.getProcesses().addAll(grouping.getProcesses());
			updateViewers();
		}

		private ProcessGrouping findRest() {
			if (groups == null)
				return null;
			for (ProcessGrouping g : groups) {
				if (g.isRest())
					return g;
			}
			return null;
		}
	}

	private class MenuGroupListener implements Listener, SelectionListener,
			Comparator<ProcessGrouping> {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		/** Executed when an item is selected: moves processes to a group. */
		@Override
		public void widgetSelected(SelectionEvent e) {
			Object o = e.widget.getData();
			if (!(o instanceof ProcessGrouping))
				return;
			ProcessGrouping targetGroup = (ProcessGrouping) o;
			ProcessGrouping sourceGroup = Viewers.getFirstSelected(groupViewer);
			if (sourceGroup == null)
				return;
			List<Process> processes = Viewers.getAllSelected(processViewer);
			if (processes == null || processes.isEmpty())
				return;
			move(sourceGroup, targetGroup, processes);
		}

		private void move(ProcessGrouping sourceGroup,
				ProcessGrouping targetGroup, List<Process> processes) {
			sourceGroup.getProcesses().removeAll(processes);
			targetGroup.getProcesses().addAll(processes);
			processViewer.setInput(sourceGroup.getProcesses());
			resultSection.update();
		}

		/** Executed when the menu is shown: fills the group-menu */
		@Override
		public void handleEvent(Event event) {
			ProcessGrouping group = Viewers.getFirstSelected(groupViewer);
			if (group == null)
				return;
			for (MenuItem item : groupMoveMenu.getItems()) {
				item.removeSelectionListener(this);
				item.dispose();
			}
			List<ProcessGrouping> other = getOther(group);
			for (ProcessGrouping g : other) {
				MenuItem menuItem = new MenuItem(groupMoveMenu, SWT.PUSH);
				menuItem.setText(g.getName());
				menuItem.setData(g);
				menuItem.addSelectionListener(this);
			}
		}

		private List<ProcessGrouping> getOther(ProcessGrouping group) {
			List<ProcessGrouping> other = new ArrayList<>();
			for (ProcessGrouping g : groups) {
				if (g.equals(group))
					continue;
				other.add(g);
			}
			Collections.sort(other, this);
			return other;
		}

		@Override
		public int compare(ProcessGrouping o1, ProcessGrouping o2) {
			if (o1 == null || o2 == null)
				return 0;
			return Strings.compare(o1.getName(), o2.getName());
		}
	}

	private class GroupPageLabel extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			if (element instanceof ProcessGrouping)
				return ImageType.FOLDER_ICON_BLUE.get();
			return ImageType.FLOW_PRODUCT.get();
		}

		@Override
		public String getText(Object element) {
			if (element instanceof ProcessGrouping) {
				ProcessGrouping group = (ProcessGrouping) element;
				return group.getName();
			} else if (element instanceof Process) {
				Process p = (Process) element;
				String name = Strings.cut(p.getName(), 75);
				if (p.getLocation() != null) {
					name += " " + p.getLocation().getCode();
				}
				return name;
			} else
				return null;
		}
	}

	/** A viewer sorter for groups and processes on the grouping page. */
	private class GroupPageSorter extends ViewerSorter {

		@Override
		public int compare(Viewer viewer, Object first, Object second) {
			if ((first instanceof ProcessGrouping)
					&& (second instanceof ProcessGrouping))
				return compareGroups((ProcessGrouping) first,
						(ProcessGrouping) second);
			if ((first instanceof Process) && (second instanceof Process))
				return compareProcesses((Process) first, (Process) second);
			return 0;
		}

		private int compareProcesses(Process first, Process second) {
			return compareNames(first.getName(), second.getName());
		}

		private int compareGroups(ProcessGrouping first, ProcessGrouping second) {
			return compareNames(first.getName(), second.getName());
		}

		private int compareNames(String first, String second) {
			if (first == null)
				return -1;
			if (second == null)
				return 1;
			return first.compareToIgnoreCase(second);
		}

	}

	/**
	 * Action for saving a group set in the grouping page of the analysis
	 * editor.
	 */
	private class SaveGroupSetAction extends Action {

		private Logger log = LoggerFactory.getLogger(getClass());
		private GroupPage page;

		public SaveGroupSetAction(GroupPage page) {
			this.page = page;
			setToolTipText(Messages.Common_Save);
			ImageDescriptor image = PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT);
			setImageDescriptor(image);
		}

		@Override
		public void run() {
			ProcessGroupSet groupSet = page.groupSet;
			if (groupSet == null)
				insertNew();
			else
				updateExisting(groupSet);
		}

		private void insertNew() {
			ProcessGroupSet groupSet;
			try {
				groupSet = createGroupSet();
				if (groupSet == null)
					return;
				setAndSaveGroups(groupSet);
				page.groupSet = groupSet;
				page.updateTitle();
			} catch (Exception e) {
				log.error("Failed to save process group set", e);
			}
		}

		private ProcessGroupSet createGroupSet() throws Exception {
			Shell shell = page.getEditorSite().getShell();
			InputDialog dialog = new InputDialog(shell, Messages.Common_SaveAs,
					Messages.Common_PleaseEnterName, "", null);
			int code = dialog.open();
			if (code == Window.CANCEL)
				return null;
			ProcessGroupSet set = new ProcessGroupSet();
			set.setId(UUID.randomUUID().toString());
			set.setName(dialog.getValue());
			IDatabase database = page.editor.getDatabase();
			database.createDao(ProcessGroupSet.class).insert(set);
			return set;
		}

		private void updateExisting(ProcessGroupSet groupSet) {
			try {
				boolean b = Question.ask(Messages.Common_SaveChanges,
						Messages.Common_SaveChangesQuestion);
				if (b)
					setAndSaveGroups(groupSet);
			} catch (Exception e) {
				log.error("Failed to save process group set", e);
			}
		}

		private List<ProcessGroup> createGroups() {
			List<ProcessGrouping> pageGroups = page.groups;
			if (pageGroups == null)
				return Collections.emptyList();
			List<ProcessGroup> groups = new ArrayList<>();
			for (ProcessGrouping pageGroup : pageGroups) {
				if (pageGroup.isRest())
					continue;
				ProcessGroup group = new ProcessGroup();
				group.setName(pageGroup.getName());
				groups.add(group);
				for (Process process : pageGroup.getProcesses())
					group.getProcessIds().add(process.getId());
			}
			return groups;
		}

		private void setAndSaveGroups(ProcessGroupSet groupSet)
				throws Exception {
			List<ProcessGroup> groups = createGroups();
			groupSet.setGroups(groups);
			IDatabase database = page.editor.getDatabase();
			database.createDao(ProcessGroupSet.class).update(groupSet);
			page.groupSet = groupSet;
		}
	}

}