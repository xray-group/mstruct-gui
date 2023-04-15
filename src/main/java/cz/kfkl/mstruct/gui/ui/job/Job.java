package cz.kfkl.mstruct.gui.ui.job;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import cz.kfkl.mstruct.gui.core.AppContext;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a task, usually run in a separate thread which has a name, status
 * (see {@link JobStatus} and a log ({@link #jobLogsStringBuilder}) where the
 * status changes and other messages can be logged.
 */
public abstract class Job {

	private static final String ERROR_PREFIX = "Error: ";

	private static final DateTimeFormatter JOB_LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	private String name;

	private ObjectProperty<JobStatus> statusProperty = new SimpleObjectProperty<>(JobStatus.Starting);
	private volatile boolean failed = false;

	private StringBuilder jobLogsStringBuilder;

	private AppContext appContext;

	public Job(AppContext context) {
		super();
		this.appContext = context;
		this.jobLogsStringBuilder = new StringBuilder();
	}

	public String getName() {
		return name;
	}

	protected AppContext getAppContext() {
		return appContext;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void failed(String message, Object... args) {
		failed = true;
		appendJobLogLine(ERROR_PREFIX + message, args);
	}

	public void failedLater(String message, Object... args) {
		LocalTime time = LocalTime.now();
		Platform.runLater(() -> {
			failed = true;
			appendJobLogLineWithTime(ERROR_PREFIX + message, time, args);
		});
	}

	public JobStatus getStatus() {
		return statusProperty.get();
	}

	public void setStatus(JobStatus status) {
		if (statusProperty.get() != status) {
			appendJobLogLine(status.toString());
		}

		this.statusProperty.set(status);
	}

	public ObjectProperty<JobStatus> getStatusProperty() {
		return statusProperty;
	}

	public void finishJob() {
		if (getStatus() == JobStatus.Terminating) {
			setStatus(JobStatus.Terminated);
		} else {
			if (failed) {
				setStatus(JobStatus.Failed);
			} else {
				setStatus(JobStatus.Finished);
			}
			jobFinishedAction();
		}
	}

	/**
	 * To be overwritten if needed.
	 * <p>
	 * It is called only if the job finished on its own, i.e. wasn't terminated.
	 */
	protected void jobFinishedAction() {
	}

	public void terminate() {
		if (this.getStatus().canTerminate()) {
			this.setStatus(JobStatus.Terminating);
			doTerminate();
		}
	}

	abstract protected void doTerminate();

	protected void appendJobLogText(String text) {
		jobLogsStringBuilder.append(text);
	}

	public String getJobLogsText() {
		return jobLogsStringBuilder.toString();
	}

	/**
	 * Shouldn't be called from other then Java FX thread
	 */
	public void appendJobLogLine(String line, Object... args) {
		appendJobLogLineWithTime(line, LocalTime.now(), args);
	}

	public void appendJobLogLineLater(String line, Object... args) {
		LocalTime time = LocalTime.now();
		Platform.runLater(() -> {
			appendJobLogLineWithTime(line, time, args);
		});
	}

	private void appendJobLogLineWithTime(String line, LocalTime time, Object... args) {
		appendJobLogText(time.format(JOB_LOG_TIME_FORMATTER) + "  ");
		if (args == null) {
			appendJobLogText(line);
		} else {
			appendJobLogText(String.format(line, args));
		}
		appendJobLogText("\n");
	}

	protected void jobDoneAction() {
	};

	@Override
	public String toString() {
		return getName();
	}

}
