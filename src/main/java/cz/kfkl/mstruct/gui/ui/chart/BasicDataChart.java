/* ----------------------------
 * CrosshairOverlayFXDemo1.java
 * ----------------------------
 * Copyright (c) 2014-2021, Object Refinery Limited.
 * All rights reserved.
 *
 * https://github.com/jfree/jfree-fxdemos
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the Object Refinery Limited nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL OBJECT REFINERY LIMITED BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package cz.kfkl.mstruct.gui.ui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.fx.overlay.CrosshairOverlayFX;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;

/**
 * A demo showing crosshairs that follow the data points on an XYPlot.
 */
public class BasicDataChart {

	private static final Logger LOG = LoggerFactory.getLogger(BasicDataChart.class);

	private String title = null;
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private XYSeriesCollection datasetPoints = new XYSeriesCollection();

	class BasicDataChartPane extends StackPane implements ChartMouseListenerFX {

		private ChartViewer chartViewer;

		private Crosshair xCrosshair;

		private Crosshair yCrosshair;

		public BasicDataChartPane() {
			JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y", dataset);

			this.chartViewer = new ChartViewer(chart);
			this.chartViewer.addChartMouseListener(this);
			getChildren().add(this.chartViewer);

			CrosshairOverlayFX crosshairOverlay = new CrosshairOverlayFX();
			this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
			this.xCrosshair.setStroke(
					new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 2.0f, 2.0f }, 0));
			this.xCrosshair.setLabelVisible(true);
			this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
			this.yCrosshair.setStroke(
					new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 2.0f, 2.0f }, 0));
			this.yCrosshair.setLabelVisible(true);
			crosshairOverlay.addDomainCrosshair(xCrosshair);
			crosshairOverlay.addRangeCrosshair(yCrosshair);

//			Plot plot = chart.getPlot();
			XYPlot xyPlot = chart.getXYPlot();
			XYLineAndShapeRenderer xyItemRenderer = new XYLineAndShapeRenderer(false, true);
//			Shape shape;
//			xyItemRenderer.setDefaultShape(shape);
			Shape defaultShape = xyItemRenderer.getDefaultShape();

			xyPlot.setDataset(1, datasetPoints);
			xyPlot.setRenderer(1, xyItemRenderer);

			Platform.runLater(() -> {
				this.chartViewer.getCanvas().addOverlay(crosshairOverlay);
			});
		}

		@Override
		public void chartMouseClicked(ChartMouseEventFX event) {
			// ignore
		}

		@Override
		public void chartMouseMoved(ChartMouseEventFX event) {
			Rectangle2D dataArea = this.chartViewer.getCanvas().getRenderingInfo().getPlotInfo().getDataArea();
			JFreeChart chart = event.getChart();
			XYPlot plot = (XYPlot) chart.getPlot();
			ValueAxis xAxis = plot.getDomainAxis();
			ValueAxis yAxis = plot.getRangeAxis();

			LOG.trace("   yTr: {}", event.getTrigger().getY());
			double xM = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
			double yM = yAxis.java2DToValue(event.getTrigger().getY(), dataArea, RectangleEdge.LEFT);
			// make the crosshairs disappear if the mouse is out of range
			if (!xAxis.getRange().contains(xM)) {
				xM = Double.NaN;
			}
			if (!yAxis.getRange().contains(yM)) {
				yM = Double.NaN;
			}

			LOG.trace("   yM: {}", yM);

			this.xCrosshair.setValue(xM);

			double y1 = DatasetUtils.findYValue(plot.getDataset(), 0, xM);
			double y2 = DatasetUtils.findYValue(plot.getDataset(), 1, xM);

			LOG.trace("   y1: {}", y1);
			LOG.trace("   y2: {}", y2);
			LOG.trace("Math.abs(yM - y1): {}", Math.abs(yM - y1));
			LOG.trace("Math.abs(yM - y2)): {}", Math.abs(yM - y2));
			if (Math.abs(yM - y1) < Math.abs(yM - y2)) {
				this.yCrosshair.setValue(y1);
				LOG.trace("y1");
			} else {
				this.yCrosshair.setValue(y2);
				LOG.trace("y2");
			}

		}

	}

	public void addSeries(XYSeries series) {
		dataset.addSeries(series);
	}

	public void addPointsSeries(XYSeries series) {
		datasetPoints.addSeries(series);
	}

	public BasicDataChartPane createChart() {
		return new BasicDataChartPane();
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
