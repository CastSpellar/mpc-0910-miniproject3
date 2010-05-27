package scmu.sensors.sys;

import simsim.core.Message;

public interface Battery {

	public double charge() ;
	
	public boolean dead();
	
	public void decrement( double second );

	public void decrementSend( Message m, double power );

	public void decrementReceive( Message m);
}
