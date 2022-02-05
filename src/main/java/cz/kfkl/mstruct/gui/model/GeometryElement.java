package cz.kfkl.mstruct.gui.model;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Geometry")
public class GeometryElement extends XmlLinkedModelElement {
	public static final Double OMEGA_BB_CONSTANT = -1d;
	public static final String OMEGA_BB_CONSTANT_STR = "-1";

	public static final Double OMEGA_BBVS_CONSTANT = -2d;
	public static final String OMEGA_BBVS_CONSTANT_STR = "-2";

	public String lastPbOmega = "0.5";

	@XmlAttributeProperty("Omega")
	public StringProperty omegaProperty = new SimpleStringProperty();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE;
	}

	public Double decodeOmega() {
		Double decoded = null;

		Double omegaD = JvStringUtils.parseDouble(omegaProperty);

		if (omegaD != null) {
			if (omegaD < OMEGA_BB_CONSTANT) {
				decoded = OMEGA_BBVS_CONSTANT;
			} else if (omegaD < 0) {
				decoded = OMEGA_BB_CONSTANT;
			} else {
				decoded = omegaD;
			}
		}

		return decoded;
	}

}
