package cz.kfkl.mstruct.gui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.kfkl.mstruct.gui.ui.PowderPatternController;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementList;
import cz.kfkl.mstruct.gui.xml.annotation.XmlElementName;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlElementName("PowderPattern")
public class PowderPatternElement extends DiffractionModel<PowderPatternController> {

	private static final String FXML_FILE_NAME = "powderPattern.fxml";

	@XmlAttributeProperty("SpaceGroup")
	public StringProperty spaceGroupProperty = new SimpleStringProperty();

//    <Par Refined="0" Limited="0" Min="-2.86479" Max="2.86479" Name="Zero">0</Par>
//    <Par Refined="0" Limited="1" Min="-2.86479" Max="2.86479" Name="2ThetaDisplacement">0</Par>
//    <Par Refined="0" Limited="1" Min="-2.86479" Max="2.86479" Name="2ThetaTransparency">0</Par>
//    <Option Name="Use Integrated Profiles" Choice="1" ChoiceName="No"/>
	@XmlUniqueElement
	public ParUniqueElement zeroPar = new ParUniqueElement("Zero");
	@XmlUniqueElement
	public ParUniqueElement thetaDisplacementPar = new ParUniqueElement("2ThetaDisplacement");
	@XmlUniqueElement
	public ParUniqueElement thetaTransparencyPar = new ParUniqueElement("2ThetaTransparency");

	@XmlUniqueElement
	public OptionUniqueElement useIntegratedProfilesOption = new OptionUniqueElement("Use Integrated Profiles", 1,
			new OptionChoice("Yes (recommended)"), new OptionChoice("No"));

	@XmlUniqueElement
	public RadiationElement radiationElement = new RadiationElement();

	@XmlUniqueElement("MaxSinThetaOvLambda")
	public SingleValueUniqueElement maxSinThetaOvLambdaElement = new SingleValueUniqueElement("0");

	// is it the incidence angle (deg)-2Theta scan, negative value-2Theta/Theta scan
	// ?
	@XmlUniqueElement
	public GeometryElement geometryElement = new GeometryElement();

	@XmlElementList
	public ObservableList<PowderPatternBackgroundModel> powderPatternComponents = FXCollections.observableArrayList();

	@XmlElementList
	public List<PowderPatternCrystalsModel> powderPatternCrystals = new ArrayList<>();

	@Override
	public List<ParUniqueElement> getParams() {
		return List.of(zeroPar, thetaDisplacementPar, thetaTransparencyPar);
	}

	@Override
	public String formatParamContainerName() {
		return "Powder Pattern: " + getName();
	}

	@Override
	public List<? extends ParamContainer> getInnerContainers() {
		List<ParamContainer> list = new ArrayList<>();
		list.add(radiationElement);
		list.addAll(powderPatternComponents);
		list.addAll(powderPatternCrystals);

		return list;
	}

	@Override
	public String getName() {
		return nameProperty.get();
	}

	@Override
	public void setName(String name) {
		this.nameProperty.set(name);

	}

	@Override
	public StringProperty getNameProperty() {
		return nameProperty;
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

}
