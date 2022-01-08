package cz.kfkl.mstruct.gui.xml;

public enum XmlIndentingStyle {
	INLINE(false, false), INLINE_WITH_GAP(false, true), MULTILINE(true, false), MULTILINE_WITH_GAP(true, true);

	private boolean endTagOnNewLine = false;
	private boolean newLineBefore = false;

	private XmlIndentingStyle(boolean endTagOnNewLine, boolean newLineBefore) {
		this.endTagOnNewLine = endTagOnNewLine;
		this.newLineBefore = newLineBefore;
	}

	public boolean isEndTagOnNewLine() {
		return endTagOnNewLine;
	}

	public boolean isNewLineBefore() {
		return newLineBefore;
	}

}
