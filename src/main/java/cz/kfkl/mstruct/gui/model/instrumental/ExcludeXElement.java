package cz.kfkl.mstruct.gui.model.instrumental;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

import java.util.Comparator;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementValueProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("ExcludeX")
public class ExcludeXElement extends XmlLinkedModelElement {

	public static final Comparator<ExcludeXElement> EXCLUDE_REGIONS_TABLE_COMPARATOR = Comparator
			.comparing(ExcludeXElement::from, nullsFirst(naturalOrder()))
			.thenComparing(ExcludeXElement::to, nullsFirst(naturalOrder()));

	@XmlElementValueProperty
	public StringProperty valueProperty = new SimpleStringProperty();

	public StringProperty fromProperty = new SimpleStringProperty();
	public StringProperty toProperty = new SimpleStringProperty();

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);

		String valueStr = valueProperty.get();
		if (valueStr != null) {
			String[] parts = valueStr.trim().split(" ");

			if (parts.length > 0) {
				fromProperty.set(parts[0]);
			}
			if (parts.length > 1) {
				toProperty.set(parts[1]);
			}
			// TODO validation message for length != 0
		}

		valueProperty.bind(Bindings.concat(fromProperty, " ", toProperty));
	}

	public Double from() {
		return JvStringUtils.parseDouble(fromProperty);
	}

	public Double to() {
		return JvStringUtils.parseDouble(toProperty);
	}

}
