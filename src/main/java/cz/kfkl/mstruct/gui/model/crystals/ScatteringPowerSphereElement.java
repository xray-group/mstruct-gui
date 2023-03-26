package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.ui.crystals.ScatteringPowerSphereController;
import cz.kfkl.mstruct.gui.ui.images.Images;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

@XmlElementName("ScatteringPowerSphere")
public class ScatteringPowerSphereElement extends ScatteringPowerModel<ScatteringPowerSphereController> {

	private static final String FXML_FILE_NAME = "scatteringPowerSphere.fxml";

	@XmlUniqueElement
	public ParUniqueElement radiusPar = new ParUniqueElement("Radius");

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(radiusPar, bisoPar);

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

	public String getSymbol() {
		return "<sphere>";
	}

	public String getBiso() {
		return this.bisoPar.getValue();
	}

	@Override
	public String getType() {
		return "Sphere";
	}

	@Override
	public Image getIcon() {
		return Images.get("sphere.png");
	}
}
