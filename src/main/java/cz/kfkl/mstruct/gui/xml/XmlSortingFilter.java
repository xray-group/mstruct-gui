package cz.kfkl.mstruct.gui.xml;

import java.util.Comparator;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.filter.AbstractFilter;

public class XmlSortingFilter extends AbstractFilter<Content> implements Comparator<Content> {
	private static final long serialVersionUID = 3643139343089385921L;

	private Map<Content, Integer> elementsToSort;

	public XmlSortingFilter(Map<Content, Integer> elementsToSort) {
		super();
		this.elementsToSort = elementsToSort;
	}

	@Override
	public Content filter(Object content) {
		return elementsToSort.containsKey(content) ? (Content) content : null;
	}

	@Override
	public int compare(Content o1, Content o2) {
		Integer order1 = elementsToSort.get(o1);
		Integer order2 = elementsToSort.get(o2);
		return order1 == null || order2 == null ? 0 : order1.intValue() - order2.intValue();
	}

}
