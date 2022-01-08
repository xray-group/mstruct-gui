package cz.kfkl.mstruct.gui.model;

import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Atom")
public class AtomElement extends XmlLinkedModelElement implements ParamContainer {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlAttributeProperty("ScattPow")
	public StringProperty scattPowPoperty = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement xPar = new ParUniqueElement("x");

	@XmlUniqueElement
	public ParUniqueElement yPar = new ParUniqueElement("y");

	@XmlUniqueElement
	public ParUniqueElement zPar = new ParUniqueElement("z");

	@XmlUniqueElement
	public ParUniqueElement occupPar = new ParUniqueElement("Occup");

	@Override
	public String formatParamContainerName() {
		return "Atom: " + getName();
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(xPar, yPar, zPar, occupPar);
	}

	@Override
	public List<ParamContainer> getInnerContainers() {
		return Collections.emptyList();
	}

	public String getName() {
		return nameProperty.get();
	}

	public void setName(String name) {
		this.nameProperty.set(name);
	}

	public String getScattPow() {
		return scattPowPoperty.get();
	}

	public void setScattPow(String scattPow) {
		this.scattPowPoperty.set(scattPow);
	}

	public String getX() {
		return xPar.valueProperty.get();
	}

	public String getY() {
		return yPar.valueProperty.get();
	}

	public String getZ() {
		return zPar.valueProperty.get();
	}

	public String getOccup() {
		return occupPar.valueProperty.get();
	}

}
