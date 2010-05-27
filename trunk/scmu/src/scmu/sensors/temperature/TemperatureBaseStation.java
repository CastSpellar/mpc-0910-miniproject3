package scmu.sensors.temperature;

import scmu.sensors.sys.AbstractBaseStation;
import java.util.*;
import simsim.core.Displayable;
import simsim.gui.geom.XY;


public abstract class TemperatureBaseStation extends AbstractBaseStation implements Displayable {
	
	protected TemperatureBaseStation( XY xy) {
		super( xy);
	}
	
	protected TemperatureBaseStation() {
	}
	
	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp( double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp( double minTemp) {
		this.minTemp = minTemp;
	}

	public double getMidTemp() {
		return midTemp;
	}

	public void setMidTemp( double midTemp) {
		this.midTemp = midTemp;
	}

	public void setTemp( XY xy, double temp) {
		temps.put( xy, temp);
	}

	public double getTemp( XY xy) {
		if( temps.containsKey( xy))
			return temps.get( xy);
		else
			return 0.0;
	}

	private double maxTemp = 0; 
	private double minTemp = 0; 
	private double midTemp = 0;
	private Map<XY,Double> temps = new HashMap<XY,Double>();
	
}
