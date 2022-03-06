package cz.kfkl.mstruct.gui.model;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertTrue;

import java.util.List;

import cz.kfkl.mstruct.gui.ui.IfExistsAction;
import cz.kfkl.mstruct.gui.ui.crystals.ImportedCrystalsController;

public class ImportedCrystalsModel implements FxmlFileNameProvider<ImportedCrystalsController> {

	private static final String FXML_FILE_NAME = "importedCrystals.fxml";

	public List<ImportedCrystal> importedCrystals;

	private IfExistsAction ifExistsAction;

	public void setImportedCrystals(List<ImportedCrystal> importedCrystals) {
		this.importedCrystals = importedCrystals;
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	public void setIfExistsActionFromObject(Object userData) {
		assertTrue(userData instanceof String, "The user data must be set to valid IfExistsAction string.");
		this.ifExistsAction = IfExistsAction.valueOf((String) userData);
	}

	public IfExistsAction getIfExistsAction() {
		return ifExistsAction;
	}

}
