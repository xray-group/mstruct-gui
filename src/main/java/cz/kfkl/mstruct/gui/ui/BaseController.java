package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;

public abstract class BaseController<T extends FxmlFileNameProvider> implements SingleModelInstanceController<T>, HasAppContext {

	private AppContext appContext;

	private T model;

	@Override
	public void init() {
	}

	@Override
	public void setModelInstance(T modelInstance) {
		this.model = modelInstance;
	}

	public T getModelInstance() {
		return model;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
