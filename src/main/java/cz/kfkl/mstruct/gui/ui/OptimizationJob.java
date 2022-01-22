package cz.kfkl.mstruct.gui.ui;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles.RowIndex;
import cz.kfkl.mstruct.gui.ui.chart.PlotlyChartGenerator;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public abstract class OptimizationJob extends Job implements TextBuffer {
	private static final Logger LOG = LoggerFactory.getLogger(OptimizationJob.class);

	private static final String HKL_FILE_NAME = "phase1_par.txt";
	private static final String DAT_FILE_NAME = "pattern0_xml.dat";

	public static final String[] DATA_TABLE_COLUMNS = new String[] { "2Theta/TOF", "Iobs", "ICalc", "Iobs-Icalc", "Weight",
			"Comp0" };

	private static final String[] HKL_TABLE_COLUMNS = new String[] { "h", "k", "l", "2Theta", "|Fhkl|^2", "Ihkl", "FWHM(deg)",
			"B(deg)" };

	private File resultDir;
	private File inputFile;

	private StringBuilder consoleStringBuilder;
	private TextArea activeConsoleTextArea;
	private TextArea activeJobLogsTextArea;

	private ObjectProperty<TableOfDoubles> datTableProperty = new SimpleObjectProperty<>();
	private ObjectProperty<TableOfDoubles> hklTableProperty = new SimpleObjectProperty<>();
	private ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty = new SimpleObjectProperty<>();

	private Set<String> refinedParams;

	private boolean showConsole;
	private boolean keepOutput;

	private JobRunner jobRunner;

	public OptimizationJob(AppContext context) {
		super(context);
		this.consoleStringBuilder = new StringBuilder();
	}

	@Override
	public void appendText(String text) {
		consoleStringBuilder.append(text);
		if (activeConsoleTextArea != null) {
			activeConsoleTextArea.appendText(text);
		}
	}

	public void setActiveConsoleTextArea(TextArea textArea) {
		this.activeConsoleTextArea = textArea;
	}

	@Override
	protected void appendJobLogText(String text) {
		super.appendJobLogText(text);

		if (activeJobLogsTextArea != null) {
			activeJobLogsTextArea.appendText(text);
		}
	}

	public void setActiveJobLogsTextArea(TextArea textArea) {
		this.activeJobLogsTextArea = textArea;
	}

	public File getResultDir() {
		return resultDir;
	}

	public void setResultDir(File resultDir) {
		this.resultDir = resultDir;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	@Override
	public String toString() {
		return getName() + " (" + getStatus() + ")";
	}

	public String getConsoleText() {
		return consoleStringBuilder.toString();
	}

	public boolean isShowConsole() {
		return showConsole;
	}

	public void setShowConsole(boolean showConsole) {
		this.showConsole = showConsole;
	}

	public boolean isKeepOutput() {
		return keepOutput;
	}

	public void setKeepOutput(boolean keepOutput) {
		this.keepOutput = keepOutput;
	}

	/**
	 * Is called from a separate, non FX thread.
	 */
	protected void processDatFile() {
		File outDatFile = new File(resultDir, DAT_FILE_NAME);
		if (outDatFile.exists()) {
			try {
				TabularDataParser parser = new TabularDataParser();
				TableOfDoubles tabularData = parser.parse(outDatFile);
				Platform.runLater(() -> datFileParsed(tabularData));
			} catch (Exception e) {
				this.appendJobLogLineLater("Exception parsing output dat file [%s]: %s", outDatFile,
						e.getStackTrace().toString());
			}

		} else {
			this.failedLater("The output dat file [%s] doesn't exist, full path: %s", outDatFile.getName(), outDatFile);
		}
	}

	private void datFileParsed(TableOfDoubles tabularData) {
		this.datTableProperty.set(tabularData);
		appendJobLogLine("Output data was parsed from [%s], rows count: %s", DAT_FILE_NAME, tabularData.getRows().size());
	}

	/**
	 * Is called from a separate, non FX thread.
	 */
	protected void processHklFile() {
		File outDatFile = new File(resultDir, HKL_FILE_NAME);
		if (outDatFile.exists()) {
			try {
				TabularDataParser parser = new TabularDataParser();
				TableOfDoubles tabularData = parser.parse(outDatFile);
				Platform.runLater(() -> this.hklFileParsed(tabularData));
			} catch (Exception e) {
				this.appendJobLogLineLater("Exception parsing output hkl file [%s]: %s", outDatFile,
						e.getStackTrace().toString());
			}

		} else {
			this.failedLater("The output hkl file [%s] doesn't exist, full path: %s", outDatFile.getName(), outDatFile);
		}
	}

	private void hklFileParsed(TableOfDoubles tabularData) {
		this.hklTableProperty.set(tabularData);
		appendJobLogLine("HKL data was parsed from [%s], rows count: %s", HKL_FILE_NAME, tabularData.getRows().size());
	}

	protected void processXmlOutFile() {
		File outXmlFile = new File(resultDir, "xray_out.xml");

		LOG.debug("Processing XML output file [{}]", outXmlFile);
		if (outXmlFile.exists()) {
			try {

				SAXBuilder builder = new SAXBuilder();
				Document openedDocument = builder.build(outXmlFile);
				Element root = openedDocument.getRootElement();
				Validator.validateEquals(MStructGuiController.OBJ_CRYST, root.getName(),
						"Expected XML file with root element [%s], got [%s]", MStructGuiController.OBJ_CRYST, root.getName());

				ObjCrystModel rootModel = new ObjCrystModel();
				rootModel.bindToElement(null, root);

				Map<String, ParUniqueElement> map = ParametersController.createParamsLookup(rootModel);
				LOG.debug("XML output params loaded [{}]", map.size());

				Platform.runLater(() -> this.fittedParamsProperty.set(map));
			} catch (Exception e) {
				this.appendJobLogLineLater("Exception parsing output xml file [%s]: %s", outXmlFile,
						e.getStackTrace().toString());
			}

		} else {
			this.failedLater("The output xml file [%s] doesn't exist, full path: %s", outXmlFile.getName(), outXmlFile);
		}
	}

	public ObjectProperty<TableOfDoubles> getTabularDataProperty() {
		return datTableProperty;
	}

	public void updateTabs(MStructGuiController mainController, OptimizationController optimizationController) {

		updateFittedParamsTableTab(mainController, optimizationController.fittedParamsTab);
		updateDataTableTab(optimizationController);
		updateHklTableTab(optimizationController.outputHklTableView);
		updateChartTab(optimizationController.chartTabTitledPane);
	}

	private void updateFittedParamsTableTab(MStructGuiController mainController, Tab fittedParamsTab) {

		ParametersController parametersTabController = mainController.initParametersTab(fittedParamsTab, fittedParamsProperty,
				refinedParams);
		parametersTabController.showFittedOptions();
		BindingUtils.doWhenPropertySet(t -> parametersTabController.refreshTable(), fittedParamsProperty);
	}

	private void updateDataTableTab(OptimizationController optimizationController) {
		optimizationController.datRowsCountLabel.setText("N/A");

		BindingUtils.initTableView(optimizationController.outputDataTableView, DATA_TABLE_COLUMNS);

		BindingUtils.doWhenPropertySet(t -> optimizationController.outputDataTableView.getItems().addAll(t.getRowIndexes()),
				datTableProperty);
	}

	private void updateHklTableTab(TableView<RowIndex> dataTableView) {

		BindingUtils.initTableView(dataTableView, HKL_TABLE_COLUMNS);

		BindingUtils.doWhenPropertySet(t -> dataTableView.getItems().addAll(t.getRowIndexes()), hklTableProperty);
	}

	private void updateChartTab(BorderPane pane) {

		BindingUtils.doWhenPropertySet(t -> {
			PlotlyChartGenerator chartGenerator = new PlotlyChartGenerator(getContext());
			chartGenerator.forJob(this);
			chartGenerator.useGuiTemplate();

			String errMessage = chartGenerator.nothingToExportMessage();
			if (errMessage == null) {

				WebView webView = new WebView();
				WebEngine webEngine = webView.getEngine();
				webEngine.loadContent(chartGenerator.exportedData());
				webView.setMaxHeight(Double.POSITIVE_INFINITY);

				pane.setCenter(webView);

				if (LOG.isTraceEnabled()) {
					ChangeListener<Number> sizeChangeListener = (observable, oldValue, newValue) -> {
						LOG.trace("WebView height: {}, width: {}", webView.getHeight(), webView.getWidth());
					};

					webView.widthProperty().addListener(sizeChangeListener);
					webView.heightProperty().addListener(sizeChangeListener);
				}
			} else {
				pane.setCenter(new Text(errMessage));
			}

		}, datTableProperty);
	}

	public ObjectProperty<TableOfDoubles> getDatTableProperty() {
		return datTableProperty;
	}

	public ObjectProperty<TableOfDoubles> getHklTableProperty() {
		return hklTableProperty;
	}

	abstract List<String> getCommandList();

	public void startJob() {
		this.jobRunner = new JobRunner(this);
		jobRunner.setCommandList(getCommandList());
		jobRunner.setWorkingDir(this.getResultDir());
		jobRunner.setRemoveWorkingDirectory(!this.isKeepOutput());

		if (this.isShowConsole()) {
			jobRunner.logProcessToConsole(this);
		}

		jobRunner.start();
	}

	@Override
	protected void jobDoneAction() {
		this.processXmlOutFile();
		this.processHklFile();
		this.processDatFile();
	}

	@Override
	protected void doTerminate() {
		jobRunner.terminate();
	}

	public void setRefinedParams(Set<String> refinedParams) {
		this.refinedParams = refinedParams;
	}

	public int getRefinedParamsCount() {
		return refinedParams.size();
	}

}
