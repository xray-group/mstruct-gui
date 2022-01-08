package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElementKey;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

@XmlElementName("PowderPatternComponent")
public class PowderPatternComponentElement extends XmlLinkedModelElement {

	@XmlAttributeProperty("Scale")
	public StringProperty scalePoperty = new SimpleStringProperty("1");

	@XmlUniqueElementKey("Name")
	public String name;

	public PowderPatternComponentElement(StringProperty nameProperty) {
		nameProperty.addListener((ChangeListener<? super String>) (ov, oldValue, newValue) -> name = newValue);
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE_WITH_GAP;
	}

}
