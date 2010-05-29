package scmu.sensors.sys;

import scmu.sensors.ParentMessage;
import scmu.sensors.PingMessage;
import scmu.sensors.TemperatureMessage;
import simsim.core.EndPoint;

public interface SensorMessageHandler {

	public void onReceive( EndPoint src, PingMessage m ) ;

	public void onReceive(EndPoint src, ParentMessage parentMessage);

	public void onReceive(EndPoint src, TemperatureMessage temperatureMessage);
}
