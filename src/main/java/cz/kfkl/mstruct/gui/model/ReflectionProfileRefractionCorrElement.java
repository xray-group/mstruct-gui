package cz.kfkl.mstruct.gui.model;

import java.util.List;

import cz.kfkl.mstruct.gui.ui.ReflectionProfileRefractionCorrController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

@XmlElementName("RefractionCorr")
public class ReflectionProfileRefractionCorrElement extends ReflectionProfileModel<ReflectionProfileRefractionCorrController> {

	private static final String FXML_FILE_NAME = "refractionCorr.fxml";

	@XmlUniqueElement
	public OptionUniqueElement chi0SourceOption = new OptionUniqueElement("chi0.source", 1, "value", "crystal", "formula");
	@XmlUniqueElement
	public ParUniqueElement relDensityPar = new ParUniqueElement("relDensity");

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public ReflectionProfileType getType() {
		return ReflectionProfileType.RefractionCorr;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(relDensityPar);
	}

}
