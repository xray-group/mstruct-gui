package cz.kfkl.mstruct.gui.ui;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.crystals.CrystalModel;
import cz.kfkl.mstruct.gui.model.instrumental.ExcludeXElement;
import cz.kfkl.mstruct.gui.model.instrumental.InstrumentalModel;
import cz.kfkl.mstruct.gui.model.instrumental.PowderPatternElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.ObservableListWrapper;
import cz.kfkl.mstruct.gui.utils.SimpleCombinedObservableList;
import cz.kfkl.mstruct.gui.utils.tree.FilterableTreeItem;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ObjCrystModel extends XmlLinkedModelElement implements ParamContainer {
	private static final Logger LOG = LoggerFactory.getLogger(ObjCrystModel.class);

	// Following two are used when creating a new file
	@XmlAttributeProperty("Date")
	public StringProperty date = new SimpleStringProperty(ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	@XmlAttributeProperty("Revision")
	public StringProperty revision = new SimpleStringProperty("2017002");

	@XmlElementList
	public ObservableList<CrystalModel> crystals = FXCollections.observableArrayList();
	public ObservableList<CrystalModel> crystalsObserved;

	public FilteredList<CrystalModel> usedCrystals = new FilteredList<>(crystals);

	@XmlElementList
	public ObservableList<InstrumentalModel<?>> instruments = FXCollections.observableArrayList();

	public FilterableTreeItem<ParamTreeNode> treeRoot;
	public IntegerProperty parametersCount = new SimpleIntegerProperty(0);
	public IntegerProperty refinedParametersCount = new SimpleIntegerProperty(0);

	private ChangeListener<? super Boolean> refinedParamsListener;
	private ListChangeListener<ParamTreeNode> addRemoveParamTreeNodeListener;

	private SimpleCombinedObservableList<ParamTreeNode> children = new SimpleCombinedObservableList<ParamTreeNode>(usedCrystals,
			instruments);

	public ObjCrystModel() {

		refinedParamsListener = createRefinedParamsListener();
		// adding the listener before the children lists are populated so it can add
		// corresponding listeners on nodes added to them
		addRemoveParamTreeNodeListener = new AddRemoveParamTreeNodeListener();
		children.addListener(addRemoveParamTreeNodeListener);
	}

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		updateUsedCrystalsPredicate();
		crystalsObserved = new ObservableListWrapper<CrystalModel>(crystals, item -> new Observable[] { item.nameProperty });
	}

	public void updateUsedCrystalsPredicate() {
		usedCrystals.setPredicate(createUsedCrystalsPredicate());
	}

	private Predicate<? super CrystalModel> createUsedCrystalsPredicate() {
		Set<String> usedCrystalNames = findUsedCrystals();

		Predicate<? super CrystalModel> usedCrystalsPredicate = new Predicate<>() {

			@Override
			public boolean test(CrystalModel t) {
				return usedCrystalNames.contains(t.nameProperty.get());
			}
		};
		return usedCrystalsPredicate;
	}

	@Override
	protected ObjCrystModel decideRoot(XmlLinkedModelElement parentModelElement) {
		return this;
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return new SimpleStringProperty("Root");
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

	public Set<String> findUsedCrystals() {
		Set<String> usedCrystalNames = new LinkedHashSet<String>();
		for (InstrumentalModel<?> inst : instruments) {
			usedCrystalNames.addAll(inst.findUsedCrystals());
		}
		return usedCrystalNames;
	}

	public CrystalModel getCrystal(String name) {
		if (name != null) {
			for (CrystalModel cm : crystals) {
				if (name.equals(cm.getName())) {
					return cm;
				}
			}
		}
		return null;
	}

	// TODO if we support multiple Instruments (not only "pattern0") this would be
	// done differently
	public PowderPatternElement getFirstPowderPattern() {
		for (InstrumentalModel<?> instModel : instruments) {
			if (instModel instanceof PowderPatternElement) {
				return (PowderPatternElement) instModel;
			}
		}

		throw new UnexpectedException("An instrumtal PowderPattern model should be created first.");
	}

	public List<ExcludeXElement> getExcludeRegions() {
		return getFirstPowderPattern().excludeRegions;
	}

	public void replaceExcludeRegions(List<ExcludeXElement> newRegions) {
		getFirstPowderPattern().replaceExcludeRegions(newRegions);
	}

	public void updateIhklParams(ObjCrystModel fittedRootModel) {
		getFirstPowderPattern().updateIhklParams(fittedRootModel.getFirstPowderPattern());
	}

	private void registerRefinedPropertyListener(ParamTreeNode ch) {
		unregisterRefinedPropertyListener(ch);
		ch.getRefinedProperty().addListener(refinedParamsListener);
	}

	private void unregisterRefinedPropertyListener(ParamTreeNode ch) {
		ch.getRefinedProperty().removeListener(refinedParamsListener);
	}

	private ChangeListener<? super Boolean> createRefinedParamsListener() {
		return (ov, o, n) -> {
			int inc = 0;
			if (o && !n) {
				inc = -1;
			} else if (!o && n) {
				inc = 1;
			}
			LOG.trace("  refinedParamsListener inc {}", inc);
			if (inc != 0) {
				this.refinedParametersCount.set(this.refinedParametersCount.get() + inc);
			}
		};
	}

	private void increaseCounts(ParamTreeNode node, int inc) {
		parametersCount.set(parametersCount.get() + inc);
		if (node.getRefinedProperty().get()) {
			refinedParametersCount.set(refinedParametersCount.get() + inc);
		}
	}

	private class AddRemoveParamTreeNodeListener implements ListChangeListener<ParamTreeNode> {
		private Consumer<ParamTreeNode> removedPar = (node) -> {
			increaseCounts(node, -1);
			unregisterRefinedPropertyListener(node);
		};

		private Consumer<ParamTreeNode> addedPar = (node) -> {
			increaseCounts(node, 1);
			registerRefinedPropertyListener(node);
		};

		@Override
		public void onChanged(Change<? extends ParamTreeNode> c) {
			while (c.next()) {
				if (c.wasRemoved()) {
					ParamTreeNode.applyRecursively(c.getRemoved(), removedPar,
							list -> list.removeListener(addRemoveParamTreeNodeListener));
				}

				if (c.wasAdded()) {
					ParamTreeNode.applyRecursively(c.getAddedSubList(), addedPar,
							list -> list.addListener(addRemoveParamTreeNodeListener));
				}
			}
		}
	}

}
