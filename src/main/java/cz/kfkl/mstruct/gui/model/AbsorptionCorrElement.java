package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("AbsorptionCorr")
public class AbsorptionCorrElement extends UniqueElement {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Thickness")
	public StringProperty thicknessPoperty = new SimpleStringProperty();

	@XmlAttributeProperty("Depth")
	public StringProperty depthPoperty = new SimpleStringProperty();

	@XmlAttributeProperty("AbsorptionFactor")
	public StringProperty absorptionFactorPoperty = new SimpleStringProperty();

	public AbsorptionCorrElement() {
		super("AbsorptionCorr");
	}

}
