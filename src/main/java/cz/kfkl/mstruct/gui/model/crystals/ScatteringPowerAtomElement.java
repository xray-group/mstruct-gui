package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.ui.crystals.ScatteringPowerAtomController;
import cz.kfkl.mstruct.gui.ui.images.Images;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

@XmlElementName("ScatteringPowerAtom")
public class ScatteringPowerAtomElement extends ScatteringPowerModel<ScatteringPowerAtomController> {
	private static final String FXML_FILE_NAME = "scatteringPowerAtom.fxml";

	@XmlAttributeProperty("Symbol")
	public StringProperty symbolProperty = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement mlErrorPar = new ParUniqueElement("ML Error");

	@XmlUniqueElement
	public ParUniqueElement mlNbGhostPar = new ParUniqueElement("ML-NbGhost");

	@XmlUniqueElement
	public ParUniqueElement formalChargePar = new ParUniqueElement("Formal Charge");

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(bisoPar, mlErrorPar, mlNbGhostPar,
			formalChargePar);

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
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

	@Override
	public Image getIcon() {
		return Images.get("atom.png");
	}

}
