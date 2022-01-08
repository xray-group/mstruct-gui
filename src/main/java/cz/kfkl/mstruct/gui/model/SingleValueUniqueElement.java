package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SingleValueUniqueElement extends XmlLinkedModelElement {

	@XmlElementValueProperty
	public StringProperty valueProperty = new SimpleStringProperty();

	public SingleValueUniqueElement(String value) {
		this.valueProperty.set(value);
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE;
	}

}
