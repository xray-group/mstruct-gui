package cz.kfkl.mstruct.gui.ui.job;

import cz.kfkl.mstruct.gui.ui.images.Images;
import javafx.scene.image.Image;

public enum JobStatus {

	Starting("boxLoading_3.gif", true), Started("boxLoading_3.gif", true), Finished("accept.png"), Failed("errorTriangle.png"),
	Terminating("boxLoading_3.gif"), Terminated("stop.png");

	private Image image;
	private boolean canTerminate;

	JobStatus(String imageFileName) {
		this(imageFileName, false);
	}

	JobStatus(String imageFileName, boolean canTerminate) {
		this.image = Images.get(imageFileName);
		this.canTerminate = canTerminate;
	}

	public Image getImage() {
		return image;
	}

	boolean canTerminate() {
		return canTerminate;
	}

}
