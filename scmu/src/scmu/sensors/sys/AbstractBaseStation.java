package scmu.sensors.sys;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import scmu.sensors.temperature.TemperatureField;
import simsim.core.AbstractNode;
import simsim.core.Displayable;
import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.NetAddress;
import simsim.core.PeriodicTask;
import simsim.core.TcpChannel;
import simsim.core.UdpPacket;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Rectangle;
import simsim.gui.geom.XY;
import static simsim.core.Simulation.Network;

public class AbstractBaseStation extends AbstractSensor implements Displayable {

	protected AbstractBaseStation( XY xy) {
		super(new BSRadioImpl(), new BSBatteryImpl(), xy);
	}

	protected AbstractBaseStation() {
		super(new BSRadioImpl(), new BSBatteryImpl());
	}

}



class BSBatteryImpl implements Battery {
	public static double JOULES = 10e6;
	public double charge = 6750*JOULES ;
	
	public double charge() {
		return charge ;
	}
	
	public void decrementSend( Message m, double power ) {
		/** do nothing */	
	}

	public void decrementReceive( Message m) {
		/** do nothing */	
	}

	public void decrement( double seconds ) {
		/** do nothing */	
	}
	
	public boolean dead() {
		return false;
	}
	
	public String toString() {
		return String.format("%3.1f", charge) ;
	}
}

class BSRadioImpl implements Radio {
	static final double MAX_RANGE = 250 ;
	private double power = 1 ;
	
	public void setPower( double power ) {
		this.power = Math.max( 0, Math.min(1, power)) ;
	}
	
	public double getPower() {
		return power;
	}
	
	public double range() {
		return MAX_RANGE * power ;
	}
}