package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("DihedralAngle")
public class MoleculeAtomDihedralAngleElement extends MoleculeAtomBondAngleElement {

	@XmlAttributeProperty("Atom4")
	public StringProperty atom4Property = new SimpleStringProperty();

}
