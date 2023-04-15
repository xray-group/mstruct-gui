package cz.kfkl.mstruct.gui.model.phases;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;
import cz.kfkl.mstruct.gui.model.HasUniqueName;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.crystals.CrystalModel;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("PowderPatternCrystal")
public class PowderPatternCrystalsModel extends XmlLinkedModelElement
		implements FxmlFileNameProvider<PowderPatternController>, ParamContainer, HasUniqueName {
	private static final String DIFF_DATA_PREFIX = "diffData_";

	private static final String FXML_FILE_NAME = "powderPatternCrystal.fxml";

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty(DIFF_DATA_PREFIX);

	public StringProperty paramContainerName = new SimpleStringProperty();

	public StringProperty userNameProperty = new SimpleStringProperty();
	public StringProperty phaseShortName = new SimpleStringProperty();

	@XmlAttributeProperty("Crystal")
	public StringProperty crystalProperty = new SimpleStringProperty("");

	@XmlAttributeProperty("IgnoreImagScattFact")
	public IntegerProperty ignoreImagScattFactProperty = new SimpleIntegerProperty();

	@XmlUniqueElement
	public ReflectionProfileElement reflectionProfile = new ReflectionProfileElement();

	@XmlUniqueElement
	public ParUniqueElement globalBisoPar = new ParUniqueElement("globalBiso");

	@XmlUniqueElement
	public AbsorptionCorrElement absorptionCorrElement = new AbsorptionCorrElement();

	@XmlUniqueElement
	public ArbitraryTextureElement arbitraryTextureElement = new ArbitraryTextureElement();

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentWithScaleParElement powderPatternComponent = new PowderPatternComponentWithScaleParElement(
			nameProperty);

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(globalBisoPar,
			powderPatternComponent.scalePar, reflectionProfile, arbitraryTextureElement);

	public PowderPatternCrystalsModel() {
		// needed for the powderPatternComponent sibling component when creating new
		// from UI
		this.nameProperty.set(DIFF_DATA_PREFIX);
	}

	@Override
	public void bindToElement(Element wrappedElement) {
		super.bindToElement(wrappedElement);

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

		phaseShortName.bind(Bindings.when(userNameProperty.isEmpty()).then(crystalProperty).otherwise(userNameProperty));
		nameProperty.bind(Bindings.concat(DIFF_DATA_PREFIX, phaseShortName));

		CrystalModel linkedCrystal = rootModel.getCrystal(crystalProperty.get());
		if (linkedCrystal != null) {
			crystalProperty.bind(linkedCrystal.nameProperty);
		} else {
			// TODO validate: there is no crystal with the given name...
		}
		crystalProperty.addListener((obs, o, n) -> {
			rootModel.updateUsedCrystalsPredicate();
		});

		paramContainerName.bind(Bindings.concat("Crystal: ", nameProperty));
	}

	public String getNameSuffix() {
		String nameSuffix = "";

		String userName = userNameProperty.get();
		if (JvStringUtils.isBlank(userName)) {
			String crystalName = crystalProperty.get();
			if (JvStringUtils.isNotBlank(crystalName)) {
				nameSuffix = crystalName;
			}
		} else {
			nameSuffix = userName;
		}

		return nameSuffix;
	}

	@Override
	public Element getLastOwnedXmlElement() {
		return powderPatternComponent.getLastOwnedXmlElement();
	}

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return paramContainerName;
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
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

	public void updateIhklParams(PowderPatternCrystalsModel fittedPpc) {
		ArbitraryTextureElement fittedArbitraryTextrure = fittedPpc.arbitraryTextureElement;
		if (arbitraryTextureElement.canUpdateIhklParas() && fittedArbitraryTextrure.canUpdateIhklParas()) {
			arbitraryTextureElement.updateIhklParams(fittedArbitraryTextrure);
		}
	}

}
