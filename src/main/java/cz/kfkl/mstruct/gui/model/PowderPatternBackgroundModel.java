package cz.kfkl.mstruct.gui.model;

import org.jdom2.Element;

import com.google.common.base.Strings;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlMappedSubclasses({ PowderPatternBackgroundInvX.class, PowderPatternBackgroundChebyshev.class })
abstract public class PowderPatternBackgroundModel<C extends BaseController<?, ?>> extends XmlLinkedModelElement
		implements FxmlFileNameProvider<C>, ParamContainer {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty("bkgData_");

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentElement powderPatternComponent = new PowderPatternComponentElement(nameProperty);

	@XmlUniqueElement
	public OptionUniqueElement xFuncTypeOption = new OptionUniqueElement("X-func.type", 1, "X", "sin(Th)");

	public PowderPatternBackgroundModel() {
		super();
		nameProperty.set("bkgComp_" + getType().getTypeName());
	}

	public abstract PowderPatternBackgroundType getType();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public Element getLastOwnedXmlElement() {
		return powderPatternComponent.getLastOwnedXmlElement();
	}

	@Override
	public String formatParamContainerName() {
		return "Background: " + getName();
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

	@Override
	public String toString() {
		return Strings.isNullOrEmpty(nameProperty.get()) ? "[N/A]" : nameProperty.get();
	}

}
