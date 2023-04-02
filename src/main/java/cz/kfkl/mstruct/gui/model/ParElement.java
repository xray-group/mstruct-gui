package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.BooleanZeroOneStringFormatter;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("Par")
public class ParElement extends XmlLinkedModelElement implements ParamTreeNode {

	@XmlAttributeProperty(value = "Refined", converter = BooleanZeroOneStringFormatter.class)
	public BooleanProperty refinedProperty = new SimpleBooleanProperty(false);

	@XmlAttributeProperty(value = "Limited", converter = BooleanZeroOneStringFormatter.class)
	public BooleanProperty limitedProperty = new SimpleBooleanProperty(false);

	@XmlAttributeProperty("Min")
	public StringProperty minProperty = new SimpleStringProperty("1");

	@XmlAttributeProperty("Max")
	public StringProperty maxProperty = new SimpleStringProperty("100");

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty;

	@XmlElementValueProperty
	public StringProperty valueProperty = new SimpleStringProperty("0");

	public BooleanProperty fittedProperty = new SimpleBooleanProperty(false);
	public StringProperty fittedValueProperty = new SimpleStringProperty("");

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE;
	}

	public ParElement(StringProperty nameProperty) {
		this.nameProperty = nameProperty;
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

	public String getName() {
		return nameProperty == null ? "propertyNull" : nameProperty.get();
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return FXCollections.emptyObservableList();
	}

	@Override
	public boolean isParameter() {
		return true;
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return nameProperty;
	}

	@Override
	public BooleanProperty getRefinedProperty() {
		return refinedProperty;
	}

	@Override
	public BooleanProperty getLimitedProperty() {
		return limitedProperty;
	}

	@Override
	public StringProperty getMinProperty() {
		return minProperty;
	}

	@Override
	public StringProperty getMaxProperty() {
		return maxProperty;
	}

	@Override
	public StringProperty getValueProperty() {
		return valueProperty;
	}

	@Override
	public boolean isIhklParameter() {
		return false;
	}

	@Override
	public BooleanProperty getFittedProperty() {
		return fittedProperty;
	}

	@Override
	public StringProperty getFittedValueProperty() {
		return fittedValueProperty;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + getName() + "]";
	}

}
