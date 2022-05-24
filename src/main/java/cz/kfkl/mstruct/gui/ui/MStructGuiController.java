package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.BindingUtils.createUniqueName;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.initTableView;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.loadViewAndInitController;
import static cz.kfkl.mstruct.gui.utils.BindingUtils.setupListViewListener;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;
import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.MoreFiles;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.model.OptimizaitonModel;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParametersModel;
import cz.kfkl.mstruct.gui.model.SingleValueUniqueElement;
import cz.kfkl.mstruct.gui.model.crystals.CrystalModel;
import cz.kfkl.mstruct.gui.model.crystals.ImportedCrystal;
import cz.kfkl.mstruct.gui.model.crystals.ImportedCrystalsModel;
import cz.kfkl.mstruct.gui.model.instrumental.InstrumentalModel;
import cz.kfkl.mstruct.gui.model.instrumental.PowderPatternElement;
import cz.kfkl.mstruct.gui.model.phases.PowderPatternCrystalsModel;
import cz.kfkl.mstruct.gui.ui.crystals.CrystalCifImportJob;
import cz.kfkl.mstruct.gui.ui.crystals.ImportedCrystalsController;
import cz.kfkl.mstruct.gui.ui.job.JobStatus;
import cz.kfkl.mstruct.gui.ui.optimization.OptimizationController;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import cz.kfkl.mstruct.gui.xml.XmlUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MStructGuiController implements HasAppContext {

	private static final int EXPECTED_DAT_FILE_ROWS = 4;

	private static final Logger LOG = LoggerFactory.getLogger(MStructGuiController.class);

	public static final ExtensionFilter ALL_TYPES_EXTENSION_FILTER = new FileChooser.ExtensionFilter("All types", "*.*");
	private static final ExtensionFilter MSTRUCT_XML_EXTENSION_FILTER = new FileChooser.ExtensionFilter("MStruct ObjCryst",
			"*.xml");
	private static final ExtensionFilter CIF_EXTENSION_FILTER = new FileChooser.ExtensionFilter("CIF Crystals", "*.cif");
	private static final ExtensionFilter DAT_EXTENSION_FILTER = new FileChooser.ExtensionFilter("Tablated Textual Data", "*.dat");
//XIobsSigmaWeight
	private static final String[] DATA_TABLE_COLUMNS = new String[] { "2Theta/TOF", "Iobs", "Sigma", "Weight" };

	public static final String OBJ_CRYST = "ObjCryst";
	public static final String INPUT_DATA_ELEMENT_NAME = "/ObjCryst/PowderPattern/XIobsSigmaWeightList";

	@FXML
	private BorderPane topBorderPanel;

	@FXML
	private MenuItem saveMenuItem;
	@FXML
	private MenuItem saveAsMenuItem;
	@FXML
	private MenuItem closeMenuItem;

	@FXML
	private Label bottomLabel;

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabCrystals;
	@FXML
	private ListView<CrystalModel> crystalsListView;
	@FXML
	private ScrollPane tabCrystalsCenterPane;
	@FXML
	private Button removeCrystalButton;

	@FXML
	private Tab tabInstrumental;
	@FXML
	private ListView<InstrumentalModel> instrumentalListView;
	@FXML
	private ScrollPane tabInstrumentalCenterPane;
	@FXML
	private Button addInstrumentButton;

	@FXML
	private Tab tabPhases;
	@FXML
	private ComboBox<InstrumentalModel> instrumentalComboBox;
	@FXML
	private ListView<PowderPatternCrystalsModel> phasesListView;
	@FXML
	private ScrollPane tabPhasesCenterPane;
	@FXML
	private Button addPhaseButton;
	@FXML
	private Button removePhaseButton;

	@FXML
	private Tab tabParameters;

	@FXML
	private Tab tabInputData;
	@FXML
	private TableView<TableOfDoubles.RowIndex> inputDataTableView;

	@FXML
	private Tab tabOptimization;

	private AppContext appContext;

	ObjCrystModel rootModel;

	OptimizationController optimizationController;

	private ObjectProperty<File> openedFileProperty = new SimpleObjectProperty<>();
	private Document openedDocument;

	private StringProperty titleProperty;

	public void init() {

		LOG.debug("TITLE: {}", titleProperty.get());
		titleProperty.bind(Bindings.createStringBinding(() -> MStructGuiMain.M_STRUCT_UI_TITLE + formatOpenedFileName(),
				openedFileProperty));

		saveMenuItem.disableProperty().bind(openedFileProperty.isNull());
		saveAsMenuItem.disableProperty().bind(openedFileProperty.isNull());
		closeMenuItem.disableProperty().bind(openedFileProperty.isNull());

		instrumentalListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				instrumentalComboBox.getSelectionModel().select(newValue);
			}
		});

		instrumentalComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				if (newValue instanceof PowderPatternElement) {
					PowderPatternElement ppElement = (PowderPatternElement) newValue;
					phasesListView.setItems(ppElement.powderPatternCrystals);
					phasesListView.getSelectionModel().selectFirst();
				}
			}
		});

		// enable the Add instrument button only if instrumentalListView has no item
		ListProperty<InstrumentalModel> lstProp = new SimpleListProperty<>(instrumentalListView.getItems());
		lstProp.bind(instrumentalListView.itemsProperty());
		addInstrumentButton.disableProperty().bind(Bindings.not(lstProp.emptyProperty()));

		crystalsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		removeCrystalButton.disableProperty().bind(crystalsListView.getSelectionModel().selectedItemProperty().isNull());

		removePhaseButton.disableProperty().bind(phasesListView.getSelectionModel().selectedItemProperty().isNull());

		crystalsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		configOptimizationTab();
	}

	private String formatOpenedFileName() {
		return openedFileProperty.get() == null ? "" : " - " + openedFileProperty.get().getName();
	}

	@FXML
	void closeFile(ActionEvent event) {
		// TODO dialog
		this.crystalsListView.setItems(FXCollections.observableArrayList());
		this.instrumentalListView.setItems(FXCollections.observableArrayList());
		this.instrumentalComboBox.setItems(FXCollections.observableArrayList());
		this.phasesListView.setItems(FXCollections.observableArrayList());
		this.tabParameters.setContent(null);
		optimizationController.setRootModel(null);

		this.rootModel = null;
		this.openedDocument = null;

		setBottomLabelText("File [%s] was closed.", openedFileProperty.get());
		this.openedFileProperty.set(null);
	}

	@FXML
	void openFile(ActionEvent event) throws FileNotFoundException, JDOMException, IOException {
		// TODO dialog ??
//		try {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open MStruct ObjCryst XML File");
		configureExtensionFilter(fileChooser, MSTRUCT_XML_EXTENSION_FILTER);

		fileChooser.setInitialDirectory(appContext.getLastSelectedFileDirectoryOrDefault());
		File selectedFile = fileChooser.showOpenDialog(topBorderPanel.getScene().getWindow());
		if (selectedFile != null) {
			openSelectedFile(selectedFile);
		}
//		} catch (PopupErrorException pee) {
//			pee.printStackTrace();
//			Alert alert = new Alert(AlertType.ERROR, pee.getMessage());
//			alert.showAndWait();
//		}
	}

	private void configureExtensionFilter(FileChooser fileChooser, ExtensionFilter specialFilterilter) {
		ObservableList<ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
		extensionFilters.add(ALL_TYPES_EXTENSION_FILTER);
		extensionFilters.add(specialFilterilter);

		fileChooser.setSelectedExtensionFilter(specialFilterilter);
	}

	public void openSelectedFile(File selectedFile) {
		appContext.setLastSelectedFileDirectory(selectedFile.getParentFile());
		this.openedFileProperty.set(selectedFile);

		processSelectedFile(selectedFile);

		setBottomLabelText("File [%s] was opened.", selectedFile);
	}

	@FXML
	void exitApplication(ActionEvent event) {
		// TODO dialog
		System.exit(0);
	}

	private void processSelectedFile(File selectedFile) {
		setupListViewListener(this, crystalsListView, tabCrystalsCenterPane, getAppContext());
		setupListViewListener(this, instrumentalListView, tabInstrumentalCenterPane, getAppContext());
		setupListViewListener(this, phasesListView, tabPhasesCenterPane, getAppContext());

		parseMStructXml(selectedFile);
	}

	private void parseMStructXml(File selectedFile) {
		try {
			SAXBuilder builder = new SAXBuilder();
			openedDocument = builder.build(selectedFile);
			Element root = openedDocument.getRootElement();
			Validator.assertEquals(OBJ_CRYST, root.getName(), "Expected XML file with root element [%s], got [%s]", OBJ_CRYST,
					root.getName());

			rootModel = new ObjCrystModel();
			rootModel.bindToElement(null, root);

			crystalsListView.setItems(rootModel.crystals);
			crystalsListView.getSelectionModel().selectFirst();

			instrumentalListView.setItems(rootModel.instruments);
			instrumentalComboBox.setItems(rootModel.instruments);
			instrumentalListView.getSelectionModel().selectFirst();

			initParametersTab(this.tabParameters);

			optimizationController.setRootModel(rootModel);

			configInputDataTable(findSingleXIobsSigmaWeightListElement().valueProperty.get());

		} catch (Exception e) {
			throw new PopupErrorException(e, "Failed to parse the xml file [%s]", selectedFile);
		}
	}

	private SingleValueUniqueElement findSingleXIobsSigmaWeightListElement() {
		return rootModel.getFirstPowderPattern().xIobsSigmaWeightListElement;
	}

	private void configInputDataTable(String data) {

		String[] columnNames = DATA_TABLE_COLUMNS;

		initTableView(inputDataTableView, columnNames);

		updateInputData(createInputDataParser().parse(data));

	}

	public ParametersController initParametersTab(Tab tabParameters) {
		return initParametersTab(tabParameters, new SimpleObjectProperty<>(), new LinkedHashSet<>());
	}

	public ParametersController initParametersTab(Tab tabParameters,
			ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty, Set<String> refinedParams) {
		ParametersModel parametersModel = new ParametersModel(rootModel);
		parametersModel.fittedParamsProperty = fittedParamsProperty;
		parametersModel.refinedParams = refinedParams;
		ParametersController controller = loadViewAndInitController(this, getAppContext(), parametersModel,
				(view) -> tabParameters.setContent(view));

		tabParameters.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				controller.createAndSetParamTree();
			}
		});

		return controller;
	}

	private void configOptimizationTab() {
		OptimizaitonModel parametersModel = new OptimizaitonModel();
		optimizationController = loadViewAndInitController(this, getAppContext(), parametersModel,
				(view) -> tabOptimization.setContent(view));

	}

	public void setBottomLabelText(String message, Object... args) {
		String text = String.format(message, args);
		bottomLabel.setText(text);
		LOG.info("{}", text);
	}

	@FXML
	void saveFile(ActionEvent event) {
		File outFile = openedFileProperty.get();
		saveXmlDocument(outFile);
		setBottomLabelText("File [%s] was saved.", outFile);
	}

	@FXML
	void saveFileAs(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save MStruct ObjCryst XML File");

		configureExtensionFilter(fileChooser, MSTRUCT_XML_EXTENSION_FILTER);

		fileChooser.setInitialDirectory(appContext.getLastSelectedFileDirectoryOrDefault());
		File selectedFile = fileChooser.showSaveDialog(topBorderPanel.getScene().getWindow());

		if (selectedFile != null) {
			appContext.setLastSelectedFileDirectory(selectedFile.getParentFile());

			saveXmlDocument(selectedFile);

			String oldName = this.openedFileProperty.get().getName();
			this.openedFileProperty.set(selectedFile);
			setBottomLabelText("File [%s] was saved as [%s].", oldName, selectedFile);
		}
	}

	// TODO Remove
	@FXML
	void saveFileAsTest(ActionEvent event) {
		File outFile = addFileNameSuffix(openedFileProperty.get(), "_TEST");
		saveXmlDocument(outFile);
		setBottomLabelText("File saved with _TEST suffix to [%s].", outFile);
	}

	@FXML
	void importCrystalsFromCif(ActionEvent event) {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Crystal CIF File");
			configureExtensionFilter(fileChooser, CIF_EXTENSION_FILTER);

			fileChooser.setInitialDirectory(appContext.getLastSelectedCifFileDirectoryOrDefault());
			File selectedFile = fileChooser.showOpenDialog(topBorderPanel.getScene().getWindow());
			if (selectedFile != null) {
				LOG.debug("Importing CIF file [{}]", selectedFile);

				appContext.setLastSelectedCifFileDirectory(selectedFile.getParentFile());

				CrystalCifImportJob cifJob = new CrystalCifImportJob(getAppContext());

				String outputFolderName = formatOutFolderName(selectedFile);
				cifJob.setName(outputFolderName);
				cifJob.setInputFile(selectedFile);

				File resultDir = new File(getAppContext().getLastSelectedFileDirectoryOrDefault(), outputFolderName);
				boolean created = resultDir.mkdir();
				cifJob.setResultDir(resultDir);

				// TODO start some kind of progress indication here...
				cifJob.startJob();

				Platform.runLater(() -> {

					if (cifJob.getStatus() == JobStatus.Finished) {
						List<ImportedCrystal> importedCrystals = cifJob.getImportedCrystals();
						assertNotNull(importedCrystals, "List of imported crystals is not set. ");
						showCrystalSelectionDialogAndImportCrystals(importedCrystals);
					} else {
						LOG.error("Importing of CIF file has failed, status [{}]", cifJob.getStatus());
						LOG.debug("*** Console Text:\n{}", cifJob.getConsoleText());
						LOG.debug("*** Job log:\n{}", cifJob.getJobLogsText());
						PopupErrorException pee = new PopupErrorException("The CIF file loading has failed. Status [%s].",
								cifJob.getStatus());
						throw pee;
					}
				});

				// openSelectedFile(selectedFile);
			}
		} catch (Exception pee) {
			throw pee;
		}
	}

	private String formatOutFolderName(File selectedFile) {
		Path selectedFilePath = selectedFile.toPath();
		String nameWithoutExtension = MoreFiles.getNameWithoutExtension(selectedFilePath);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss").withZone(ZoneId.systemDefault());
		Instant startTime = Instant.now();
		String outputFolderName = nameWithoutExtension + "_" + formatter.format(startTime);
		return outputFolderName;
	}

	@FXML
	void importCrystalsFromXml(ActionEvent event) {

		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open MStruct ObjCryst XML File");
			configureExtensionFilter(fileChooser, MSTRUCT_XML_EXTENSION_FILTER);

			fileChooser.setInitialDirectory(appContext.getLastSelectedFileDirectoryOrDefault());
			File selectedFile = fileChooser.showOpenDialog(topBorderPanel.getScene().getWindow());
			if (selectedFile != null) {
				processCrystalXmlFile(selectedFile);

			}
		} catch (PopupErrorException pee) {
			pee.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, pee.getMessage());
			alert.showAndWait();
		}

	}

	private void processCrystalXmlFile(File selectedFile) {
		appContext.setLastSelectedFileDirectory(selectedFile.getParentFile());

		List<ImportedCrystal> importedCrystals = ImportedCrystalsController.parseObjCrystXmlCrystals(selectedFile);
		showCrystalSelectionDialogAndImportCrystals(importedCrystals);
	}

	private void showCrystalSelectionDialogAndImportCrystals(List<ImportedCrystal> importedCrystals) {
		ImportedCrystalsModel importedCrystalsModel = new ImportedCrystalsModel();
		markExistingCrystals(importedCrystals);
		importedCrystalsModel.setImportedCrystals(importedCrystals);

		Dialog crystalSelectionDialog = new Dialog();
		DialogPane dialogPane = crystalSelectionDialog.getDialogPane();
		crystalSelectionDialog.setTitle("Crystals Import");

//				ImportedCrystalsController importXmlController = 
		loadViewAndInitController(this, getAppContext(), importedCrystalsModel, (view) -> dialogPane.setContent(view));

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		crystalSelectionDialog.showAndWait();

		if (ButtonType.OK == crystalSelectionDialog.getResult()) {
			importCrystals(importedCrystalsModel);
		}
	}

	private void importCrystals(ImportedCrystalsModel importedCrystalsModel) {
		int importedCrystalsCount = 0;
		for (ImportedCrystal importedCrystal : importedCrystalsModel.importedCrystals) {
			if (importedCrystal.selectedProperty.get()) {

				CrystalModel cm = new CrystalModel();

				XmlUtils.detachContent(importedCrystal.getCrystalWithPreceedingBallast());

				cm.setXmlElement(importedCrystal.getCrystalElement());
				cm.setImportedXmlContent(importedCrystal.getCrystalWithPreceedingBallast());

				String newCrystalName = null;

				CrystalModel existingCrystal = importedCrystal.existingCrystal;
				int existingCrystalIndexOf = -1;
				if (existingCrystal != null) {
					existingCrystalIndexOf = rootModel.crystals.indexOf(existingCrystal);

					switch (importedCrystalsModel.getIfExistsAction()) {
					case REPLACE:
						rootModel.crystals.remove(existingCrystal);
						break;
					case RENAME_NEW:

						int suffix = 1;
						do {
							newCrystalName = importedCrystal.getCrystalName() + " (" + suffix + ")";
							suffix++;
						} while (rootModel.getCrystal(newCrystalName) != null);
						break;
					}
				}

				if (existingCrystalIndexOf >= 0 && existingCrystalIndexOf < rootModel.crystals.size()) {
					rootModel.crystals.add(existingCrystalIndexOf, cm);
				} else {
					rootModel.crystals.add(cm);
				}

				if (newCrystalName != null) {
					cm.setName(newCrystalName);
				}

				getCrystalsListView().refresh();

				importedCrystalsCount++;
			}
		}
		setBottomLabelText(importedCrystalsCount + " crystals imported");
	}

	private void markExistingCrystals(List<ImportedCrystal> importedCrystals) {
		for (ImportedCrystal importedCrystal : importedCrystals) {
			String crystalName = importedCrystal.getCrystalName();
			CrystalModel existingCrystal = rootModel.getCrystal(crystalName);
			if (existingCrystal != null) {
				importedCrystal.existsProperty.set(true);
				importedCrystal.existingCrystal = existingCrystal;
			}
		}
	}

	@FXML
	void importDataFromDat(ActionEvent event) {

		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open MStruct Input Data DAT File");
			configureExtensionFilter(fileChooser, DAT_EXTENSION_FILTER);

			fileChooser.setInitialDirectory(appContext.getLastSelectedDatFileDirectoryOrDefault());
			File selectedFile = fileChooser.showOpenDialog(topBorderPanel.getScene().getWindow());
			if (selectedFile != null) {
				importDatFile(selectedFile);
			}
		} catch (PopupErrorException pee) {
			pee.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, pee.getMessage());
			alert.showAndWait();
		}
	}

	private void importDatFile(File selectedFile) {
		appContext.setLastSelectedDatFileDirectory(selectedFile.getParentFile());

		assertTrue(selectedFile.exists(), "The input file [%s] doesn't exist, full path: %s", selectedFile.getName(),
				selectedFile);
		try {
			TableOfDoubles tabularData = createInputDataParser().parse(selectedFile);

			inputDataTableView.getItems().clear();
			updateInputData(tabularData);

			SingleValueUniqueElement xIobsSigmaWeightList = findSingleXIobsSigmaWeightListElement();
			xIobsSigmaWeightList.valueProperty.set(tableToString(tabularData, xIobsSigmaWeightList.getXmlLevel()));
		} catch (Exception e) {
			throw new PopupErrorException(e, "Exception parsing output dat file [%s]: %s", selectedFile,
					e.getStackTrace().toString());
		}
	}

	private void updateInputData(TableOfDoubles tabularData) {
		calculateSigmaWeight(tabularData);
		inputDataTableView.getItems().addAll(tabularData.getRowIndexes());
		inputDataTableView.refresh();
	}

	private TabularDataParser createInputDataParser() {
		TabularDataParser parser = new TabularDataParser();
		parser.setExpectedRows(EXPECTED_DAT_FILE_ROWS);
		return parser;
	}

	private void calculateSigmaWeight(TableOfDoubles tabularData) {
		// TODO validation, some reporting of calculated rows
		for (double[] row : tabularData.getRows()) {
			if (row[2] == TabularDataParser.DEFAULT_DOUBLE) {
				if (row[1] != 0) {
					row[2] = Math.sqrt(row[1]);
				}
			}

			if (row[3] == TabularDataParser.DEFAULT_DOUBLE) {
				if (row[1] != 0) {
					row[3] = 1.0 / row[1];
				}
			}
		}
	}

	// TODO consider different formatting
	private String tableToString(TableOfDoubles tabularData, int xmlIndent) {
		String indentStrInner = XmlUtils.indentString(xmlIndent + 1);
		return "\n" + indentStrInner
				+ JvStringUtils.join("\n" + indentStrInner, tabularData.getColumnsDataJoined(" ", 0, 1, 2, 3)) + "\n"
				+ XmlUtils.indentString(xmlIndent);
	}

	@FXML
	public void removeCrystal() {
		ObservableList<CrystalModel> selectedCrystals = crystalsListView.getSelectionModel().getSelectedItems();

		if (confirmRemoveIfNeeded(findUsedCrystalsToRemove(selectedCrystals))) {
			crystalsListView.getItems().removeAll(selectedCrystals);
		}
	}

	private Set<String> findUsedCrystalsToRemove(ObservableList<CrystalModel> selectedCrystals) {
		Set<String> usedCrystals = rootModel.findUsedCrystals();

		Set<String> usedCrystalsToRemove = new LinkedHashSet<>();
		for (CrystalModel cm : selectedCrystals) {
			String cName = cm.getName();
			if (usedCrystals.contains(cName)) {
				usedCrystalsToRemove.add(cName);
			}
		}
		return usedCrystalsToRemove;
	}

	private boolean confirmRemoveIfNeeded(Set<String> usedCrystalsToRemove) {
		boolean doRemove = true;
		if (!usedCrystalsToRemove.isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING, "Are you sure you want to remove all selected crystal(s)?", ButtonType.YES,
					ButtonType.CANCEL);
			alert.setHeaderText(
					"Removing crystals referenced by crystal phases: " + JvStringUtils.joinAbreviate(usedCrystalsToRemove, 5));
			Optional<ButtonType> opt = alert.showAndWait();
			if (opt.get() != ButtonType.YES) {
				doRemove = false;
			}
		}
		return doRemove;
	}

	@FXML
	public void addPhase() {
		PowderPatternCrystalsModel newInstance = new PowderPatternCrystalsModel();

		newInstance.setName(createUniqueName(newInstance, phasesListView.getItems()));

		phasesListView.getItems().add(newInstance);
		phasesListView.getSelectionModel().select(newInstance);
	}

	@FXML
	public void removePhase() {
		phasesListView.getItems().removeAll(phasesListView.getSelectionModel().getSelectedItems());
	}

	@FXML
	public void addInstrument() {
		InstrumentalModel newInstance = new PowderPatternElement();
		instrumentalListView.getItems().add(newInstance);
		instrumentalListView.getSelectionModel().select(newInstance);
	}

	@FXML
	public void removeInstrument() {
		// multiple instruments not supported - Remove button is commnted out in the
		// fxml
	}

	public File addFileNameSuffix(File file, String suffix) {
		Path selectedFilePath = file.toPath();
		String nameWithoutExtension = MoreFiles.getNameWithoutExtension(selectedFilePath);
		String extension = MoreFiles.getFileExtension(selectedFilePath);
		extension = Strings.isNullOrEmpty(extension) ? "xml" : extension;

		File outFile = new File(file.getParentFile(), nameWithoutExtension + suffix + "." + extension);
		return outFile;
	}

	public void saveXmlDocument(File outFile) {
//		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getRawFormat().setOmitDeclaration(true));

		try (FileWriter fw = new FileWriter(outFile)) {
			xmlOutputter.output(openedDocument, fw);
		} catch (Exception e) {
			throw new PopupErrorException(e, "Failed to save the xml file [%s]", outFile);
		}
	}

	public ObjectProperty<File> getOpenedFileProperty() {
		return openedFileProperty;
	}

	public void setTitleProperty(StringProperty titleProperty) {
		this.titleProperty = titleProperty;
	}

	public ListView<CrystalModel> getCrystalsListView() {
		return crystalsListView;
	}

	public Tab getTabCrystals() {
		return tabCrystals;
	}

	public void setTabCrystals(Tab tabCrystals) {
		this.tabCrystals = tabCrystals;
	}

	public void instrumentNameUpdated() {
		instrumentalListView.refresh();
		instrumentalComboBox.setItems(FXCollections.observableArrayList());
		instrumentalComboBox.setItems(rootModel.instruments);
		instrumentalComboBox.getSelectionModel().select(instrumentalListView.getSelectionModel().getSelectedItem());
	}

	public void phaseNameUpdated() {
		phasesListView.refresh();
	}

	public void settabInstrumental(Tab tabInstrumental) {
		this.tabInstrumental = tabInstrumental;
	}

	public Tab gettabInstrumental() {
		return tabInstrumental;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	@Override
	public void setAppContext(AppContext context) {
		this.appContext = context;
	}

}
