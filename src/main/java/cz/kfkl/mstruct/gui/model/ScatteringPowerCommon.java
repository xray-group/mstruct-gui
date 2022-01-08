package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlMappedSubclasses({ ScatteringPowerAtomElement.class, ScatteringPowerSphereElement.class })
public abstract class ScatteringPowerCommon extends XmlLinkedModelElement implements ParamContainer {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlUniqueElement("RGBColour")
	public SingleValueUniqueElement colourElement = new SingleValueUniqueElement("1 0 0");

	@Override
	public String formatParamContainerName() {
		return "Scattering Power " + getType() + ": " + getName();
	}

	public String getName() {
		return nameProperty.get();
	}

	public void setName(String name) {
		this.nameProperty.set(name);
	}

	public String getColour() {
		return colourElement.valueProperty.get();
	}

	public void setColour(String colour) {
		this.colourElement.valueProperty.set(colour);
	}

	public abstract String getType();

}
