package cz.kfkl.mstruct.gui.ui.optimization;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenPropertySet;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.initTableView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.PlotlyChartModel;
import cz.kfkl.mstruct.gui.model.instrumental.ExcludeXElement;
import cz.kfkl.mstruct.gui.model.phases.IhklParElement;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternCrystalsModel;
import cz.kfkl.mstruct.gui.ui.MStructGuiController;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.ui.ParametersController;
import cz.kfkl.mstruct.gui.ui.PoupupErrorExceptionHandler;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles.RowIndex;
import cz.kfkl.mstruct.gui.ui.TabularDataParser;
import cz.kfkl.mstruct.gui.ui.chart.PlotlyChartGenerator;
import cz.kfkl.mstruct.gui.ui.job.Job;
import cz.kfkl.mstruct.gui.ui.job.JobRunner;
import cz.kfkl.mstruct.gui.ui.job.TextBuffer;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public abstract class OptimizationJob extends Job implements TextBuffer {
	private static final Logger LOG = LoggerFactory.getLogger(OptimizationJob.class);

	private static final String HKL_FILE_NAME = "phase1_par.txt";
	private static final String DAT_FILE_NAME = "pattern0_xml.dat";

	public static final String[] DATA_TABLE_COLUMNS = new String[] { "2Theta/TOF", "Iobs", "ICalc", "Iobs-Icalc", "Weight",
			"Comp0" };

	private static final String[] HKL_TABLE_COLUMNS = new String[] { "h", "k", "l", "2Theta", "|Fhkl|^2", "Ihkl", "FWHM(deg)",
			"B(deg)" };

	private static Map<Pattern, BiConsumer<OptimizationJob, Matcher>> consolePatterns = new LinkedHashMap<>();

	// TODO make this configurable
	static {
		consolePatterns.put(Pattern.compile("Warning: (.*)"),
				(job, matcher) -> job.failed("Console Warning: %s", matcher.group(1)));
		consolePatterns.put(Pattern.compile("Rw-factor : ([\\d\\.]*)"), (job, matcher) -> job.rwFactor.set(matcher.group(1)));
		consolePatterns.put(Pattern.compile("GoF: ([\\d\\.]*)"), (job, matcher) -> job.goF.set(matcher.group(1)));
		consolePatterns.put(Pattern.compile("finished cycle #([\\d]*)/"), (job, matcher) -> {
			updateProgress(job, matcher.group(1));
		});
	}

	private String lastConsoleLine = "";

	private File resultDir;
	private File inputFile;

	private OptimizationController optimizationController;

	private SimpleStringProperty rwFactor = new SimpleStringProperty("N/A");
	private SimpleStringProperty goF = new SimpleStringProperty("N/A");

	private StringBuilder consoleStringBuilder;

	private ObjectProperty<TableOfDoubles> datTableProperty = new SimpleObjectProperty<>();
	private ObjectProperty<TableOfDoubles> hklTableProperty = new SimpleObjectProperty<>();
	private ObjectProperty<List<IhklParElement>> ihklTableProperty = new SimpleObjectProperty<>();
	private ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty = new SimpleObjectProperty<>();
	private List<ExcludeXElement> excludeRegions;
	// set only if exclude regions were edited
	private boolean excludeRegionsEdited;

	// map of params which were existing at the time the job has started, links to
	// the runtime model so the states are not preserved
	private Map<String, ParUniqueElement> refinedParams;
	// params which were refined at the time of starting the job
	private Set<ParUniqueElement> fittedParams;

	private Integer iterations;

	// TODO consider moving these two to a parent (newly created, the
	// jobFinishedAction method wouldn't be needed then
	private SimpleStringProperty progress = new SimpleStringProperty("0");

	private ObservableDoubleValue progressPercentage = Bindings.createDoubleBinding(() -> {
		Double progressDouble = JvStringUtils.parseDoubleSilently(progress.get());
		double progressPercent = 0;
		if (progressDouble != null) {
			progressPercent = iterations > 0 ? (progressDouble / iterations) : 1;
		}
		return progressPercent;
	}, progress);

	private boolean showConsole;
	private boolean keepOutput;

	private JobRunner jobRunner;

	private ObjCrystModel rootModel;

	public OptimizationJob(AppContext context, ObjCrystModel rootModel) {
		super(context);
		this.consoleStringBuilder = new StringBuilder();
		this.rootModel = rootModel;
	}

	@Override
	public void appendText(String text) {
		consoleStringBuilder.append(text);
		analyseConsole(text);
		if (optimizationController != null) {
			optimizationController.consoleTextArea.appendText(text);
		}
	}

	private void analyseConsole(String text) {

		for (String line : (lastConsoleLine + text).split("\n", -2)) {
			if (line.length() > 0) {
				for (Entry<Pattern, BiConsumer<OptimizationJob, Matcher>> patternEntry : consolePatterns.entrySet()) {
					Pattern pattern = patternEntry.getKey();
					Matcher matcher = pattern.matcher(line);
					while (matcher.find()) {
						BiConsumer<OptimizationJob, Matcher> consumer = patternEntry.getValue();
						consumer.accept(this, matcher);
					}
				}
			}

			// the console appender may continue writing to the last line...
			this.lastConsoleLine = line;
		}
	}

	private static void updateProgress(OptimizationJob job, String progressStr) {
		job.progress.set(progressStr);
		LOG.debug("Job [{}] parsed progress: {}", job, progressStr);
	}

	@Override
	protected void appendJobLogText(String text) {
		super.appendJobLogText(text);

		if (optimizationController != null) {
			optimizationController.jobsLogsTextArea.appendText(text);
		}
	}

	public void jobUnselected() {
		this.optimizationController = null;
		for (ParUniqueElement par : refinedParams.values()) {
			par.getFittedProperty().set(false);
			par.getFittedValueProperty().set("");
		}
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
				reportOutputFileParsingException(outDatFile, "dat", e);
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
		File outHklFile = new File(resultDir, HKL_FILE_NAME);
		if (outHklFile.exists()) {
			try {
				TabularDataParser parser = new TabularDataParser();
				TableOfDoubles tabularData = parser.parse(outHklFile);
				Platform.runLater(() -> this.hklFileParsed(tabularData));
			} catch (Exception e) {
				reportOutputFileParsingException(outHklFile, "hkl", e);
			}

		} else {
			this.failedLater("The output hkl file [%s] doesn't exist, full path: %s", outHklFile.getName(), outHklFile);
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
				// TODO JV: Hack the file so it can be processed:
				String outXmlContent = readString(outXmlFile);
				String outXmlContentFixed = outXmlContent.replaceAll("2Theta=", "twoTheta=");

				SAXBuilder builder = new SAXBuilder();
				Document openedDocument = builder.build(new ByteArrayInputStream(outXmlContentFixed.getBytes()));
				Element root = openedDocument.getRootElement();
				Validator.validateEquals(MStructGuiController.OBJ_CRYST, root.getName(),
						"Expected XML file with root element [%s], got [%s]", MStructGuiController.OBJ_CRYST, root.getName());

				ObjCrystModel fittedRootModel = new ObjCrystModel();
				fittedRootModel.bindToElement(null, root);

				Map<String, ParUniqueElement> map = ParametersController.createParamsLookup(fittedRootModel);
				LOG.debug("XML output params loaded [{}]", map.size());
				List<ExcludeXElement> excludeRegions = fittedRootModel.getExcludeRegions();

				List<IhklParElement> ihklParams = new ArrayList<>();
				for (PowderPatternCrystalsModel fittedPpc : fittedRootModel.getFirstPowderPattern().powderPatternCrystals) {
					fittedPpc.getName();
					ihklParams.addAll(fittedPpc.arbitraryTextureElement.ihklParams);
				}

				Platform.runLater(() -> {
					this.rootModel.updateIhklParams(fittedRootModel);

					this.fittedParamsProperty.set(map);
					this.excludeRegions = excludeRegions;
					this.ihklTableProperty.set(ihklParams);
				});
			} catch (Exception e) {
				reportOutputFileParsingException(outXmlFile, "xml", e);
			}

		} else {
			this.failedLater("The output xml file [%s] doesn't exist, full path: %s", outXmlFile.getName(), outXmlFile);
		}
	}

	private String readString(File outXmlFile) throws IOException {

		Path filePath = outXmlFile.toPath();
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		}
		return contentBuilder.toString();
	}

	private void reportOutputFileParsingException(File outXmlFile, String fileType, Exception e) {
		String stacktraceMessages = PoupupErrorExceptionHandler.extractStacktraceMessages(e);
		String shortMsg = "Exception parsing output " + fileType + " file";
		LOG.warn(shortMsg + " [{}]: {}", outXmlFile, stacktraceMessages);
		LOG.debug(shortMsg, e);
		this.failed(shortMsg + " [%s]: %s", outXmlFile, stacktraceMessages);
	}

	public void updateTabs(OptimizationController optimizationController) {
		this.optimizationController = optimizationController;

		updateOutputTab(optimizationController);
		updateFittedParamsTableTab(optimizationController);
		updateDataTableTab(optimizationController);
		updateIHklParamsTab(optimizationController);
		updateHklTableTab(optimizationController.outputHklTableView);
		updateChartTab(optimizationController);
	}

	private void updateOutputTab(OptimizationController optimizationController) {

		optimizationController.jobStatusLabel.textProperty().bind(this.getStatusProperty().asString());
		ObjectBinding<Image> imageBinding = Bindings.createObjectBinding(() -> this.getStatusProperty().get().getImage(),
				this.getStatusProperty());
		optimizationController.jobStatusImageView.imageProperty().bind(imageBinding);

		optimizationController.jobRwFactorLabel.textProperty().bind(rwFactor);
		optimizationController.jobGoFLabel.textProperty().bind(goF);
		optimizationController.jobProgressLabel.textProperty().bind(progress.concat(" / " + getIterations()));
		optimizationController.jobProgressBar.progressProperty().bind(progressPercentage);

		optimizationController.jobParamsRefinedLabel.textProperty().set(Integer.toString(this.getRefinedParamsCount()));

		optimizationController.consoleTextArea.setText(this.getConsoleText());
		optimizationController.jobsLogsTextArea.setText(this.getJobLogsText());
	}

	private void updateFittedParamsTableTab(OptimizationController optimizationController) {

		ParametersController parametersTabController = optimizationController.parametersTabController;
		if (parametersTabController != null) {
			parametersTabController.showFittedOptions(fittedParams);

			doWhenPropertySet(t -> {
				if (optimizationController != null) {
					LOG.debug("Job [{}] initializing param tab", this);

					Map<String, ParUniqueElement> map = fittedParamsProperty.get();
					for (Entry<String, ParUniqueElement> fittedPar : map.entrySet()) {
						ParUniqueElement par = refinedParams.get(fittedPar.getKey());
						if (par != null) {
							par.getFittedValueProperty().set(fittedPar.getValue().getValueProperty().get());
						} else {
							LOG.debug("Fitted parameter [{}] is not among refined map", fittedPar);
						}
					}
					parametersTabController.bindToParametersTree();
				}
			}, fittedParamsProperty);
		}
	}

	private void updateDataTableTab(OptimizationController optimizationController) {
		optimizationController.datRowsCountLabel.setText("N/A");

		initTableView(optimizationController.outputDataTableView, DATA_TABLE_COLUMNS);
		optimizationController.exportOutputCsvButton.setDisable(true);
		optimizationController.exportOutputDatButton.setDisable(true);

		doWhenPropertySet(t -> {
			optimizationController.datRowsCountLabel.setText(Integer.toString(t.getRows().size()));
			optimizationController.outputDataTableView.getItems().addAll(t.getRowIndexes());
			optimizationController.exportOutputCsvButton.setDisable(false);
			optimizationController.exportOutputDatButton.setDisable(false);
		}, datTableProperty);
	}

	private void updateHklTableTab(TableView<RowIndex> hklTableView) {

		initTableView(hklTableView, HKL_TABLE_COLUMNS);

		doWhenPropertySet(t -> hklTableView.getItems().addAll(t.getRowIndexes()), hklTableProperty);
	}

	private void updateIHklParamsTab(OptimizationController optimizationController) {
		TableView<IhklParElement> ihklParamsTableView = optimizationController.outputIhklParamsTableView;
		ihklParamsTableView.getItems().clear();
		doWhenPropertySet(t -> ihklParamsTableView.getItems().addAll(t), ihklTableProperty);
	}

	private void updateChartTab(OptimizationController optimizationController) {
		BorderPane chartTabTitledPane = optimizationController.chartTabTitledPane;
		chartTabTitledPane.setCenter(new Text("no data"));
		optimizationController.exportHtmlButton.setDisable(true);
		optimizationController.editExcludedRegionsButton.setDisable(true);

		doWhenPropertySet(t -> {
			chartTabTitledPane.setCenter(createChartNode(optimizationController.getCurrentExcludeRegions()));
			optimizationController.exportHtmlButton.setDisable(false);
			optimizationController.editExcludedRegionsButton.setDisable(false);

		}, datTableProperty);
	}

	public Node createChartNode(List<ExcludeXElement> currentExcludeRegions) {
		PlotlyChartGenerator chartGenerator = createChartGenerator(currentExcludeRegions);
		PlotlyChartModel optimizationEditRegionsModel = new PlotlyChartModel(this, getContext());

		return optimizationEditRegionsModel.createChartNode(chartGenerator);
	}

	private PlotlyChartGenerator createChartGenerator(List<ExcludeXElement> currentExcludeRegions) {
		PlotlyChartGenerator chartGenerator = new PlotlyChartGenerator(getContext());
		chartGenerator.forJob(this);
		chartGenerator.useGuiTemplate();
		if (excludeRegionsEdited) {
			chartGenerator.setExcludeRegionsEdited(currentExcludeRegions);
		}
		return chartGenerator;
	}

	public void setExcludeRegionsEdited(boolean excludeRegionsEdited) {
		this.excludeRegionsEdited = excludeRegionsEdited;
	}

	public ObjectProperty<TableOfDoubles> getDatTableProperty() {
		return datTableProperty;
	}

	public ObjectProperty<TableOfDoubles> getHklTableProperty() {
		return hklTableProperty;
	}

	public List<ExcludeXElement> getExcludeRegions() {
		return excludeRegions;
	}

	public abstract List<String> getCommandList();

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
	protected void jobFinishedAction() {
		this.progressPercentage = new SimpleDoubleProperty(1.0);
		if (optimizationController != null) {
			optimizationController.jobProgressBar.progressProperty().bind(progressPercentage);
		}
	}

	@Override
	protected void doTerminate() {
		jobRunner.terminate();
	}

	public void setRefinedParams(Map<String, ParUniqueElement> map) {
		this.refinedParams = map;
	}

	public void markFittedParams() {
		Set<ParUniqueElement> refinedParamKey = new LinkedHashSet<>();
		for (Entry<String, ParUniqueElement> entry : refinedParams.entrySet()) {
			if (entry.getValue().refinedProperty.get()) {
				refinedParamKey.add(entry.getValue());
			}
		}
		this.fittedParams = refinedParamKey;
	}

	public int getRefinedParamsCount() {
		return refinedParams.size();
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public Integer getIterations() {
		return iterations;
	}

}
