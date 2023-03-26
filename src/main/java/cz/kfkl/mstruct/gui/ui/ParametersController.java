package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.ParametersModel;
import cz.kfkl.mstruct.gui.utils.CombinedObservableList;
import cz.kfkl.mstruct.gui.utils.DoubleTextFieldTreeTableCell;
import cz.kfkl.mstruct.gui.utils.tree.DirectTreeTableViewSelectionModel;
import cz.kfkl.mstruct.gui.utils.tree.FilterableTreeItem;
import cz.kfkl.mstruct.gui.utils.tree.FlexibleTableResizingPolicy;
import cz.kfkl.mstruct.gui.utils.tree.SelectableTreeItemFilter;
import cz.kfkl.mstruct.gui.utils.tree.TreeItemPredicate;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class ParametersController extends BaseController<ParametersModel, MStructGuiController> {
	private static final Logger LOG = LoggerFactory.getLogger(ParametersController.class);

	private static final Escaper ESCAPER = Escapers.builder().addEscape('|', "||").build();
	private static final String KEY_DELIM = "|";

	@FXML
	private TreeTableView<ParamTreeNode> parametersTreeTableView;
	@FXML
	private TreeTableColumn<ParamTreeNode, String> nameTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, Boolean> fittedTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, String> fittedValueTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, String> valueTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, Boolean> refinedTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, Boolean> limitedTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, String> minTreeTableColumn;
	@FXML
	private TreeTableColumn<ParamTreeNode, String> maxTreeTableColumn;

	@FXML
	private TextField paramFilterNameTextField;
	@FXML
	private CheckBox paramFilterRefinedCheckBox;
	@FXML
	private CheckBox paramFilterLimitedCheckBox;
	@FXML
	private CheckBox paramFilterIhklBox;
	@FXML
	private CheckBox paramFilterFittedCheckBox;

	@FXML
	private Button copyFittedValuesButton;

	TreeItemPredicate<ParamTreeNode> treeFilterPredicate;

	private FilterableTreeItem<ParamTreeNode> treeRoot;

	@Override
	public void init() {
		LOG.debug("Initialising parameters tab");
		ParametersModel model = getModelInstance();

		configParamsTable();

		if (LOG.isTraceEnabled()) {
			parametersTreeTableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<>() {
				@Override
				public void onChanged(Change<? extends Integer> c) {
					LOG.trace("  selection changed ({}): {}", c.getList().size(), Joiner.on(", ").join(c.getList()));
				}
			});
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("Selected: {}", Joiner.on(", ").join(parametersTreeTableView.getSelectionModel().getSelectedIndices()));
		}
	}

	private void configParamsTable() {

//		this.minTreeTableColumn.setPrefWidth(Control.USE_COMPUTED_SIZE);

		this.parametersTreeTableView.setColumnResizePolicy(new FlexibleTableResizingPolicy());

//		this.nameTreeTableColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		this.nameTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getParamContainerNameProperty());

		this.fittedTreeTableColumn.setCellFactory(c -> new cz.kfkl.mstruct.gui.utils.CheckBoxTreeTableCell());
		this.fittedTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getFittedProperty());
//		this.fittedTreeTableColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("fitted"));
		this.fittedTreeTableColumn.setStyle("-fx-alignment: CENTER;");

		this.fittedValueTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getFittedValueProperty());
		this.fittedValueTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

		this.valueTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getValueProperty());
		this.valueTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

		// if the custom CheckBoxTreeTableCell is not good may try something like this:
		// https://stackoverflow.com/questions/37136324/checkboxes-only-on-leafs-of-treetableview-in-javafx

		this.refinedTreeTableColumn.setCellFactory(c -> new cz.kfkl.mstruct.gui.utils.CheckBoxTreeTableCell());
		this.refinedTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getRefinedProperty());
		this.refinedTreeTableColumn.setStyle("-fx-alignment: CENTER;");

		this.limitedTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getLimitedProperty());
		this.limitedTreeTableColumn.setCellFactory(c -> new cz.kfkl.mstruct.gui.utils.CheckBoxTreeTableCell());
		this.limitedTreeTableColumn.setStyle("-fx-alignment: CENTER;");

		this.minTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getMinProperty());
		this.minTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

		this.maxTreeTableColumn.setCellValueFactory(cdf -> cdf.getValue().getValue().getMaxProperty());
		this.maxTreeTableColumn.setCellFactory(DoubleTextFieldTreeTableCell.forTreeTableColumn());

