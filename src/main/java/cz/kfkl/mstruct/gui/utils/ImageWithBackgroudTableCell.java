package cz.kfkl.mstruct.gui.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class ImageWithBackgroudTableCell<T> extends TableCell<T, ImageWithBackgroud> {

	public ImageWithBackgroudTableCell(TableColumn<T, ImageWithBackgroud> column) {
	}

	@Override
	protected void updateItem(ImageWithBackgroud item, boolean empty) {
		super.updateItem(item, empty);

		setText(null);
		if (empty) {
			setGraphic(null);
		} else {
			this.setGraphic(item.getGraphic());
			this.setBackground(item.getBackground());
		}
	}
}