package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.UniqueElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("AbsorptionCorr")
public class AbsorptionCorrElement extends UniqueElement {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Thickness")
	public StringProperty thicknessProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Depth")
	public StringProperty depthProperty = new SimpleStringProperty();

	@XmlAttributeProperty("AbsorptionFactor")
	public StringProperty absorptionFactorProperty = new SimpleStringProperty();

	public AbsorptionCorrElement() {
		super("AbsorptionCorr");
	}

}
