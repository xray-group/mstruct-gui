package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;

/**
 * A common class for controllers which are associated with model of type
 * {@link FxmlFileNameProvider} and they have a parent controller (typically a
 * controller which creates the controller and the view)
 */
public abstract class BaseController<M extends FxmlFileNameProvider<?>, P>
		implements SingleModelInstanceController<M>, HasAppContext, HasParentController<P> {

	private AppContext appContext;

	private M model;

	private P parentController;

	@Override
	public void init() {
	}

	@Override
	public void setModelInstance(M modelInstance) {
		this.model = modelInstance;
	}

	@Override
	public void setParenController(P parentController) {
		this.parentController = parentController;
	}

	@Override
	public P getParentController() {
		return parentController;
	}

	public M getModelInstance() {
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
