package cz.kfkl.mstruct.gui.model.phases;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
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
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		rootModel.registerChildren(this.getChildren());
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return new SimpleStringProperty("Reflection Profiles");
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return reflectionProfilesList;
	}

}
