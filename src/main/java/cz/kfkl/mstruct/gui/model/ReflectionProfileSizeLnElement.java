package cz.kfkl.mstruct.gui.model;

import java.util.List;

import cz.kfkl.mstruct.gui.ui.ReflectionProfileSizeLnController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

@XmlElementName("SizeLn")
public class ReflectionProfileSizeLnElement extends ReflectionProfileModel<ReflectionProfileSizeLnController> {

	private static final String FXML_FILE_NAME = "sizeLn.fxml";

	@XmlUniqueElement
	public ParUniqueElement mPar = new ParUniqueElement("M");
	@XmlUniqueElement
	public ParUniqueElement sigmaPar = new ParUniqueElement("Sigma");

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.SizeLn;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(mPar, sigmaPar);
	}

}