//		FilteredTreeTableViewSelectionModel<RefinableParameter> fiteredSelectionModel = new FilteredTreeTableViewSelectionModel<RefinableParameter>(
//				parametersTreeTableView, parametersTreeTableView.getSelectionModel(), (ti) -> !ti.getValue().isMocked());
//		fiteredSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
//		parametersTreeTableView.setSelectionModel(fiteredSelectionModel);

		DirectTreeTableViewSelectionModel<ParamTreeNode> selectionModel = new DirectTreeTableViewSelectionModel<ParamTreeNode>(
				parametersTreeTableView, (SelectableTreeItemFilter<ParamTreeNode>) (ti) -> ti.getValue().isParameter());
		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		parametersTreeTableView.setSelectionModel(selectionModel);
	}

	public void bindToRootModel(ObjCrystModel rootModel) {
		LOG.debug("Initialising parameters tab tree");
		treeRoot = createFilterableTreeItem(rootModel);
		bindToParametersTree();
	}

	private FilterableTreeItem<ParamTreeNode> createFilterableTreeItem(ParamTreeNode paramNode) {
		FilterableTreeItem<ParamTreeNode> filterableTreeItem;
		if (paramNode.isParameter()) {
			filterableTreeItem = new FilterableTreeItem<ParamTreeNode>(paramNode, FXCollections.emptyObservableList());
		} else {
			ObservableList<TreeItem<ParamTreeNode>> list = new CombinedObservableList<ParamTreeNode, TreeItem<ParamTreeNode>>(
					(ParamTreeNode pn) -> createFilterableTreeItem(pn), paramNode.getChildren());
			filterableTreeItem = new FilterableTreeItem<ParamTreeNode>(paramNode, list);
			filterableTreeItem.setExpanded(true);
		}

		return filterableTreeItem;
	}

	public void bindToParametersTree() {

		treeRoot.predicateProperty()
				.bind(Bindings.createObjectBinding(() -> createFilterPredicate(), paramFilterNameTextField.textProperty(),
						paramFilterRefinedCheckBox.selectedProperty(), paramFilterRefinedCheckBox.indeterminateProperty(),
						paramFilterLimitedCheckBox.selectedProperty(), paramFilterLimitedCheckBox.indeterminateProperty(),
						paramFilterIhklBox.selectedProperty(), paramFilterIhklBox.indeterminateProperty(),
						paramFilterFittedCheckBox.selectedProperty(), paramFilterFittedCheckBox.indeterminateProperty()));
		parametersTreeTableView.setRoot(treeRoot);
		parametersTreeTableView.refresh();
	}

	public void showFittedOptions(Set<ParUniqueElement> fittedParams) {

		fittedTreeTableColumn.setVisible(true);
		fittedValueTreeTableColumn.setVisible(true);
		copyFittedValuesButton.setVisible(true);

		paramFilterFittedCheckBox.setVisible(true);
		paramFilterFittedCheckBox.setIndeterminate(false);
		paramFilterFittedCheckBox.setSelected(true);

		for (ParUniqueElement par : fittedParams) {
			par.getFittedProperty().set(true);
		}
	}

	public void clearFilters() {
		paramFilterNameTextField.textProperty().set(null);
		paramFilterRefinedCheckBox.indeterminateProperty().set(true);
		paramFilterLimitedCheckBox.indeterminateProperty().set(true);
		paramFilterIhklBox.indeterminateProperty().set(true);
	}

	/**
	 * Similar to the {@link #initParamsTree()}
	 */
	public static Map<String, ParUniqueElement> createParamsLookup(ObjCrystModel rootModel) {
		return createParamsLookup(rootModel, null);
	}

	public static Map<String, ParUniqueElement> createParamsLookup(ObjCrystModel rootModel,
			Predicate<ParUniqueElement> filterPredicate) {

		Map<String, ParUniqueElement> map = new LinkedHashMap<>();

//		FilterableTreeItem<RefinableParameter> treeRoot = addContainer(null, rootModel.formatParamContainerName());
		String rootContainerKey = "";
		formatKey("", rootModel.getParamContainerNameProperty().get());

//		String parentKey = parent == null ? RefinableParameter.KEY_DELIM : parent.getValue().getKey();
//		RefinableParameter refPar = new RefinableParameter(tiName, parentKey);

		Deque<ParamContainer> paramParentsStack = new ArrayDeque<>();
		Deque<String> treeItemsStack = new ArrayDeque<>();

		paramParentsStack.addLast(rootModel);
		treeItemsStack.addLast(rootContainerKey);
		do {
			ParamContainer parent = paramParentsStack.removeFirst();
			String parentKey = treeItemsStack.removeFirst();

			ObservableList<? extends ParamTreeNode> children = parent.getChildren();
			if (children != null) {
				for (ParamTreeNode node : children) {
					if (node.isParameter()) {
						if (node instanceof ParUniqueElement) {
							ParUniqueElement par = (ParUniqueElement) node;
							String paramKey = formatKey(parentKey, par.getName());
							if (filterPredicate == null || filterPredicate.test(par)) {
								map.put(paramKey, par);
								LOG.trace("Adding paramKey [{}], param [{}]", paramKey, par);
							}
						} else {
							LOG.warn("The node [{}] should be of type ParUniqueElement", node);
						}
					} else {
						ParamContainer child = (ParamContainer) node;
						paramParentsStack.addLast(child);
						String parentItemName = child.getParamContainerNameProperty().get();
						if (parentItemName == null) {
							// null name means to "inline" parameters of the ParamParent into its parent
							treeItemsStack.addLast(parentKey);
						} else {
							String containerKey = formatKey(parentKey, parentItemName);
							treeItemsStack.addLast(containerKey);
						}
					}
				}
			}
		} while (!paramParentsStack.isEmpty());

		return map;

	}

	public static String formatKey(String parentKey, String name) {
		return parentKey + KEY_DELIM + ESCAPER.escape(name);
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
		applyToAllViableAndSelectedParameters((par) -> par.getRefinedProperty().set(true));
	}

	public void uncheckRefined() {
		applyToAllViableAndSelectedParameters((par) -> par.getRefinedProperty().set(false));
	}

	public void checkLimited() {
		applyToAllViableAndSelectedParameters((par) -> par.getLimitedProperty().set(true));
	}

	public void uncheckLimited() {
		applyToAllViableAndSelectedParameters((par) -> par.getLimitedProperty().set(false));
	}

	public void copyFittedValues() {
		applyToAllViableAndSelectedParameters((par) -> par.getValueProperty().set(par.getFittedValueProperty().get()));
	}

	private void applyToAllViableAndSelectedParameters(Consumer<ParamTreeNode> consumer) {
		for (int row = 0; row < parametersTreeTableView.getExpandedItemCount(); row++) {
			TreeItem<ParamTreeNode> treeItem = parametersTreeTableView.getTreeItem(row);
			if (parametersTreeTableView.getSelectionModel().isSelected(row)) {
				ParamTreeNode par = treeItem.getValue();
				if (par.isParameter()) {
					consumer.accept(par);
				}
			}
		}
	}

	private TreeItemPredicate<ParamTreeNode> createFilterPredicate() {
		LOG.trace("Creating new predicate.");
		TreeItemPredicate<ParamTreeNode> predicate = new TreeItemPredicate<ParamTreeNode>() {

			@Override
			public boolean test(TreeItem<ParamTreeNode> parent, ParamTreeNode par) {
				return parIsMatchingFilter(parent, par);
			}
		};

		return predicate;
	}

	public boolean parIsMatchingFilter(TreeItem<ParamTreeNode> parent, ParamTreeNode par) {
		if (!par.isParameter()) {
			return false;
		}

		String searchTextValue = paramFilterNameTextField.textProperty().getValue();
		if (searchTextValue != null && !searchTextValue.isBlank()) {
			if (!par.getParamContainerNameProperty().get().startsWith(searchTextValue)) {
				return false;
			}
		}

		if (!paramFilterRefinedCheckBox.isIndeterminate() && par.getRefinedProperty() != null) {
			if (par.getRefinedProperty().get() != paramFilterRefinedCheckBox.selectedProperty().get()) {
				return false;
			}
		}

		if (!paramFilterLimitedCheckBox.isIndeterminate() && par.getLimitedProperty() != null) {
			if (par.getLimitedProperty().get() != paramFilterLimitedCheckBox.selectedProperty().get()) {
				return false;
			}
		}
		if (!paramFilterIhklBox.isIndeterminate()) {
			if (par.isIhklParameter() != paramFilterIhklBox.selectedProperty().get()) {
				return false;
			}
		}
		if (!paramFilterFittedCheckBox.isIndeterminate() && par.getFittedProperty() != null) {
			if (par.getFittedProperty().get() != paramFilterFittedCheckBox.selectedProperty().get()) {
				return false;
			}
		}

		return true;
	}

}
