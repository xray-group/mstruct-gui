package cz.kfkl.mstruct.gui.model.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.xml.XmlUtils;
import javafx.collections.ListChangeListener;

public class XmlLinkedObservableListListener<T extends XmlLinkedModelElement> implements ListChangeListener<T> {

	private static final Logger LOG = LoggerFactory.getLogger(XmlLinkedObservableListListener.class);

	private XmlLinkedModelElement parrentModelElement;
	private Content previousXmlEl;

	public XmlLinkedObservableListListener(XmlLinkedModelElement parrentModelElement) {
		this.parrentModelElement = parrentModelElement;
	}

	public XmlLinkedObservableListListener(XmlLinkedModelElement parrentModelElement, Content previousXmlElement) {
		this(parrentModelElement);
		this.previousXmlEl = previousXmlElement;
	}

	@Override
	public void onChanged(Change<? extends T> c) {
		while (c.next()) {
			if (c.wasPermutated()) {
				List<Element> ordered = new ArrayList<>();
				for (int i = c.getFrom(); i < c.getTo(); ++i) {
					LOG.trace("  [{}] [{}] was permuted to [{}] [{}]", c.getList().get(i), i, c.getPermutation(i),
							c.getList().get(c.getPermutation(i)));

					ordered.add(c.getList().get(i).getXmlElement());
				}
				Element parent = parrentModelElement.getXmlElement();

				XmlUtils.sortContent(parent, ordered);
			} else if (c.wasUpdated()) {
				// TODO ? update item
			} else {
				if (c.wasRemoved()) {
					for (T removedItem : c.getRemoved()) {
						Element parent = parrentModelElement.getXmlElement();
						Element elementToRemove = removedItem.getXmlElement();

						Collection<Element> ownedSiblings = removedItem.getOwnedSiblings();
						XmlUtils.removeWithIndent(parent, elementToRemove);
						for (Element sibling : ownedSiblings) {
							XmlUtils.removeWithIndent(parent, sibling);
						}
					}
				}

				if (c.wasAdded()) {

					Content addAfterXmlEl = null;
					int fromIndex = c.getFrom();
					if (fromIndex > 0) {
						XmlLinkedModelElement previousItemModelEl = c.getList().get(fromIndex - 1);
						addAfterXmlEl = previousItemModelEl.getLastOwnedXmlElement();
					} else {
						addAfterXmlEl = previousXmlEl;
					}

					for (T addedItem : c.getAddedSubList()) {
						parrentModelElement.bindAndAddAfter(addedItem, addAfterXmlEl);
						addAfterXmlEl = addedItem.getLastOwnedXmlElement();
					}
				}
			}

		}
	}

}
