package cz.kfkl.mstruct.gui.ui;

import java.util.ArrayList;
import java.util.List;

import cz.kfkl.mstruct.gui.core.AppContext;

public class RefinementJob extends OptimizationJob {

	public RefinementJob(AppContext context) {
		super(context);
	}

	@Override
	List<String> getCommandList() {
		List<String> command = new ArrayList<>();

		command.add(getContext().getMstructExeFile().getAbsolutePath());
		command.add(this.getInputFile().getAbsolutePath());

		return command;
	}

}
