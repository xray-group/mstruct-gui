package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Atom")
public class MoleculeAtomElement extends XmlLinkedModelElement {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlAttributeProperty("ScattPow")
	public StringProperty scattPowProperty = new SimpleStringProperty();

	@XmlAttributeProperty("x")
	public StringProperty xProperty = new SimpleStringProperty();

	@XmlAttributeProperty("y")
	public StringProperty yProperty = new SimpleStringProperty();

	@XmlAttributeProperty("z")
	public StringProperty zProperty = new SimpleStringProperty();

	@XmlAttributeProperty("Occup")
	public StringProperty occupProperty = new SimpleStringProperty();

	@XmlAttributeProperty("NonFlip")
	public StringProperty nonFlipProperty = new SimpleStringProperty();

}
