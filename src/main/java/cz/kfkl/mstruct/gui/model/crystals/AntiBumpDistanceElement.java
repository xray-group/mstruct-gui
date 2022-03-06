package cz.kfkl.mstruct.gui.model.crystals;

import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("AntiBumpDistance")
public class AntiBumpDistanceElement extends ScattPowerMatrixComon {

	@XmlAttributeProperty("AllowMerge")
	public StringProperty allowMergeProperty = new SimpleStringProperty();

}
