package cz.kfkl.mstruct.gui.model.crystals;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.newChanged;

import org.jdom2.Element;

import cz.kfkl.mstruct.gui.model.FxmlFileNameProvider;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParamContainer;
import cz.kfkl.mstruct.gui.model.SingleValueUniqueElement;
import cz.kfkl.mstruct.gui.model.utils.XmlLinkedModelElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.ui.images.FramedRectangle;
import cz.kfkl.mstruct.gui.utils.ImageWithBackgroud;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.xml.annotation.XmlAttributeProperty;
import cz.kfkl.mstruct.gui.xml.annotation.XmlMappedSubclasses;
import cz.kfkl.mstruct.gui.xml.annotation.XmlUniqueElement;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

@XmlMappedSubclasses({ ScatteringPowerAtomElement.class, ScatteringPowerSphereElement.class })
public abstract class ScatteringPowerModel<C extends BaseController<?, ?>> extends XmlLinkedModelElement
		implements ParamContainer, FxmlFileNameProvider<C> {

	@XmlAttributeProperty("Name")
	public StringProperty nameProperty = new SimpleStringProperty();

	@XmlUniqueElement("RGBColour")
	public SingleValueUniqueElement colourElement = new SingleValueUniqueElement("1 0 0");

	@XmlUniqueElement
	public ParUniqueElement bisoPar = new ParUniqueElement("Biso");

	public Property<Color> colorProperty;

	@Override
	public void bindToElement(XmlLinkedModelElement parentModelElement, Element wrappedElement) {
		super.bindToElement(parentModelElement, wrappedElement);

		colorProperty = new SimpleObjectProperty<>(colorFromColourStr());
		colorProperty.addListener(newChanged(v -> this.colorToColourStr(v)));
	}

	@Override
	public String formatParamContainerName() {
		return "Scattering Power " + getType() + ": " + getName();
	}

	public String getName() {
		return nameProperty.get();
	}

	public void setName(String name) {
		this.nameProperty.set(name);
	}

	public String getColour() {
		return colourElement.valueProperty.get();
	}

	public void setColour(String colour) {
		this.colourElement.valueProperty.set(colour);
	}

	public abstract String getType();

	abstract public Image getIcon();

	public ImageWithBackgroud getTypeGraphics() {
		HBox graphic = new HBox(3, new FramedRectangle(9, 9, colorProperty), new ImageView(getIcon()));
		return new ImageWithBackgroud(graphic, null);
	}

	private Color colorFromColourStr() {
		String rgbStr = colourElement.valueProperty.get();
		String[] split = rgbStr.split(" ");

		double red = parseSplit(split, 0);
		double green = parseSplit(split, 1);
		double blue = parseSplit(split, 2);

		return Color.color(red, green, blue);
	}

	private double parseSplit(String[] split, int index) {
		Double val = null;
		if (split.length > index) {
			String strVal = split[index];
			val = JvStringUtils.parseDoubleSilently(strVal);
		}

		return val == null ? 0 : val;
	}

	private void colorToColourStr(Color color) {
		this.colourElement.valueProperty.set(JvStringUtils.join(" ", JvStringUtils.toStringNoDotZero(color.getRed()),
				JvStringUtils.toStringNoDotZero(color.getGreen()), JvStringUtils.toStringNoDotZero(color.getBlue())));
	}

}
