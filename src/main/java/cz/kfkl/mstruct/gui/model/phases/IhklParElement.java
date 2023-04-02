package cz.kfkl.mstruct.gui.model.phases;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

import java.util.Comparator;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.ParElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Par")
public class IhklParElement extends ParElement {

	private static final String IHKL_PAR_NAME_PATTERN = "%s_Ihkl_%s_%s_%s";

	public static final Comparator<IhklParElement> IHKL_PAR_COMPARATOR = Comparator.comparing(IhklParElement::getTwoThetaDouble,
			nullsFirst(naturalOrder()));

	@XmlAttributeProperty("twoTheta")
	public StringProperty twoTheta = new SimpleStringProperty("");

	@XmlAttributeProperty("SFactSqMult")
	public StringProperty sFactSqMult = new SimpleStringProperty("");

	@XmlAttributeProperty("H")
	public StringProperty h = new SimpleStringProperty("");
	@XmlAttributeProperty("K")
	public StringProperty k = new SimpleStringProperty("");
	@XmlAttributeProperty("L")
	public StringProperty l = new SimpleStringProperty("");

	private StringProperty phaseName = new SimpleStringProperty("");

	public IhklParElement() {
		super(new SimpleStringProperty(""));

		minProperty.set("0");
		maxProperty.set("100000");
	}

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		nameProperty.bind(Bindings.format(IHKL_PAR_NAME_PATTERN, phaseName, h, k, l));
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return nameProperty;
	}

	Double getTwoThetaDouble() {
		return JvStringUtils.parseDouble(twoTheta);
	}

	Double getValueDouble() {
		Double valueOf = null;
		try {
			valueOf = Double.valueOf(this.valueProperty.get());
		} catch (NumberFormatException e) {
			// ignore
		}
		return valueOf;
	}

	public void setDoubleInteger(Double value) {
		this.valueProperty.set(value == null ? null : value.toString());
	}

	public void setPhaseName(String phaseName) {
		this.phaseName.set(phaseName);
	}

	public String getPhaseName() {
		return phaseName.get();
	}

	/**
	 * @see ArbitraryTextureElement#updateIhklParams(ArbitraryTextureElement)
	 */
	public void updateFrom(IhklParElement fittedPar) {
		h.set(fittedPar.h.get());
		k.set(fittedPar.k.get());
		l.set(fittedPar.l.get());

		twoTheta.set(fittedPar.twoTheta.get());
		sFactSqMult.set(fittedPar.sFactSqMult.get());

		refinedProperty.set(fittedPar.refinedProperty.get());
		limitedProperty.set(fittedPar.limitedProperty.get());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + nameProperty.get() + "]";
	}

	@Override
	public boolean isIhklParameter() {
		return true;
	}

}
