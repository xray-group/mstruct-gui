package cz.kfkl.mstruct.gui.core;

import static cz.kfkl.mstruct.gui.utils.validation.Validator.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.ui.MStructGuiController;
import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.config.JvProperties;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;

public class AppContext {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static final Logger LOG = LoggerFactory.getLogger(AppContext.class);
	private final static boolean IS_TEST_RUN = true;

//	private static final String PLOTLY_JS_PATH = "/cz/kfkl/mstruct/gui/ui/chart/plotly-latest.min.js";
//
//	private static final String DEFAULT_HTML_GUI_TEMPLATE_PATH = "/cz/kfkl/mstruct/gui/ui/chart/DefaultPlotlyChartTemplate_GUI.html";
//	private static final String DEFAULT_HTML_EXPORT_TEMPLATE_PATH = "/cz/kfkl/mstruct/gui/ui/chart/DefaultPlotlyChartTemplate_Export.html";

	private static final String PLOTLY_JS_PATH = "/js/plotly-latest.min.js";

	private static final String DEFAULT_HTML_GUI_TEMPLATE_PATH = "/DefaultPlotlyChartTemplate_GUI.html";
	private static final String DEFAULT_HTML_EDIT_TEMPLATE_PATH = "/DefaultPlotlyChartTemplate_EditShapes.html";
	private static final String DEFAULT_HTML_EXPORT_TEMPLATE_PATH = "/DefaultPlotlyChartTemplate_Export.html";

	private File appLocationDir;
	private File lastSelectedFileDirectory;
	private File lastSelectedCifFileDirectory;
	private File lastSelectedDatFileDirectory;

	private MStructGuiController mainController;
	private File foxExeFile;
	private File mstructExeFile;
	private File autoOpenFile;

	private File plotlyChartGuiTemplateFile;
	private File plotlyChartEditShapesTemplateFile;
	private File plotlyChartExportTemplateFile;

	private String guiTemplate;
	private String editShapesTemplate;
	private String exportTemplate;

	private boolean showExportedChart = true;
	private boolean confirmFileClose = true;
	private boolean confirmComponentRemove = true;

	public void init() {

		initDirectories();
		loadProperties();
	}

	private void initDirectories() {
		String userDir = System.getProperty("user.dir");

		LOG.info("System working directory: {}", userDir);

		setAppLocationDir(new File(userDir));
	}

	public void loadProperties() {

		Properties props = new Properties();
		File propetiesFile = new File(appLocationDir, "MStructGUI.properties");
		if (propetiesFile.exists()) {
			try {
				props.load(new FileInputStream(propetiesFile));
				LOG.info("Properties loaded from [{}]", propetiesFile.getAbsoluteFile());
			} catch (IOException e) {
				LOG.error("Failed to load [{}] due to: {}", propetiesFile.getAbsoluteFile(), e);
			}
		} else {
			LOG.warn("Properties file [{}] doesn't exist.", propetiesFile.getAbsoluteFile());
		}

		processProperties(props);
	}

	private void processProperties(Properties props) {
		foxExeFile = findExeFile(props.getProperty("fox.exe.path"), "FOX exe",
				new File(new File(appLocationDir, "Fox-2017-06-28"), "Fox-2017-06-28.exe"));

		mstructExeFile = findExeFile(props.getProperty("mstruct.exe.path"), "MStruct exe",
				new File(new File(appLocationDir, "mstruct_xml"), "mstruct_xml.exe"));

		autoOpenFile = findReadableFile(props, "mstruct.gui.open.on.startup");
		plotlyChartGuiTemplateFile = findReadableFile(props, "mstruct.gui.plotly.chart.gui.template");
		plotlyChartEditShapesTemplateFile = findReadableFile(props, "mstruct.gui.plotly.chart.edit.template");
		plotlyChartExportTemplateFile = findReadableFile(props, "mstruct.gui.plotly.chart.export.template");

		showExportedChart = parseBoolean(props, "mstruct.gui.plotly.chart.show.exported", Boolean.TRUE);
		confirmFileClose = parseBoolean(props, "mstruct.gui.confirm.file.close", Boolean.TRUE);
		confirmComponentRemove = parseBoolean(props, "mstruct.gui.confirm.component.remove", Boolean.TRUE);
	}

	private boolean parseBoolean(Properties props, String propertyName, Boolean defaultValue) {
		boolean res = defaultValue.booleanValue();

		String val = props.getProperty(propertyName);
		if (JvStringUtils.isNotBlank(val)) {
			res = Boolean.getBoolean(val);
		}

		return res;
	}

	private File findReadableFile(Properties props, String propName) {
		String filePath = props.getProperty(propName);

		File confirmedFile = null;
		if (JvStringUtils.isNotBlank(filePath)) {
			File openFile = new File(filePath);
			if (openFile.exists()) {
				if (openFile.canRead()) {
					confirmedFile = openFile;
				} else {
					LOG.warn("The file [{}] is not readable.", openFile);
				}
			} else {
				LOG.warn("The file [{}] defined by [" + propName + "] property with value [{}] doesn't exist.", openFile,
						filePath);
			}
		}
		return confirmedFile;
	}

