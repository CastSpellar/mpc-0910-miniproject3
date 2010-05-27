package scmu.sensors.temperature;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.HSB;
import simsim.gui.geom.XY;

public class TemperatureField {

	private static final int MAX_TEMP = 50, MIN_TEMP = 0 ;
	private static int IMG_W = 64, IMG_H = 64 ;
	private static BufferedImage img = null;


	private static final double TIME_FACTOR = 0.05 ;
	public static double temperature( XY pos ) {
		double now = TIME_FACTOR * Simulation.currentTime() ;
		double tx = 200 + 30 * Math.sin( 0.000113 * now ) ;
		double ty = 230 + 50 * Math.cos( -0.000113 * now ) ;
		double s = 0.005 * (1 + 0.1 * Math.sin(0.01*now)) ;
		
		XY p1 = new XY( tx + pos.x * s, ty + pos.y * s ) ;
		double v = PerlinNoise.noise(p1.x, p1.y, 0.01 * now) ;		
		v = Math.max(0, Math.min(1, 0.5 * (1 + v) ) ) ;
		return MIN_TEMP + v * (MAX_TEMP - MIN_TEMP) ;
	}
	
	
	static {
		new PeriodicTask(0, 1){
			public void run() {
				doImg() ;
			}
		};
	}
	
	static public void displayOn( Canvas canvas ) {
		if (img == null) {
			img = canvas.gu.getDeviceConfiguration().createCompatibleImage(IMG_W, IMG_H, Transparency.OPAQUE);
			doImg() ;
		}
		canvas.gs.drawImage(img, 0, 0, 1000, 1000, null);
	}
	


	private static void doImg() {
		if( img == null)
			return ;
		
		Graphics2D G = (Graphics2D) img.getGraphics();

		G.setComposite(AlphaComposite.Src);
		G.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		G.fillRect(0, 0, img.getWidth(), img.getHeight());

		double sx = 1000.0 / (IMG_W - 1), sy = 1000.0 / (IMG_H - 1);
		for( int i = 0 ; i < IMG_W ; i++ )
			for( int j = 0 ; j < IMG_H ; j++) {
				XY p = new XY( i * sx, j * sy ) ;
				double t = temperature(p);
				double h = 0.5 * (1 - t / (MAX_TEMP - MIN_TEMP)) ;
				HSB clr = new HSB( h, 0.3, 0.9 ) ;

				int r = clr.getRed(), g = clr.getGreen(), b = clr.getBlue() ;
				img.setRGB(i, j, r << 16 | g << 8 | b);
			}
	}

}
