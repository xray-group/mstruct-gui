package cz.kfkl.mstruct.gui.utils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;
import cz.kfkl.mstruct.gui.model.HasUniqueName;
import cz.kfkl.mstruct.gui.model.OptionChoice;
import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.ui.HasParentController;
import cz.kfkl.mstruct.gui.ui.MStructGuiMain;
import cz.kfkl.mstruct.gui.ui.SingleModelInstanceController;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles.RowIndex;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public final class BindingUtils {

	private static final Logger LOG = LoggerFactory.getLogger(BindingUtils.class);

	private static final int MIN_MAX_FIELD_MIN_WIDTH = 25;
	private static final int VALUE_FIELD_MIN_WIDTH = 65;

	private BindingUtils() {
	}

	public static <T> void bindToggleGroupToProperty(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {
		// Check all toggles for required user data
		toggleGroup.getToggles().forEach(toggle -> {
			if (toggle.getUserData() == null) {
				throw new IllegalArgumentException("The ToggleGroup contains at least one Toggle without user data!");
			}
		});
		// Select initial toggle for current property state
		for (Toggle toggle : toggleGroup.getToggles()) {
			if (property.getValue() != null && property.getValue().equals(toggle.getUserData())) {
				toggleGroup.selectToggle(toggle);
				break;
			}
		}
		// Update property value on toggle selection changes
		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && newValue.getUserData() != null) {
				property.setValue((T) newValue.getUserData());
			}
		});
	}

	public static void bindToggleGroupToPropertyByText(ToggleGroup toggleGroup, OptionUniqueElement constrainLatticeOption) {
		// Select initial toggle for current property state
		String selectedDisplayText = constrainLatticeOption.getSelectedChoice().getDisplayText();
		if (selectedDisplayText != null) {
			for (Toggle toggle : toggleGroup.getToggles()) {
				if (toggle instanceof ToggleButton) {
					ToggleButton toggleButton = (ToggleButton) toggle;
					if (selectedDisplayText.equals(toggleButton.getText())) {
						toggleGroup.selectToggle(toggle);
						break;
					}
				}
			}
		}
		// Update property value on toggle selection changes
		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue instanceof ToggleButton) {
				ToggleButton newValueToggleButton = (ToggleButton) newValue;
				constrainLatticeOption.selectOptionByDisplayText(newValueToggleButton.getText());
			}
		});
	}

//	BooleanBinding booleanBinding = crystalModel.aPar.refinedProperty.greaterThan(0);
//	aParRefined.selectedProperty().bind(booleanBinding);
//
//	Callable<Integer> callable = () -> {
//		return booleanBinding.get() ? 1 : 0;
//	};
//	IntegerBinding integerBinding = Bindings.createIntegerBinding(callable, aParRefined.selectedProperty());
//	crystalModel.aPar.refinedProperty.bind(integerBinding);

