package cz.kfkl.mstruct.gui.ui.job;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;

public class JobRunner {

	private Job job;
	private TextBuffer consoleTextBuffer;
	private boolean removeWorkingDirectory;

	private File workingDir;

	private List<String> commandList;

	private Process process;
	private CompletableFuture<Object> processCompletitionFuture;

	public JobRunner(Job job) {
		this.job = job;
	}

	public void start() {
		ProcessBuilder builder = new ProcessBuilder(commandList);

		builder.directory(workingDir);
		builder.redirectErrorStream(true);

		try {
			Process process = builder.start();

			this.setProcess(process);
			job.setStatus(JobStatus.Started);
			this.processCompletitionFuture = process.onExit().thenApply(p1 -> {

				job.jobDoneAction();

				if (removeWorkingDirectory) {
					job.appendJobLogLineLater("Removing output directory [%s]", workingDir);

					deleteOutputDirectory(workingDir);
				}
				Platform.runLater(() -> {
					job.finishJob();
				});
				return p1;
			});

			if (consoleTextBuffer != null) {
				Thread t = new Thread(new InputStreamToTextBufferRunnable(process, process.getInputStream(), consoleTextBuffer));
				t.start();
			}

		} catch (IOException e) {
			job.failed("The process [%s] failed to start. Exception [%s]", job.getName(), e);
			job.setStatus(JobStatus.Failed);
//				job.appendJobLogLine(e.getStackTrace().toString());
//				throw new UnexpectedException(e); //TODO consider...
		}
	}

	public void waitForRes(int timeoutSeconds) throws InterruptedException {
		try {
			processCompletitionFuture.get(timeoutSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			job.terminate();
		} catch (ExecutionException e) {
			job.failed("Job [%s] failed with exception: %s", job.getName(), e);
		}
	}

	private void deleteOutputDirectory(File resultDir) {
		for (File f : resultDir.listFiles()) {
			f.delete();
		}
		resultDir.delete();
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void logProcessToConsole(TextBuffer consoleTextBuffer) {
		this.consoleTextBuffer = consoleTextBuffer;
	}

	public void setRemoveWorkingDirectory(boolean removeWorkingDirectory) {
		this.removeWorkingDirectory = removeWorkingDirectory;
	}

	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
	}

	public void setCommandList(List<String> commandList) {
		this.commandList = commandList;
	}

	public List<String> getCommandList() {
		return commandList;
	}

	public void terminate() {
		this.getProcess().destroyForcibly();

	}

}
