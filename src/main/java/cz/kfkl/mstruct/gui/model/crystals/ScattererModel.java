package cz.kfkl.mstruct.gui.model.crystals;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlMappedSubclasses({ AtomElement.class, MoleculeElement.class })
public abstract class ScattererModel<C extends BaseController<?, ?>> extends XmlLinkedModelElement
		implements ParamContainer, FxmlFileNameProvider<C> {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	public StringProperty paramContainerName = new SimpleStringProperty();

	@XmlUniqueElement
	public ParUniqueElement xPar = new ParUniqueElement("x");

	@XmlUniqueElement
	public ParUniqueElement yPar = new ParUniqueElement("y");

	@XmlUniqueElement
	public ParUniqueElement zPar = new ParUniqueElement("z");

	@XmlUniqueElement
	public ParUniqueElement occupPar = new ParUniqueElement("Occup");

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(xPar, yPar, zPar, occupPar);

	@Override
	public void bindToElement(Element wrappedElement) {
		super.bindToElement(wrappedElement);
		paramContainerName.bind(Bindings.format("%s: %s", getType(), nameProperty));
	}

	public abstract String getType();

	public String getName() {
		return nameProperty.get();
	}

	public void setName(String name) {
		this.nameProperty.set(name);
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return paramContainerName;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

	public String getX() {
		return xPar.valueProperty.get();
	}

	public String getY() {
		return yPar.valueProperty.get();
	}

	public String getZ() {
		return zPar.valueProperty.get();
	}

	public String getOccup() {
		return occupPar.valueProperty.get();
	}

}
