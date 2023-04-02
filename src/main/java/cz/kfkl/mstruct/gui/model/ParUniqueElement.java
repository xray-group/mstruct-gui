package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElementKey;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

@XmlElementName("Par")
public class ParUniqueElement extends ParElement implements ParamTreeNode {

	@XmlUniqueElementKey("Name")
	public StringProperty nameProperty;

	public ParUniqueElement(String name) {
		super(new ReadOnlyStringWrapper(name));
		this.nameProperty = super.nameProperty;
	}
}
