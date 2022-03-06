package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Bond")
public class MoleculeAtomBondElement extends MoleculeAtomBondCommon {

	@XmlAttributeProperty("Length")
	public StringProperty lengthProperty = new SimpleStringProperty();

	@XmlAttributeProperty("BondOrder")
	public StringProperty bondOrderProperty = new SimpleStringProperty();

	@XmlAttributeProperty("FreeTorsion")
	public StringProperty freeTorsionProperty = new SimpleStringProperty();

}
