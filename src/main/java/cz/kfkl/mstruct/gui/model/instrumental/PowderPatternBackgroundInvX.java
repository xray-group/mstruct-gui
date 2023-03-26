package cz.kfkl.mstruct.gui.model.instrumental;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternComponentWithScaleParElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.instrumental.PowderPatternBackgroundInvXController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("PowderPatternBackgroundInvX")
public class PowderPatternBackgroundInvX extends PowderPatternBackgroundXFuncCommon<PowderPatternBackgroundInvXController> {

	private static final String FXML_FILE_NAME = "powderPatternBackgroundInvX.fxml";

	@XmlUniqueElement(isSibling = true)
	public PowderPatternComponentWithScaleParElement powderPatternComponent = new PowderPatternComponentWithScaleParElement(
			nameProperty);

	private ObservableList<? extends ParamTreeNode> children = FXCollections.observableArrayList(powderPatternComponent.scalePar);

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		rootModel.registerChildren(this.getChildren());
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	@Override
	public PowderPatternBackgroundType getType() {
		return PowderPatternBackgroundType.InvX;
	}

	@Override
	public Element getLastOwnedXmlElement() {
		return powderPatternComponent.getLastOwnedXmlElement();
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

}
