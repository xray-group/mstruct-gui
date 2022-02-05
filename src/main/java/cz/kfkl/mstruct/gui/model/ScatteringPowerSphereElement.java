package cz.kfkl.mstruct.gui.model;

import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.ui.ScatteringPowerSphereController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

@XmlElementName("ScatteringPowerSphere")
public class ScatteringPowerSphereElement extends ScatteringPowerModel<ScatteringPowerSphereController> {

	private static final String FXML_FILE_NAME = "scatteringPowerSphere.fxml";

	@XmlUniqueElement
	public ParUniqueElement radiusPar = new ParUniqueElement("Radius");

	@XmlUniqueElement
	public ParUniqueElement bisoPar = new ParUniqueElement("Biso");

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(radiusPar, bisoPar);
	}

	@Override
	public List<ParamContainer> getInnerContainers() {
		return Collections.emptyList();
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
}
