package simsim.gui.charts;

import java.util.*;

import org.jfree.chart.*;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;

/**
 * A convenience wrapper class for using simple XYLineChart charts of the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XYLineChart extends XYChart<XYSeries> {

	public XYLineChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		super( frame, fps, xAxisLabel, yAxisLabel) ;
	}

	protected void createChart() {
		data = new XYSeriesCollection();
		series = new HashMap<String,XYSeries>() ;
		renderer = new XYLineAndShapeRenderer();		
		chart = ChartFactory.createXYLineChart( name, yAxisLabel, xAxisLabel, data, PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setRenderer( renderer);

	}
	
	public void setSeriesLinesAndShapes( String name, boolean visibleLines, boolean shapesVisible ) {
		int i = getSeriesIndex( name ) ;
		((XYLineAndShapeRenderer)renderer).setSeriesLinesVisible(i, visibleLines);
		((XYLineAndShapeRenderer)renderer).setSeriesShapesVisible(i, shapesVisible);
	}

	protected XYSeries createSeries(String name) {
		XYSeries s = new XYSeries(name) ;
		((XYSeriesCollection)data).addSeries(s) ;
		return s;
	}
}