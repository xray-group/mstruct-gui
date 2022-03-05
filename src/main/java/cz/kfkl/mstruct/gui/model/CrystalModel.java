package cz.kfkl.mstruct.gui.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.CrystalController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("Crystal")
public class CrystalModel extends XmlLinkedModelElement implements FxmlFileNameProvider<CrystalController>, ParamContainer {

	private static final String FXML_FILE_NAME = "crystal.fxml";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty("test");

	@XmlAttributeProperty("SpaceGroup")
	public StringProperty spaceGroupProperty = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement aPar = new ParUniqueElement("a");
	@XmlUniqueElement
	public ParUniqueElement bPar = new ParUniqueElement("b");
	@XmlUniqueElement
	public ParUniqueElement cPar = new ParUniqueElement("c");

	@XmlUniqueElement
	public ParUniqueElement alphaPar = new ParUniqueElement("alpha");
	@XmlUniqueElement
	public ParUniqueElement betaPar = new ParUniqueElement("beta");
	@XmlUniqueElement
	public ParUniqueElement gammaPar = new ParUniqueElement("gamma");

	@XmlUniqueElement
	public OptionUniqueElement constrainLatticeOption = new OptionUniqueElement("Constrain Lattice to SpaceGroup Symmetry", 0,
			new OptionChoice("Yes (Default)", "Yes"), new OptionChoice("No (Allow Crystallographic Pseudo-Symmetry)", "No"));

	@XmlUniqueElement
	public OptionUniqueElement useOccupancyCorrectionOption = new OptionUniqueElement("Use Dynamical Occupancy Correction", 0,
			"No", "Yes");

	@XmlUniqueElement
	public OptionUniqueElement displayEnantiomerOption = new OptionUniqueElement("Display Enantiomer", 0, "No", "Yes");

	@XmlElementList
	public List<ScatteringPowerModel> scatterintPowers = new ArrayList<>();

	@XmlElementList
	public List<ScattererModel> scatterers = new ArrayList<>();

	@XmlElementList
	public ObservableList<AntiBumpDistanceElement> antiBumpDistances = FXCollections.observableArrayList();
	@XmlUniqueElement("AntiBumpScale")
	public SingleValueUniqueElement antiBumpScale = new SingleValueUniqueElement("1");

	@XmlElementList
	public ObservableList<BondValenceRoElement> bondValences = FXCollections.observableArrayList();
	@XmlUniqueElement("BondValenceCostScale")
	public SingleValueUniqueElement bondValenceCostScale = new SingleValueUniqueElement("1");

	public CrystalModel() {
	}

	@Override
	public String formatParamContainerName() {
		return "Crystal: " + getName();
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(aPar, bPar, cPar, alphaPar, betaPar, gammaPar);
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		List<ParamContainer> list = new ArrayList<>();
		list.addAll(scatterintPowers);
		list.addAll(scatterers);
		return list;
	}

	public String getName() {
		return nameProperty.get();
	}

	public void setName(String name) {
		this.nameProperty.set(name);

	}

	public StringProperty getNameProperty() {
		return nameProperty;
	}

	public StringProperty getSpaceGroupProperty() {
		return spaceGroupProperty;
	}

	@Override
	public String toString() {
		return Strings.isNullOrEmpty(nameProperty.get()) ? "[N/A]" : nameProperty.get();
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}
}
