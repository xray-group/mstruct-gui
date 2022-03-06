package cz.kfkl.mstruct.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternBackgroundChebyshevController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;

@XmlElementName("PowderPatternBackgroundChebyshev")
public class PowderPatternBackgroundChebyshev extends PowderPatternBackgroundModel<PowderPatternBackgroundChebyshevController> {

	private static final String FXML_FILE_NAME = "powderPatternBackgroundChebyshev.fxml";

	private static final String COEFFICIENT_NAME_PATTERN = CoefficientParElement.BACKGROUND_COEF_NAME_PREFIX + "%s";

	@XmlAttributeProperty("Polynomial_degree")
	public IntegerProperty polynomialDegreeProperty = new SimpleIntegerProperty(2);

	@XmlElementList
	public List<CoefficientParElement> coefficients = FXCollections.observableArrayList();

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public PowderPatternBackgroundType getType() {
		return PowderPatternBackgroundType.Chebyshev;
	}

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);

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
	public List<ParUniqueElement> getParams() {
		List<ParUniqueElement> list = new ArrayList<>();
		list.addAll(coefficients);
		return list;
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		return Collections.emptyList();
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
