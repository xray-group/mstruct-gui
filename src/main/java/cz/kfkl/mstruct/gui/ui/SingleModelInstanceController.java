package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;

public interface SingleModelInstanceController<T extends FxmlFileNameProvider> {

	public void init();

	void setModelInstance(T modelInstance);
}
