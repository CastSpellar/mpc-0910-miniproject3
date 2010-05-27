package scmu.sensors.sys;

import scmu.sensors.PingMessage;
import simsim.core.EndPoint;

public interface SensorMessageHandler {

	public void onReceive( EndPoint src, PingMessage m ) ;
}
