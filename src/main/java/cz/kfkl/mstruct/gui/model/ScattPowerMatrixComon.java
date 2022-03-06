package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.matrix.Tuple;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScattPowerMatrixComon extends XmlLinkedModelElement implements Tuple {

	@XmlAttributeProperty("ScattPow1")
	public StringProperty scattPowe1Property = new SimpleStringProperty();
	@XmlAttributeProperty("ScattPow2")
	public StringProperty scattPowe2Property = new SimpleStringProperty();

	@XmlElementValueProperty
	public StringProperty valueProperty = new SimpleStringProperty("0");

	@Override
	public String getRowIndex() {
		return scattPowe1Property.get();
	}

	@Override
	public String getColumnIndex() {
		return scattPowe2Property.get();
	}

	@Override
	public void setRowIndex(String rowIndex) {
		scattPowe1Property.set(rowIndex);
	}

	@Override
	public void setColumnIndex(String colIndex) {
		scattPowe2Property.set(colIndex);
	}

	@Override
	public String toString() {
		return "[" + getRowIndex() + "] [" + getColumnIndex() + "]: " + valueProperty.get();
	}

}
