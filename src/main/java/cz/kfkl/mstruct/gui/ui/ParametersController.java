package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParametersModel;
import cz.kfkl.mstruct.gui.utils.DoubleTextFieldTreeTableCell;
import cz.kfkl.mstruct.gui.utils.tree.DirectTreeTableViewSelectionModel;
import cz.kfkl.mstruct.gui.utils.tree.FilterableTreeItem;
import cz.kfkl.mstruct.gui.utils.tree.FlexibleTableResizingPolicy;
import cz.kfkl.mstruct.gui.utils.tree.SelectableTreeItemFilter;
import cz.kfkl.mstruct.gui.utils.tree.TreeItemPredicate;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class ParametersController extends BaseController<ParametersModel, MStructGuiController> {
	private static final Logger LOG = LoggerFactory.getLogger(ParametersController.class);

	@FXML
	private TreeTableView<RefinableParameter> parametersTreeTableView;
	@FXML
	private TreeTableColumn<RefinableParameter, String> nameTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, Boolean> fittedTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, String> fittedValueTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, String> valueTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, Boolean> refinedTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, Boolean> limitedTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, String> minTreeTableColumn;
	@FXML
	private TreeTableColumn<RefinableParameter, String> maxTreeTableColumn;

	@FXML
	private TextField paramFilterNameTextField;
	@FXML
	private CheckBox paramFilterRefinedCheckBox;
	@FXML
	private CheckBox paramFilterLimitedCheckBox;
	@FXML
	private CheckBox paramFilterBackgroundCheckBox;
	@FXML
	private CheckBox paramFilterFittedCheckBox;

	@FXML
	private Button copyFittedValuesButton;

	@FXML
	private Label parametersCounts;

	private FilterableTreeItem<RefinableParameter> treeRoot;
	private TreeItemPredicate<RefinableParameter> treeFilterPredicate;

	@Override
	public void init() {
		LOG.debug("Initializing parameters tab");
		ParametersModel model = getModelInstance();

		ObjCrystModel rootModel = model.getRootModel();
		configParamsTable(rootModel);

		if (LOG.isTraceEnabled()) {
			parametersTreeTableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<>() {
				@Override
				public void onChanged(Change<? extends Integer> c) {
					LOG.trace("  selection changed: {}", Joiner.on(", ").join(c.getList()));
				}
			});
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("Selected: {}", Joiner.on(", ").join(parametersTreeTableView.getSelectionModel().getSelectedIndices()));
		}

		treeFilterPredicate = createFilterPredicate();
		createAndSetParamTree();

		parametersCounts.textProperty().bind(
				rootModel.parametersCount.asString().concat(" (").concat(rootModel.refinedParameters.asString()).concat(")"));
	}

	private void configParamsTable(ObjCrystModel rootModel) {

//		this.minTreeTableColumn.setPrefWidth(Control.USE_COMPUTED_SIZE);

		this.parametersTreeTableView.setColumnResizePolicy(new FlexibleTableResizingPolicy());

		this.nameTreeTableColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

		this.fittedTreeTableColumn.setCellFactory(c -> new cz.kfkl.mstruct.gui.utils.CheckBoxTreeTableCell());
		this.fittedTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().fittedProperty);
//		this.fittedTreeTableColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("fitted"));
		this.fittedTreeTableColumn.setStyle("-fx-alignment: CENTER;");

		this.fittedValueTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getFittedValueProperty());
		this.fittedValueTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

		this.valueTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().valueProperty);
		this.valueTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

		// if the custom CheckBoxTreeTableCell is not good may try something like this:
		// https://stackoverflow.com/questions/37136324/checkboxes-only-on-leafs-of-treetableview-in-javafx

		this.refinedTreeTableColumn.setCellFactory(c -> new cz.kfkl.mstruct.gui.utils.CheckBoxTreeTableCell());
		this.refinedTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().refinedProperty);
		this.refinedTreeTableColumn.setStyle("-fx-alignment: CENTER;");

		this.limitedTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().limitedProperty);
		this.limitedTreeTableColumn.setCellFactory(c -> new cz.kfkl.mstruct.gui.utils.CheckBoxTreeTableCell());
		this.limitedTreeTableColumn.setStyle("-fx-alignment: CENTER;");

		this.minTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().minProperty);
		this.minTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

		this.maxTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().maxProperty);
		this.maxTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

