package cz.kfkl.mstruct.gui.ui;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;

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
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.MoreFiles;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.core.HasAppContext;
import cz.kfkl.mstruct.gui.model.CrystalModel;
import cz.kfkl.mstruct.gui.model.DiffractionModel;
import cz.kfkl.mstruct.gui.model.OptimizaitonModel;
import cz.kfkl.mstruct.gui.model.ParUniqueElement;
import cz.kfkl.mstruct.gui.model.ParametersModel;
import cz.kfkl.mstruct.gui.ui.TableOfDoubles.RowIndex;
import cz.kfkl.mstruct.gui.utils.BindingUtils;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import cz.kfkl.mstruct.gui.xml.XmlUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MStructGuiController implements HasAppContext {

	private static final Logger LOG = LoggerFactory.getLogger(MStructGuiController.class);

	public static final ExtensionFilter ALL_TYPES_EXTENSION_FILTER = new FileChooser.ExtensionFilter("All types", "*.*");
	private static final ExtensionFilter MSTRUCT_EXTENSION_FILTER = new FileChooser.ExtensionFilter("MStruct ObjCryst", "*.xml");
	private static final ExtensionFilter CIF_EXTENSION_FILTER = new FileChooser.ExtensionFilter("CIF Crystals", "*.cif");
//XIobsSigmaWeight
	private static final String[] DATA_TABLE_COLUMNS = new String[] { "2Theta/TOF", "Iobs", "Sigma", "Weight" };

	public static final String OBJ_CRYST = "ObjCryst";
	public static final String INPUT_DATA_ELEMENT_NAME = "/ObjCryst/PowderPattern/XIobsSigmaWeightList";

	@FXML
	private BorderPane topBorderPanel;

	@FXML
	private MenuBar menuBar;

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
	private Tab tabDiffractions;
	@FXML
	private ListView<DiffractionModel> diffractionsListView;
	@FXML
	private ScrollPane tabDiffractionsCenterPane;

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

		configOptimizationTab();
	}

	private String formatOpenedFileName() {
		return openedFileProperty.get() == null ? "" : " - " + openedFileProperty.get().getName();
	}

	@FXML
	void closeFile(ActionEvent event) {
		// TODO dialog
		this.crystalsListView.setItems(FXCollections.observableArrayList());
		this.diffractionsListView.setItems(FXCollections.observableArrayList());
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
		configureMStructExtensionFilters(fileChooser);

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

	private void configureMStructExtensionFilters(FileChooser fileChooser) {
		ObservableList<ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
		extensionFilters.add(ALL_TYPES_EXTENSION_FILTER);
		extensionFilters.add(MSTRUCT_EXTENSION_FILTER);

		fileChooser.setSelectedExtensionFilter(MSTRUCT_EXTENSION_FILTER);
	}

	private void configureCifExtensionFilters(FileChooser fileChooser) {
		ObservableList<ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
		extensionFilters.add(ALL_TYPES_EXTENSION_FILTER);
		extensionFilters.add(CIF_EXTENSION_FILTER);

		fileChooser.setSelectedExtensionFilter(CIF_EXTENSION_FILTER);
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
		BindingUtils.setupListViewListener(crystalsListView, tabCrystalsCenterPane, getAppContext());
		BindingUtils.setupListViewListener(diffractionsListView, tabDiffractionsCenterPane, getAppContext());

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

//			clearAndSetListViewItems(crystalsListView, rootModel.crystals);
			crystalsListView.setItems(rootModel.crystals);
			crystalsListView.getSelectionModel().selectFirst();

			clearAndSetListViewItems(diffractionsListView, rootModel.diffractions);

			initParametersTab(this.tabParameters);

			optimizationController.setRootModel(rootModel);

			XPathFactory xpf = XPathFactory.instance();
			XPathExpression<Element> expr = xpf.compile(INPUT_DATA_ELEMENT_NAME, Filters.element());

			List<Element> list = expr.evaluate(openedDocument);
			if (list.size() > 1) {
				// TODO report as warning
			}
			Element dataElement = list.isEmpty() ? null : list.get(0);
			String text = dataElement == null ? "" : dataElement.getTextTrim();
			configInputDataTable(text);

		} catch (Exception e) {
			throw new PopupErrorException(e, "Failed to parse the xml file [%s]", selectedFile);
		}
	}

	private void configInputDataTable(String data) {

		TableView<RowIndex> dataTableView = inputDataTableView;
		String[] columnNames = DATA_TABLE_COLUMNS;

		BindingUtils.initTableView(dataTableView, columnNames);

		TabularDataParser parser = new TabularDataParser();
		TableOfDoubles tabularData = parser.parse(data);

		dataTableView.getItems().addAll(tabularData.getRowIndexes());
	}

	public ParametersController initParametersTab(Tab tabParameters) {
		return initParametersTab(tabParameters, new SimpleObjectProperty<>(), new LinkedHashSet<>());
	}

	public ParametersController initParametersTab(Tab tabParameters,
			ObjectProperty<Map<String, ParUniqueElement>> fittedParamsProperty, Set<String> refinedParams) {
		ParametersModel parametersModel = new ParametersModel(rootModel);
		parametersModel.fittedParamsProperty = fittedParamsProperty;
		parametersModel.refinedParams = refinedParams;
		ParametersController controller = BindingUtils.loadViewAndInitController(getAppContext(), parametersModel,
				(view) -> tabParameters.setContent(view));

		tabParameters.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				controller.createAndSetParamTree();
			}
		});

		return controller;
	}

	private <I> void clearAndSetListViewItems(ListView<I> listView, List<I> items) {
		listView.getItems().clear();
		listView.getItems().addAll(items);
		listView.getSelectionModel().selectFirst();
	}

	private void configOptimizationTab() {
		OptimizaitonModel parametersModel = new OptimizaitonModel();
		optimizationController = BindingUtils.loadViewAndInitController(getAppContext(), parametersModel,
				(view) -> tabOptimization.setContent(view));

	}

	private void addPar(TreeItem<ParUniqueElement> parent, ParUniqueElement par) {
		parent.getChildren().add(new TreeItem<ParUniqueElement>(par));
	}

	private TreeItem<ParUniqueElement> createTreeItemParent(String tiName) {
		ParUniqueElement mockPar = new ParUniqueElement(tiName);
		mockPar.minProperty.set("");
		mockPar.maxProperty.set("");
		mockPar.valueProperty.set("");
		TreeItem<ParUniqueElement> crystal = new TreeItem<ParUniqueElement>(mockPar);
		return crystal;
	}

	void setBottomLabelText(String message, Object... args) {
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

		configureMStructExtensionFilters(fileChooser);

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
			configureCifExtensionFilters(fileChooser);

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
			configureMStructExtensionFilters(fileChooser);

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
		BindingUtils.loadViewAndInitController(getAppContext(), importedCrystalsModel, (view) -> dialogPane.setContent(view));

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

	public ListView<DiffractionModel> getDiffractionsListView() {
		return diffractionsListView;
	}

	public void setTabDiffractions(Tab tabDiffractions) {
		this.tabDiffractions = tabDiffractions;
	}

	public Tab getTabDiffractions() {
		return tabDiffractions;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	@Override
	public void setAppContext(AppContext context) {
		this.appContext = context;
	}

}
