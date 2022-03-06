package cz.kfkl.mstruct.gui.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import cz.kfkl.mstruct.gui.model.ImportedCrystalsModel;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import cz.kfkl.mstruct.gui.utils.validation.Validator;
import cz.kfkl.mstruct.gui.xml.XmlUtils;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class ImportedCrystalsController extends BaseController<ImportedCrystalsModel, MStructGuiController> {

	private static final String CRYSTAL_EL_NAME = "Crystal";

	@FXML
	private TableView<ImportedCrystal> importedCrystalsTableView;

	@FXML
	private TableColumn<ImportedCrystal, Boolean> importedCrystalsImportTableColumn;
	@FXML
	private TableColumn<ImportedCrystal, String> importedCrystalsNameTableColumn;
	@FXML
	private TableColumn<ImportedCrystal, Boolean> importedCrystalsExistsTableColumn;

	@FXML
	private ToggleGroup ifExistsToggleGroup;

	@Override
	public void init() {
		ImportedCrystalsModel model = getModelInstance();

		importedCrystalsTableView.setEditable(true);

		if (model.importedCrystals.size() == 1) {
			model.importedCrystals.get(0).selectedProperty.set(true);
		}

		SortedList<ImportedCrystal> sortedList = new SortedList<>(FXCollections.observableArrayList(model.importedCrystals));
		importedCrystalsTableView.setItems(sortedList);
		sortedList.comparatorProperty().bind(importedCrystalsTableView.comparatorProperty());

		importedCrystalsImportTableColumn.setCellValueFactory(cdf -> cdf.getValue().selectedProperty);
		importedCrystalsImportTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(importedCrystalsImportTableColumn));
		importedCrystalsImportTableColumn.setEditable(true);

		importedCrystalsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("crystalName"));
		// When variable is renamed, re-evaluate formula
		// coefficientValueTableColumn.setOnEditCommit(event -> parseFormula());

		importedCrystalsExistsTableColumn.setCellValueFactory(cdf -> cdf.getValue().existsProperty);
		importedCrystalsExistsTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(importedCrystalsExistsTableColumn));

		model.setIfExistsActionFromObject(ifExistsToggleGroup.getSelectedToggle().getUserData());
		ifExistsToggleGroup.selectedToggleProperty()
				.addListener((ov, o, n) -> model.setIfExistsActionFromObject(n.getUserData()));
	}

	public static List<ImportedCrystal> parseObjCrystXmlCrystals(File selectedFile) {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document openedDocument = builder.build(selectedFile);
			Element root = openedDocument.getRootElement();
			Validator.assertEquals(MStructGuiController.OBJ_CRYST, root.getName(),
					"Expected XML file with root element [%s], got [%s]", MStructGuiController.OBJ_CRYST, root.getName());

			List<ImportedCrystal> crystalsToImport = new ArrayList<>();

			for (Element cr : root.getChildren(CRYSTAL_EL_NAME)) {
				ImportedCrystal importedCrystal = new ImportedCrystal(cr);
				importedCrystal.setCrystalWithPreceedingBallast(XmlUtils.elementWithPreceedingBallast(root, cr));

				crystalsToImport.add(importedCrystal);
			}

			return crystalsToImport;

		} catch (Exception e) {
			throw new PopupErrorException(e, "Failed to parse the xml file [%s]", selectedFile);
		}
	}

}
