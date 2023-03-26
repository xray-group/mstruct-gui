package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.ui.crystals.ScattererAtomController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Atom")
public class AtomElement extends ScattererModel<ScattererAtomController> {

	private static final String FXML_FILE_NAME = "scattererAtom.fxml";

	@XmlAttributeProperty("ScattPow")
	public StringProperty scattPowProperty = new SimpleStringProperty();

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public String getType() {
		return "Atom";
	}

	public String getScattPow() {
		return scattPowProperty.get();
	}

	public void setScattPow(String scattPow) {
		this.scattPowProperty.set(scattPow);
	}

}
