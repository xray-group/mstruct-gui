package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.utils.BooleanZeroOneStringFormatter;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Par")
public class ParUniqueElement extends UniqueElement {

	@XmlAttributeProperty(value = "Refined", converter = BooleanZeroOneStringFormatter.class)
	public BooleanProperty refinedProperty = new SimpleBooleanProperty(false);

	@XmlAttributeProperty(value = "Limited", converter = BooleanZeroOneStringFormatter.class)
	public BooleanProperty limitedProperty = new SimpleBooleanProperty(false);

	@XmlAttributeProperty("Min")
	public StringProperty minProperty = new SimpleStringProperty("1");

	@XmlAttributeProperty("Max")
	public StringProperty maxProperty = new SimpleStringProperty("100");

	@XmlElementValueProperty
	public StringProperty valueProperty = new SimpleStringProperty("0");

	public ParUniqueElement(String name) {
		super(name);
	}

	public String getValue() {
		return valueProperty.get();
	}

	public void setValue(String value) {
		this.valueProperty.set(value);
	}

	public String getMin() {
		return minProperty.get();
	}

	public void setMin(String min) {
		this.minProperty.set(min);
	}

	public String getMax() {
		return maxProperty.get();
	}

	public void setMax(String max) {
		this.maxProperty.set(max);
	}

	public boolean isMocked() {
		return getXmlElement() == null;
	}

}
