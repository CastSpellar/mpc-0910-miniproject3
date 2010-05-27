package scmu.sensors.temperature;

import scmu.sensors.sys.AbstractSensor;

public class TemperatureSensor extends AbstractSensor {

	public double temperature() {
		return TemperatureField.temperature(address.pos) ;
	}
}
