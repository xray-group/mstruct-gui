package cz.kfkl.mstruct.gui.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.core.AppContext;
import cz.kfkl.mstruct.gui.model.instrumental.ExcludeXElement;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.ui.chart.PlotlyChartGenerator;
import cz.kfkl.mstruct.gui.ui.optimization.OptimizationEditRegionsController;
import cz.kfkl.mstruct.gui.ui.optimization.OptimizationJob;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class PlotlyChartModel implements FxmlFileNameProvider<OptimizationEditRegionsController> {
	private static final Logger LOG = LoggerFactory.getLogger(PlotlyChartModel.class);

	private static final String FXML_FILE_NAME = "optimizationEditRegions.fxml";

	// may be null if the file is not loaded
	private ObjCrystModel rootModel;

	private OptimizationJob activeJob;

	private WebEngine webEngine;

	boolean excludeRegionsEdited;

	private DecimalFormat excludeXFormatter;

	public PlotlyChartModel(OptimizationJob activeJob, AppContext appContext) {
		this.activeJob = activeJob;

		this.excludeXFormatter = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		// TODO set from property
		this.excludeXFormatter.setMaximumFractionDigits(2);
	}

	public Node createChartNode(PlotlyChartGenerator chartGenerator) {
		Node chartNode;

		String errMessage = chartGenerator.nothingToExportMessage();
		if (errMessage == null) {

			WebView webView = new WebView();
			webEngine = webView.getEngine();

			if (LOG.isDebugEnabled()) {
				webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
					@Override
					public void changed(ObservableValue ov, State oldState, State newState) {
						LOG.debug("WebEngine state: {}", newState);
					}
				});
			}
			webEngine.loadContent(chartGenerator.exportedData());
			webView.setMaxHeight(Double.POSITIVE_INFINITY);

			chartNode = webView;

			if (LOG.isTraceEnabled()) {
				ChangeListener<Number> sizeChangeListener = (observable, oldValue, newValue) -> {
					LOG.trace("WebView height: {}, width: {}", webView.getHeight(), webView.getWidth());
				};

				webView.widthProperty().addListener(sizeChangeListener);
				webView.heightProperty().addListener(sizeChangeListener);
			}

		} else {
			chartNode = new Text(errMessage);
		}
		return chartNode;
	}

	public List<ExcludeXElement> retrieveExcludedRegions() {

		List<ExcludeXElement> regions = new ArrayList<>();
		if (webEngine != null) {
			Object result = webEngine.executeScript("shapes");
			LOG.debug("WebEngine shapes object: {}", result);
			if (result instanceof JSObject) {
				JSObject resultJs = (JSObject) result;

				Object lengthObj = resultJs.getMember("length");
				LOG.debug("shapes lengthObj: {}", lengthObj);

				int length = (Integer) lengthObj;
				for (int i = 0; i < length; i++) {
					Object slot = resultJs.getSlot(i);
					if (slot instanceof JSObject) {
						JSObject slotJs = (JSObject) slot;
						Object x0 = slotJs.getMember("x0");
						Object x1 = slotJs.getMember("x1");

						LOG.debug("Shapes [{}]: x0=[{}], x1=[{}]", lengthObj, x0, x1);

						if (x0 instanceof Number && x1 instanceof Number) {
							ExcludeXElement region = new ExcludeXElement();
							region.fromProperty.set(formatExcludeX(x0));
							region.toProperty.set(formatExcludeX(x1));
							regions.add(region);
						}
					}
				}

			}
		}

		return regions;
	}

	private String formatExcludeX(Object x0) {
		return excludeXFormatter.format(((Number) x0).doubleValue());
	}

	public ObjCrystModel getRootModel() {
		return rootModel;
	}

	public void setRootModel(ObjCrystModel rootModel) {
		this.rootModel = rootModel;
	}

	@Override
	public String getFxmlFileName() {
		return FXML_FILE_NAME;
	}

	public void setActiveJob(OptimizationJob activeJob) {
		this.activeJob = activeJob;
	}

	public OptimizationJob getActiveJob() {
		return activeJob;
	}

}
