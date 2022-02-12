package cz.kfkl.mstruct.gui.model;

import java.util.ArrayList;
import java.util.List;

import cz.kfkl.mstruct.gui.ui.ScattererMoleculeController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Molecule")
public class MoleculeElement extends ScattererModel<ScattererMoleculeController> {

	private static final String FXML_FILE_NAME = "scattererMolecule.fxml";

	@XmlAttributeProperty("MDMoveFreq")
	public StringProperty mDMoveFreqProperty = new SimpleStringProperty("0");

	@XmlAttributeProperty("MDMoveEnergy")
	public StringProperty mDMoveEnergyProperty = new SimpleStringProperty("40");

	@XmlAttributeProperty("LogLikelihoodScale")
	public StringProperty logLikelihoodScaleProperty = new SimpleStringProperty("1");

	@XmlUniqueElement
	public OptionUniqueElement flexibilityModelOption = new OptionUniqueElement("Flexibility Model", 0,
			"Automatic from Restraints, relaxed - RECOMMENDED", "Rigid Body", "Automatic from Restraints, strict");

	@XmlUniqueElement
	public OptionUniqueElement enableFlippingOption = new OptionUniqueElement("Enable Flipping", 0, "Yes", "No");

	@XmlUniqueElement
	public OptionUniqueElement autoOptimizeStartingConformationOption = new OptionUniqueElement(
			"Auto Optimize Starting Conformation", 0, "Yes", "No");

	@XmlUniqueElement
	public OptionUniqueElement optimizeOrientationOption = new OptionUniqueElement("Optimize Orientation", 0, "Yes", "No");

	@XmlUniqueElement
	public OptionUniqueElement rotationCenterOption = new OptionUniqueElement("Rotation Center", 0,
			"Geometrical center (recommended)", "User-chosen Atom");

	@XmlUniqueElement
	public CenterAtomElement centerAtomElement = new CenterAtomElement();

	@XmlElementList
	public List<MoleculeAtomElement> moleculeAtoms = new ArrayList<>();

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		List<ParUniqueElement> list = new ArrayList<>(super.getParams());
		return list;
	}

	@Override
	public String getType() {
		return "Molecule";
	}

	@Override
	public String formatParamContainerName() {
		return "Molecule: " + getName();
	}

}
