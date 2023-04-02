package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("AbsorptionCorr")
public class AbsorptionCorrElement extends XmlLinkedModelElement {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty("AbsorptionCorr");

	@XmlAttributeProperty("Thickness")
	public StringProperty thicknessProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Depth")
	public StringProperty depthProperty = new SimpleStringProperty();

	@XmlAttributeProperty("AbsorptionFactor")
	public StringProperty absorptionFactorProperty = new SimpleStringProperty();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE;
	}

}