//		FilteredTreeTableViewSelectionModel<RefinableParameter> fiteredSelectionModel = new FilteredTreeTableViewSelectionModel<RefinableParameter>(
//				parametersTreeTableView, parametersTreeTableView.getSelectionModel(), (ti) -> !ti.getValue().isMocked());
//		fiteredSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
//		parametersTreeTableView.setSelectionModel(fiteredSelectionModel);

		DirectTreeTableViewSelectionModel<RefinableParameter> selectionModel = new DirectTreeTableViewSelectionModel<RefinableParameter>(
				parametersTreeTableView, (SelectableTreeItemFilter<RefinableParameter>) (ti) -> !ti.getValue().isMocked());
		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		parametersTreeTableView.setSelectionModel(selectionModel);
	}

	public void refreshTable() {
		parametersTreeTableView.refresh();
	}

	public void showFittedOptions() {
		fittedTreeTableColumn.setVisible(true);
		fittedValueTreeTableColumn.setVisible(true);
		copyFittedValuesButton.setVisible(true);

		paramFilterFittedCheckBox.setVisible(true);
		paramFilterFittedCheckBox.setIndeterminate(false);
		paramFilterFittedCheckBox.setSelected(true);
	}

	public void createAndSetParamTree() {
		this.treeRoot = createParamsTree();

		treeRoot.predicateProperty()
				.bind(Bindings.createObjectBinding(() -> createFilterPredicate(), paramFilterNameTextField.textProperty(),
						paramFilterRefinedCheckBox.selectedProperty(), paramFilterRefinedCheckBox.indeterminateProperty(),
						paramFilterLimitedCheckBox.selectedProperty(), paramFilterLimitedCheckBox.indeterminateProperty(),
						paramFilterFittedCheckBox.selectedProperty(), paramFilterFittedCheckBox.indeterminateProperty()));
		parametersTreeTableView.setRoot(treeRoot);

	}

	public void clearFilters() {
		paramFilterNameTextField.textProperty().set(null);
		paramFilterRefinedCheckBox.indeterminateProperty().set(true);
		paramFilterLimitedCheckBox.indeterminateProperty().set(true);
	}

	/**
	 * Similar to the {@link #createParamsTree()}
	 */
	public static Map<String, ParUniqueElement> createParamsLookup(ObjCrystModel rootModel) {

		Map<String, ParUniqueElement> map = new LinkedHashMap<>();

//		FilterableTreeItem<RefinableParameter> treeRoot = addContainer(null, rootModel.formatParamContainerName());
		String rootContainerKey = RefinableParameter.formatKey(RefinableParameter.KEY_DELIM,
				rootModel.formatParamContainerName());

//		String parentKey = parent == null ? RefinableParameter.KEY_DELIM : parent.getValue().getKey();
//		RefinableParameter refPar = new RefinableParameter(tiName, parentKey);

		Deque<ParamContainer> paramParentsStack = new ArrayDeque<>();
		Deque<String> treeItemsStack = new ArrayDeque<>();

		paramParentsStack.addLast(rootModel);
		treeItemsStack.addLast(rootContainerKey);
		do {
			ParamContainer parent = paramParentsStack.removeFirst();
			String parentKey = treeItemsStack.removeFirst();

			for (ParUniqueElement par : parent.getParams()) {
				String paramKey = RefinableParameter.formatKey(parentKey, par.getName());
				map.put(paramKey, par);
				LOG.trace("Adding paramKey [{}], param [{}]", paramKey, par);
			}

			for (ParamContainer child : parent.getInnerContainers()) {
				if (ParamContainer.hasAnyChildren(child)) {
					paramParentsStack.addLast(child);
					String parentItemName = child.formatParamContainerName();
					if (parentItemName == null) {
						// null name means to "inline" parameters of the ParamParent into its parent
						treeItemsStack.addLast(parentKey);
					} else {
						String containerKey = RefinableParameter.formatKey(parentKey, parentItemName);
						treeItemsStack.addLast(containerKey);
					}
				}
			}
		} while (!paramParentsStack.isEmpty());

		return map;

	}

	private FilterableTreeItem<RefinableParameter> createParamsTree() {
		LOG.debug("Refreshing parameters tab tree");

		ObjCrystModel rootModel = getModelInstance().getRootModel();

		rootModel.refinedParameters.set(0);
		rootModel.parametersCount.set(0);

		FilterableTreeItem<RefinableParameter> treeRoot = addContainer(null, rootModel.formatParamContainerName());

		Deque<ParamContainer> paramParentsStack = new ArrayDeque<>();
		Deque<FilterableTreeItem<RefinableParameter>> treeItemsStack = new ArrayDeque<>();

		paramParentsStack.addLast(rootModel);
		treeItemsStack.addLast(treeRoot);
		do {
			ParamContainer parent = paramParentsStack.removeFirst();
			FilterableTreeItem<RefinableParameter> treeItem = treeItemsStack.removeFirst();

			for (ParUniqueElement par : parent.getParams()) {
				addParam(treeItem, par);
				rootModel.registerParameter(par);
			}

			for (ParamContainer child : parent.getInnerContainers()) {
				if (ParamContainer.hasAnyChildren(child)) {
					paramParentsStack.addLast(child);
					String parentItemName = child.formatParamContainerName();
					if (parentItemName == null) {
						// null name means to "inline" parameters of the ParamParent into its parent
						treeItemsStack.addLast(treeItem);
					} else {
						FilterableTreeItem<RefinableParameter> childTreeItem = addContainer(treeItem, parentItemName);
						treeItemsStack.addLast(childTreeItem);
					}
				}
			}
		} while (!paramParentsStack.isEmpty());

		return treeRoot;
	}

	public void selectFiltered() {
		setSelectionOnFiltered(true);
	}

	public void unselectFiltered() {
		setSelectionOnFiltered(false);
	}

	public void unselectAll() {
		parametersTreeTableView.getSelectionModel().clearSelection();
	}

	private void setSelectionOnFiltered(boolean isSelected) {
		for (int row = 0; row < parametersTreeTableView.getExpandedItemCount(); row++) {

			if (isSelected) {
				parametersTreeTableView.getSelectionModel().select(row);
			} else {
				parametersTreeTableView.getSelectionModel().clearSelection(row);
			}

		}
	}

	public void checkRefined() {
		applyToAllViableAndSelectedParameters((par) -> par.refinedProperty.set(true));
	}

	public void uncheckRefined() {
		applyToAllViableAndSelectedParameters((par) -> par.refinedProperty.set(false));
	}

	public void checkLimited() {
		applyToAllViableAndSelectedParameters((par) -> par.limitedProperty.set(true));
	}

	public void uncheckLimited() {
		applyToAllViableAndSelectedParameters((par) -> par.limitedProperty.set(false));
	}

	public void copyFittedValues() {
		applyToAllViableAndSelectedParameters((par) -> par.valueProperty.set(par.getFittedValueProperty().get()));
	}

	private void applyToAllViableAndSelectedParameters(Consumer<RefinableParameter> consumer) {
		for (int row = 0; row < parametersTreeTableView.getExpandedItemCount(); row++) {
			TreeItem<RefinableParameter> treeItem = parametersTreeTableView.getTreeItem(row);
			if (parametersTreeTableView.getSelectionModel().isSelected(row)) {
				RefinableParameter par = treeItem.getValue();
				if (!par.isMocked()) {
					consumer.accept(par);
				}
			}
		}
	}

	private TreeItemPredicate<RefinableParameter> createFilterPredicate() {
		LOG.trace("Creating new predicate.");
		TreeItemPredicate<RefinableParameter> predicate = new TreeItemPredicate<RefinableParameter>() {

			@Override
			public boolean test(TreeItem<RefinableParameter> parent, RefinableParameter par) {
				return parIsMatchingFilter(parent, par);
			}
		};

		return predicate;
	}

	public boolean parIsMatchingFilter(TreeItem<RefinableParameter> parent, RefinableParameter par) {
		if (par.isMocked()) {
			return false;
		}

		String searchTextValue = paramFilterNameTextField.textProperty().getValue();
		if (searchTextValue != null && !searchTextValue.isBlank()) {
			if (!par.getName().startsWith(searchTextValue)) {
				return false;
			}
		}

		if (!paramFilterRefinedCheckBox.isIndeterminate() && par.refinedProperty != null) {
			if (par.refinedProperty.get() != paramFilterRefinedCheckBox.selectedProperty().get()) {
				return false;
			}
		}

		if (!paramFilterLimitedCheckBox.isIndeterminate() && par.limitedProperty != null) {
			if (par.limitedProperty.get() != paramFilterLimitedCheckBox.selectedProperty().get()) {
				return false;
			}
		}
		if (!paramFilterFittedCheckBox.isIndeterminate() && par.fittedProperty != null) {
			if (par.fittedProperty.get() != paramFilterFittedCheckBox.selectedProperty().get()) {
				return false;
			}
		}

		return true;
	}

	private void addParam(FilterableTreeItem<RefinableParameter> parent, ParUniqueElement par) {
		ParametersModel model = getModelInstance();
		RefinableParameter refPar = new RefinableParameter(par, parent.getValue().getKey(), model.fittedParamsProperty,
				model.refinedParams);

		LOG.trace("Adding main paramKey [{}], param [{}]", refPar.getKey(), refPar);

		parent.getInternalChildren().add(new FilterableTreeItem<RefinableParameter>(refPar));
	}

	/**
	 * Parent is null for the root
	 */
	private FilterableTreeItem<RefinableParameter> addContainer(FilterableTreeItem<RefinableParameter> parent, String tiName) {
		String parentKey = parent == null ? RefinableParameter.KEY_DELIM : parent.getValue().getKey();
		RefinableParameter refPar = new RefinableParameter(tiName, parentKey);

		FilterableTreeItem<RefinableParameter> parentTreeItem = new FilterableTreeItem<>(refPar);
		parentTreeItem.setExpanded(true);
		parentTreeItem.setPredicate(treeFilterPredicate);

		if (parent != null) {
			parent.getInternalChildren().add(parentTreeItem);
		}
		return parentTreeItem;
	}

}
