package cz.kfkl.mstruct.gui.utils;

import javafx.util.StringConverter;

public class ObjectToStringConverter extends StringConverter<Object> {

	public static final ObjectToStringConverter INSTANCE = new ObjectToStringConverter();

	@Override
	public String toString(Object object) {
		return object == null ? null : object.toString();
	}

	@Override
	public Object fromString(String string) {
		return string;
	}

}
