package simsim.gui.geom;

import java.awt.Shape;
import java.awt.geom.*;

public class Circle extends Ellipse2D.Double implements Shape {

	public Circle( XY center, double diameter ) {
		this( center.x, center.y, diameter ) ;
	}

	public Circle( double cx, double cy, double diameter ) {
		super( cx - diameter * 0.5, cy - diameter * 0.5, diameter, diameter) ;
	}


	public Circle( Point2D xy, double diameter ) {
		this( xy.getX(), xy.getY(), diameter ) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
