package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("StiffnessConstant")
public class StiffnessConstantElement extends XmlLinkedModelElement implements Comparable<StiffnessConstantElement> {

	public static final String C_PREFIX = "C";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty(C_PREFIX);

	@XmlElementValueProperty
	public StringProperty valueProperty = new SimpleStringProperty("0");

	public StiffnessConstantElement() {
	}

	public StiffnessConstantElement(String name) {
		nameProperty.set(name);
	}

	public String getName() {
		return nameProperty.get();
	}

	public String getValue() {
		return valueProperty.get();
	}

	public void setValue(String value) {
		this.valueProperty.set(value);
	}

	@Override
	public int compareTo(StiffnessConstantElement o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + getName() + "]";
	}

}
