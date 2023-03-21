package cz.kfkl.mstruct.gui.ui.instrumental;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.autoHeight;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindAndBuildRadioButtonsOption;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindBooleanTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenFocuseLost;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.instrumental.PowderPatternBackgroundChebyshev;
import cz.kfkl.mstruct.gui.ui.BaseController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class PowderPatternBackgroundChebyshevController
		extends BaseController<PowderPatternBackgroundChebyshev, PowderPatternController> {

	@FXML
	private TextField powderPatternComponentName;
	@FXML
	private Label powderPatternComponentTypeLabel;

	@FXML
	private HBox xFuncTypeOptionContainer;

	@FXML
	private Spinner<Integer> polynomialDegreeSpinner;

	@FXML
	private TableView<ParUniqueElement> coefficientsTableView;
	@FXML
	private TableColumn<ParUniqueElement, Integer> coefficientIndexTableColumn;
	@FXML
	private TableColumn<ParUniqueElement, String> coefficientValueTableColumn;
	@FXML
	private TableColumn<ParUniqueElement, Boolean> coefficientRefinedTableColumn;

	@Override
	public void init() {
		PowderPatternBackgroundChebyshev model = getModelInstance();

		powderPatternComponentName.textProperty().bindBidirectional(model.getNameProperty());
		powderPatternComponentTypeLabel.textProperty().set(model.getType().getTypeName());

		doWhenFocuseLost(powderPatternComponentName, () -> getParentController().powderPatternComponentNameChanged());

		bindAndBuildRadioButtonsOption(xFuncTypeOptionContainer, model.xFuncTypeOption);

		polynomialDegreeSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 16, model.polynomialDegreeProperty.getValue()));
		// model.polynomialDegreeProperty.bind(polynomialDegreeSpinner.valueProperty());
		polynomialDegreeSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if (newValue != null) {
					model.setNewDegreeeAndAdjustCoeficients(newValue);
					coefficientsTableView.getItems().clear();
					coefficientsTableView.getItems().addAll(model.coefficients);
					autoHeight(coefficientsTableView);
				}
			}
		});

		coefficientsTableView.setEditable(true);
		coefficientsTableView.getItems().addAll(model.coefficients);
		autoHeight(coefficientsTableView);

		coefficientIndexTableColumn.setCellValueFactory(new PropertyValueFactory<>("index"));

		bindDoubleTableColumn(coefficientValueTableColumn, v -> v.valueProperty);

		bindBooleanTableColumn(coefficientRefinedTableColumn, v -> v.refinedProperty);
	}

	public static StringConverter<String> converter = new StringConverter<String>() {
		@Override
		public String fromString(String s) {
			if (s == null || s.isEmpty()) {
				return null;
			} else if ("-".equals(s) || ".".equals(s) || "-.".equals(s)) {
				return Double.toString(0.0);
			} else {
				return Double.toString(Double.valueOf(s));
			}
		}

		@Override
		public String toString(String d) {
			return d == null ? null : d.toString();
		}
	};

}
