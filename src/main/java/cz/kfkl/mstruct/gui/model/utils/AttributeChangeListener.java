package cz.kfkl.mstruct.gui.model.utils;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.utils.ToStringFunction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AttributeChangeListener<T> implements ChangeListener<T> {

	private Element element;
	private String attributeName;
	private ToStringFunction<T> toStringConvertor;

	public AttributeChangeListener(Element element, String attributeName) {
		this.element = element;
		this.attributeName = attributeName;
	}

	public AttributeChangeListener(Element element, String attributeName, ToStringFunction<T> toStringConvertor) {
		this(element, attributeName);
		this.toStringConvertor = toStringConvertor;
	}

	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

		if (toStringConvertor != null) {
			element.setAttribute(attributeName, toStringConvertor.toString(newValue));
		} else if (newValue != null) {
			element.setAttribute(attributeName, newValue.toString());
		}

	}
}
