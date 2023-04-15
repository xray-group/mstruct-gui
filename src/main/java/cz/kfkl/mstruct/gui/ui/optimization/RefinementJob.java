package cz.kfkl.mstruct.gui.ui.optimization;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;

public class RefinementJob extends OptimizationJob {

	public RefinementJob(AppContext context, ObjCrystModel rootModel) {
		super(context, rootModel);
	}

	@Override
	public List<String> getCommandList() {
		List<String> command = new ArrayList<>();

		command.add(getAppContext().getMstructExeFile().getAbsolutePath());
		command.add(this.getInputFile().getAbsolutePath());

		Integer iterations = getIterations();
		assertNotNull(iterations, "Number of iteration was not set.");
		command.add("--niteraction");
		command.add(iterations.toString());

		return command;
	}

}
