package org.openlca.app.inventory;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.openlca.app.db.Cache;
import org.openlca.app.util.Numbers;
import org.openlca.app.util.TableColumnSorter;
import org.openlca.app.util.Tables;
import org.openlca.app.util.UI;
import org.openlca.core.database.EntityCache;
import org.openlca.core.model.descriptors.ImpactCategoryDescriptor;

import com.google.common.primitives.Doubles;

public class ImpactResultPage extends FormPage {

	private final String IMPACT_CATEGORY = "Impact category";
	private final String RESULT = "Result";
	private final String REFERENCE_UNIT = "Reference unit";

	private EntityCache cache = Cache.getEntityCache();
	private FormToolkit toolkit;
	private ImpactResultProvider result;

	public ImpactResultPage(FormEditor editor, ImpactResultProvider result) {
		super(editor, "ImpactResultPage", "LCIA Result");
		this.result = result;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(
				HyperlinkSettings.UNDERLINE_HOVER);
		form.setText("LCIA - Total");
		toolkit.decorateFormHeading(form.getForm());
		Composite body = UI.formBody(form, toolkit);
		TableViewer impactViewer = createSectionAndViewer(body);
		form.reflow(true);
		impactViewer.setInput(result.getImpactCategories(cache));
	}

	private TableViewer createSectionAndViewer(Composite parent) {
		Section section = UI.section(parent, toolkit, "Impact results");
		UI.gridData(section, true, true);
		Composite composite = toolkit.createComposite(section);
		section.setClient(composite);
		UI.gridLayout(composite, 1);
		String[] columns = { IMPACT_CATEGORY, RESULT, REFERENCE_UNIT };
		TableViewer viewer = Tables.createViewer(composite, columns);
		LCIALabelProvider labelProvider = new LCIALabelProvider();
		viewer.setLabelProvider(labelProvider);
		createColumnSorters(viewer, labelProvider);
		Tables.bindColumnWidths(viewer.getTable(), 0.50, 0.30, 0.2);
		return viewer;
	}

	private void createColumnSorters(TableViewer viewer, LCIALabelProvider p) {
		//@formatter:off
		Tables.registerSorters(viewer, 
				new TableColumnSorter<>(ImpactCategoryDescriptor.class, 0, p),
				new AmountSorter(),
				new TableColumnSorter<>(ImpactCategoryDescriptor.class, 2, p));
		//@formatter:on
	}

	private class LCIALabelProvider extends BaseLabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int col) {
			if (!(element instanceof ImpactCategoryDescriptor))
				return null;
			ImpactCategoryDescriptor impactCategory = (ImpactCategoryDescriptor) element;
			switch (col) {
			case 0:
				return impactCategory.getName();
			case 1:
				return Numbers.format(result.getAmount(impactCategory));
			case 2:
				return impactCategory.getReferenceUnit();
			default:
				return null;
			}
		}
	}

	private class AmountSorter extends
			TableColumnSorter<ImpactCategoryDescriptor> {
		public AmountSorter() {
			super(ImpactCategoryDescriptor.class, 1);
		}

		@Override
		public int compare(ImpactCategoryDescriptor d1,
				ImpactCategoryDescriptor d2) {
			double val1 = result.getAmount(d1);
			double val2 = result.getAmount(d2);
			return Doubles.compare(val1, val2);
		}
	}

}
