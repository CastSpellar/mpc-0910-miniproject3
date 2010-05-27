package simsim.core;

import static simsim.core.Simulation.Traffic;
import static simsim.core.Simulation.currentTime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import simsim.gui.canvas.Canvas;

public class UdpPacket extends Task {
      
    final EndPoint src, dst ;
    final private int length ;
    final private double delay ;
    final private EncodedMessage payload ;
    
    public UdpPacket( EndPoint src, EndPoint dst, Message payload, double delay ) {
    	super( null, delay, payload.color ) ;
        this.src = src ;
        this.dst = dst ;
        this.delay = delay ;
        this.payload = payload.encode() ;        
        this.length = payload.length() ;
        
        if( Traffic.displayLivePackets )
        	liveUdpPackets.add( this ) ;      
        
        if( checkMessageLength ) {
	        src.address.uploadedBytes += udpHeaderLength + payload.length ;
	        
	        if( payload.length < 0 )
	        	throw new RuntimeException(payload.getClass() + " does not set message length") ;
        }
    }
    
    public double delay() {
        return delay ;
    }
    
    public int length() {
        return length ;
    }
    
    public void run() {
    	if( Traffic.displayLivePackets ) {
    		liveUdpPackets.remove( this ) ;
    	}
    	if( Traffic.displayDeadPackets )
    		deadUdpPackets.add( this ) ;
    	
    	if( dst.address.online ) {
    		
    		if( checkMessageLength)
    			dst.address.downloadedBytes += udpHeaderLength + length ;
    		
    		payload.decode().deliverTo( src, dst.handler) ;
    	} else {
    		src.handler.onSendFailure(dst, payload.decode() ) ;
    	}
    }
    
    public String toString() {
        return "UdpPacket from:" + src + " to " +  dst ;
    }
    
    public void displayOn ( Canvas canvas ) {
    	Message msg = payload.decode() ;
    	
    	if( msg.isVisible() ) {
    		double t = due - currentTime() ;
    		double p = t / delay ;
    		msg.displayOn( canvas, src, dst, t, p ) ;   	
    	}
    }
    
	static java.util.Set<UdpPacket> liveUdpPackets = new HashSet<UdpPacket>();
	static java.util.LinkedList<UdpPacket> deadUdpPackets = new LinkedList<UdpPacket>();

	private static final double udpHeaderLength = Globals.get("Net_UdpHeaderLength", 28.0 ) ;
	private static final double deadPacketHistory = Globals.get("Traffic_DeadPacketHistory", 5.0) ;
	private static final double deadPacketHistoryMaxSize = Globals.get("Traffic_DeadPacketHistoryMaxSize", 512) ;
	
	private static final boolean checkMessageLength = Globals.get("Traffic_CheckMessageLength", false ) ;
	
	static PeriodicTask udpPacketGC = new PeriodicTask( deadPacketHistory / 5 ) {
		public void run() {
			
			double now = currentTime() ;
			for( Iterator<UdpPacket> i = deadUdpPackets.iterator() ; i.hasNext() ; )
				if( now - i.next().due < deadPacketHistory ) break ;
				else i.remove() ;
			
			while(deadUdpPackets.size() > deadPacketHistoryMaxSize)
				deadUdpPackets.removeLast() ;

		}
	} ;
}
