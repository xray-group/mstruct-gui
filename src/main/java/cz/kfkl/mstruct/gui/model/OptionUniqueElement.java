package cz.kfkl.mstruct.gui.model;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Option")
public class OptionUniqueElement extends UniqueElement {

	@XmlAttributeProperty("Choice")
	public IntegerProperty choiceProperty = new SimpleIntegerProperty(0);

	@XmlAttributeProperty("ChoiceName")
	public StringProperty choiceNameProperty = new SimpleStringProperty("No");

	public OptionChoice[] optionChoices;

	public int selectedOptionIndex;

	// <Option Name="Display Enantiomer" Choice="0" ChoiceName="No"/>

	public OptionUniqueElement(String name, int selectedOptionIndex, OptionChoice... optionChoices) {
		super(name);
		this.optionChoices = optionChoices;
		this.selectedOptionIndex = selectedOptionIndex;

		validate();
		setOption(this.selectedOptionIndex);
	}

	public OptionUniqueElement(String name, int selectedOptionIndex, String... optionChoicesNames) {
		this(name, selectedOptionIndex, createOptionChoices(optionChoicesNames));
	}

	private static OptionChoice[] createOptionChoices(String... optionChoicesNames) {
		Validator.assertNotNull(optionChoicesNames, "Option choices must be defined.");
		OptionChoice[] newOptionChoices = new OptionChoice[optionChoicesNames.length];
		for (int i = 0; i < optionChoicesNames.length; i++) {
			String choiceName = optionChoicesNames[i];
			Validator.assertNotBlank(choiceName, "Blank is not allowed as option choice.");
			newOptionChoices[i] = new OptionChoice(choiceName);
		}
		return newOptionChoices;
	}

	protected void validate() {
		Validator.assertNotNull(optionChoices, "Option choices must be defined.");
		Validator.assertTrue(this.optionChoices.length > 0, "There must be at least one option choices defined.");
		Validator.assertTrue(this.selectedOptionIndex < this.optionChoices.length,
				"Selected option index is larger then number of available options.");
	}

	public void setOption(int optionIndex) {
		OptionChoice optionChoice = optionChoices[optionIndex];
		choiceProperty.set(optionIndex);
		choiceNameProperty.set(optionChoice.getChoiceName());
	}

	public void selectOptionByDisplayText(String displayText) {
		assertNotNull(displayText, "Display text must be specify on options to select.");
		setOption(findOptionIndex(displayText));
	}

	protected int findOptionIndex(String displayText) {
		for (int i = 0; i < optionChoices.length; i++) {
			OptionChoice optionChoice = optionChoices[i];
			if (displayText.equals(optionChoice.getDisplayText())) {
				return i;
			}
		}
		throw new PopupErrorException("No choice with text [%s] found on option [%s]", displayText, getName());
	}

	public OptionChoice[] getOptionChoices() {
		return optionChoices;
	}

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		setOption(choiceProperty.get());
	}

	public IntegerProperty getChoiceProperty() {
		return choiceProperty;
	}

	public StringProperty getChoiceNameProperty() {
		return choiceNameProperty;
	}

	public OptionChoice getSelectedChoice() {
		return optionChoices[choiceProperty.get()];
	}

}
