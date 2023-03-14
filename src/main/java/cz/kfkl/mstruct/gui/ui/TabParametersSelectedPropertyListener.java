package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.validateNotNull;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableView;

/**
 * Recreates the parameter tree structures on the {@link ParametersController}
 * and refreshes the parameters {@link TreeTableView}.
 */
public class TabParametersSelectedPropertyListener implements ChangeListener<Boolean> {

	private ParametersController controller;

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (newValue) {
			validateNotNull(newValue, "The ParentController need to be set first on the TabParametersSelectedPropertyListener");
			controller.createAndSetParamTree();
			controller.refreshTable();
		}
	}

	public void setController(ParametersController controller) {
		this.controller = controller;
	}

}
