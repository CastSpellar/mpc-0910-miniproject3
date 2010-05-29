package scmu.sensors;

import scmu.sensors.sys.SensorMessageHandler;
import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.MessageHandler;
import simsim.gui.canvas.RGB;

public class ParentMessage extends Message {

	public int level;
	public Double temperature;
	
	public ParentMessage(int level) {
		super(true, RGB.DARK_GRAY) ;
		this.level = level;
		length = Integer.SIZE/8;
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
