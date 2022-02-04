package cz.kfkl.mstruct.gui.model;

import java.util.Collections;
import java.util.List;

import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;

@XmlElementName("ScatteringPowerSphere")
public class ScatteringPowerSphereElement extends ScatteringPowerCommon {

	@XmlUniqueElement
	public ParUniqueElement radiusPar = new ParUniqueElement("Radius");

	@XmlUniqueElement
	public ParUniqueElement bisoPar = new ParUniqueElement("Biso");

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(radiusPar, bisoPar);
	}

	@Override
	public List<ParamContainer> getInnerContainers() {
		return Collections.emptyList();
	}

	public String getSymbol() {
		return "<sphere>";
	}

	public String getBiso() {
		return this.bisoPar.getValue();
	}

	@Override
	public String getType() {
		return "Sphere";
	}
}
