package cz.kfkl.mstruct.gui.model;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ImportedCrystal {

	private static final String CRYSTAL_NAME_ATT = "Name";

	public String crystalName;

	public Element crystalElement;
	public List<Content> crystalWithPreceedingBallast;

	public BooleanProperty selectedProperty = new SimpleBooleanProperty(false);
	public BooleanProperty existsProperty = new SimpleBooleanProperty(false);
	public CrystalModel existingCrystal;

	public ImportedCrystal(Element crystalElement) {
		this.crystalElement = crystalElement;
		this.crystalName = crystalElement.getAttributeValue(CRYSTAL_NAME_ATT);
	}

	public List<Content> getCrystalWithPreceedingBallast() {
		return crystalWithPreceedingBallast;
	}

	public void setCrystalWithPreceedingBallast(List<Content> crystalWithPreceedingBallast) {
		this.crystalWithPreceedingBallast = crystalWithPreceedingBallast;
	}

	public String getCrystalName() {
		return crystalName;
	}

	public Element getCrystalElement() {
		return crystalElement;
	}

}
