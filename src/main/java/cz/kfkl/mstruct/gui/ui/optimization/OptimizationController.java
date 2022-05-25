package cz.kfkl.mstruct.gui.ui.optimization;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.validateIsNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.io.MoreFiles;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.model.OptimizaitonModel;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
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
import cz.kfkl.mstruct.gui.utils.ListCellWithIcon;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
	private Button exportOutputCsvButton;
	@FXML
	private Button exportOutputDatButton;
	@FXML
	private Button exportHtmlButton;

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
	Tab chartTab;

	@FXML
	BorderPane chartTabTitledPane;

	private List<JobType> jobTypes;

	MStructGuiController mainController;
	ObjectProperty<File> openedFileProperty;

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
				if (oldJob != null) {
					oldJob.setActiveJob(null);
				}
				newJob.updateTabs(mainController, this);
			}
		});

		mainController = getAppContext().getMainController();
		openedFileProperty = mainController.getOpenedFileProperty();
	}

	public void setRootModel(ObjCrystModel rootModel) {
		getModelInstance().setRootModel(rootModel);

		boolean disabled = rootModel == null;
		// TODO left for testing, remove at some stage
//		jobTypeChoiceBox.setDisable(disabled);
		iterationsSpinner.setDisable(disabled);
		outputFolderNameTextField.setDisable(disabled);

		// TODO left for testing, remove at some stage
//		runButton.setDisable(disabled);
		refineButton.setDisable(disabled);
		simulateButton.setDisable(disabled);
	}

	// TODO left for testing, remove at some stage
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

		File openedFile = openedFileProperty.get();
		Validator.assertNotNull(openedFile, "There is no file openned.");

		Path selectedFilePath = openedFile.toPath();
		String nameWithoutExtension = MoreFiles.getNameWithoutExtension(selectedFilePath);

		String outputFolderName = formatName(nameWithoutExtension, startTime);

		OptimizationJob job = jobType.createJob(getAppContext());

		job.setIterations(iterationsSpinner.getValue());

		job.setName(outputFolderName);
		job.setShowConsole(showConsoleOutputCheckBox.selectedProperty().get());
		job.setKeepOutput(keepOutputFilesCheckBox.selectedProperty().get());
		job.setRefinedParams(findCurrentlyRefinedParameters());

		File resultDir = new File(getAppContext().getLastSelectedFileDirectoryOrDefault(), outputFolderName);
		job.setResultDir(resultDir);
		job.getStatusProperty().addListener((obs, oldV, newV) -> {
			jobsListView.refresh();
		});

		boolean created = resultDir.mkdir();

		jobsListView.getItems().add(0, job);
		jobsListView.getSelectionModel().clearSelection();
		jobsListView.getSelectionModel().select(job);

		File inputFile = new File(resultDir, openedFile.getName());
		mainController.saveXmlDocument(inputFile);
		job.setInputFile(inputFile);

		job.startJob();
	}

	private Set<String> findCurrentlyRefinedParameters() {
		ObjCrystModel rootModel = getModelInstance().getRootModel();
		Map<String, ParUniqueElement> map = ParametersController.createParamsLookup(rootModel);

		Set<String> refinedParamKey = new LinkedHashSet<>();
		for (Entry<String, ParUniqueElement> entry : map.entrySet()) {
			if (entry.getValue().refinedProperty.get()) {
				refinedParamKey.add(entry.getKey());
			}
		}

		return refinedParamKey;
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
