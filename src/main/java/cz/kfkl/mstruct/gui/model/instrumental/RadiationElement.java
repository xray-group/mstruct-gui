package cz.kfkl.mstruct.gui.model.instrumental;

import cz.kfkl.mstruct.gui.model.OptionUniqueElement;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.ParamTreeNode;
import cz.kfkl.mstruct.gui.model.SingleValueUniqueElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("Radiation")
public class RadiationElement extends XmlLinkedModelElement implements ParamContainer {

	@XmlUniqueElement
	public OptionUniqueElement radiationOption = new OptionUniqueElement("Radiation", 0, "Neutron", "X-Ray", "Electron");

	@XmlUniqueElement
	public OptionUniqueElement spectrumOption = new OptionUniqueElement("Spectrum", 0, "Monochromatic", "X-Ray Tube",
			"Time Of Flight");

	@XmlUniqueElement("LinearPolarRate")
	public SingleValueUniqueElement linearPolarRateElement = new SingleValueUniqueElement("0.0");

	@XmlUniqueElement
	public ParUniqueElement wavelengthPar = new ParUniqueElement("Wavelength");

	@XmlUniqueElement
	public ParUniqueElement xRayTubeDeltaLambdaPar = new ParUniqueElement("XRayTubeDeltaLambda");

	@XmlUniqueElement
	public ParUniqueElement xRayTubeAlpha2Alpha1RatioPar = new ParUniqueElement("XRayTubeAlpha2Alpha1Ratio");

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(wavelengthPar, xRayTubeDeltaLambdaPar,
			xRayTubeAlpha2Alpha1RatioPar);

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public StringProperty getParamContainerNameProperty() {
		return new SimpleStringProperty("Radiation");
	}

	@Override
	public ObservableList<? extends ParamTreeNode> getChildren() {
		return children;
	}

}
