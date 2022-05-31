package cz.kfkl.mstruct.gui.ui.optimization;

import java.util.List;

import cz.kfkl.mstruct.gui.model.PlotlyChartModel;
import cz.kfkl.mstruct.gui.model.instrumental.ExcludeXElement;
import cz.kfkl.mstruct.gui.ui.BaseController;
import cz.kfkl.mstruct.gui.ui.ObjCrystModel;
import cz.kfkl.mstruct.gui.ui.chart.PlotlyChartGenerator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class OptimizationEditRegionsController extends BaseController<PlotlyChartModel, OptimizationController> {
	@FXML
	BorderPane editRegionsChartPane;

	@Override
	public void init() {
		super.init();

		PlotlyChartModel model = getModelInstance();

		OptimizationController parentController = getParentController();
		ObjCrystModel rootModel = parentController.getModelInstance().getRootModel();
		List<ExcludeXElement> currentExcludeRegions = rootModel.getExcludeRegions();

		PlotlyChartGenerator chartGenerator = createChartGenerator(model.getActiveJob(), currentExcludeRegions);
		Node chartNode = model.createChartNode(chartGenerator);

		editRegionsChartPane.setCenter(chartNode);
	}

	private PlotlyChartGenerator createChartGenerator(OptimizationJob activeJob, List<ExcludeXElement> currentExcludeRegions) {
		PlotlyChartGenerator chartGenerator = new PlotlyChartGenerator(getAppContext());
		chartGenerator.forJob(activeJob);

		chartGenerator.useEditShapesTemplate();
		chartGenerator.setExcludeRegions(null);
		chartGenerator.setExcludeRegionsEdited(currentExcludeRegions);

		return chartGenerator;
	}

}
