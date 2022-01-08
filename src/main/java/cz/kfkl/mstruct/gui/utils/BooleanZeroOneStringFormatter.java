package cz.kfkl.mstruct.gui.utils;

import javafx.util.StringConverter;

public class BooleanZeroOneStringFormatter extends StringConverter<Boolean> {

	private static final String TRUE_STRING = "1";
	private static final String FALSE_STRING = "0";

	@Override
	public String toString(Boolean object) {
		return object == null ? null : (object.booleanValue() ? TRUE_STRING : FALSE_STRING);
	}

	@Override
	public Boolean fromString(String string) {
		return string == null ? null : FALSE_STRING.equals(string.trim());
	}

}
