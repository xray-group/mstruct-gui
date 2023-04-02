package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.StringProperty;

@XmlElementName("PowderPatternComponent")
public class PowderPatternComponentWithScaleParElement extends PowderPatternComponentElement {

	@XmlUniqueElement
	public ParUniqueElement scalePar = new ParUniqueElement("Scale");

	public PowderPatternComponentWithScaleParElement(StringProperty ownerObjectNameProperty) {
		super(ownerObjectNameProperty);
		scalePar.setValue("1");
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

}
