package cz.kfkl.mstruct.gui.model.utils;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.utils.ToStringFunction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ElementContentChangeListener<T> implements ChangeListener<T> {

	private Element element;
	private ToStringFunction<T> toStringConvertor;

	public ElementContentChangeListener(Element element) {
		this.element = element;
	}

	public ElementContentChangeListener(Element element, ToStringFunction<T> toStringConvertor) {
		this(element);
		this.toStringConvertor = toStringConvertor;
	}

	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

		if (toStringConvertor != null) {
			setXmlValue(toStringConvertor.toString(newValue));
		} else if (newValue != null) {
			setXmlValue(newValue.toString());
		}

	}

	protected void setXmlValue(String strValue) {
		element.setText(strValue);
	}
}
