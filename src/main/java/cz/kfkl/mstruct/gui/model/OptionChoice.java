package cz.kfkl.mstruct.gui.model;

public class OptionChoice {
	private String choiceName;
	private String displayText;

	public OptionChoice(String choiceName, String displayText) {
		super();
		this.choiceName = choiceName;
		this.displayText = displayText;
	}

	public OptionChoice(String choiceName) {
		super();
		this.choiceName = choiceName;
	}

	public String getChoiceName() {
		return choiceName;
	}

	public String getDisplayText() {
		return displayText == null ? choiceName : displayText;
	}

	@Override
	public String toString() {
		return getDisplayText();
	}

}
