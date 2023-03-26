package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.ui.phases.ReflectionProfileStressSimpleController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("StressSimple")
public class ReflectionProfileStressSimpleElement extends ReflectionProfileModel<ReflectionProfileStressSimpleController> {

	private static final String FXML_FILE_NAME = "reflectionProfileStressSimple.fxml";

	@XmlUniqueElement
	public ParUniqueElement stressPar = new ParUniqueElement("Stress");

	@XmlUniqueElement
	public XECsReussVoigtElement xECsReussVoigt = new XECsReussVoigtElement();

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(stressPar, xECsReussVoigt.rvWeightPar);

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.StressSimple;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}
}
