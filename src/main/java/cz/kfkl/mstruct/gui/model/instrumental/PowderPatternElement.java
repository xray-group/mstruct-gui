package cz.kfkl.mstruct.gui.model.instrumental;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.model.OptionChoice;
import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.SingleValueUniqueElement;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternCrystalsModel;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternController;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.ObservableListWrapper;
import cz.kfkl.mstruct.gui.utils.SimpleCombinedObservableList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("PowderPattern")
public class PowderPatternElement extends InstrumentalModel<PowderPatternController> {
	private static final Logger LOG = LoggerFactory.getLogger(PowderPatternElement.class);

	private static final String FXML_FILE_NAME = "powderPattern.fxml";

	private static final String DEFAULT_PATTERN_NAME = "pattern0";

	public StringProperty paramContainerName = new SimpleStringProperty();

	@XmlAttributeProperty("SpaceGroup")
	public StringProperty spaceGroupProperty = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement zeroPar = new ParUniqueElement("Zero");
	@XmlUniqueElement
	public ParUniqueElement thetaDisplacementPar = new ParUniqueElement("2ThetaDisplacement");
	@XmlUniqueElement
	public ParUniqueElement thetaTransparencyPar = new ParUniqueElement("2ThetaTransparency");

	@XmlUniqueElement
	public OptionUniqueElement useIntegratedProfilesOption = new OptionUniqueElement("Use Integrated Profiles", 1,
			new OptionChoice("Yes (recommended)"), new OptionChoice("No"));

	@XmlUniqueElement
	public RadiationElement radiationElement = new RadiationElement();

	@XmlUniqueElement("MaxSinThetaOvLambda")
	public SingleValueUniqueElement maxSinThetaOvLambdaElement = new SingleValueUniqueElement("0");

	@XmlUniqueElement
	public GeometryElement geometryElement = new GeometryElement();

	@XmlElementList
	public ObservableList<PowderPatternBackgroundModel> powderPatternComponents = FXCollections.observableArrayList();

	@XmlElementList
	public ObservableList<PowderPatternCrystalsModel> powderPatternCrystals = FXCollections.observableArrayList();
	public ObservableList<PowderPatternCrystalsModel> powderPatternCrystalsObserved = null;

	@XmlUniqueElement("XIobsSigmaWeightList")
	public SingleValueUniqueElement xIobsSigmaWeightListElement = new SingleValueUniqueElement("");

	@XmlElementList
	public ObservableList<ExcludeXElement> excludeRegions = FXCollections.observableArrayList();

	private SimpleCombinedObservableList<ParamTreeNode> children = new SimpleCombinedObservableList<ParamTreeNode>(
			List.of(zeroPar, thetaDisplacementPar, thetaTransparencyPar), List.of(radiationElement), powderPatternComponents,
			powderPatternCrystals);

	public PowderPatternElement() {
		this.nameProperty.set(DEFAULT_PATTERN_NAME);
		this.paramContainerName.bind(Bindings.concat("Powder Pattern: ", this.nameProperty));
	}

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		
		// must be done after the binding so the wrapped list is already populated from
		// XML, items added to the wrapped list only are shown but their observable
		// properties are not bound
		powderPatternCrystalsObserved = new ObservableListWrapper<PowderPatternCrystalsModel>(powderPatternCrystals,
				item -> new Observable[] { item.nameProperty, item.crystalProperty });
	}

	@Override
	public Set<? extends String> findUsedCrystals() {
		Set<String> usedCrystalNames = new LinkedHashSet<String>();
		for (PowderPatternCrystalsModel ppCrystalPhase : powderPatternCrystals) {
			String crystalName = ppCrystalPhase.crystalProperty.get();
			if (JvStringUtils.isNotBlank(crystalName)) {
				usedCrystalNames.add(crystalName);
			}
		}
		return usedCrystalNames;
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return paramContainerName;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

	public void replaceExcludeRegions(List<ExcludeXElement> newRegions) {
		Collections.sort(newRegions, ExcludeXElement.EXCLUDE_REGIONS_TABLE_COMPARATOR);

		excludeRegions.clear();
		for (ExcludeXElement el : newRegions) {

			ExcludeXElement newEl = new ExcludeXElement();
			excludeRegions.add(newEl);
			// the add binds the newEl with newly created XML element which would overwrite
			// the value
			newEl.fromProperty.set(el.fromProperty.get());
			newEl.toProperty.set(el.toProperty.get());
		}

	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	public void updateIhklParams(PowderPatternElement firstPowderPattern) {
		for (PowderPatternCrystalsModel ppc : powderPatternCrystals) {

			String name = ppc.getName();
			PowderPatternCrystalsModel fittedPpc = firstPowderPattern.findPowderPatternCrytal(name);
			if (fittedPpc != null) {
				ppc.updateIhklParams(fittedPpc);
			} else {
				LOG.info("No Phase (PowderPatternModel) found for name [{}]", name);
			}

		}

	}

	private PowderPatternCrystalsModel findPowderPatternCrytal(String name) {
		for (PowderPatternCrystalsModel ppc : powderPatternCrystals) {
			if (ppc.getName().equals(name)) {
				return ppc;
			}
		}
		return null;
	}

}
