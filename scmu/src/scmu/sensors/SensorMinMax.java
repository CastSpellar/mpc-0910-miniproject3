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

public class SensorMinMax extends TemperatureSensor implements SensorMessageHandler {

	public int level,parentLevel = Integer.MAX_VALUE;
	private double lastSeenParent = 0;
	public EndPoint parentEndPoint = null;
	
	private HashMap<XY, EndPoint> neighbours;
	
	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;
	private double avg = 0.0;

	private double lastMessageTime = 0.0;
	
	private double lastMin = Double.MAX_VALUE;
	private double lastMax = Double.MIN_VALUE;
	private double lastAvg = 0.0;
	
	public SensorMinMax() {
		SensorDB.store(this) ;
	}
	
	public void init() {
		neighbours = new HashMap<XY, EndPoint>();
	
		new PeriodicTask(this, 1 + Simulation.rg.nextInt(20)) {
			public void run() {
				endpoint.broadcast( new PingMessage() ) ;
				if(parentEndPoint == null) {
					this.cancel();
				}
			}
		};
		
		new PeriodicTask(this, 1 + Simulation.rg.nextInt(50)) {
			public void run() {
				if(parentEndPoint != null) {
					endpoint.broadcast(new ParentMessage(level));
					//this.cancel();
				}
			}
		};

		new PeriodicTask(this, 1 + Simulation.rg.nextInt(30)) {
			public void run() {
				if(parentEndPoint != null) {
					max = Math.max(max, temperature());
					min = Math.min(min, temperature());
					avg = (avg + temperature()) / 2;
					if(Math.abs(max - lastMax) > 1.0 || Math.abs(min - lastMin) > 1.0 
						|| Math.abs(avg - lastAvg) > 1.0 || expired()){
						double distance = endpoint.address.pos.distance(parentEndPoint.address.pos);
						double power = radio().powerFromDistance(distance) + 5;
						power = power < 25 ? 25 : power > 100 ? 100 : power;
						radio().setPower(power);
						endpoint.broadcast(new TemperatureMinMaxMessage(parentEndPoint.address.pos,
											level, 
											min, 
											max, 
											avg));
						//radio().setPower(25);
						lastMessageTime = currentTime();
						lastMax = max;
						lastMin = min;
						lastAvg = avg;
						max = temperature();
						min = temperature();					
						avg = 0.0;
						radio().setPower(25);
					}
				}
			}
		};

		new PeriodicTask(this, 1) {
			public void run() {
				if(currentTime() - lastSeenParent > 30){
					parentEndPoint = null;
					level = Integer.MAX_VALUE;
					parentLevel = Integer.MAX_VALUE;
					radio().setPower(100);
				}
				if(expired()){	
					lastMessageTime = 0.0;
					lastMin = min = temperature();
					lastMax = max = temperature();
					lastAvg = 0.0;
				}
			}
		};
	}
	
	private boolean expired() {
		return currentTime() - lastMessageTime > 60;
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
		endpoint.latency(src);
		neighbours.put(endpoint.address.pos, src);
		
		//endpoint.address.pos.
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		canvas.sDraw( RGB.BLACK, String.format("%.0fºC", temperature()), address.pos.x - 10, address.pos.y - 10 ) ;
	}

	@Override
	public void onReceive(EndPoint src, ParentMessage parentMessage) {
		if(parentEndPoint == null){
			this.parentLevel = parentMessage.level;
			this.level = this.parentLevel+1;
			parentEndPoint = src;
			lastSeenParent = currentTime();
		}
		if (parentMessage.level < this.parentLevel){
			this.parentLevel = parentMessage.level;
			this.level = parentLevel+1;
			parentEndPoint = src;
			lastSeenParent = currentTime();
		} 
		if (parentMessage.level == parentLevel && endpoint.latency(src) < endpoint.latency(parentEndPoint)){
			this.parentLevel = parentMessage.level;
			this.level = this.parentLevel+1;
			this.parentEndPoint = src;
			lastSeenParent = currentTime();
		}		
	}

	@Override
	public void onReceive(EndPoint src, TemperatureMessage temperatureMessage) {
		//
	}

	@Override
	public void onReceive(EndPoint src, TemperatureMinMaxMessage temperatureMinMaxMessage) {
		//if(temperatureMinMaxMessage.level < level){
			this.max = Math.max(this.max, temperatureMinMaxMessage.max);
			this.min = Math.min(this.min, temperatureMinMaxMessage.min);
			if (this.avg == 0.0)
				this.avg = temperatureMinMaxMessage.avg;
			else
				this.avg = (this.avg + temperatureMinMaxMessage.avg) / 2;
		//}
			if(parentEndPoint == null){
				this.parentLevel = temperatureMinMaxMessage.level;
				this.level = this.parentLevel+1;
				parentEndPoint = src;
				lastSeenParent = currentTime();
			} else
				if(src.address.pos.equals(parentEndPoint.address.pos))
					lastSeenParent = currentTime();
			if (temperatureMinMaxMessage.level < this.parentLevel){
				this.parentLevel = temperatureMinMaxMessage.level;
				this.level = parentLevel+1;
				parentEndPoint = src;
				lastSeenParent = currentTime();
			} 
			if (temperatureMinMaxMessage.level == parentLevel && endpoint.latency(src) < endpoint.latency(parentEndPoint)){
				this.parentLevel = temperatureMinMaxMessage.level;
				this.level = this.parentLevel+1;
				this.parentEndPoint = src;
				lastSeenParent = currentTime();
			}
	}

}
