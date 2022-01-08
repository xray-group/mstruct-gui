package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayList;
import java.util.List;

import cz.kfkl.mstruct.gui.core.AppContext;

public class RefinmentTestJob extends OptimizationJob {

	public RefinmentTestJob(AppContext context) {
		super(context);
	}

	@Override
	List<String> getCommandList() {
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
