package cz.kfkl.mstruct.gui.model.utils;

import org.jdom2.Element;

public class XmlElementContentUpdater<T> extends XmlValueUpdater<T> {

	private Element element;

	public XmlElementContentUpdater(Element element) {
		super();
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
