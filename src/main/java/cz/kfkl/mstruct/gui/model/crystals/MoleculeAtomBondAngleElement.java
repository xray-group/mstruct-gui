package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("BondAngle")
public class MoleculeAtomBondAngleElement extends MoleculeAtomBondCommon {

	@XmlAttributeProperty("Atom3")
	public StringProperty atom3Property = new SimpleStringProperty();

	@XmlAttributeProperty("Angle")
	public StringProperty angleProperty = new SimpleStringProperty();

}
