package cz.kfkl.mstruct.gui.model.instrumental;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternComponentElement;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternBackgroundChebyshevController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("PowderPatternBackgroundChebyshev")
public class PowderPatternBackgroundChebyshev
		extends PowderPatternBackgroundXFuncCommon<PowderPatternBackgroundChebyshevController> {

	private static final String FXML_FILE_NAME = "powderPatternBackgroundChebyshev.fxml";

	private static final String COEFFICIENT_NAME_PATTERN = CoefficientParElement.BACKGROUND_COEF_NAME_PREFIX + "%s";

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentElement powderPatternComponent = new PowderPatternComponentElement(nameProperty);

	@XmlAttributeProperty("Polynomial_degree")
	public IntegerProperty polynomialDegreeProperty = new SimpleIntegerProperty(2);

	@XmlElementList
	public ObservableList<CoefficientParElement> coefficients = FXCollections.observableArrayList();

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public PowderPatternBackgroundType getType() {
		return PowderPatternBackgroundType.Chebyshev;
	}

	@Override
	public void bindToElement(Element wrappedElement) {
		super.bindToElement(wrappedElement);

		// TODO validate degree and coefficient names and order

//		if (coefficients.size() - 1 >= polynomialDegreeProperty.get()) {
//			polynomialDegreeProperty.set(coefficients.size() - 1);
//		} else {
		for (int i = 0; i < coefficients.size(); i++) {
			setCorrectName(coefficients.get(i), i);
		}
		addMissingCoeficients();
//		}
	}

	public void setNewDegreeeAndAdjustCoeficients(int newDegree) {
		polynomialDegreeProperty.set(newDegree);
		removeExtraCoeficients();
		addMissingCoeficients();
	}

	@Override
	public Element getLastOwnedXmlElement() {
		return powderPatternComponent.getLastOwnedXmlElement();
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return coefficients;
	}

	private void removeExtraCoeficients() {
		for (int i = polynomialDegreeProperty.get() + 1; i < coefficients.size(); i++) {
			coefficients.remove(i);
		}
	}

	private void addMissingCoeficients() {
		for (int i = coefficients.size(); i < polynomialDegreeProperty.get() + 1; i++) {
			CoefficientParElement coeff = new CoefficientParElement();
			coefficients.add(coeff);
			// must be after coeff is added to observable list
			setCorrectName(coeff, i);
		}
	}

	private void setCorrectName(CoefficientParElement coeff, int index) {
		String correctName = formatCorrectName(index);
		coeff.nameProperty.set(correctName);
		coeff.setIndex(index);
	}

	private String formatCorrectName(int index) {
		return String.format(COEFFICIENT_NAME_PATTERN, index);
	}

}
