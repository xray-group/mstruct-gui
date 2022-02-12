package cz.kfkl.mstruct.gui.model;

import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.ui.ScatteringPowerAtomController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("ScatteringPowerAtom")
public class ScatteringPowerAtomElement extends ScatteringPowerModel<ScatteringPowerAtomController> {
	private static final String FXML_FILE_NAME = "scatteringPowerAtom.fxml";

	@XmlAttributeProperty("Symbol")
	public StringProperty symbolProperty = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement bisoPar = new ParUniqueElement("Biso");

	@XmlUniqueElement
	public ParUniqueElement mlErrorPar = new ParUniqueElement("ML Error");

	@XmlUniqueElement
	public ParUniqueElement mlNbGhostPar = new ParUniqueElement("ML-NbGhost");

	@XmlUniqueElement
	public ParUniqueElement formalChargePar = new ParUniqueElement("Formal Charge");

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(bisoPar, mlErrorPar, mlNbGhostPar, formalChargePar);
	}

	@Override
	public List<ParamContainer> getInnerContainers() {
		return Collections.emptyList();
	}

	public String getSymbol() {
		return symbolProperty.get();
	}

	public void setSymbol(String symbol) {
		this.symbolProperty.set(symbol);
	}

	public String getBiso() {
		return this.bisoPar.getValue();
	}

	@Override
	public String getType() {
		return "Atom";
	}

}