	private File findExeFile(String exePath, String fileDescription, File defaultFile) {
		File exeFile = null;
		if (exePath == null) {
			exeFile = defaultFile;
		} else {
			exeFile = new File(exePath);
		}
		LOG.info("The {} file is [{}].", fileDescription, exeFile);

		if (exeFile.exists()) {
			if (!exeFile.canExecute()) {
				LOG.warn("The {} file [{}] is not executable.", fileDescription, exeFile);
			}
		} else {
			LOG.warn("The {} file [{}] doesn't exist.", fileDescription, exeFile);
		}
		return exeFile;
	}

	public File getLastSelectedFileDirectoryOrDefault() {
		File dir = null;
		if (lastSelectedFileDirectory != null) {
			dir = lastSelectedFileDirectory;
		} else if (appLocationDir != null) {
			dir = appLocationDir;
		}
		return dir;
	}

	public void setLastSelectedFileDirectory(File lastSelectedFileDirectory) {
		this.lastSelectedFileDirectory = lastSelectedFileDirectory;
	}

	public File getLastSelectedCifFileDirectoryOrDefault() {
		File dir = null;
		if (lastSelectedCifFileDirectory == null) {
			dir = getLastSelectedFileDirectoryOrDefault();
		} else {
			dir = lastSelectedCifFileDirectory;
		}
		return dir;
	}

	public void setLastSelectedCifFileDirectory(File lastSelectedCifFileDirectory) {
		this.lastSelectedCifFileDirectory = lastSelectedCifFileDirectory;
	}

	public File getLastSelectedDatFileDirectoryOrDefault() {
		File dir = null;
		if (lastSelectedDatFileDirectory == null) {
			dir = getLastSelectedFileDirectoryOrDefault();
		} else {
			dir = lastSelectedDatFileDirectory;
		}
		return dir;
	}

	public void setLastSelectedDatFileDirectory(File lastSelectedDatFileDirectory) {
		this.lastSelectedDatFileDirectory = lastSelectedDatFileDirectory;
	}

	public String loadPlotlyJs() {
		return readResourceAsString(PLOTLY_JS_PATH);
	}

	public String loadGuiTemplate() {
		if (guiTemplate == null) {
			File userTemplateFile = getPlotlyChartGuiTemplateFile();
			guiTemplate = loadTemplateOrDefault(userTemplateFile, DEFAULT_HTML_GUI_TEMPLATE_PATH);
		}

		return guiTemplate;
	}

	public String loadEditShapesTemplate() {
		if (editShapesTemplate == null) {
			File userTemplateFile = getPlotlyChartGuiTemplateFile();
			editShapesTemplate = loadTemplateOrDefault(userTemplateFile, DEFAULT_HTML_EDIT_TEMPLATE_PATH);
		}

		return editShapesTemplate;
	}

	public String loadExportTemplate() {
		if (exportTemplate == null) {
			File userTemplateFile = getPlotlyChartExportTemplateFile();
			exportTemplate = loadTemplateOrDefault(userTemplateFile, DEFAULT_HTML_EXPORT_TEMPLATE_PATH);
		}

		return exportTemplate;
	}

	private String loadTemplateOrDefault(File userTemplateFile, String defaultTemplatePath) {
		String templ = null;
		if (userTemplateFile != null) {
			templ = initTemplateFromUserFile(userTemplateFile);
		}

		if (templ == null) {
			templ = readResourceAsString(defaultTemplatePath);
		}
		return templ;
	}

	private String initTemplateFromUserFile(File userTemplateFile) {
		String template = null;
		try {
			template = Files.readString(userTemplateFile.toPath());
		} catch (IOException e) {
			LOG.error(String.format("Failed to read HTML template file [%s], will use default template.", userTemplateFile), e);
		}
		return template;
	}

	public static String readResourceAsString(String path) {
		try {
			InputStream resourceAsStream = AppContext.class.getResourceAsStream(path);
			assertNotNull(resourceAsStream, "No resource found for path [%s]", path);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			resourceAsStream.transferTo(bos);

			return bos.toString(UTF8_CHARSET);

		} catch (Exception e) {
			throw new UnexpectedException(e, "Failed to read resource from [%s]", path);
		}
	}

	public File getAppLocationDir() {
		return appLocationDir;
	}

	public void setAppLocationDir(File appLocationDir) {
		this.appLocationDir = appLocationDir;
	}

	public void setMainController(MStructGuiController mc) {
		this.mainController = mc;
	}

	public MStructGuiController getMainController() {
		return mainController;
	}

	public File getFoxExeFile() {
		return foxExeFile;
	}

	public File getMstructExeFile() {
		return mstructExeFile;
	}

	public File getAutoOpenFile() {
		return autoOpenFile;
	}

	public File getPlotlyChartGuiTemplateFile() {
		return plotlyChartGuiTemplateFile;
	}

	public File getPlotlyChartEditShapesTemplateFile() {
		return plotlyChartEditShapesTemplateFile;
	}

	public File getPlotlyChartExportTemplateFile() {
		return plotlyChartExportTemplateFile;
	}

	public boolean isTestRun() {
		return IS_TEST_RUN;
	}

	public boolean showExportedChart() {
		return showExportedChart;
	}

	public boolean confirmFileClose() {
		return confirmFileClose;
	}

	public boolean confirmComponentRemove() {
		return confirmComponentRemove;
	}

}
