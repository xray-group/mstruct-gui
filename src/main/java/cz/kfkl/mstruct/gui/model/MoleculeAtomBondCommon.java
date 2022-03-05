package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MoleculeAtomBondCommon extends XmlLinkedModelElement {

	@XmlAttributeProperty("Atom1")
	public StringProperty atom1Property = new SimpleStringProperty();

	@XmlAttributeProperty("Atom2")
	public StringProperty atom2Property = new SimpleStringProperty();

	@XmlAttributeProperty("Delta")
	public StringProperty deltaProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Sigma")
	public StringProperty sigmaProperty = new SimpleStringProperty();

}
