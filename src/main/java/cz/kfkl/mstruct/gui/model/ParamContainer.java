package cz.kfkl.mstruct.gui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public interface ParamContainer extends ParamTreeNode {
	static final SimpleStringProperty EMPTY_STRING_PROPERTY = new SimpleStringProperty("");
	static final SimpleBooleanProperty BOOLEAN_FALSE_PROPERTY = new SimpleBooleanProperty(false);

	@Override
	default public boolean isParameter() {
		return false;
	}

	@Override
	default public BooleanProperty getRefinedProperty() {
		return null;
	}

	@Override
	default public BooleanProperty getLimitedProperty() {
		return null;
	}

	@Override
	default public StringProperty getMinProperty() {
		return EMPTY_STRING_PROPERTY;
	}

	@Override
	default public StringProperty getMaxProperty() {
		return EMPTY_STRING_PROPERTY;
	}

	@Override
	default public StringProperty getValueProperty() {
		return EMPTY_STRING_PROPERTY;
	}

	@Override
	default public boolean isIhklParameter() {
		return false;
	}

	@Override
	default public BooleanProperty getFittedProperty() {
		return null;
	}

	@Override
	default public StringProperty getFittedValueProperty() {
		return EMPTY_STRING_PROPERTY;
	}
}
