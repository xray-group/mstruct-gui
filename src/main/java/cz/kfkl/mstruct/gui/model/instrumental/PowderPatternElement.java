package cz.kfkl.mstruct.gui.model.instrumental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.kfkl.mstruct.gui.model.OptionChoice;
import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.SingleValueUniqueElement;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternCrystalsModel;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternController;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("PowderPattern")
public class PowderPatternElement extends InstrumentalModel<PowderPatternController> {
	private static final String FXML_FILE_NAME = "powderPattern.fxml";

	private static final String DEFAULT_PATTERN_NAME = "pattern0";

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

	@XmlUniqueElement("XIobsSigmaWeightList")
	public SingleValueUniqueElement xIobsSigmaWeightListElement = new SingleValueUniqueElement("");

	@XmlElementList
	public ObservableList<ExcludeXElement> excludeRegions = FXCollections.observableArrayList();

	public PowderPatternElement() {
		this.nameProperty.set(DEFAULT_PATTERN_NAME);
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
	public List<ParUniqueElement> getParams() {
		return List.of(zeroPar, thetaDisplacementPar, thetaTransparencyPar);
	}

	@Override
	public String formatParamContainerName() {
		return "Powder Pattern: " + nameProperty.get();
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		List<ParamContainer> list = new ArrayList<>();
		list.add(radiationElement);
		list.addAll(powderPatternComponents);
		list.addAll(powderPatternCrystals);

		return list;
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

}
