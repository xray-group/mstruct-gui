package cz.kfkl.mstruct.gui.model;

import java.util.Arrays;
import java.util.List;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.PowderPatternController;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("PowderPatternCrystal")
public class PowderPatternCrystalsModel extends XmlLinkedModelElement
		implements FxmlFileNameProvider<PowderPatternController>, ParamContainer {
	private static final String FXML_FILE_NAME = "powderPatternCrystal.fxml";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	// <PowderPatternCrystal Name="diffData_Anatase" Crystal="Anatase"
	// IgnoreImagScattFact="1">

	@XmlAttributeProperty("Crystal")
	public StringProperty crystalProperty = new SimpleStringProperty();

	@XmlAttributeProperty("IgnoreImagScattFact")
	public IntegerProperty ignoreImagScattFactProperty = new SimpleIntegerProperty();

	@XmlUniqueElement
	public ReflectionProfileElement reflectionProfile = new ReflectionProfileElement();

	@XmlUniqueElement
	public ParUniqueElement globalBisoPar = new ParUniqueElement("globalBiso");

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentElement powderPatternComponent = new PowderPatternComponentElement(nameProperty);

	@XmlUniqueElement
	public AbsorptionCorrElement absorptionCorrElement = new AbsorptionCorrElement();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(globalBisoPar);
	}

	@Override
	public String formatParamContainerName() {
		return "Crystal: " + nameProperty.get();
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		return List.of(reflectionProfile);
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public String toString() {
		return nameProperty.get() + " (" + crystalProperty.get() + ")";
	}
}
