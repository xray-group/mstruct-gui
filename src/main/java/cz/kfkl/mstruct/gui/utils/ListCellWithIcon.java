package cz.kfkl.mstruct.gui.utils;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.util.Callback;

public class ListCellWithIcon<T> extends ListCell<T> {

	private Callback<T, String> textProvider;
	private Callback<T, Node> graphicProvider;

	public ListCellWithIcon() {
	}

	public ListCellWithIcon(Callback<T, Node> graphicProvider, Callback<T, String> textProvider) {
		super();
		this.graphicProvider = graphicProvider;
		this.textProvider = textProvider;
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setText(null);
			setGraphic(null);

		} else {
			if (textProvider != null) {
				setText(textProvider.call(item));
			}

			if (graphicProvider != null) {
				setGraphic(graphicProvider.call(item));
			}
		}
	}

}
