package cz.kfkl.mstruct.gui.ui;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.chart.JobOutputExporter;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;

public class CsvOutputDataExporter implements JobOutputExporter {

	private AppContext appContext;
	private Job job;
	private TableOfDoubles datTable;

	public CsvOutputDataExporter(AppContext appContext) {
		this.appContext = appContext;
	}

	@Override
	public void forJob(Job job) {
		this.job = job;
		if (job instanceof OptimizationJob) {
			OptimizationJob optJob = (OptimizationJob) job;
			this.datTable = optJob.getDatTableProperty().get();
		}
	}

	@Override
	public String nothingToExportMessage() {
		return datTableHasRows() ? null : "there are no data";
	}

	private boolean datTableHasRows() {
		return datTable != null && !datTable.getRows().isEmpty();
	}

	@Override
	public String exportedData() {
		StringBuilder sb = new StringBuilder();
		sb.append(JvStringUtils.join(",", OptimizationJob.DATA_TABLE_COLUMNS));
		sb.append("\n");

		for (double[] row : datTable.getRows()) {
			sb.append(JvStringUtils.join(",", row));
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public String getFileChooserTitle() {
		return "Export Output to CSV";
	}

	@Override
	public String getInitialFileName() {
		return job.getName() + ".csv";
	}

}
