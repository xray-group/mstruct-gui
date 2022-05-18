package cz.kfkl.mstruct.gui.ui.optimization;

import cz.kfkl.mstruct.gui.core.AppContext;

public class SimulationJob extends RefinementJob {

	public SimulationJob(AppContext context) {
		super(context);
	}

	@Override
	public Integer getIterations() {
		return 0;
	}

}
