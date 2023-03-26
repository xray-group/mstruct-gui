package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.ui.phases.ReflectionProfileRefractionCorrController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("RefractionCorr")
public class ReflectionProfileRefractionCorrElement extends ReflectionProfileModel<ReflectionProfileRefractionCorrController> {

	private static final String FXML_FILE_NAME = "reflectionProfileRefractionCorr.fxml";

	@XmlUniqueElement
	public OptionUniqueElement chi0SourceOption = new OptionUniqueElement("chi0.source", 1, "value", "crystal", "formula");
	@XmlUniqueElement
	public ParUniqueElement relDensityPar = new ParUniqueElement("relDensity");

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(relDensityPar);

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.RefractionCorr;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}
}
