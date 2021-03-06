package cz.kfkl.mstruct.gui.model.phases;

import java.util.List;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;
import cz.kfkl.mstruct.gui.model.HasUniqueName;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternController;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("PowderPatternCrystal")
public class PowderPatternCrystalsModel extends XmlLinkedModelElement
		implements FxmlFileNameProvider<PowderPatternController>, ParamContainer, HasUniqueName {
	private static final String DIFF_DATA_PREFIX = "diffData_";

	private static final String FXML_FILE_NAME = "powderPatternCrystal.fxml";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	public StringProperty userNameProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Crystal")
	public StringProperty crystalProperty = new SimpleStringProperty();

	@XmlAttributeProperty("IgnoreImagScattFact")
	public IntegerProperty ignoreImagScattFactProperty = new SimpleIntegerProperty();

	@XmlUniqueElement
	public ReflectionProfileElement reflectionProfile = new ReflectionProfileElement();

	@XmlUniqueElement
	public ParUniqueElement globalBisoPar = new ParUniqueElement("globalBiso");

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentWithScaleParElement powderPatternComponent = new PowderPatternComponentWithScaleParElement(
			nameProperty);

	@XmlUniqueElement
	public AbsorptionCorrElement absorptionCorrElement = new AbsorptionCorrElement();

	public PowderPatternCrystalsModel() {
		// needed for the powderPatternComponent sibling component when creating new
		// from UI
		this.nameProperty.set(DIFF_DATA_PREFIX);
	}

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);

		String xmlValue = nameProperty.getValue();
		if (xmlValue != null && xmlValue.startsWith(DIFF_DATA_PREFIX)) {
			String substringAfter = JvStringUtils.substringAfter(xmlValue, DIFF_DATA_PREFIX);
			if (!substringAfter.equals(crystalProperty.getValue())) {
				userNameProperty.set(substringAfter);
			}
		} else {
			// TODO: some validation error
			userNameProperty.set(xmlValue);
		}

		nameProperty.bind(Bindings.concat(DIFF_DATA_PREFIX,
				Bindings.when(userNameProperty.isEmpty()).then(crystalProperty).otherwise(userNameProperty)));

	}

	public String getNameSuffix() {
		return JvStringUtils.isEmpty(userNameProperty.get()) ? crystalProperty.get() : userNameProperty.get();
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(globalBisoPar, powderPatternComponent.scalePar);
	}

	@Override
	public String formatParamContainerName() {
		return "Crystal: " + nameProperty.get();
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		return List.of(reflectionProfile);
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public String toString() {
		return nameProperty.get() + " (" + crystalProperty.get() + ")";
	}

	@Override
	public String getName() {
		return nameProperty.getValue();
	}

	public void setName(String name) {
		nameProperty.setValue(name);
	}

}
