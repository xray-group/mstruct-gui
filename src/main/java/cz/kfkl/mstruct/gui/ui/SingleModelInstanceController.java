package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;

public interface SingleModelInstanceController<M extends FxmlFileNameProvider> {

	public void init();

	void setModelInstance(M modelInstance);
}
