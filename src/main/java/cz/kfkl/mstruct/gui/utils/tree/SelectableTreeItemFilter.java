package cz.kfkl.mstruct.gui.utils.tree;

import javafx.scene.control.TreeItem;

public interface SelectableTreeItemFilter<S> {

	public boolean isSelectable(TreeItem<S> treeItem);
}