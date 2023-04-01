package cz.kfkl.mstruct.gui.ui.optimization;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindBooleanTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.bindDoubleTableColumn;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.loadViewAndInitController;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.validateIsNull;

import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.model.OptimizaitonModel;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.PlotlyChartModel;
import cz.kfkl.mstruct.gui.model.instrumental.ExcludeXElement;
import cz.kfkl.mstruct.gui.model.phases.IhklParElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.ui.CsvOutputDataExporter;
import cz.kfkl.mstruct.gui.ui.DatOutputDataExporter;
import cz.kfkl.mstruct.gui.ui.MStructGuiController;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.ui.ParametersController;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles;
import cz.kfkl.mstruct.gui.ui.chart.JobOutputExporter;
import cz.kfkl.mstruct.gui.ui.chart.PlotlyChartGenerator;
import cz.kfkl.mstruct.gui.ui.job.Job;
import cz.kfkl.mstruct.gui.ui.job.JobType;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.ListCellWithIcon;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class OptimizationController extends BaseController<OptimizaitonModel, MStructGuiController> {

	private static final String TIME_STAMP_PLACEHOLDER = "${timeStamp}";
	private static final String FILE_NAME_PLACEHOLDER = "${fileName}";
	private static final String DEFAULT_OUTPUT_FOLDER_NAME = FILE_NAME_PLACEHOLDER + "_" + TIME_STAMP_PLACEHOLDER;
	private static final ExtensionFilter HTML_EXTENSION_FILTER = new FileChooser.ExtensionFilter("HTML", "*.html");

	@FXML
	private BorderPane topBorderPanel;

	@FXML
	private ChoiceBox<JobType> jobTypeChoiceBox;
	@FXML
	private Spinner<Integer> iterationsSpinner;
	@FXML
	private TextField outputFolderNameTextField;
	@FXML
	private Button runButton;
	@FXML
	private Button refineButton;
	@FXML
	private Button simulateButton;

	@FXML
	private CheckBox showConsoleOutputCheckBox;
	@FXML
	private CheckBox keepOutputFilesCheckBox;

	@FXML
	private ListView<OptimizationJob> jobsListView;
	@FXML
	private Button terminateButton;
	@FXML
	private Button removeButton;

	@FXML
	public Button exportOutputCsvButton;
	@FXML
	public Button exportOutputDatButton;

	@FXML
	public Button exportHtmlButton;
	@FXML
	public Button editExcludedRegionsButton;

	@FXML
	private TabPane jobTabPane;

	@FXML
	private Tab outputTab;
	@FXML
	Label jobStatusLabel;
	@FXML
	ImageView jobStatusImageView;
	@FXML
	ProgressBar jobProgressBar;
	@FXML
	Label jobProgressLabel;
	@FXML
	Label jobRwFactorLabel;
	@FXML
	Label jobGoFLabel;
	@FXML
	Label jobParamsRefinedLabel;
	@FXML
	Label datRowsCountLabel;
	@FXML
	TextArea consoleTextArea;
	@FXML
	TextArea jobsLogsTextArea;

	@FXML
	Tab fittedParamsTab;

	@FXML
	private Tab outputDataTab;
	@FXML
	TableView<TableOfDoubles.RowIndex> outputDataTableView;

	@FXML
	private Tab outputHklTab;
	@FXML
	TableView<TableOfDoubles.RowIndex> outputHklTableView;

	@FXML
	private Tab outputIhklParamsTab;
	@FXML
	TableView<IhklParElement> outputIhklParamsTableView;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsPhaseTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsHTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsKTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsLTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParams2ThetaTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsSFactSqMultTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsValueTableColumn;
	@FXML
	private TableColumn<IhklParElement, Boolean> outputIhklParamsRefinedTableColumn;
	@FXML
	private TableColumn<IhklParElement, Boolean> outputIhklParamsLimitedTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsMinTableColumn;
	@FXML
	private TableColumn<IhklParElement, String> outputIhklParamsMaxTableColumn;

	@FXML
	public Button copyAllIhklParamsButton;
	@FXML
	public Button copyRefinedIhklParamsButton;
	@FXML
	public Button exportIhklParamsCsvButton;

	@FXML
	Tab chartTab;

	@FXML
	BorderPane chartTabTitledPane;

	private List<JobType> jobTypes;

	MStructGuiController mainController;
	private OptimizationJob activeJob;
	ParametersController parametersTabController;

	public static final StringConverter<String> IDENTITY_STRING_CONVERTER = new StringConverter<String>() {
		@Override
		public String toString(String text) {
			return text == null || text.isBlank() ? DEFAULT_OUTPUT_FOLDER_NAME : text;
		}

		@Override
		public String fromString(String string) {
			return string;
		}
	};

	@Override
	public void init() {
		super.init();

		jobTypes = new ArrayList<>();

		jobTypes.addAll(List.of(JobType.values()));

		// TODO left for testing, remove at some stage
//		jobTypeChoiceBox.getItems().addAll(jobTypes);
//		jobTypeChoiceBox.getSelectionModel().selectFirst();

		int initValue = 10; // model.interations.getValue();
		iterationsSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 999, initValue));

		TextFormatter<String> tf = new TextFormatter<>(IDENTITY_STRING_CONVERTER);
		outputFolderNameTextField.setTextFormatter(tf);

		jobsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		jobsListView.setCellFactory(
				(lv) -> (new ListCellWithIcon<>(j -> new ImageView(j.getStatus().getImage()), j -> j.toString())));

		jobsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldJob, newJob) -> {

			if (newJob != null && newJob != oldJob) {
				if (activeJob != null) {
					activeJob.jobUnselected();
				}

				activeJob = newJob;
				newJob.updateTabs(this);
			}
		});

		mainController = getAppContext().getMainController();

		outputIhklParamsPhaseTableColumn.setCellValueFactory(new PropertyValueFactory<>("phaseName"));
		bindDoubleTableColumn(outputIhklParamsHTableColumn, v -> v.h);
		bindDoubleTableColumn(outputIhklParamsKTableColumn, v -> v.k);
		bindDoubleTableColumn(outputIhklParamsLTableColumn, v -> v.l);

		bindDoubleTableColumn(outputIhklParams2ThetaTableColumn, v -> v.twoTheta);
		bindDoubleTableColumn(outputIhklParamsSFactSqMultTableColumn, v -> v.sFactSqMult);
		bindDoubleTableColumn(outputIhklParamsValueTableColumn, v -> v.valueProperty);

		bindBooleanTableColumn(outputIhklParamsRefinedTableColumn, v -> v.refinedProperty);
		bindBooleanTableColumn(outputIhklParamsLimitedTableColumn, v -> v.limitedProperty);

		bindDoubleTableColumn(outputIhklParamsMinTableColumn, v -> v.minProperty);
		bindDoubleTableColumn(outputIhklParamsMaxTableColumn, v -> v.maxProperty);
	}

	public void setRootModel(ObjCrystModel rootModel) {
		getModelInstance().setRootModel(rootModel);

		boolean disabled = rootModel == null;
		// TODO TEST: left for testing, remove at some stage
//		jobTypeChoiceBox.setDisable(disabled);
		iterationsSpinner.setDisable(disabled);
		outputFolderNameTextField.setDisable(disabled);

		// TODO TEST: left for testing, remove at some stage
//		runButton.setDisable(disabled);
		refineButton.setDisable(disabled);
		simulateButton.setDisable(disabled);

		parametersTabController = mainController.initParametersTab(fittedParamsTab);
		if (rootModel != null) {
			parametersTabController.bindToRootModel(rootModel);
		}
	}

	// TODO TEST: for testing, remove at some stage
	@FXML
	public void run() {

		JobType jobType = jobTypeChoiceBox.getSelectionModel().getSelectedItem();
		assertNotNull(jobType, "No Job type selectd.");

		runChoosenJob(jobType);
	}

	@FXML
	public void refine() {
		runChoosenJob(JobType.DATA_REFINEMENT);
	}

	@FXML
	public void simulate() {
		runChoosenJob(JobType.DATA_SIMULATION);
	}

	private void runChoosenJob(JobType jobType) {
		Instant startTime = Instant.now();

		String openedFileName = mainController.fileNameProperty.get();
		Validator.assertNotNull(openedFileName, "There is no file openned.");

		String nameWithoutExtension = JvStringUtils.getNameWithoutExtension(openedFileName);

		String outputFolderName = formatName(nameWithoutExtension, startTime);

		OptimizationJob job = jobType.createJob(getAppContext(), getModelInstance().getRootModel());

		job.setIterations(iterationsSpinner.getValue());

		job.setName(outputFolderName);
		job.setShowConsole(showConsoleOutputCheckBox.selectedProperty().get());
		job.setKeepOutput(keepOutputFilesCheckBox.selectedProperty().get());

		// Stores the map of all params as a lookup map
		job.setRefinedParams(mapAllRefineableParameters());
		// Those which are currently refined are marked as fitted
		job.markFittedParams();

		File resultDir = new File(getAppContext().getLastSelectedFileDirectoryOrDefault(), outputFolderName);
		job.setResultDir(resultDir);
		job.getStatusProperty().addListener((obs, oldV, newV) -> {
			jobsListView.refresh();
		});

		resultDir.mkdir();

		jobsListView.getItems().add(0, job);
		jobsListView.getSelectionModel().clearSelection();
		jobsListView.getSelectionModel().select(job);

		File inputFile = new File(resultDir, openedFileName);
		mainController.saveXmlDocument(inputFile);
		job.setInputFile(inputFile);

		job.startJob();
	}

	private Map<String, ParUniqueElement> mapAllRefineableParameters() {
		ObjCrystModel rootModel = getModelInstance().getRootModel();
		return ParametersController.createParamsLookup(rootModel);
	}

	@FXML
	public void exportOutputDataAsCsv() {
		exportJobOutput(new CsvOutputDataExporter(getAppContext()));
	}

	@FXML
	public void exportOutputDataAsDat() {
		exportJobOutput(new DatOutputDataExporter(getAppContext()));
	}

	@FXML
	public void exportDatAsHtml() {
		PlotlyChartGenerator htmlChartGenerator = new PlotlyChartGenerator(getAppContext());
		htmlChartGenerator.useExportTemplate();

		File selectedFile = exportJobOutput(htmlChartGenerator);

		if (selectedFile != null) {
			if (getAppContext().showExportedChart()) {
				showHtmlInNewWindow(htmlChartGenerator.exportedData(), selectedFile.getName());
			}
		}
	}

	// TODO JV finish copy
	@FXML
	public void copyAndUseAllIhkParams() {
//		PlotlyChartGenerator htmlChartGenerator = new PlotlyChartGenerator(getAppContext());
//		htmlChartGenerator.useExportTemplate();
//
//		File selectedFile = exportJobOutput(htmlChartGenerator);
//
//		if (selectedFile != null) {
//			if (getAppContext().showExportedChart()) {
//				showHtmlInNewWindow(htmlChartGenerator.exportedData(), selectedFile.getName());
//			}
//		}
//
//		List<IhklParElement> ihklParams = new ArrayList<>();
//		for (PowderPatternCrystalsModel fittedPpc : fittedRootModel.getFirstPowderPattern().powderPatternCrystals) {
//			fittedPpc.getName();
//			ihklParams.addAll(fittedPpc.arbitraryTextureElement.ihklParams);
//		}
//
//		if (activeJob != null) {
//
//			ObjCrystModel rootModel = getModelInstance().getRootModel();
//			rootModel.updateIhklParams(rootModel);
//
//			PlotlyChartModel optimizationEditRegionsModel = new PlotlyChartModel(activeJob, getAppContext());
//
//			Dialog editRegionsDialog = new Dialog();
//			DialogPane dialogPane = editRegionsDialog.getDialogPane();
//			editRegionsDialog.setTitle("Edit Excluded Regions");
//
//			loadViewAndInitController(this, getAppContext(), optimizationEditRegionsModel, (view) -> dialogPane.setContent(view));
//
//			dialogPane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
//			editRegionsDialog.showAndWait();
//
//			if (ButtonType.APPLY == editRegionsDialog.getResult()) {
//				List<ExcludeXElement> updateRegions = optimizationEditRegionsModel.retrieveExcludedRegions();
//				ObjCrystModel rootModel = getModelInstance().getRootModel();
//				rootModel.replaceExcludeRegions(updateRegions);
//				activeJob.setExcludeRegionsEdited(true);
//
//				updateChartTab();
//			}
//		}
	}

	@FXML
	public void editExcludedRegions() {

		if (activeJob != null) {
			PlotlyChartModel optimizationEditRegionsModel = new PlotlyChartModel(activeJob, getAppContext());

			Dialog<ButtonType> editRegionsDialog = new Dialog<>();
			DialogPane dialogPane = editRegionsDialog.getDialogPane();
			editRegionsDialog.setTitle("Edit Excluded Regions");

			loadViewAndInitController(this, getAppContext(), optimizationEditRegionsModel, (view) -> dialogPane.setContent(view));

			dialogPane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
			editRegionsDialog.showAndWait();

			if (ButtonType.APPLY == editRegionsDialog.getResult()) {
				List<ExcludeXElement> updateRegions = optimizationEditRegionsModel.retrieveExcludedRegions();
				ObjCrystModel rootModel = getModelInstance().getRootModel();
				rootModel.replaceExcludeRegions(updateRegions);
				activeJob.setExcludeRegionsEdited(true);

				updateChartTab();
			}
		}
	}

	private void updateChartTab() {
		List<ExcludeXElement> currentExcludeRegions = getCurrentExcludeRegions();
		Node chartNode = activeJob.createChartNode(currentExcludeRegions);
		chartTabTitledPane.setCenter(chartNode);
	}

	public List<ExcludeXElement> getCurrentExcludeRegions() {
		ObjCrystModel rootModel = getModelInstance().getRootModel();
		return rootModel.getExcludeRegions();
	}

	private File exportJobOutput(JobOutputExporter jobExporter) {
		Job selectedJob = jobsListView.getSelectionModel().getSelectedItem();

		AppContext appContext = getAppContext();
		jobExporter.forJob(selectedJob);

		String errMessage = jobExporter.nothingToExportMessage();
		validateIsNull(errMessage, "Cannot create export as %s", errMessage);

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(jobExporter.getFileChooserTitle());

		fileChooser.setInitialFileName(jobExporter.getInitialFileName());

		configureHtmlExtensionFilters(fileChooser);

		fileChooser.setInitialDirectory(appContext.getLastSelectedFileDirectoryOrDefault());
		File selectedFile = fileChooser.showSaveDialog(topBorderPanel.getScene().getWindow());

		if (selectedFile != null) {
			appContext.setLastSelectedFileDirectory(selectedFile.getParentFile());

			try (FileWriter fw = new FileWriter(selectedFile)) {
				fw.append(jobExporter.exportedData());
				mainController.setBottomLabelText("Data exported to [%s].", selectedFile);
			} catch (Exception e) {
				throw new PopupErrorException(e, "Failed to %s to file [%s]", jobExporter.getFileChooserTitle(), selectedFile);
			}
		}
		return selectedFile;
	}

	private void showHtmlInNewWindow(String htmlText, String fileName) {
		WebView webView = new WebView();

		webView.getEngine().loadContent(htmlText);
		VBox vBox = new VBox(webView);
		Scene scene = new Scene(vBox, 960, 600);

		// New window (Stage)
		Stage newWindow = new Stage();
		newWindow.setTitle("MStruct Chart - " + fileName);
		newWindow.setScene(scene);

		newWindow.show();
	}

	private void configureHtmlExtensionFilters(FileChooser fileChooser) {
		ObservableList<ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
		extensionFilters.add(MStructGuiController.ALL_TYPES_EXTENSION_FILTER);
		extensionFilters.add(HTML_EXTENSION_FILTER);

		fileChooser.setSelectedExtensionFilter(HTML_EXTENSION_FILTER);
	}

	@FXML
	public void terminateJob() {
		Job selectedJob = jobsListView.getSelectionModel().getSelectedItem();
		if (selectedJob != null) {
			selectedJob.terminate();
		}
	}

	@FXML
	public void removeJob() {
		for (Job job : jobsListView.getSelectionModel().getSelectedItems()) {
			job.terminate();
		}
		jobsListView.getItems().removeAll(jobsListView.getSelectionModel().getSelectedItems());
	}

	private String formatName(String nameWithoutExtension, Instant startTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss").withZone(ZoneId.systemDefault());
		String text = outputFolderNameTextField.getText();

		text = text.replace(FILE_NAME_PLACEHOLDER, nameWithoutExtension);
		return text.replace(TIME_STAMP_PLACEHOLDER, formatter.format(startTime));
	}

}
