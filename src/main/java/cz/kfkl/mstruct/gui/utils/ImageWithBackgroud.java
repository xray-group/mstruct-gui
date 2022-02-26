package cz.kfkl.mstruct.gui.utils;

import javafx.scene.Node;
import javafx.scene.layout.Background;

public class ImageWithBackgroud {

	private Node graphic;
	private Background background;

	public ImageWithBackgroud(Node graphic, Background background) {
		super();
		this.graphic = graphic;
		this.background = background;
	}

	public Node getGraphic() {
		return graphic;
	}

	public Background getBackground() {
		return background;
	}

}
