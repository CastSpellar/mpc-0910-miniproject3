package scmu.sensors;

import scmu.sensors.sys.*;
import scmu.sensors.temperature.TemperatureSensor;
import simsim.core.EndPoint;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;

public class SensorData extends TemperatureSensor implements SensorMessageHandler {

	public SensorData() {
		SensorDB.store(this) ;
	}
	
	public void init() {
	
		new PeriodicTask(this, 1 + Simulation.rg.nextInt(20)) {
			public void run() {
				endpoint.broadcast( new PingMessage() ) ;
			}
		};
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		canvas.sDraw( RGB.BLACK, String.format("%.0fºC", temperature()), address.pos.x - 10, address.pos.y - 10 ) ;
	}
}