package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.ui.phases.ReflectionProfilePVoigtAController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("pVoigtA")
public class ReflectionProfilePVoigtAElement extends ReflectionProfileModel<ReflectionProfilePVoigtAController> {

	private static final String FXML_FILE_NAME = "reflectionProfilePVoigtA.fxml";

	@XmlUniqueElement
	public ParUniqueElement wPar = new ParUniqueElement("W");
	@XmlUniqueElement
	public ParUniqueElement vPar = new ParUniqueElement("V");
	@XmlUniqueElement
	public ParUniqueElement uPar = new ParUniqueElement("U");

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

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(wPar, vPar, uPar, eta0Par, eta1Par,
			asym0Par, asym1Par, asym2Par);

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.PVoightA;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

}
