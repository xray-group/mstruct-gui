package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Par")
public class CoefficientParElement extends ParUniqueElement {

	public static final String BACKGROUND_COEF_NAME_PREFIX = "Background_Coef_";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty(BACKGROUND_COEF_NAME_PREFIX);

	private int index;

	public CoefficientParElement() {
		super(null);
	}

	// The getName() is called from the Parameters tree
	@Override
	public String getName() {
		return nameProperty.getValue();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Integer getValueInteger() {
		Integer valueOf = null;
		try {
			valueOf = Integer.valueOf(this.valueProperty.get());
		} catch (NumberFormatException e) {
			// ignore
		}
		return valueOf;
	}

	public void setValueInteger(Integer value) {
		this.valueProperty.set(value == null ? null : value.toString());
	}

}
