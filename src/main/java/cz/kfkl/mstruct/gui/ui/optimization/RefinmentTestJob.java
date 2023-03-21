package cz.kfkl.mstruct.gui.ui.optimization;

import java.util.ArrayList;
import java.util.List;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;

public class RefinmentTestJob extends OptimizationJob {

	public RefinmentTestJob(AppContext context, ObjCrystModel rootModel) {
		super(context, rootModel);
	}

	@Override
	public List<String> getCommandList() {
		List<String> command = new ArrayList<>();

		// cmd.exe /c
		command.add("cmd.exe");
		command.add("/c");
		command.add("copy");
		command.add("/y");
		command.add("..\\_mock\\*");
//		command.add('"' + this.getResultDir().getAbsolutePath() + '"');
		command.add(this.getResultDir().getAbsolutePath());

		return command;
	}

}
