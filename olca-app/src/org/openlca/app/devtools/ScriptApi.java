package org.openlca.app.devtools;

import org.openlca.app.App;
import org.openlca.app.db.Cache;
import org.openlca.core.database.ActorDao;
import org.openlca.core.database.FlowDao;
import org.openlca.core.database.FlowPropertyDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ImpactMethodDao;
import org.openlca.core.database.ProcessDao;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.database.ProjectDao;
import org.openlca.core.database.SourceDao;
import org.openlca.core.database.UnitGroupDao;
import org.openlca.core.math.CalculationSetup;
import org.openlca.core.math.SystemCalculator;
import org.openlca.core.model.Actor;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.ImpactMethod;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProductSystem;
import org.openlca.core.model.Project;
import org.openlca.core.model.Source;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.descriptors.ActorDescriptor;
import org.openlca.core.model.descriptors.Descriptors;
import org.openlca.core.model.descriptors.FlowDescriptor;
import org.openlca.core.model.descriptors.FlowPropertyDescriptor;
import org.openlca.core.model.descriptors.ImpactMethodDescriptor;
import org.openlca.core.model.descriptors.ProcessDescriptor;
import org.openlca.core.model.descriptors.ProductSystemDescriptor;
import org.openlca.core.model.descriptors.ProjectDescriptor;
import org.openlca.core.model.descriptors.SourceDescriptor;
import org.openlca.core.model.descriptors.UnitGroupDescriptor;
import org.openlca.core.results.ContributionResult;
import org.openlca.core.results.ContributionResultProvider;
import org.openlca.core.results.SimpleResult;
import org.openlca.core.results.SimpleResultProvider;

import java.util.List;

public class ScriptApi {

	private final IDatabase database;

	public ScriptApi(IDatabase database) {
		this.database = database;
	}

	/** Returns true if a connection to a database exists. */
	public boolean isConnected() {
		return database != null;
	}

