package scmu.sensors;

import scmu.sensors.sys.*;
import scmu.sensors.temperature.*;
import simsim.core.EndPoint;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.*;

public class BaseStationMinMax extends TemperatureBaseStation implements SensorMessageHandler {

	public BaseStationMinMax( XY xy) {
		super( xy);
		SensorDB.store(this) ;
	}
	
	public BaseStationMinMax() {
		SensorDB.store(this) ;
	}
	
	public void init() {
	
	}
	
	public void onReceive(EndPoint src, PingMessage m) {
	}
		
	public void displayOn( Canvas canvas ) {
		super.displayOn(canvas) ;
		
		canvas.sFill( RGB.BLUE, new Rectangle( address.pos.x, address.pos.y, 13.0, 13.0 ) ) ;
		canvas.sDraw( RGB.BLACK, String.format("BS"), address.pos.x - 10, address.pos.y - 10 ) ;
	}
}
