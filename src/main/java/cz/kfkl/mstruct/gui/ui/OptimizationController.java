package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.doWhenPropertySet;
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
import cz.kfkl.mstruct.gui.ui.chart.JobOutputExporter;
import cz.kfkl.mstruct.gui.ui.chart.PlotlyChartGenerator;
import cz.kfkl.mstruct.gui.utils.ListCellWithIcon;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
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
import javafx.scene.image.Image;
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
	private Spinner<Integer> iteractionsSpinner;
	@FXML
	private TextField outputFolderNameTextField;
	@FXML
	private Button runButton;

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
	private Label jobStatusLabel;
	@FXML
	private ImageView jobStatusImageView;
	@FXML
	private ProgressBar jobProgressBar;
	@FXML
	private Label jobProgressLabel;
	@FXML
	private Label jobRwFactorLabel;
	@FXML
	private Label jobParamsRefinedLabel;
	@FXML
	Label datRowsCountLabel;
	@FXML
	private TextArea consoleTextArea;
	@FXML
	private TextArea jobsLogsTextArea;

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

		jobTypeChoiceBox.getItems().addAll(jobTypes);
		jobTypeChoiceBox.getSelectionModel().selectFirst();

		int initValue = 10; // model.interations.getValue();
		iteractionsSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 999, initValue));

		TextFormatter<String> tf = new TextFormatter<>(IDENTITY_STRING_CONVERTER);
//				(c) -> {
//			String text = c.getControlNewText();
//			String newText = text == null || text.isBlank() ? "${timeStamp}" : text;
//			c.setText(newText);
//			return c;
//		});
		outputFolderNameTextField.setTextFormatter(tf);

		jobsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		jobsListView.setCellFactory(
				(lv) -> (new ListCellWithIcon<>(j -> new ImageView(j.getStatus().getImage()), j -> j.toString())));

		jobsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldJob, newJob) -> {
			if (oldJob != null) {
				oldJob.setActiveConsoleTextArea(null);
				oldJob.setActiveJobLogsTextArea(null);
			}
			if (newJob != null && newJob != oldJob) {
				consoleTextArea.setText(newJob.getConsoleText());
				newJob.setActiveConsoleTextArea(consoleTextArea);
				jobParamsRefinedLabel.textProperty().set(Integer.toString(newJob.getRefinedParamsCount()));

				jobsLogsTextArea.setText(newJob.getJobLogsText());
				newJob.setActiveJobLogsTextArea(jobsLogsTextArea);

//				jobTabPane.getTabs().remove(1, jobTabPane.getTabs().size());
				newJob.updateTabs(mainController, this);

				doWhenPropertySet(v -> datRowsCountLabel.setText(Integer.toString(v.getRows().size())),
						newJob.getDatTableProperty());

				jobStatusLabel.textProperty().bind(newJob.getStatusProperty().asString());
				ObjectBinding<Image> imageBinding = Bindings
						.createObjectBinding(() -> newJob.getStatusProperty().get().getImage(), newJob.getStatusProperty());
				jobStatusImageView.imageProperty().bind(imageBinding);

			}
		});

		mainController = getAppContext().getMainController();
		openedFileProperty = mainController.getOpenedFileProperty();

//		removeButton.setGraphic(new ImageView(Images.get("delete.png")));
//		terminateButton.setGraphic(new ImageView(Images.get("stop.png")));
	}

	public void setRootModel(ObjCrystModel rootModel) {
		getModelInstance().setRootModel(rootModel);

		boolean disabled = rootModel == null;
		jobTypeChoiceBox.setDisable(disabled);
		iteractionsSpinner.setDisable(disabled);
		outputFolderNameTextField.setDisable(disabled);
		runButton.setDisable(disabled);
	}

	@FXML
	public void runButtonAction() {
		Instant startTime = Instant.now();

		File openedFile = openedFileProperty.get();
		Validator.assertNotNull(openedFile, "There is no file openned.");

		Path selectedFilePath = openedFile.toPath();
		String nameWithoutExtension = MoreFiles.getNameWithoutExtension(selectedFilePath);

		String outputFolderName = formatName(nameWithoutExtension, startTime);

		JobType jobType = jobTypeChoiceBox.getSelectionModel().getSelectedItem();
		assertNotNull(jobType, "No Job type selectd.");
		OptimizationJob job = jobType.createJob(getAppContext());

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
