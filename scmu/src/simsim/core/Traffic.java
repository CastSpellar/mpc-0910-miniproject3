package simsim.core;

import simsim.gui.canvas.Canvas;
import static simsim.core.Simulation.* ;

class Traffic implements Displayable {
	
	Traffic() {
		displayLivePackets = Globals.get("Traffic_DisplayLivePackets", true ) ;
		displayDeadPackets = Globals.get("Traffic_DisplayDeadPackets", false ) ;		
		displayLiveChannels = Globals.get("Traffic_DisplayLiveChannels", true ) ;
		displayDeadChannels = Globals.get("Traffic_DisplayDeadChannels", false ) ;		
		
		String deadPacketFilterMode = Globals.get("Traffic_DisplayDeadPacketsHistory", "time" ) ;
		filterDeadPackets = deadPacketFilterMode.equals("display") ;
	}
	
	private double lastDisplay = 0 ;
	public void displayOn( Canvas canvas ) {
		
		double now = currentTime() ;
		
		if( displayDeadPackets )
			for (UdpPacket i : UdpPacket.deadUdpPackets)
				if( !filterDeadPackets || i.due >= lastDisplay )
					i.displayOn( canvas );
			
		if( displayLivePackets )
			for (UdpPacket i : UdpPacket.liveUdpPackets)
				i.displayOn( canvas );

		if( displayDeadChannels )
			for (TcpChannel i : TcpChannel.deadTcpChannels)
				i.displayOn( canvas );

		if( displayLiveChannels )
			for (TcpChannel i : TcpChannel.liveTcpChannels)
				i.displayOn( canvas );

		lastDisplay = now ;
	}
	
	boolean filterDeadPackets ;
	boolean displayLivePackets, displayDeadPackets ;
	boolean displayLiveChannels, displayDeadChannels ;
}
