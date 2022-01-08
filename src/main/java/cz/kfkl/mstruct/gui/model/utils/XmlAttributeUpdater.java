package cz.kfkl.mstruct.gui.model.utils;

import org.jdom2.Element;

public class XmlAttributeUpdater<T> extends XmlValueUpdater<T> {

	private Element element;
	private String attributeName;

	public XmlAttributeUpdater(Element element, String attributeName) {
		super();
		this.element = element;
		this.attributeName = attributeName;
	}

	@Override
	protected void setXmlValue(String strValue) {
		element.setAttribute(attributeName, strValue);
	}

	@Override
	protected String getXmlValue() {
		return element.getAttributeValue(attributeName);
	}

}
