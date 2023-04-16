package cz.kfkl.mstruct.gui.model.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;

public abstract class XmlValueUpdater<T> implements ChangeListener<T> {

	private static final Logger LOG = LoggerFactory.getLogger(XmlValueUpdater.class);

	private StringConverter<T> stringConvertor;

	public XmlValueUpdater(StringConverter<T> stringConvertor) {
		super();
		this.stringConvertor = stringConvertor;
	}

	void bind(Property<T> ssp) {
		String xmlValue = getXmlValue();
		initValue(ssp, xmlValue);
		ssp.addListener(this);
	}

	public void initValue(Property<T> ssp, String xmlValue) {
		if (xmlValue == null) {
			T defaultValue = ssp.getValue();
			if (defaultValue != null) {
				LOG.debug("Using default value [{}] for property [{}]", defaultValue, ssp);
				convertAndSetValue(defaultValue);
			}
		} else {
			ssp.setValue(stringConvertor.fromString(xmlValue));
		}

	}

	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		convertAndSetValue(newValue);
	}

	private void convertAndSetValue(T newValue) {
		setXmlValue(stringConvertor.toString(newValue));
	}

	protected abstract void setXmlValue(String strValue);

	protected abstract String getXmlValue();
}
