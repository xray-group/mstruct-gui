package cz.kfkl.mstruct.gui.model.phases;

import org.jdom2.Element;

import com.google.common.base.Strings;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;
import cz.kfkl.mstruct.gui.model.HasUniqueName;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlMappedSubclasses({ ReflectionProfilePVoigtAElement.class, ReflectionProfileSizeLnElement.class,
		ReflectionProfileRefractionCorrElement.class, ReflectionProfileStressSimpleElement.class })
abstract public class ReflectionProfileModel<C extends BaseController<?, ?>> extends XmlLinkedModelElement
		implements FxmlFileNameProvider<C>, ParamContainer, HasUniqueName {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	public StringProperty paramContainerName = new SimpleStringProperty();

	public abstract ReflectionProfileType getType();

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		paramContainerName.bind(Bindings.format("%s (%s)", nameProperty, getType()));
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return paramContainerName;
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
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
