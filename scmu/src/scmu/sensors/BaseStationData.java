package scmu.sensors;

import scmu.sensors.sys.*;
import scmu.sensors.temperature.*;
import simsim.core.EndPoint;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.*;

public class BaseStationData extends TemperatureBaseStation implements SensorMessageHandler {

	public BaseStationData( XY xy) {
		super( xy);
		SensorDB.store(this) ;
	}
	
	public void init() {
	
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
		System.out.println("Pos: " + src.address.pos.getX() + " "+ src.address.pos.getY() );
		System.out.println("Temperature: " + m.temperature);
		setTemp(src.address.pos, m.temperature);
		endpoint.broadcast(new ParentMessage(0));
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		
		canvas.sFill( RGB.BLUE, new Rectangle( address.pos.x, address.pos.y, 13.0, 13.0 ) ) ;
		canvas.sDraw( RGB.BLACK, String.format("BS"), address.pos.x - 10, address.pos.y - 10 ) ;
	}

	@Override
	public void onReceive(EndPoint src, ParentMessage parentMessage) {
		// Do nothing;
		/*
		if(parentMessage.level > 1)
			endpoint.tcpSend(src, new ParentMessage(0));
			*/
	}

	@Override
	public void onReceive(EndPoint src, TemperatureMessage temperatureMessage) {
		setTemp(temperatureMessage.pos, temperatureMessage.temperature);
	}
}

