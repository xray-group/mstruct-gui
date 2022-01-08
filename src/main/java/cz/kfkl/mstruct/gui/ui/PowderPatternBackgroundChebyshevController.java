package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.PowderPatternBackgroundChebyshev;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.DoubleTextFieldTableCell;
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

public class PowderPatternBackgroundChebyshevController extends BaseController<PowderPatternBackgroundChebyshev> {

	@FXML
	private TextField powderPatternComponentName;
	@FXML
	private Label powderPatternComponentTypeLabel;

	@FXML
	private HBox xFuncTypeOptionContainer;

	@FXML
	private TextField powderPatternComponentScaleTextField;

	@FXML
	private Spinner<Integer> polynomialDegreeSpinner;

	@FXML
	private TableView<ParUniqueElement> coefficientsTableView;
	@FXML
	private TableColumn<ParUniqueElement, Integer> coefficientIndexTableColumn;
	@FXML
	private TableColumn<ParUniqueElement, String> coefficientValueTableColumn;

	@Override
	public void bindToInstance() {
		PowderPatternBackgroundChebyshev model = getModelInstance();

		powderPatternComponentName.textProperty().bindBidirectional(model.getNameProperty());
		powderPatternComponentTypeLabel.textProperty().set(model.getType().getTypeName());

//		BindingUtils.doWhenNodeFocusedLost(powderPatternComponentName,
//				() -> getAppContext().getMainController().getDiffractionsListView().refresh());

		BindingUtils.bindAndBuildRadioButtonsOption(xFuncTypeOptionContainer, model.xFuncTypeOption);

		powderPatternComponentScaleTextField.textProperty().bindBidirectional(model.powderPatternComponent.scalePoperty);

		polynomialDegreeSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 16, model.polynomialDegreeProperty.getValue()));
		// model.polynomialDegreeProperty.bind(polynomialDegreeSpinner.valueProperty());
		polynomialDegreeSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if (newValue != null) {
					model.setNewDegreeeAndAdjustCoeficients(newValue);
					coefficientsTableView.getItems().clear();
					coefficientsTableView.getItems().addAll(model.coefficients);
					BindingUtils.autoHeight(coefficientsTableView);
				}
			}
		});

		coefficientsTableView.setEditable(true);
		coefficientsTableView.getItems().addAll(model.coefficients);
		BindingUtils.autoHeight(coefficientsTableView);

		coefficientIndexTableColumn.setCellValueFactory(new PropertyValueFactory<>("index"));
		// coefficientIndexTableColumn.setEditable(fa);

		coefficientValueTableColumn.setCellValueFactory(c -> c.getValue().valueProperty);
		coefficientValueTableColumn.setCellFactory(DoubleTextFieldTableCell.forTableColumn());
		// When variable is renamed, re-evaluate formula
		// coefficientValueTableColumn.setOnEditCommit(event -> parseFormula());

		coefficientValueTableColumn.setEditable(true);
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
