package cz.kfkl.mstruct.gui.ui;

public interface HasParentController<P> {

	void setParenController(P parentController);

	P getParentController();

}
