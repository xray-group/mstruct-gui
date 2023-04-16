package cz.kfkl.mstruct.gui.model.utils;

import org.jdom2.Element;

import javafx.util.StringConverter;

public class XmlElementContentUpdater<T> extends XmlValueUpdater<T> {

	private Element element;

	public XmlElementContentUpdater(Element element, StringConverter<T> converter) {
		super(converter);
		this.element = element;
	}

	@Override
	protected void setXmlValue(String strValue) {
		element.setText(strValue);
	}

	@Override
	protected String getXmlValue() {
		return element.getContentSize() == 0 ? null : element.getTextTrim();
	}

}
