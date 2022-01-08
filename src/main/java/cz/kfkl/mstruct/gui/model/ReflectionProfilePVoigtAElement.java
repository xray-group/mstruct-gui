package cz.kfkl.mstruct.gui.model;

import java.util.List;

import cz.kfkl.mstruct.gui.ui.ReflectionProfilePVoigtAController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

@XmlElementName("pVoigtA")
public class ReflectionProfilePVoigtAElement extends ReflectionProfileModel<ReflectionProfilePVoigtAController> {

	private static final String FXML_FILE_NAME = "pVoigtA.fxml";

	@XmlUniqueElement
	public ParUniqueElement uPar = new ParUniqueElement("U");
	@XmlUniqueElement
	public ParUniqueElement vPar = new ParUniqueElement("V");
	@XmlUniqueElement
	public ParUniqueElement wPar = new ParUniqueElement("W");
	@XmlUniqueElement
	public ParUniqueElement eta0Par = new ParUniqueElement("Eta0");
	@XmlUniqueElement
	public ParUniqueElement eta1Par = new ParUniqueElement("Eta1");
	@XmlUniqueElement
	public ParUniqueElement asym0Par = new ParUniqueElement("Asym0");
	@XmlUniqueElement
	public ParUniqueElement asym1Par = new ParUniqueElement("Asym1");
	@XmlUniqueElement
	public ParUniqueElement asym2Par = new ParUniqueElement("Asym2");

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.PVoightA;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(uPar, vPar, wPar, eta0Par, eta1Par, asym0Par, asym1Par, asym2Par);
	}

}
