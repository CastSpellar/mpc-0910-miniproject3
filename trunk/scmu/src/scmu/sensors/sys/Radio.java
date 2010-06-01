package scmu.sensors.sys;

public interface Radio {

	/* Should be a value between 25 (mW) and 100 (mW) */
	public void setPower( double power ) ;
	
	public double getPower() ;
	
	public double range();
	
	public double powerFromDistance(double distance);
}
