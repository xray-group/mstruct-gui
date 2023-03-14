package cz.kfkl.mstruct.gui.ui;

import java.util.Map;
import java.util.Set;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RefinableParameter {

	private static final SimpleStringProperty EMPTY__STRING_PROPERTY = new SimpleStringProperty("");
	private static final Escaper ESCAPER = Escapers.builder().addEscape('|', "||").build();
	public static final String KEY_DELIM = "|";

	private String name;
	private String key;

	public BooleanProperty refinedProperty;
	public BooleanProperty limitedProperty;
	public StringProperty minProperty;
	public StringProperty maxProperty;
	public StringProperty valueProperty;

	private StringProperty fittedValueProperty;
	public BooleanProperty fittedProperty;

	private boolean isMocked = false;
	private ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty;
	private Set<String> refinedParams;

	/**
	 * Creates RefinableParameter wrapping a {@link ParUniqueElement}.
	 * 
	 * @param fittedParamsProperty
	 * @param refinedParams
	 */
	public RefinableParameter(ParUniqueElement par, String parentKey,
			ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty, Set<String> refinedParams) {
		this.name = par.getName();

		this.refinedProperty = par.refinedProperty;
		this.limitedProperty = par.limitedProperty;
		this.minProperty = par.minProperty;
		this.maxProperty = par.maxProperty;
		this.valueProperty = par.valueProperty;

		this.fittedParamsProperty = fittedParamsProperty;
		this.refinedParams = refinedParams;

		this.key = parentKey + "|" + ESCAPER.escape(this.name);
		this.fittedProperty = new SimpleBooleanProperty(refinedParams.contains(key));
	}

	/**
	 * Creates RefinableParameter representing a container for other params (i.e.
	 * tree inner node).
	 */
	public RefinableParameter(String name, String parentKey) {
		this.name = name;

		this.minProperty = EMPTY__STRING_PROPERTY;
		this.maxProperty = EMPTY__STRING_PROPERTY;
		this.valueProperty = EMPTY__STRING_PROPERTY;
		this.fittedValueProperty = EMPTY__STRING_PROPERTY;
		this.isMocked = true;

		this.key = formatKey(parentKey, this.name);
	}

	public StringProperty getFittedValueProperty() {
		Map<String, ParUniqueElement> fittedParamsMap = fittedParamsProperty == null ? null : fittedParamsProperty.get();
		if (fittedValueProperty == null && fittedParamsMap != null) {
			ParUniqueElement parUniqueElement = fittedParamsMap.get(key);
			if (parUniqueElement == null) {
				fittedValueProperty = EMPTY__STRING_PROPERTY;
			} else {
				fittedValueProperty = parUniqueElement.valueProperty;
			}
		}
		return fittedValueProperty == null ? EMPTY__STRING_PROPERTY : fittedValueProperty;
	}

	public static String formatKey(String parentKey, String name) {
		return parentKey + KEY_DELIM + ESCAPER.escape(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return valueProperty.get();
	}

	public void setValue(String value) {
		this.valueProperty.set(value);
	}

	public String getMin() {
		return minProperty.get();
	}

	public void setMin(String min) {
		this.minProperty.set(min);
	}

	public String getMax() {
		return maxProperty.get();
	}

	public void setMax(String max) {
		this.maxProperty.set(max);
	}

	public boolean isMocked() {
		return isMocked;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [ key : " + key + " ]";
	}
}
