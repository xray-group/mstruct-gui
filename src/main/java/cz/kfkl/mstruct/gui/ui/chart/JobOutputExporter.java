package cz.kfkl.mstruct.gui.ui.chart;

import cz.kfkl.mstruct.gui.ui.job.Job;

public interface JobOutputExporter {

	void forJob(Job job);

	String nothingToExportMessage();

	String exportedData();

	String getFileChooserTitle();

	String getInitialFileName();

}