	public Actor getActor(String name) {
		ActorDao dao = new ActorDao(database);
		List<Actor> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public Actor getActor(int id) {
		ActorDao dao = new ActorDao(database);
		return dao.getForId(id);
	}

	public List<ActorDescriptor> getActorDescriptors() {
		ActorDao dao = new ActorDao(database);
		return dao.getDescriptors();
	}

	public void eachActor(Consumer<Actor> consumer) {
		ActorDao dao = new ActorDao(database);
		for (ActorDescriptor descriptor : dao.getDescriptors()) {
			Actor val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public Source getSource(String name) {
		SourceDao dao = new SourceDao(database);
		List<Source> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public Source getSource(int id) {
		SourceDao dao = new SourceDao(database);
		return dao.getForId(id);
	}

	public List<SourceDescriptor> getSourceDescriptors() {
		SourceDao dao = new SourceDao(database);
		return dao.getDescriptors();
	}

	public void eachSource(Consumer<Source> consumer) {
		SourceDao dao = new SourceDao(database);
		for (SourceDescriptor descriptor : dao.getDescriptors()) {
			Source val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public UnitGroup getUnitGroup(String name) {
		UnitGroupDao dao = new UnitGroupDao(database);
		List<UnitGroup> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public UnitGroup getUnitGroup(int id) {
		UnitGroupDao dao = new UnitGroupDao(database);
		return dao.getForId(id);
	}

	public List<UnitGroupDescriptor> getUnitGroupDescriptors() {
		UnitGroupDao dao = new UnitGroupDao(database);
		return dao.getDescriptors();
	}

	public void eachUnitGroup(Consumer<UnitGroup> consumer) {
		UnitGroupDao dao = new UnitGroupDao(database);
		for (UnitGroupDescriptor descriptor : dao.getDescriptors()) {
			UnitGroup val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public FlowProperty getFlowProperty(String name) {
		FlowPropertyDao dao = new FlowPropertyDao(database);
		List<FlowProperty> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public FlowProperty getFlowProperty(int id) {
		FlowPropertyDao dao = new FlowPropertyDao(database);
		return dao.getForId(id);
	}

	public List<FlowPropertyDescriptor> getFlowPropertyDescriptors() {
		FlowPropertyDao dao = new FlowPropertyDao(database);
		return dao.getDescriptors();
	}

	public void eachFlowProperty(Consumer<FlowProperty> consumer) {
		FlowPropertyDao dao = new FlowPropertyDao(database);
		for (FlowPropertyDescriptor descriptor : dao.getDescriptors()) {
			FlowProperty val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public Flow getFlow(String name) {
		FlowDao dao = new FlowDao(database);
		List<Flow> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public Flow getFlow(int id) {
		FlowDao dao = new FlowDao(database);
		return dao.getForId(id);
	}

	public List<FlowDescriptor> getFlowDescriptors() {
		FlowDao dao = new FlowDao(database);
		return dao.getDescriptors();
	}

	public void eachFlow(Consumer<Flow> consumer) {
		FlowDao dao = new FlowDao(database);
		for (FlowDescriptor descriptor : dao.getDescriptors()) {
			Flow val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public Process getProcess(String name) {
		ProcessDao dao = new ProcessDao(database);
		List<Process> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public Process getProcess(int id) {
		ProcessDao dao = new ProcessDao(database);
		return dao.getForId(id);
	}

	public List<ProcessDescriptor> getProcessDescriptors() {
		ProcessDao dao = new ProcessDao(database);
		return dao.getDescriptors();
	}

	public void eachProcess(Consumer<Process> consumer) {
		ProcessDao dao = new ProcessDao(database);
		for (ProcessDescriptor descriptor : dao.getDescriptors()) {
			Process process = dao.getForId(descriptor.getId());
			consumer.accept(process);
		}
	}

	public ImpactMethod getMethod(String name) {
		ImpactMethodDao dao = new ImpactMethodDao(database);
		List<ImpactMethod> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public ImpactMethod getMethod(int id) {
		ImpactMethodDao dao = new ImpactMethodDao(database);
		return dao.getForId(id);
	}

	public List<ImpactMethodDescriptor> getMethodDescriptors() {
		ImpactMethodDao dao = new ImpactMethodDao(database);
		return dao.getDescriptors();
	}

	public void eachMethod(Consumer<ImpactMethod> consumer) {
		ImpactMethodDao dao = new ImpactMethodDao(database);
		for (ImpactMethodDescriptor descriptor : dao.getDescriptors()) {
			ImpactMethod val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public ProductSystem getSystem(String name) {
		ProductSystemDao dao = new ProductSystemDao(database);
		List<ProductSystem> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public ProductSystem getSystem(int id) {
		ProductSystemDao dao = new ProductSystemDao(database);
		return dao.getForId(id);
	}

	public List<ProductSystemDescriptor> getSystemDescriptors() {
		ProductSystemDao dao = new ProductSystemDao(database);
		return dao.getDescriptors();
	}

	public void eachSystem(Consumer<ProductSystem> consumer) {
		ProductSystemDao dao = new ProductSystemDao(database);
		for (ProductSystemDescriptor descriptor : dao.getDescriptors()) {
			ProductSystem val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public Project getProject(String name) {
		ProjectDao dao = new ProjectDao(database);
		List<Project> list = dao.getForName(name);
		return list.isEmpty() ? null : list.get(0);
	}

	public Project getProject(int id) {
		ProjectDao dao = new ProjectDao(database);
		return dao.getForId(id);
	}

	public List<ProjectDescriptor> getProjectDescriptors() {
		ProjectDao dao = new ProjectDao(database);
		return dao.getDescriptors();
	}

	public void eachProject(Consumer<Project> consumer) {
		ProjectDao dao = new ProjectDao(database);
		for (ProjectDescriptor descriptor : dao.getDescriptors()) {
			Project val = dao.getForId(descriptor.getId());
			consumer.accept(val);
		}
	}

	public void updateActor(Actor model) {
		ActorDao dao = new ActorDao(database);
		dao.update(model);
	}

	public void insertActor(Actor model) {
		ActorDao dao = new ActorDao(database);
		dao.insert(model);
	}

	public void updateSource(Source model) {
		SourceDao dao = new SourceDao(database);
		dao.update(model);
	}

	public void insertSource(Source model) {
		SourceDao dao = new SourceDao(database);
		dao.insert(model);
	}

	public void updateUnitGroup(UnitGroup model) {
		UnitGroupDao dao = new UnitGroupDao(database);
		dao.update(model);
	}

	public void insertUnitGroup(UnitGroup model) {
		UnitGroupDao dao = new UnitGroupDao(database);
		dao.insert(model);
	}

	public void updateFlowProperty(FlowProperty model) {
		FlowPropertyDao dao = new FlowPropertyDao(database);
		dao.update(model);
	}

	public void insertFlowProperty(FlowProperty model) {
		FlowPropertyDao dao = new FlowPropertyDao(database);
		dao.insert(model);
	}

	public void updateFlow(Flow model) {
		FlowDao dao = new FlowDao(database);
		dao.update(model);
	}

	public void insertFlow(Flow model) {
		FlowDao dao = new FlowDao(database);
		dao.insert(model);
	}

	public void updateProcess(Process model) {
		ProcessDao dao = new ProcessDao(database);
		dao.update(model);
	}

	public void insertProcess(Process model) {
		ProcessDao dao = new ProcessDao(database);
		dao.insert(model);
	}

	public void updateSystem(ProductSystem model) {
		ProductSystemDao dao = new ProductSystemDao(database);
		dao.update(model);
	}

	public void insertSystem(ProductSystem model) {
		ProductSystemDao dao = new ProductSystemDao(database);
		dao.insert(model);
	}

	public void updateMethod(ImpactMethod model) {
		ImpactMethodDao dao = new ImpactMethodDao(database);
		dao.update(model);
	}

	public void insertMethod(ImpactMethod model) {
		ImpactMethodDao dao = new ImpactMethodDao(database);
		dao.insert(model);
	}

	public void updateProject(Project model) {
		ProjectDao dao = new ProjectDao(database);
		dao.update(model);
	}

	public void insertProject(Project model) {
		ProjectDao dao = new ProjectDao(database);
		dao.insert(model);
	}

	public SimpleResultProvider<SimpleResult> calculate(ProductSystem system) {
		return calculate(system, null);
	}

	public SimpleResultProvider<SimpleResult> calculate(ProductSystem system,
			ImpactMethod method) {
		CalculationSetup setup = new CalculationSetup(system);
		if (method != null)
			setup.setImpactMethod(Descriptors.toDescriptor(method));
		setup.getParameterRedefs().addAll(system.getParameterRedefs());
		SystemCalculator calculator = new SystemCalculator(
				Cache.getMatrixCache(), App.getSolver());
		SimpleResult result = calculator.calculateSimple(setup);
		return new SimpleResultProvider<>(result, Cache.getEntityCache());
	}

	public ContributionResultProvider<ContributionResult> analyze(ProductSystem
			system) {
		return analyze(system, null);
	}

	public ContributionResultProvider<ContributionResult> analyze(ProductSystem
			system, ImpactMethod method) {
		CalculationSetup setup = new CalculationSetup(system);
		if (method != null)
			setup.setImpactMethod(Descriptors.toDescriptor(method));
		setup.getParameterRedefs().addAll(system.getParameterRedefs());
		SystemCalculator calculator = new SystemCalculator(
				Cache.getMatrixCache(), App.getSolver());
		ContributionResult result = calculator.calculateContributions(setup);
		return new ContributionResultProvider<>(result, Cache.getEntityCache());
	}

	// It seems that Jython does not map lambdas to interfaces with more than
	// 1 method even when the other methods are declared as default methods.
	// Thus,
	// we have our own consumer function here.
	@FunctionalInterface
	public static interface Consumer<T> {
		void accept(T value);
	}

}