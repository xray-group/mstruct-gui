package cz.kfkl.mstruct.gui.ui.optimization;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;

public class SimulationJob extends RefinementJob {

	public SimulationJob(AppContext context, ObjCrystModel rootModel) {
		super(context, rootModel);
	}

	@Override
	public Integer getIterations() {
		return 0;
	}

}
