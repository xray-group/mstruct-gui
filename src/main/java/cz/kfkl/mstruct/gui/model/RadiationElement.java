package cz.kfkl.mstruct.gui.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

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

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.MULTILINE_WITH_GAP;
	}

	@Override
	public String formatParamContainerName() {
		return "Radiation";
	}

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(wavelengthPar, xRayTubeDeltaLambdaPar, xRayTubeAlpha2Alpha1RatioPar);
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		return Collections.emptyList();
	}

}
