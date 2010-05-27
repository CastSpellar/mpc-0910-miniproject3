package simsim.gui.geom;

import java.awt.geom.*;

public class Ellipse extends Ellipse2D.Double {

	public Ellipse( XY center, XY radius ) {
		this( center.x, center.y, radius.x, radius.y) ;
	}

	public Ellipse( XY center, double w, double h ) {
		this( center.x, center.y, w, h) ;
	}
	
	public Ellipse( double cx, double cy, double w, double h ) {
		super( cx - w * 0.5, cy - h * 0.5, w, h) ;
	}
	
	public Ellipse( Point2D xy, double w, double h ) {
		super( xy.getX(), xy.getY(), w, h ) ;
	}
	
	public Ellipse( Point2D xy, Point2D wh ) {
		super( xy.getX(), xy.getY(), wh.getX(), wh.getY() ) ;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