//	StringConverter<Number> coverter = new StringConverter<Number>() {
//
//		@Override
//		public String toString(Number object) {
//			return object == null || object.longValue() == 0 ? "0" : "1";
//		}
//
//		@Override
//		public Number fromString(String string) {
//			return Integer.parseInt(string);
//		}
//	};
//	Bindings.bindBidirectional(aParRefinedHelper, crystalModel.aPar.refinedProperty, coverter);
//
//	StringConverter<Boolean> booleanCoverter = new StringConverter<Boolean>() {
//
//		@Override
//		public String toString(Boolean object) {
//			return object == null || !object ? "0" : "1";
//		}
//
//		@Override
//		public Boolean fromString(String string) {
//			return string == null || "0".equals(string);
//		}
//	};
//
//	Bindings.bindBidirectional(aParRefinedHelper, aParRefined.selectedProperty(), booleanCoverter);

	public static void bindlBooleanPropertyToInteger(BooleanProperty booleanProperty, IntegerProperty integerProperty) {
		Integer value = integerProperty.getValue();
		if (value != null) {
			booleanProperty.set(value > 0);
		}

		booleanProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				integerProperty.setValue(newValue ? 1 : 0);
			}
		});
	}

	public static void bindDoubleTextField(TextField textField, StringProperty stringProperty) {
		doubleTextField(textField);
		textField.textProperty().bindBidirectional(stringProperty);
	}

	public static void doubleTextField(TextField textField) {
		textField.setPrefWidth(Double.MIN_VALUE);
		textField.setMinWidth(60);
		textField.setMaxWidth(160);

//		textField.setPrefWidth(100);
		HBox.setHgrow(textField, Priority.ALWAYS);
		textField.setTextFormatter(new DoubleTextFormatter());
	}

	public static void bindAndBuildParFieldsFullLarge(HBox bParContainer, ParUniqueElement par) {
		bParContainer.getChildren().addAll(new Label(par.getName() + ":"));

		addParValue(bParContainer, par);
		addParRefinedLimited(bParContainer, par);
		addParMinMax(bParContainer, par);
	}

	public static void bindAndBuildParFieldsNoName(HBox bParContainer, ParUniqueElement par) {
		addParValue(bParContainer, par);
		addParRefinedLimited(bParContainer, par);
		addParMinMax(bParContainer, par);
	}

	public static void bindAndBuildParFieldsNoNameShort(HBox bParContainer, ParUniqueElement par) {
		addParValue(bParContainer, par);
		addParRefinedLimitedShort(bParContainer, par);
		addParMinMax(bParContainer, par);
	}

	private static void addParValue(HBox bParContainer, ParUniqueElement par) {
		TextField parValue = new TextField();
		parValue.setMinWidth(VALUE_FIELD_MIN_WIDTH);
		BindingUtils.doubleTextField(parValue);
		parValue.textProperty().bindBidirectional(par.valueProperty);

		bParContainer.getChildren().addAll(parValue);
	}

	private static void addParRefinedLimited(HBox bParContainer, ParUniqueElement par) {
		CheckBox refinedCb = new CheckBox("Refined");
		refinedCb.selectedProperty().bindBidirectional(par.refinedProperty);
		setHBoxMarginLeft(refinedCb);
		// BindingUtils.bindlBooleanPropertyToInteger(refinedCb.selectedProperty(),
		// par.refinedProperty);

		CheckBox limitedCb = new CheckBox("Limited");
		limitedCb.selectedProperty().bindBidirectional(par.limitedProperty);
		setHBoxMarginLeft(limitedCb);

		bParContainer.getChildren().addAll(refinedCb, limitedCb);
	}

	private static void setHBoxMarginLeft(Node refinedCb) {
		HBox.setMargin(refinedCb, new Insets(0.0d, 0.0d, 0.0d, 6.0d));
	}

	private static void addParRefinedLimitedShort(HBox bParContainer, ParUniqueElement par) {
		CheckBox refinedCb = new CheckBox("R");
		refinedCb.selectedProperty().bindBidirectional(par.refinedProperty);
//		BindingUtils.bindlBooleanPropertyToInteger(refinedCb.selectedProperty(), par.refinedProperty);

		CheckBox limitedCb = new CheckBox("L");
		limitedCb.selectedProperty().bindBidirectional(par.limitedProperty);
//		BindingUtils.bindlBooleanPropertyToInteger(limitedCb.selectedProperty(), par.limitedProperty);

		bParContainer.getChildren().addAll(refinedCb, limitedCb);
	}

	private static void addParMinMax(HBox bParContainer, ParUniqueElement par) {
		Label minLabel = new Label("Min:");
		TextField min = new TextField();
		BindingUtils.doubleTextField(min);
		min.setMinWidth(MIN_MAX_FIELD_MIN_WIDTH);
		min.textProperty().bindBidirectional(par.minProperty);
		setHBoxMarginLeft(minLabel);

		Label maxLabel = new Label("Max:");
		TextField max = new TextField();
		BindingUtils.doubleTextField(max);
		max.setMinWidth(MIN_MAX_FIELD_MIN_WIDTH);
		max.textProperty().bindBidirectional(par.maxProperty);
		bParContainer.getChildren().addAll(minLabel, min, maxLabel, max);
		setHBoxMarginLeft(maxLabel);
	}

	public static void bindAndBuildRadioButtonsOption(HBox container, OptionUniqueElement optionEl) {
		// The idea is that with this spacing adjustment the ComboBoxOption and
		// ChoiceBoxOption containers can both have the same spacing (typically 4)
		// defined in fxml
		container.setSpacing(container.getSpacing() + 4);
		Label label = new Label(optionEl.getName() + ":");

		container.getChildren().add(label);
		ToggleGroup toggleGroup = new ToggleGroup();
		for (OptionChoice oc : optionEl.getOptionChoices()) {
			RadioButton rb = new RadioButton(oc.getDisplayText());
			rb.setToggleGroup(toggleGroup);

			container.getChildren().add(rb);
		}

		BindingUtils.bindToggleGroupToPropertyByText(toggleGroup, optionEl);
	}

	public static void bindAndBuildChoiceBoxOption(HBox container, OptionUniqueElement optionEl) {
		Label label = new Label(optionEl.getName() + ":");
		container.getChildren().add(label);

		ChoiceBox<OptionChoice> choices = new ChoiceBox<>();
		choices.getItems().addAll(optionEl.getOptionChoices());
		choices.setValue(optionEl.getSelectedChoice());
		choices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OptionChoice>() {
			@Override
			public void changed(ObservableValue<? extends OptionChoice> observable, OptionChoice oldValue,
					OptionChoice newValue) {
				optionEl.selectOptionByDisplayText(newValue.getDisplayText());
			}
		});

		container.getChildren().add(choices);
	}

	public static void bindAndBuildComboBoxOption(HBox container, OptionUniqueElement optionEl) {
		Label label = new Label(optionEl.getName() + ":");
		container.getChildren().add(label);

		ComboBox<OptionChoice> choices = new ComboBox<>();

		choices.getItems().addAll(optionEl.getOptionChoices());
		choices.setValue(optionEl.getSelectedChoice());
		choices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OptionChoice>() {
			@Override
			public void changed(ObservableValue<? extends OptionChoice> observable, OptionChoice oldValue,
					OptionChoice newValue) {
				optionEl.selectOptionByDisplayText(newValue.getDisplayText());
			}
		});

		container.getChildren().add(choices);
	}

	public static void doWhenFocuseLost(javafx.scene.Node node, Runnable action) {
		ChangeListener<Boolean> list = new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue != null && !newValue.booleanValue()) {
					action.run();
				}
			}
		};
		node.focusedProperty().addListener(list);
	}

	public static <M extends FxmlFileNameProvider, P> void setupListViewListener(P parentController, ListView<M> listView,
			ScrollPane scrollPane, AppContext appContext) {
		updateContentWhenSelected(parentController, listView.getSelectionModel().selectedItemProperty(),
				() -> scrollPane.setContent(null), (n) -> {
					scrollPane.setContent(n);
					adjustVerticalScrollSpeed(scrollPane);
				}, appContext);
	}

	private static void adjustVerticalScrollSpeed(ScrollPane scrollPane) {
		scrollPane.getContent().setOnScroll(scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY();

			double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
			double scrollPaneHeight = scrollPane.getHeight();

			double diff = contentHeight - scrollPaneHeight;

			LOG.trace("Content scrollEvent.getDeltaY(): {}", deltaY);
			LOG.trace("  contentHeight: {}, scrollPaneHeight: {},  diff: {}", contentHeight, scrollPaneHeight, diff);

			if (diff < 1)
				diff = 1;

			double vvalue = scrollPane.getVvalue();

			double mdDiff = -deltaY / diff;
			LOG.trace("  vvalue = [{}], - deltaY / diff = [{}]", vvalue, mdDiff);
			scrollPane.setVvalue(vvalue - deltaY / diff);
		});
	}

	public static <M extends FxmlFileNameProvider, P> void setupSelectionToChildrenListener(P parentController,
			ReadOnlyObjectProperty<M> readOnlyObjectProperty, ObservableList<Node> children, AppContext appContext) {
		updateContentWhenSelected(parentController, readOnlyObjectProperty, () -> children.clear(), (n) -> children.add(n),
				appContext);
	}

	public static <M extends FxmlFileNameProvider, P> void updateContentWhenSelected(P parentController,
			ReadOnlyObjectProperty<M> selectedItemProperty, Runnable cleanContent, Consumer<Node> setContent,
			AppContext appContext) {

		selectedItemProperty.addListener(new ChangeListener<M>() {
			@Override
			public void changed(ObservableValue<? extends M> observable, M oldValue, M newValue) {
				LOG.debug("Selected item [{}]", newValue);

				if (newValue == null) {
					cleanContent.run();
				} else {
					loadViewAndInitController(parentController, appContext, newValue, (parent) -> {
						cleanContent.run();
						setContent.accept(parent);
					});

				}
			}

		});
	}

	public static <C extends BaseController<?, ?>, P> C loadViewAndInitController(P parentController, AppContext appContext,
			FxmlFileNameProvider<C> modelInstance, Consumer<Parent> viewConsumer) {
		Parent parent = null;
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(MStructGuiMain.class.getResource(modelInstance.getFxmlFileName()));
			parent = fxmlLoader.load();
			C controller = fxmlLoader.getController();

			if (controller instanceof HasAppContext) {
				((HasAppContext) controller).setAppContext(appContext);
			}

			if (controller instanceof HasParentController<?>) {
				((HasParentController<P>) controller).setParenController(parentController);
			}

			if (controller instanceof SingleModelInstanceController) {
				SingleModelInstanceController<FxmlFileNameProvider> cc = (SingleModelInstanceController<FxmlFileNameProvider>) controller;

				cc.setModelInstance(modelInstance);
				cc.init();
			}

			viewConsumer.accept(parent);
			return controller;
		} catch (IOException e) {
			throw new PopupErrorException(e, "Failed to load [%s] and instantiate it with element [%s].",
					modelInstance.getFxmlFileName(), modelInstance);
		}
	}

	public static <T> void autoHeight(ListView<T> listView) {
		listView.setFixedCellSize(25);
		listView.prefHeightProperty().bind(Bindings.size(listView.getItems()).multiply(listView.getFixedCellSize()).add(2));

	}

	public static <T> void autoHeight(TableView<T> table) {
		autoHeight(table, 25);
	}

	public static <T> void autoHeight(TableView<T> table, double fixedCellSize) {
		table.setFixedCellSize(fixedCellSize);
		table.prefHeightProperty()
				.bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(fixedCellSize + 1));
	}

	public static void initTableView(TableView<RowIndex> tableView, String[] columnNames) {
		tableView.getColumns().clear();
		tableView.getItems().clear();

		for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
			TableColumn<RowIndex, Number> column = new TableColumn<>(columnNames[colIndex]);
			column.setUserData(colIndex);

			column.setCellValueFactory((cdf) -> cdf.getValue().getObservableValue((Integer) cdf.getTableColumn().getUserData()));
			tableView.getColumns().add(column);
		}
	}

	public static <T> void doWhenPropertySet(Consumer<T> propertyValueConsumer, ObjectProperty<T> objectProperty) {
		T value = objectProperty.get();
		if (value == null) {
			objectProperty.addListener((obsTbd, oldV, newV) -> {
				if (newV != null) {
					propertyValueConsumer.accept(newV);
				}
			});
		} else {
			propertyValueConsumer.accept(value);
		}
	}

	public static <T> ChangeListener<T> newChanged(Consumer<T> consumer) {
		return new ChangeListener<T>() {
			@Override
			public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
				if (newValue != null) {
					consumer.accept(newValue);
				}
			}
		};
	}

	public static String createUniqueName(HasUniqueName newInstance, List<? extends HasUniqueName> powderPatternComponents) {
		Set<String> existingUniqueNames = new LinkedHashSet<>();
		powderPatternComponents.forEach((el) -> existingUniqueNames.add(el.getName()));

		int suffix = 2;
		String originalName = newInstance.getName();
		String newName = originalName;
		while (existingUniqueNames.contains(newName)) {
			newName = originalName + "_" + suffix;
			suffix++;
		}

		return newName;
	}

	public static <E> void bindDoubleTableColumn(TableColumn<E, String> tableColumn,
			Function<E, StringProperty> stringPropertyFunction) {
		tableColumn.setCellValueFactory(c -> stringPropertyFunction.apply(c.getValue()));
		tableColumn.setCellFactory(DoubleTextFieldTableCell.forTableColumn());
		tableColumn.setEditable(true);
	}

	public static <E> void bindStringTableColumn(TableColumn<E, String> tableColumn,
			Function<E, StringProperty> stringPropertyFunction) {
		tableColumn.setCellValueFactory(c -> stringPropertyFunction.apply(c.getValue()));
		tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumn.setEditable(true);
	}

}