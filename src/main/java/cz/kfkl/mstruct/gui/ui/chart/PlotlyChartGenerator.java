package cz.kfkl.mstruct.gui.ui.chart;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import javafx.beans.property.ObjectProperty;

public class PlotlyChartGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(PlotlyChartGenerator.class);

	private static final String PLOTLY_JS_MIN = "${plotly_js_min}";

	private static final String HKL_LABELS = "${hkl_Labels}";
	private static Map<String, Integer> datPlaceholders = new LinkedHashMap<>();
	static {
		datPlaceholders.put("${dat_2Theta/TOF}", 0);
		datPlaceholders.put("${dat_Iobs}", 1);
		datPlaceholders.put("${dat_ICalc}", 2);
		datPlaceholders.put("${dat_Iobs-Icalc}", 3);
		datPlaceholders.put("${dat_Weight}", 4);
		datPlaceholders.put("${dat_Comp0}", 5);
	}
	private static Map<String, Integer> hklPlaceholders = new LinkedHashMap<>();
	static {
		hklPlaceholders.put("${hkl_2Theta}", 3);
		hklPlaceholders.put("${hkl_fhkl^2}", 4);
		hklPlaceholders.put("${hkl_Ihkl}", 5);
		hklPlaceholders.put("${hkl_FWHM}", 6);
		hklPlaceholders.put("${hkl_B}", 7);
	}

	private AppContext appContext;

	private String template;

	private TableOfDoubles datTable;
	private TableOfDoubles hklTable;

	public PlotlyChartGenerator(AppContext appContext, ObjectProperty<TableOfDoubles> datTableProperty,
			ObjectProperty<TableOfDoubles> hklTableProperty) {

		this.appContext = appContext;
		this.datTable = datTableProperty.get();
		this.hklTable = hklTableProperty.get();
	}

	public void useGuiTemplate() {
		template = appContext.loadGuiTemplate();
	}

	public void useExportTemplate() {
		template = appContext.loadExportTemplate();
	}

	public String nothingToExportMessage() {
		return template == null ? "no HTML template defined" : (haveData() ? null : "there are no data");
	}

	private boolean haveData() {
		return hasRows(datTable) || hasRows(hklTable);
	}

	private boolean hasRows(TableOfDoubles table) {
		return table != null && !table.getRows().isEmpty();
	}

	public String generateHtml() {
		String res = template;
		res = replacePlaceholders(res, datTable, datPlaceholders);
		res = replacePlaceholders(res, hklTable, hklPlaceholders);

		res = res.replace(HKL_LABELS,
				hklTable == null ? "" : JvStringUtils.joinApostrophed(List.of(hklTable.getColumnsDataJoined("  ", 0, 1, 2))));

		res = res.replace(PLOTLY_JS_MIN, loadPlotlyJS());

		return res;
	}

	private CharSequence loadPlotlyJS() {
		return appContext.loadPlotlyJs();
	}

	private String replacePlaceholders(String res, TableOfDoubles dt, Map<String, Integer> placeholders) {
		for (Entry<String, Integer> entry : placeholders.entrySet()) {
			res = res.replace(entry.getKey(), dt == null ? "" : JvStringUtils.joinCsv(dt.getColumnData(entry.getValue())));
		}

		return res;
	}

}
