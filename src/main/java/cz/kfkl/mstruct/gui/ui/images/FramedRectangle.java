package cz.kfkl.mstruct.gui.ui.images;

import javafx.beans.property.Property;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * StackPane representing a rectangle with a given coler (given by painProperty)
 * framed with a gray border.
 */
public class FramedRectangle extends StackPane {

	private static final int DEFAULT_BORDER_WIDTH = 1;
	private static final double DEFAULT_GREY_LEVEL = 0.8;

	public FramedRectangle(int width, int height, Property<Color> paint) {
		this(width, height, paint, DEFAULT_BORDER_WIDTH, DEFAULT_GREY_LEVEL);
	}

	public FramedRectangle(int width, int height, Property<Color> paint, double border, double grey) {
		super();
		Rectangle rectBorder = new Rectangle(width + 2 * border, height + 2 * border, createGreaColour(grey));
		Rectangle rectInner = new Rectangle(width, height);
		rectInner.fillProperty().bind(paint);

		getChildren().addAll(rectBorder, rectInner);

		this.prefHeight(height + 2 * border);
		this.prefWidth(width + 2 * border);
	}

	private Color createGreaColour(double grey) {
		return new Color(grey, grey, grey, 1.0);
	}

}
