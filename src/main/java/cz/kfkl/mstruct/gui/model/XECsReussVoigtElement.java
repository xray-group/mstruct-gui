package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("XECsReussVoigt")
public class XECsReussVoigtElement extends XmlLinkedModelElement {

	@XmlElementList
	public ObservableList<StiffnessConstantElement> stiffnessConstants = FXCollections.observableArrayList();

	@XmlUniqueElement
	public ParUniqueElement rvWeightPar = new ParUniqueElement("RV_weight");

}
