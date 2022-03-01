package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Bond")
public class MoleculeAtomBondElement extends XmlLinkedModelElement {

	// <Bond Atom1="C2" Atom2="N3" Length="1.31501" Delta="0.02" Sigma="0.01"
	// BondOrder="0" FreeTorsion="0" />
	@XmlAttributeProperty("Atom1")
	public StringProperty atom1Property = new SimpleStringProperty();

	@XmlAttributeProperty("Atom2")
	public StringProperty atom2Property = new SimpleStringProperty();

	@XmlAttributeProperty("Length")
	public StringProperty lengthProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Delta")
	public StringProperty deltaProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Sigma")
	public StringProperty sigmaProperty = new SimpleStringProperty();

	@XmlAttributeProperty("BondOrder")
	public StringProperty bondOrderProperty = new SimpleStringProperty();

	@XmlAttributeProperty("FreeTorsion")
	public StringProperty freeTorsionProperty = new SimpleStringProperty();

}
