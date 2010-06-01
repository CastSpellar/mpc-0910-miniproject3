package scmu.sensors;

import java.util.HashMap;

import scmu.sensors.sys.*;
import scmu.sensors.temperature.TemperatureSensor;
import simsim.core.EndPoint;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.XY;

public class SensorData extends TemperatureSensor implements SensorMessageHandler {

	//public HashMap<EndPoint, V> neighbours;
	

	private HashMap<XY, Double> leafTemperatures;
	
	public int mylevel,parentLevel = 999;
	public EndPoint parentEndPoint = null;
	
	public SensorData() {
		SensorDB.store(this) ;
	}
	
	public void init() {
	/*
		new PeriodicTask(this, 1 + Simulation.rg.nextInt(20)) {
			public void run() {
				if(parentEndPoint == null) {
					endpoint.broadcast( new PingMessage(temperature()) ) ;
				}
				//radio().setPower(0.5);
			}
		};
		*/
		new PeriodicTask(this, 1 + Simulation.rg.nextInt(20)) {
			public void run() {
				if(parentEndPoint != null) {
					endpoint.broadcast(new ParentMessage(mylevel));
					this.cancel();
					//endpoint.broadcast( new PingMessage(temperature()) ) ;
				}
				//radio().setPower(0.5);
			}
		};

		new PeriodicTask(this, 1 + Simulation.rg.nextInt(600)) {
			public void run() {
				if(parentEndPoint != null) {
					endpoint.broadcast(new TemperatureMessage(parentEndPoint.address.pos,endpoint.address.pos, mylevel,temperature()));
					//endpoint.broadcast( new PingMessage(temperature()) ) ;
				}
				//radio().setPower(0.5);
			}
		};
		
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
		//ignore
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		RGB color = (parentEndPoint == null) ? RGB.BLACK : RGB.BLUE;
		canvas.sDraw( color, String.format("%.0fºC", temperature()), address.pos.x - 10, address.pos.y - 10 ) ;
	}

	@Override
	public void onReceive(EndPoint src, ParentMessage parentMessage) {
		if(parentEndPoint == null){
			parentLevel = parentMessage.level;
			mylevel = parentLevel+1;
			parentEndPoint = src;
		}
		if (parentMessage.level < parentLevel){
			parentLevel = parentMessage.level;
			mylevel = parentLevel+1;
			parentEndPoint = src;
			//endpoint.broadcast(new TemperatureMessage(endpoint.address.pos, mylevel,temperature()));
		} 
		if (parentMessage.level == parentLevel && endpoint.latency(src) < endpoint.latency(parentEndPoint)){
			parentLevel = parentMessage.level;
			mylevel = parentLevel+1;
			parentEndPoint = src;
			//endpoint.address.pos.distance(parentEndPoint.address.pos);
			//endpoint.broadcast(new TemperatureMessage(endpoint.address.pos, mylevel,temperature()));
		}		
		else {
			//endpoint.broadcast(new ParentMessage(mylevel));
		}
	}

	@Override
	public void onReceive(EndPoint src, TemperatureMessage temperatureMessage) {
		if(temperatureMessage.level > mylevel && endpoint.address.pos.equals(temperatureMessage.router)){
			temperatureMessage.level = mylevel;
			temperatureMessage.router = parentEndPoint.address.pos;

			leafTemperatures.put(temperatureMessage.pos, temperatureMessage.temperature);
			//endpoint.broadcast(temperatureMessage);
		}
			
	}

	@Override
	public void onReceive(EndPoint src,
			TemperatureMinMaxMessage temperatureMinMaxMessage) {
		// TODO Auto-generated method stub
		
	}
}
