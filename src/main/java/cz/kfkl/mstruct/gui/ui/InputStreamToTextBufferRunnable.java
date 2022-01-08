package cz.kfkl.mstruct.gui.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;
import javafx.application.Platform;

public class InputStreamToTextBufferRunnable implements Runnable {

	private Process process;
	private InputStream inputStream;
	private TextBuffer textBuffer;

	public InputStreamToTextBufferRunnable(Process process, InputStream inputStream, TextBuffer textBuffer) {
		super();
		this.process = process;
		this.inputStream = inputStream;
		this.textBuffer = textBuffer;

	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(inputStream))) {

			while (process.isAlive()) {
				readReadyChars(reader);
				try {
					process.waitFor(500, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			readReadyChars(reader);

		} catch (IOException e) {
			throw new UnexpectedException("Exception when updating", e);
		}

	}

	private void readReadyChars(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (reader.ready()) {
			int read = reader.read();
			sb.append((char) read);
		}

		Platform.runLater(() -> {
			textBuffer.appendText(sb.toString());
		});
	}

}
