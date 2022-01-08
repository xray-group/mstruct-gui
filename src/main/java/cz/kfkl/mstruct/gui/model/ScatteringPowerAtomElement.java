package cz.kfkl.mstruct.gui.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("ScatteringPowerAtom")
public class ScatteringPowerAtomElement extends ScatteringPowerCommon {

	@XmlAttributeProperty("Symbol")
	public StringProperty symbolPoperty = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement bisoPar = new ParUniqueElement("Biso");

	@XmlUniqueElement
	public ParUniqueElement mlErrorPar = new ParUniqueElement("ML Error");

	@XmlUniqueElement
	public ParUniqueElement mlNbGhostPar = new ParUniqueElement("ML-NbGhost");

	@XmlUniqueElement
	public ParUniqueElement formalChargePar = new ParUniqueElement("Formal Charge");

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(bisoPar, mlErrorPar, mlNbGhostPar, formalChargePar);
	}

	@Override
	public List<ParamContainer> getInnerContainers() {
		return Collections.emptyList();
	}

	public String getSymbol() {
		return symbolPoperty.get();
	}

	public void setSymbol(String symbol) {
		this.symbolPoperty.set(symbol);
	}

	@Override
	public String getType() {
		return "Atom";
	}

}
