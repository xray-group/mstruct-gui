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

	public static final String NEW_LINE = "\n";
	private static final String INDENT = "  ";

	public static void addNewElementAfter(Element parent, Element newElement, Content addAfterContent, int level,
			XmlIndentingStyle indentStyle) {
		int indexOf = addAfterContent == null ? -1 : parent.indexOf(addAfterContent);

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

		addContentAfterIndex(parent, newElementIndented, indexOf);
	}

	public static void addContentAfter(Element parent, List<Content> content, Content addAfterElement) {
		int indexOf = addAfterElement == null ? -1 : parent.indexOf(addAfterElement);

		addContentAfterIndex(parent, content, indexOf);
	}

	public static void addContentAfterIndex(Element parent, List<Content> content, int indexOf) {
		int contentSize = parent.getContentSize();
		if (indexOf >= 0 && indexOf + 1 < contentSize) {
			parent.addContent(indexOf + 1, content);
		} else {
			parent.addContent(0, content);
		}
	}

	public static void addContentBefore(Element parent, List<Content> content, Content addBeforeEl) {
		int indexOf = addBeforeEl == null ? -1 : parent.indexOf(addBeforeEl);

		addContentAfterIndex(parent, content, indexOf);
	}

	public static void addContentToIndex(Element parent, List<Content> content, int index) {
		int contentSize = parent.getContentSize();
		if (index >= 0 && index < contentSize) {
			parent.addContent(index, content);
		} else {
			parent.addContent(content);
		}
	}

	public static Text createIndentText(int level) {
		Text indent = new Text(newLineAndIndentString(level));
		return indent;
	}

	public static String newLineAndIndentString(int level) {
		return NEW_LINE + Strings.repeat(INDENT, level);
	}

	public static String indentString(int level) {
		return Strings.repeat(INDENT, level);
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
