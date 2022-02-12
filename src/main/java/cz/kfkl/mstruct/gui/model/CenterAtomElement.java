package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("CenterAtom")
public class CenterAtomElement extends XmlLinkedModelElement {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE;
	}

}
