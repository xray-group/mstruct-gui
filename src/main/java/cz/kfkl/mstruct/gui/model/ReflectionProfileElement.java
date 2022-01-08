package cz.kfkl.mstruct.gui.model;

import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("ReflectionProfile")
public class ReflectionProfileElement extends XmlLinkedModelElement implements ParamContainer {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlElementList
	public ObservableList<ReflectionProfileModel> reflectionProfilesList = FXCollections.observableArrayList();

	@Override
	public String formatParamContainerName() {
		return "Reflection Profiles";
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return Collections.emptyList();
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		return reflectionProfilesList;
	}

}
