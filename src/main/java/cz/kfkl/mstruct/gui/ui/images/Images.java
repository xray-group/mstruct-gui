package cz.kfkl.mstruct.gui.ui.images;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotBlank;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class Images {

	private static final String IMAGES_URL_BASE = "/cz/kfkl/mstruct/gui/ui/images/";

	private static final Images INSTANCE = new Images();

	private Map<String, Image> images;

	private Images() {
		images = new HashMap<>();
	}

	public static Images getInstance() {
		return INSTANCE;
	}

	public static Image get(String fileName) {
		assertNotBlank(fileName, "Image file name cannot be blank.");
		Image image = getInstance().images.get(fileName);
		if (image == null) {
			image = new Image(Images.class.getResourceAsStream(IMAGES_URL_BASE + fileName));
			getInstance().images.put(fileName, image);
		}
		assertNotNull(image, "No image found for file name [%s]", fileName);
		return image;
	}
}
