package cz.kfkl.mstruct.gui.model;

import java.util.List;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.PowderPatternController;
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
		implements FxmlFileNameProvider<PowderPatternController>, ParamContainer {
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
	public PowderPatternComponentElement powderPatternComponent = new PowderPatternComponentElement(nameProperty);

	@XmlUniqueElement
	public AbsorptionCorrElement absorptionCorrElement = new AbsorptionCorrElement();

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);

		String xmlValue = nameProperty.getValue();
		if (xmlValue != null && xmlValue.startsWith(DIFF_DATA_PREFIX)) {
			userNameProperty.set(JvStringUtils.substringAfter(xmlValue, DIFF_DATA_PREFIX));
		} else {
			// TODO: some validation error
			userNameProperty.set(xmlValue);
		}

		nameProperty.bind(Bindings.concat(DIFF_DATA_PREFIX,
				Bindings.when(userNameProperty.isEmpty()).then(crystalProperty).otherwise(userNameProperty)));

	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(globalBisoPar);
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
}
