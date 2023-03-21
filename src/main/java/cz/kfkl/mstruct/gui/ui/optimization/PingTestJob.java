package cz.kfkl.mstruct.gui.ui.optimization;

import java.util.List;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;

public class PingTestJob extends OptimizationJob {
	private int triesCount = 10;
	private boolean fails = false;

	public PingTestJob(AppContext context, ObjCrystModel rootModel, int triesCount) {
		super(context, rootModel);
		this.triesCount = triesCount;
	}

	public PingTestJob(AppContext context, ObjCrystModel rootModel, int triesCount, boolean fails) {
		this(context, rootModel, triesCount);
		this.fails = fails;
	}

	@Override
	public List<String> getCommandList() {
		return List.of("ping", "192.168.10.66", "-n", Integer.toString(triesCount));
	}

	@Override
	protected void jobDoneAction() {
		if (fails) {
			super.jobDoneAction();
		}
	}

}
