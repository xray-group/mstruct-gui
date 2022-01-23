package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.ui.BaseController;

/**
 * A model which can provide a name of the FXML file providing the view for the
 * model. C is the controller associated with the view.
 */
public interface FxmlFileNameProvider<C extends BaseController<?, ?>> {

	public String getFxmlFileName();
}
