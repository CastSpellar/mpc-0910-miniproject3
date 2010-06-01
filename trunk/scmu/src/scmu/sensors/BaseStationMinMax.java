package scmu.sensors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import scmu.sensors.sys.*;
import scmu.sensors.temperature.*;
import simsim.core.EndPoint;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.*;

public class BaseStationMinMax extends TemperatureBaseStation implements SensorMessageHandler {

	private HashMap<EndPoint, TemperatureMinMaxMessage> temperaturesReceived;
	private HashMap<EndPoint, Double> neighbours;
	
	public BaseStationMinMax( XY xy) {
		super( xy);
		SensorDB.store(this) ;
	}
	
	public BaseStationMinMax() {
		SensorDB.store(this) ;
	}
	
	public void init() {
		temperaturesReceived = new HashMap<EndPoint, TemperatureMinMaxMessage>();
		neighbours = new HashMap<EndPoint, Double>();

		new PeriodicTask(this, 1,1 + Simulation.rg.nextInt(60),RGB.BLUE) {
			public void run() {
				endpoint.broadcast(new ParentMessage(0));
			}
		};
		new PeriodicTask(this, 1,1 + Simulation.rg.nextInt(60)) {
			public void run() {
				temperaturesReceived.clear();
			}
		};
		new PeriodicTask(this, 1,100) {
			public void run() {
				Set<Entry<EndPoint,Double>> set = neighbours.entrySet();
			    Iterator i = set.iterator();
			    while(i.hasNext()){
			      Map.Entry me = (Map.Entry)i.next();
			      double time = (Double)me.getValue();
			      EndPoint endpoint = (EndPoint)me.getKey();
			      if(currentTime() - time > 100)
				      temperaturesReceived.remove(endpoint); 
			    }
			    

				//for (Double time : values())
					
			}
		};
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
		neighbours.put(src, currentTime());
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		
		canvas.sFill( RGB.BLUE, new Rectangle( address.pos.x, address.pos.y, 13.0, 13.0 ) ) ;
		canvas.sDraw( RGB.BLACK, String.format("BS"), address.pos.x - 10, address.pos.y - 10 ) ;
	}

	@Override
	public void onReceive(EndPoint src, ParentMessage forwardMessage) {
		neighbours.put(src, currentTime());
		endpoint.broadcast(new ParentMessage(0));
	}

	@Override
	public void onReceive(EndPoint src, TemperatureMessage temperatureMessage) {
	}

	@Override
	public void onReceive(EndPoint src,
			TemperatureMinMaxMessage temperatureMinMaxMessage) {
		
		//resetTemperatures();
		temperaturesReceived.put(src, temperatureMinMaxMessage);
		neighbours.put(src, currentTime());
		double tempMax = Double.MIN_VALUE,tempMin = Double.MAX_VALUE,avg = 0;
		for (TemperatureMinMaxMessage current : temperaturesReceived.values()){
			tempMax = (Math.max(tempMax, current.max));
			tempMin = (Math.min(tempMin, current.min));
			avg += current.avg;
		}
		setMaxTemp(tempMax);
		setMinTemp(tempMin);
		setMidTemp(avg / temperaturesReceived.size());
		
	}

	private void resetTemperatures() {
		setMinTemp(Double.MAX_VALUE);
		setMaxTemp(Double.MIN_VALUE);
		setMidTemp(0);	
	}
}
