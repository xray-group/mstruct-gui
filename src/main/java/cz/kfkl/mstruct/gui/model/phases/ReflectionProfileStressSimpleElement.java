package cz.kfkl.mstruct.gui.model.phases;

import java.util.List;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.ui.phases.ReflectionProfileStressSimpleController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

@XmlElementName("StressSimple")
public class ReflectionProfileStressSimpleElement extends ReflectionProfileModel<ReflectionProfileStressSimpleController> {

	private static final String FXML_FILE_NAME = "reflectionProfileStressSimple.fxml";

	@XmlUniqueElement
	public ParUniqueElement stressPar = new ParUniqueElement("Stress");

	@XmlUniqueElement
	public XECsReussVoigtElement xECsReussVoigt = new XECsReussVoigtElement();

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.StressSimple;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(stressPar, xECsReussVoigt.rvWeightPar);
	}

}
