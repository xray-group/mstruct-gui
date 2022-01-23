package cz.kfkl.mstruct.gui.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.XmlIndentingStyle;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlElementName("Geometry")
public class GeometryElement extends XmlLinkedModelElement {
	private static final Logger LOG = LoggerFactory.getLogger(GeometryElement.class);

	public static final Double OMEGA_BB_CONSTANT = -1d;
	public static final Double OMEGA_BBVS_CONSTANT = -2d;
	public static final Double DEFAULT_POSITIVE_OMEGA = 0.5;

	@XmlAttributeProperty("Omega")
	public StringProperty omegaProperty = new SimpleStringProperty();

	@Override
	public XmlIndentingStyle getXmlIndentingStyle() {
		return XmlIndentingStyle.INLINE;
	}

	public Double decodeOmega() {
		Double decoded = null;

		Double omegaD = parseIfPossible();

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

	private Double parseIfPossible() {
		Double omegaD = null;
		String omegaStr = omegaProperty.get();
		if (JvStringUtils.isNotBlank(omegaStr)) {
			try {
				omegaD = Double.valueOf(omegaStr);
			} catch (Exception e) {
				LOG.debug("Failed to parse [{}] into a double.", omegaStr);
				// TODO report as validation error ?
			}
		}
		return omegaD;
	}

}
