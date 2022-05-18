package cz.kfkl.mstruct.gui.model.instrumental;

import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

abstract public class PowderPatternBackgroundXFuncCommon<C extends BaseController<?, ?>> extends PowderPatternBackgroundModel<C> {

	@XmlUniqueElement
	public OptionUniqueElement xFuncTypeOption = new OptionUniqueElement("X-func.type", 1, "X", "sin(Th)");

}
