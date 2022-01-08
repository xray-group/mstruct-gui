package cz.kfkl.mstruct.gui.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.utils.validation.UserMessageException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PoupupErrorExceptionHandler implements UncaughtExceptionHandler {

	private static final int MAX_EXCEPTION_STACKTRACE_LEVEL = 7;
	private static final Logger LOG = LoggerFactory.getLogger(PoupupErrorExceptionHandler.class);

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		LOG.error("Error in thread [" + t + "]", e);
//		System.out.println("Thread " + t + ", Exception " + e);
//		e.printStackTrace();

		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR, extractStacktraceMessages(e));
			String headerText = null;
			if (e instanceof UserMessageException) {
				headerText = ((UserMessageException) e).getHeaderText();
			} else {
				headerText = UserMessageException.DEFAULT_UNEXPECTED_EXCEPTION_TITLE;
			}
			alert.setHeaderText(headerText);
			alert.showAndWait();
		});

	}

	public static String extractStacktraceMessages(Throwable e) {
		return extractStacktraceMessages(e, MAX_EXCEPTION_STACKTRACE_LEVEL);
	}

	/**
	 * Extracts exception messages from all exceptions found in the stacktrace of
	 * the passed in exception. Duplicates messages are omitted.
	 */
	public static String extractStacktraceMessages(Throwable e, int maxExceptions) {
		StringBuilder messagesSb = new StringBuilder();

		String lastMessage = "";
		Throwable parentException = e;

		while (parentException != null && maxExceptions >= 0) {
			String newMessage = parentException.getMessage();
			if (newMessage != null && !lastMessage.contains(newMessage)) {
				messagesSb.append("\n");

				if (!(e instanceof UserMessageException)) {
					messagesSb.append(parentException.getClass().getSimpleName());
					messagesSb.append(": ");
				}

				messagesSb.append(newMessage);

				lastMessage = newMessage;

				maxExceptions--;
			}

			parentException = parentException.getCause();
		}

		return messagesSb.toString();
	}

}
