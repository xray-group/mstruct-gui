package cz.kfkl.mstruct.gui.ui;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.google.common.io.MoreFiles;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.utils.validation.PopupErrorException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CrystalCifImportJob extends Job implements TextBuffer {

	private File resultDir;
	private File inputFile;

	private StringBuilder consoleAndJobLogStringBuilder;

	private ObjectProperty<TableOfDoubles> tabularDataProperty = new SimpleObjectProperty<>();

	private JobRunner jobRunner;
	private String outFileName;
	private List<ImportedCrystal> importedCrystals;

	public CrystalCifImportJob(AppContext context) {
		super(context);
		this.consoleAndJobLogStringBuilder = new StringBuilder();
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
		return consoleAndJobLogStringBuilder.toString();
	}

	/**
	 * Is called from a separate, non FX thread.
	 */
	public void processOutputFiles() {
		File outDatFile = new File(resultDir, outFileName);
		if (outDatFile.exists()) {
			importedCrystals = ImportedCrystalsController.parseObjCrystXmlCrystals(outDatFile);
		} else {
			this.failedLater("The output file [%s] doesn't exist, full path: %s", outDatFile.getName(), outDatFile);
		}
	}

	public List<ImportedCrystal> getImportedCrystals() {
		return importedCrystals;
	}

	public ObjectProperty<TableOfDoubles> getTabularDataProperty() {
		return tabularDataProperty;
	}

	protected List<String> getCommandList() {
		this.outFileName = changeFileExtension(inputFile, "xml").getName();
		return List.of(getContext().getFoxExeFile().getAbsolutePath(), inputFile.getAbsolutePath(), "--nogui", "-o", outFileName);
	}

	public File changeFileExtension(File file, String newExtension) {
		Path selectedFilePath = file.toPath();
		String nameWithoutExtension = MoreFiles.getNameWithoutExtension(selectedFilePath);

		File outFile = new File(file.getParentFile(), nameWithoutExtension + "." + newExtension);
		return outFile;
	}

	public void startJob() {
		this.jobRunner = new JobRunner(this);
		jobRunner.setCommandList(getCommandList());
		jobRunner.setWorkingDir(this.getResultDir());
		jobRunner.logProcessToConsole(this);
		jobRunner.setRemoveWorkingDirectory(true);

		jobRunner.start();

		try {
			jobRunner.waitForRes(5);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new PopupErrorException(e, "FOX has failed to convert Crystal CIF file [%s] to crystal XML.", getInputFile());

		}
	}

	@Override
	protected void jobDoneAction() {
		this.processOutputFiles();
	}

	@Override
	protected void doTerminate() {
		jobRunner.terminate();
	}

	@Override
	public void appendText(String text) {
		consoleAndJobLogStringBuilder.append(text);
	}

}
