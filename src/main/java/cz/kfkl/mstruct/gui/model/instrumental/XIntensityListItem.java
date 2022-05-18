package cz.kfkl.mstruct.gui.model.instrumental;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class XIntensityListItem {

	public DoubleProperty x = new SimpleDoubleProperty();
	public DoubleProperty y = new SimpleDoubleProperty();
	public BooleanProperty refinedProperty = new SimpleBooleanProperty(false);

}
