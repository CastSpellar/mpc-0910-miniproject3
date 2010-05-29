package scmu.sensors;

import scmu.sensors.sys.*;
import scmu.sensors.temperature.TemperatureSensor;
import simsim.core.EndPoint;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;

public class SensorData extends TemperatureSensor implements SensorMessageHandler {

	public int mylevel,parentLevel = 999;
	public EndPoint parentEndPoint = null;
	
	public SensorData() {
		SensorDB.store(this) ;
	}
	
	public void init() {
	
		new PeriodicTask(this, 1 + Simulation.rg.nextInt(20)) {
			public void run() {
				endpoint.broadcast( new PingMessage(temperature()) ) ;
			}
		};
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		canvas.sDraw( RGB.BLACK, String.format("%.0fºC", temperature()), address.pos.x - 10, address.pos.y - 10 ) ;
	}

	@Override
	public void onReceive(EndPoint src, ParentMessage parentMessage) {
		if(parentEndPoint == null){
			parentLevel = parentMessage.level;
			mylevel = parentLevel+1;
			parentEndPoint = src;
		}
		if (parentMessage.level <= parentLevel && endpoint.latency(src) < endpoint.latency(parentEndPoint)){
			parentLevel = parentMessage.level;
			mylevel = parentLevel+1;
			parentEndPoint = src;
			endpoint.broadcast(new TemperatureMessage(endpoint.address.pos, mylevel,temperature()));
		} else {
			endpoint.broadcast(new ParentMessage(mylevel));
		}
	}

	@Override
	public void onReceive(EndPoint src, TemperatureMessage temperatureMessage) {
		if(temperatureMessage.level > mylevel)
			endpoint.broadcast(temperatureMessage);
	}
}
