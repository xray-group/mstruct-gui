package cz.kfkl.mstruct.gui.model.instrumental;

import org.jdom2.Element;

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

//    RadiationTypeName="Radiation";
//    RadiationTypeChoices[0]="Neutron";
//    RadiationTypeChoices[1]="X-Ray";
//    RadiationTypeChoices[2]="Electron";

	@XmlUniqueElement
	public OptionUniqueElement radiationOption = new OptionUniqueElement("Radiation", 0, "Neutron", "X-Ray", "Electron");

//    WavelengthTypeName="Spectrum";
//    WavelengthTypeChoices[0]="Monochromatic";
//    WavelengthTypeChoices[1]="X-Ray Tube";
//    WavelengthTypeChoices[2]="Time Of Flight";
//    //WavelengthTypeChoices[2]="MAD";
//    //WavelengthTypeChoices[3]="DAFS";
//    //WavelengthTypeChoices[4]="LAUE";
	@XmlUniqueElement
	public OptionUniqueElement spectrumOption = new OptionUniqueElement("Spectrum", 0, "Monochromatic", "X-Ray Tube",
			"Time Of Flight");

	// LinearPolarRate
	@XmlUniqueElement("LinearPolarRate")
	public SingleValueUniqueElement linearPolarRateElement = new SingleValueUniqueElement("0.0");

//    <Par Refined="0" Limited="1" Min="0.05" Max="20" Name="Wavelength">1.54184</Par>
//    <Par Refined="0" Limited="1" Min="0.01" Max="20" Name="XRayTubeDeltaLambda">0.00382996</Par>
//    <Par Refined="0" Limited="1" Min="0.5" Max="0.5" Name="XRayTubeAlpha2Alpha1Ratio">0.5</Par>
	@XmlUniqueElement
	public ParUniqueElement wavelengthPar = new ParUniqueElement("Wavelength");
	@XmlUniqueElement
	public ParUniqueElement xRayTubeDeltaLambdaPar = new ParUniqueElement("XRayTubeDeltaLambda");
	@XmlUniqueElement
	public ParUniqueElement xRayTubeAlpha2Alpha1RatioPar = new ParUniqueElement("XRayTubeAlpha2Alpha1Ratio");

	private ObservableList<ParamTreeNode> children = FXCollections.observableArrayList(wavelengthPar, xRayTubeDeltaLambdaPar,
			xRayTubeAlpha2Alpha1RatioPar);

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);
		rootModel.registerChildren(this.getChildren());
	}

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
