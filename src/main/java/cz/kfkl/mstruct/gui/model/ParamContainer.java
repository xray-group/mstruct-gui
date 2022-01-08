package cz.kfkl.mstruct.gui.model;

import java.util.List;

public interface ParamContainer {

	String formatParamContainerName();

	static boolean hasAnyChildren(ParamContainer parent) {
		return !parent.getParams().isEmpty() || !parent.getInnerContainers().isEmpty();
	}

	List<ParUniqueElement> getParams();

	List<? extends ParamContainer> getInnerContainers();

}
