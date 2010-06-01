package scmu.sensors;

import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.TIME;
import static simsim.core.Simulation.DisplayFlags.TRAFFIC;
import static simsim.core.Simulation.DisplayFlags.NETWORK;

import java.util.EnumSet;
import scmu.sensors.sys.*;
import scmu.sensors.temperature.*;
import simsim.core.*;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYLineChart;
import simsim.gui.geom.XY;

public class MainMinMax extends Simulation  {

	public static final int TOTAL_SENSORS = 150;
	
	MainMinMax() {
		super(5, EnumSet.of(SIMULATION, TIME, TRAFFIC, NETWORK));
	}

	MainMinMax init() {
		
		Gui.setFrameRectangle("MainFrame", 0, 0, 600, 600);		
		Gui.setFrameTransform("MainFrame", 1000, 1000, 0, false) ;
			
		final BaseStationMinMax bs = new BaseStationMinMax();
		XY xy =  new XY( 10, 990);
		bs.address.pos = xy;
		
		// Create the simulation sensors
		for (int i = 0; i < TOTAL_SENSORS; i++)
			new SensorMinMax();

		// Initialize the simulation sensors
		for (AbstractSensor i : SensorDB.sensors())
			i.init();

		final XYLineChart chart = new XYLineChart("Live Sensors", 5.0, "Live Sensors (%)", "time(s)") ;
		chart.setYRange( true, 0, 100 ) ;
		chart.setSeriesLinesAndShapes("s0", true, false) ;
		chart.chart().removeLegend() ;
		
		Gui.setFrameRectangle("Live Sensors", 600, 0, 300, 300);
		
		final XYLineChart chart2 = new XYLineChart("Temperature", 5.0, "Temp error", "time(s)") ;
		chart2.setYRange( true, 0, 20 ) ;
		chart2.setSeriesLinesAndShapes("Min", true, false) ;
		chart2.setSeriesLinesAndShapes("Max", true, false) ;
		chart2.setSeriesLinesAndShapes("Med", true, false) ;
		Gui.setFrameRectangle("Temperature", 600, 304, 300, 300);
		
		new PeriodicTask(30) {
			public void run() {
				int n = -1 ;
				double maxTemp = Double.MIN_VALUE;
				double minTemp = Double.MAX_VALUE;
				double medTemp = 0;
					
				for( AbstractSensor i : SensorDB.sensors() ) {
					if( i.isOnline() )
						n++ ;
					if( i instanceof TemperatureSensor) {
						double temp = ((TemperatureSensor)i).temperature();
						if( temp < minTemp)
							minTemp = temp;
						if( temp > maxTemp)
							maxTemp = temp;
						medTemp += temp;
					}
				}
				System.out.println("***************************************************");
				System.out.println("********************* Real ************************");
				System.out.println("	Min: "+ minTemp+" 	Max: "+maxTemp+" 	Avg: "+medTemp / TOTAL_SENSORS);
				System.out.println("****************** Base Station *******************");
				System.out.println("	Min: "+ bs.getMinTemp()+" 	Max: "+bs.getMaxTemp()+" 	Avg: "+bs.getMidTemp());				
				System.out.println("***************************************************");
				
				chart.getSeries("s0").add( currentTime(), 100 * n / TOTAL_SENSORS) ;

				chart2.getSeries("Min").add( currentTime(), Math.abs( minTemp - bs.getMinTemp())) ;

				chart2.getSeries("Max").add( currentTime(), Math.abs( maxTemp - bs.getMaxTemp())) ;
				chart2.getSeries("Med").add( currentTime(), Math.abs( (medTemp/ TOTAL_SENSORS) - bs.getMidTemp())) ;

				if( n == 0 ) {
					new Task(30) {
						public void run() {
							stop() ;
							try {
								chart.saveChartToPDF("liveSensors.pdf", 800, 800) ;
								chart2.saveChartToPDF("temperature.pdf", 800, 800) ;
							} catch( Exception x ) {
								x.printStackTrace() ;
							}
							
						}
					};
				}
			}
		};
		
		super.setSimulationMaxTimeWarp(1000);
		
		Gui.setDesktopSize(920, 650) ;
		return this;
	}

	public void displayOn( Canvas canvas ) {
		TemperatureField.displayOn(canvas) ;

		for( AbstractSensor i : SensorDB.sensors() )
			i.displayOn(canvas) ;
		
	}
	
	public static void main(String[] args) throws Exception {

		Globals.set("Traffic_DisplayDeadPackets", true ) ;
		Globals.set("Traffic_DeadPacketHistory", 1.0) ;

		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 1L);

		Globals.set("Net_Euclidean_NodeRadius", 10.0);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 20.0);
	

		new MainMinMax().init().start();
	}
}
