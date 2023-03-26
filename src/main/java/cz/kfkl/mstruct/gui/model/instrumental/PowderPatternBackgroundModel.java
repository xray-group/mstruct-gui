package cz.kfkl.mstruct.gui.model.instrumental;

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

@XmlMappedSubclasses({ PowderPatternBackgroundInvX.class, PowderPatternBackgroundChebyshev.class,
		PowderPatternBackgroundInterpolated.class })
abstract public class PowderPatternBackgroundModel<C extends BaseController<?, ?>> extends XmlLinkedModelElement
		implements FxmlFileNameProvider<C>, ParamContainer, HasUniqueName {

	private static final String DEFAULT_BACKGROUND_PREFIX = "bkgData_";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty(DEFAULT_BACKGROUND_PREFIX);

	public StringProperty paramContainerName = new SimpleStringProperty();

	public PowderPatternBackgroundModel() {
		super();
		nameProperty.set(DEFAULT_BACKGROUND_PREFIX + getType().getTypeName());

		paramContainerName.bind(Bindings.concat("Background: ", nameProperty));
	}

	public abstract PowderPatternBackgroundType getType();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return paramContainerName;
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
