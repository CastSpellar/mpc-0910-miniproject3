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
import simsim.gui.geom.XY;
import static simsim.core.Simulation.Network;

public class AbstractSensor extends AbstractNode implements Displayable {

	Radio radio;
	Battery battery ;
	
	
	protected AbstractSensor() {
		this( new RadioImpl(), new BatteryImpl());
	}

		
	protected AbstractSensor( Radio radio, Battery battery) {
		super(false) ;
		this.radio = radio ;
		this.battery = battery ;
		this.address.endpoint = this.endpoint = new SensorEndpoint(this);		
		this.radio.setPower(100.0) ;
	}

	protected AbstractSensor( Radio radio, Battery battery, XY xy) {
		super(false) ;
		this.radio = radio ;
		this.battery = battery ;
		this.address.pos = xy;
		this.address.endpoint = this.endpoint = new SensorEndpoint(this);		
		this.radio.setPower(100.0) ;
	}

	public Radio radio() {
		return radio ;
	}
	
	public Battery battery() {
		return battery ;
	}

	final Pen pen = new Pen( RGB.darkGray, 0.5, 4.0) ;

	public void displayOn( Canvas canvas ) {
		XY mouse = canvas.sMouse() ;
		double d = mouse.distanceSq( address.pos ) ;
		if( d < 10 * 10 ) {
			canvas.sDraw( pen, new Circle( address.pos, 2 * radio.range() ) ) ;		
		}
	}	
	
	static {
		new PeriodicTask(1.0) {
			public void run() {
				for( NetAddress i : Network.addresses() ) {
					if( i.isOnline() ) {
						AbstractSensor s = (AbstractSensor) i.endpoint.handler ;
						s.battery.decrement( this.period ) ;
					}
				}
			}
		};
	}
}

/*class BatteryImpl implements Battery {
	public static final double ENERGY_COST_PER_BYTE = 1e-3 ;
	public static final double ENERGY_COST_PER_SECOND = 1e-5 ;
	public double charge = 1.0 ;
	
	public double charge() {
		return charge ;
	}
	
	public void decrement( Message m, boolean receive ) {
		if( receive)
			charge -= Math.min(1, m.length()) * ENERGY_COST_PER_BYTE * 0.2;
		else
			charge -= Math.min(1, m.length()) * ENERGY_COST_PER_BYTE ;
	}
	
	public void decrement( double seconds ) {
		charge -= seconds * ENERGY_COST_PER_SECOND ;	
	}
	
	public boolean dead() {
		return charge < 0 ;
	}
	
	public String toString() {
		return String.format("%3.1f", charge) ;
	}
}
*/

class BatteryImpl implements Battery {
	public static double JOULES = 10e1;
	
//	public static double JOULES = 10e6;
	public static double INIT_CHARGE = 6750*JOULES; 
	
//	public static final double ENERGY_COST_PER_BYTE = 1e-3 ;
//	public static final double ENERGY_COST_PER_SECOND = 1e-5 ;
//	private static double CIPHERING_COST = 1.79, MAC_COST = 2.47, SENDING_COST = 59.2, RECEIVING_COST = 28.6, 
//	ACTIVE_COST = 0.096, SLEEP_COST = 0.00033, IDLE_COST = 0.0096, CHANGE_MODE_COST = 2.86;
	
	private double SENDING_COST = 59.2;
	private double RECEIVING_COST = 5.92;
	private double ACTIVE_COST = 0.096;

	public double charge = INIT_CHARGE ;
	
	public double charge() {
		return charge / INIT_CHARGE ;
	}
	
	public void decrementSend( Message m, double power) {
		charge -= Math.max( 6, 6 + m.length()) * SENDING_COST * power / 100.0;
	}

	public void decrementReceive( Message m) {
		charge -= Math.max(6, 6 + m.length()) * RECEIVING_COST ;
	}

	public void decrement( double seconds ) {
		charge -= seconds * ACTIVE_COST ;	
	}
	
	public boolean dead() {
		return charge < 0 ;
	}
	
	public String toString() {
		return String.format("%3.1f", charge) ;
	}
}


class RadioImpl implements Radio {
	static final double MAX_RANGE = 600 / 3 ;
	static final double MAX_RANGE_2 = MAX_RANGE * MAX_RANGE ;
	private double power = 100.0 ;
	
	public void setPower( double power) {
		if( power < 25 || power > 100)
			throw new RuntimeException( "Power value not expected : " + power);
		this.power = Math.max( 25, Math.min(100, power)) ;
	}
	
	public double getPower() {
		return power;
	}
	
	public double range() {
		return Math.sqrt( MAX_RANGE_2 * power / 100.0 );
	}
}
