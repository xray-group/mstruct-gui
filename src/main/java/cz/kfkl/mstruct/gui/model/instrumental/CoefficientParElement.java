package cz.kfkl.mstruct.gui.model.instrumental;

import cz.kfkl.mstruct.gui.model.ParElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;

@XmlElementName("Par")
public class CoefficientParElement extends ParElement {

	public static final String BACKGROUND_COEF_NAME_PREFIX = "Background_Coef_";

	private int index;

	public CoefficientParElement() {
		super(new SimpleStringProperty(BACKGROUND_COEF_NAME_PREFIX));
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
