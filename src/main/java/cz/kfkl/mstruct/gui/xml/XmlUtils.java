package cz.kfkl.mstruct.gui.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Element;
import org.jdom2.Text;

import com.google.common.base.Strings;

public class XmlUtils {

	public static void addNewElementAfter(Element parent, Element newElement, Element addAfterElement, int level,
			XmlIndentingStyle indentStyle) {
		int indexOf = addAfterElement == null ? -1 : parent.indexOf(addAfterElement);

		List<Content> newElementIndented = new ArrayList<>();
		if (indentStyle != null) {
			if (indentStyle.isNewLineBefore() && indexOf >= 0) {
				newElementIndented.add(createIndentText(0));
			}
			if (indentStyle.isEndTagOnNewLine()) {
				newElement.addContent(createIndentText(level));
			}
		}
		newElementIndented.add(createIndentText(level));
		newElementIndented.add(newElement);

		addContentToIndex(parent, newElementIndented, indexOf);
	}

	public static void addContentAfter(Element parent, List<Content> content, Element addAfterElement, int level,
			XmlIndentingStyle indentStyle) {
		int indexOf = addAfterElement == null ? -1 : parent.indexOf(addAfterElement);

		addContentToIndex(parent, content, indexOf);
	}

	private static void addContentToIndex(Element parent, List<Content> content, int indexOf) {
		int contentSize = parent.getContentSize();
		if (indexOf >= 0 && indexOf + 1 < contentSize) {
			parent.addContent(indexOf + 1, content);
		} else {
			parent.addContent(0, content);
		}
	}

	private static Text createIndentText(int level) {
		Text indent = new Text("\n" + Strings.repeat("  ", level));
		return indent;
	}

	public static void removeWithIndent(Element parent, Element elementToRemove) {
		List<Content> toRemove = elementWithPreceedingBallast(parent, elementToRemove);
		for (Content content : toRemove) {
			parent.removeContent(content);
		}
	}

	public static List<Content> elementWithPreceedingBallast(Element parent, Content child) {
		LinkedList<Content> res = new LinkedList<>();
		int index = parent.indexOf(child);
		boolean isBallast = true;
		while (index >= 0 && isBallast) {
			res.addFirst(child);
			index--;
			child = index >= 0 ? parent.getContent(index) : null;
			isBallast = child == null ? false : isBallast(child);
		}

		return res;
	}

	/**
	 * Currently ballast is comment or empty text.
	 */
	private static boolean isBallast(Content child) {
		return child.getCType() == CType.Comment
				|| (child.getCType() == CType.Text && ((Text) child).getTextNormalize().isEmpty());
	}

	public static void sortContent(Element parent, List<Element> desiredOrder) {

		Map<Content, Integer> elementsToSort = new LinkedHashMap<>();
		int order = 0;
		for (Element el : desiredOrder) {
			List<Content> toOrder = elementWithPreceedingBallast(parent, el);
			for (Content to : toOrder) {
				elementsToSort.put(to, order);
			}
			order++;
		}

		XmlSortingFilter sortingFilter = new XmlSortingFilter(elementsToSort);
		parent.sortContent(sortingFilter, sortingFilter);
	}

	public static <T extends Content> void detachContent(List<T> contentList) {
		for (T content : contentList) {
			content.detach();
		}
	}

}
