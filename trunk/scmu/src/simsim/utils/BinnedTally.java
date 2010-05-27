package simsim.utils;

import java.util.ArrayList;
import java.util.List;

import umontreal.iro.lecuyer.stat.Tally;

public class BinnedTally {

	public String name ;
	public List<Tally> bins  ;
		
	public BinnedTally(String name) {
		this.name = name ;
		this.bins = new ArrayList<Tally>() ;
	}
	
	public Tally bin( int i) {
		while( i >= bins.size() )
			bins.add( new Tally() ) ;
		return bins.get(i) ;
	}
}
