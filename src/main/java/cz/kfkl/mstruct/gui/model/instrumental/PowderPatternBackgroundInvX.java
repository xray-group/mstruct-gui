package cz.kfkl.mstruct.gui.model.instrumental;

import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternBackgroundInvXController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;

@XmlElementName("PowderPatternBackgroundInvX")
public class PowderPatternBackgroundInvX extends PowderPatternBackgroundXFuncCommon<PowderPatternBackgroundInvXController> {

	private static final String FXML_FILE_NAME = "powderPatternBackgroundInvX.fxml";

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public PowderPatternBackgroundType getType() {
		return PowderPatternBackgroundType.InvX;
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return Collections.emptyList();
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		return Collections.emptyList();
	}

}
