package scmu.sensors.sys;

import static simsim.core.Simulation.Network;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.NetAddress;
import simsim.core.TcpChannel;
import simsim.core.UdpPacket;
import simsim.gui.geom.XY;

class SensorEndpoint extends EndPoint {
	
	Set<NetAddress> neighbours = new HashSet<NetAddress>() ;
	
	final AbstractSensor owner ;
	SensorEndpoint( AbstractSensor owner ) {
		super( owner.endpoint.address, owner.endpoint.port, owner.endpoint.handler ) ;
		this.owner = owner ;
		this.initNeighborhood() ;
	}
	
	private void initNeighborhood() {
		for( NetAddress i : Network.addresses() ) {
			double d = i.pos.distance( owner.address.pos ) ;
			if( d <= RadioImpl.MAX_RANGE && i != owner.address ) {
				neighbours.add( i.endpoint.address ) ;
				if( i.endpoint instanceof SensorEndpoint )
					((SensorEndpoint)(i.endpoint)).neighbours.add( owner.address ) ;
			}
		}
	}
	
	public void broadcast(final Message m) {

		double radius = owner.radio.range() ;
		
		for(Iterator<NetAddress> i = neighbours.iterator() ; i.hasNext() ; ) {
			NetAddress other = i.next() ;
			if( other.isOnline() ) {
				double d = owner.address.pos.distance( other.pos ) ;
				if( d < radius ) 
					new SensorUdpPacket(this, other.endpoint, m) ;
			}
			else 
				i.remove() ;
		}
		
		owner.battery.decrementSend( m, owner.radio.getPower());
		if( owner.battery.dead() ) {
			owner.dispose() ;
		}
	}

	public boolean udpSend(final EndPoint dst, final Message m) {
		throw new RuntimeException("Not available. Use broadcast()") ;
	}

	public boolean udpSend(final EndPoint dst, final Message m, double l) {
		throw new RuntimeException("Not available. Use broadcast()") ;
	}
	
	public TcpChannel tcpSend(final EndPoint dst, final Message m) {
		throw new RuntimeException("Not available. Use broadcast()") ;
	}
	
	public TcpChannel tcpSend(final EndPoint dst, final Message m, double appLat) {
		throw new RuntimeException("Not available. Use broadcast()") ;
	}
	
	public EndPoint joinGroup(Object group) {
		throw new RuntimeException("Not available. Use broadcast()") ;		
	}
	
	public void leaveGroup(Object group) {
		throw new RuntimeException("Not available. Use broadcast()") ;		
	}
	
	private static class SensorUdpPacket extends UdpPacket {
		
		EndPoint dst ;
		Message payload ;
		public SensorUdpPacket( EndPoint src, EndPoint dst, Message payload) {
			super( src, dst, payload, dst.latency(src));
			this.dst = dst ;
			this.payload = payload ;
		}

		public void run() {
			if( dst instanceof SensorEndpoint ) {
				AbstractSensor other = (AbstractSensor)(dst.handler) ;
				other.battery.decrementReceive( payload) ;
				if( other.battery.dead() ) {
					other.dispose() ;
					return;
				}
			}
			super.run() ;
		}
	}
}

