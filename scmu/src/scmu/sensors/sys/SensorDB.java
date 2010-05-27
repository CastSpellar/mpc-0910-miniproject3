package scmu.sensors.sys;

import java.util.*;

import simsim.core.AbstractNode;
import simsim.utils.*;

public class SensorDB {
	
	static RandomList<AbstractSensor> sensors = new RandomList<AbstractSensor>() ;
	
	public static void store( AbstractSensor n ) {
		if( n != null )
			sensors.add(n) ;
	}

	public static void dispose( AbstractSensor n ) {
		if( n != null )
			sensors.remove(n) ;
	}
	public static int size() {
		return sensors.size();
	}
			
	public static Collection<AbstractSensor> sensors() {
		return sensors ;
	}
	
	static AbstractSensor randomSensor() {
		return sensors.randomElement() ;
	}	
}