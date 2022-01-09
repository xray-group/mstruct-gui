package cz.kfkl.mstruct.gui.ui;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MStructGuiMain extends Application {

	private static final Logger LOG = LoggerFactory.getLogger(MStructGuiMain.class);

	public static final String M_STRUCT_UI_TITLE = "MStruct GUI";
	private AppContext context = new AppContext();

	@Override
	public void start(Stage primaryStage) {
		try {

			context.init();

			Scene scene = new Scene(loadFXML("mStructGui.fxml"), 880, 740);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle(M_STRUCT_UI_TITLE + " - starting");

			UncaughtExceptionHandler generalExceptionHandler = new PoupupErrorExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(generalExceptionHandler);

			MStructGuiController controller = context.getMainController();

			controller.setTitleProperty(primaryStage.titleProperty());
			controller.init();

			loadTestFileOnStartup(controller);

			primaryStage.setScene(scene);
			LOG.trace("scene set");

			primaryStage.show();
			LOG.trace("show finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadTestFileOnStartup(MStructGuiController mc) {
		File autoOpenFile = context.getAutoOpenFile();

		if (autoOpenFile != null) {
			try {
				mc.openSelectedFile(autoOpenFile);
			} catch (Exception e) {
				LOG.error("Failed auto open file [{}], exception: ", e, PoupupErrorExceptionHandler.extractStacktraceMessages(e));
				LOG.debug("Failed auto open file.", e);
			}
		}
	}

	public static void main(String[] args) {
		LOG.debug("Java version: {}", System.getProperty("java.version"));
		LOG.debug("Java home:", System.getProperty("java.home"));

		try {
			launch(args);
			LOG.trace("launch finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Parent loadFXML(String fxml) throws IOException {
		URL fxmlUrl = MStructGuiMain.class.getResource(fxml);
		LOG.debug("Loading main FXML from URL [{}]", fxmlUrl);

		FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
		Parent Parent = fxmlLoader.load();
		Object controller = fxmlLoader.getController();

		if (controller instanceof HasAppContext) {
			((HasAppContext) controller).setAppContext(context);
		}

		if (controller instanceof MStructGuiController) {
			context.setMainController((MStructGuiController) controller);
		} else {
			throw new UnexpectedException(
					"The file [%s] should be configured with context of type MStructGuiController, got [%s]", fxml, controller);
		}

		return Parent;
	}

}
