package cz.kfkl.mstruct.gui.ui;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.utils.validation.ConfirmationUtils;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MStructGuiMain extends Application {

	private static final Logger LOG = LoggerFactory.getLogger(MStructGuiMain.class);

	public static final String M_STRUCT_UI_TITLE = "MStruct GUI";
	private AppContext appContext = new AppContext();

	@Override
	public void start(Stage primaryStage) {
		try {

			appContext.init();

//			Scene scene = new Scene(loadFXML("mStructGui.fxml"), 880, 740);
			Scene scene = new Scene(loadFXML("mStructGui_testNoMenu.fxml"), 880, 740);
//			Scene scene = new Scene(loadFXML("mStructGui_testNoMenu_withIcons.fxml"), 880, 740);

			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle(M_STRUCT_UI_TITLE + " - starting");

			UncaughtExceptionHandler generalExceptionHandler = new PoupupErrorExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(generalExceptionHandler);

			MStructGuiController controller = appContext.getMainController();

			controller.setTitleProperty(primaryStage.titleProperty());
			controller.init();

			loadTestFileOnStartup(controller);

			primaryStage.setScene(scene);
			LOG.trace("scene set");

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					ConfirmationUtils.confirmAction(appContext.confirmFileClose(),
							"Are you sure you want to exit the application? All not saved changes will be lost.",
							() -> System.exit(0));
					event.consume();
				}
			});

			primaryStage.show();
			LOG.trace("show finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadTestFileOnStartup(MStructGuiController mc) {
		File autoOpenFile = appContext.getAutoOpenFile();

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
			((HasAppContext) controller).setAppContext(appContext);
		}

		if (controller instanceof MStructGuiController) {
			appContext.setMainController((MStructGuiController) controller);
		} else {
			throw new UnexpectedException(
					"The file [%s] should be configured with controller of type MStructGuiController, got [%s]", fxml, controller);
		}

		return Parent;
	}

}
