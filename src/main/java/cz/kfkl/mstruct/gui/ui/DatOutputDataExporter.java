package cz.kfkl.mstruct.gui.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.chart.JobOutputExporter;
import cz.kfkl.mstruct.gui.utils.ic.TableReportBuilder;

public class DatOutputDataExporter implements JobOutputExporter {

	private AppContext appContext;
	private Job job;
	private TableOfDoubles datTable;

	public DatOutputDataExporter(AppContext appContext) {
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
		sb.append("#    2Theta/TOF Iobs       ICalc   Iobs-Icalc    Weight  Comp0\n");

		DecimalFormat df8 = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df8.setMinimumFractionDigits(8);
		df8.setMinimumFractionDigits(8);

		TableReportBuilder trb = TableReportBuilder.create(17, 17, 17, 17, 17);
		for (double[] row : datTable.getRows()) {
			Object[] values = new String[row.length];
			for (int i = 0; i < row.length; i++) {
				String str = df8.format(row[i]);
				if (str.length() > 16) {
					str = String.valueOf(row[i]);
				}
				values[i] = str;
			}
			trb.appendLine(values);

		}
		sb.append(trb.format());
		return sb.toString();
	}

	@Override
	public String getFileChooserTitle() {
		return "Export Output to DAT";
	}

	@Override
	public String getInitialFileName() {
		return job.getName() + ".dat";
	}

}
