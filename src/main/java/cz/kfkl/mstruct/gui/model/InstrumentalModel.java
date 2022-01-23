package cz.kfkl.mstruct.gui.model;

import com.google.common.base.Strings;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlMappedSubclasses({ PowderPatternElement.class })
abstract public class InstrumentalModel<C extends BaseController<?>> extends XmlLinkedModelElement
		implements FxmlFileNameProvider<C>, ParamContainer {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty("test");

	public String getName() {
		return nameProperty.get();
	}

	public void setName(String name) {
		this.nameProperty.set(name);

	}

	public StringProperty getNameProperty() {
		return nameProperty;
	}

	@Override
	public String toString() {
		return Strings.isNullOrEmpty(nameProperty.get()) ? "[N/A]" : nameProperty.get();
	}

}
