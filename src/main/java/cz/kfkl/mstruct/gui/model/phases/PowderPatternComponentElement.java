package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElementKey;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("PowderPatternComponent")
public class PowderPatternComponentElement extends XmlLinkedModelElement {

	@XmlAttributeProperty("Scale")
	public StringProperty scaleProperty = new SimpleStringProperty("1");

	@XmlUniqueElementKey("Name")
	public StringProperty name = new SimpleStringProperty();

	public PowderPatternComponentElement(StringProperty ownerObjectNameProperty) {
		name.bind(ownerObjectNameProperty);
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE_WITH_GAP;
	}

}
