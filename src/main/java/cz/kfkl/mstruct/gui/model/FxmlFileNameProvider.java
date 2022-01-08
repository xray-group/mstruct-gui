package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.ui.BaseController;

// C is the controller associated with the view defined by the FXML file
public interface FxmlFileNameProvider<C extends BaseController<?>> {

	public String getFxmlFileName();
}
