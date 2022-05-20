package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElementKey;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("PowderPatternComponent")
public class PowderPatternComponentWithScaleParElement extends XmlLinkedModelElement {

	@XmlUniqueElementKey("Name")
	public StringProperty name = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement scalePar = new ParUniqueElement("Scale");

	public PowderPatternComponentWithScaleParElement(StringProperty ownerObjectNameProperty) {
		name.bind(ownerObjectNameProperty);
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

}
