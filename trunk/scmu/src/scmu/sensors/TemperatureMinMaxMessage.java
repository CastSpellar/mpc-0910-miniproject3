package scmu.sensors;

import scmu.sensors.sys.SensorMessageHandler;
import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.MessageHandler;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.XY;

public class TemperatureMinMaxMessage extends Message {

	public XY router;
	public double max,min,avg;
	public int level;
	
	public TemperatureMinMaxMessage(XY router,int level,double min,double max,double avg) {
		super(true, RGB.DARK_GRAY) ;
		this.router = router;
		this.level = level;
		this.min = min;
		this.max = max;
		this.avg = avg;
		length = ((5*Double.SIZE)+Integer.SIZE)/8;
	}

	public int length() {
		if( length < 0 ) {
			length = 10 ;
		} 
		return length ;
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((SensorMessageHandler)handler).onReceive( src, this ) ;
	}	
	private static final long serialVersionUID = 1L;
}
