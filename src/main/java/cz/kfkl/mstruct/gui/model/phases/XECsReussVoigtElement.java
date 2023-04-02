package cz.kfkl.mstruct.gui.model.phases;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("XECsReussVoigt")
public class XECsReussVoigtElement extends XmlLinkedModelElement {

	// Seems not really used, leaving not set for newly created elements
	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlElementList
	public ObservableList<StiffnessConstantElement> stiffnessConstants = FXCollections.observableArrayList();

	@XmlUniqueElement
	public ParUniqueElement rvWeightPar = new ParUniqueElement("RV_weight");

}
