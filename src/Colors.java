
//import java.awt.Color;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class Colors {
	
	//Colors inspired by Monokai: http://www.colourlovers.com/palette/1718713/Monokai

	public static final Color[] SHAPES = {
		new Color(154,204,253), //triangle, blue
		new Color(249, 38,114), //square,   red
		new Color( 34,179,129), //pentagon, green
		//new Color(166,226,46), //pentagon
		new Color(153,  0,229), //hexagon, purple
		new Color(255,232,106), //heptagon, yellow
		//new Color(255,167,138), //heptagon
		new Color(243,114,89), //octagon, orange
		//new Color(255,182,212) //octagon
	};

	public static final Color LETTERBOX = new Color(39,40,34);
	public static final Color BACKGROUND = new Color(39,40,34);
	public static final Color PAUSED = new Color(128,128,128);
	public static final Color PAUSED_TEXT = new Color(150,150,150);
	public static final Color PLATFORM = Color.WHITE;
	public static final Color SCORE = Color.WHITE;
	public static final Color LEADERBOARD = Color.WHITE;

	public static final Color REWARD = Color.GREEN;
	public static final Color PENALTY = Color.RED;

	public static final Color HEALTH = Color.WHITE;
	public static final Color HEALTH_GOOD = new Color(27,204,41);
	public static final Color HEALTH_MID = new Color(255,210,0);
	public static final Color HEALTH_BAD = new Color(255,47,0);
	public static final Color HEALTH_PAUSED = new Color(50,50,50);

	public static final Color MENU_GAP = new Color(20,20,20,150);
	public static final Color MENU_ITEM = new Color(180,180,180);
	public static final Color MENU_ITEM_SELECTED = new Color(0x20D972);
	public static final Color MENU_TOOLTIP = new Color(255,255,255);
	public static final Color MENU_CURSOR = new Color(255,0,0,150);

	public static final Color MENU_MODE_SINGLE_SELECTED = new Color(0xFF195C);
	public static final Color MENU_MODE_DUAL_SELECTED = new Color(0x00C3FF);
	public static final Color MENU_MODE_FREE_SELECTED = new Color(0xF6E614);
	public static final Color MENU_MODE_SINGLE_ACTIVE = new Color(0xFF195C);
	public static final Color MENU_MODE_DUAL_ACTIVE = new Color(0x00C3FF);
	public static final Color MENU_MODE_FREE_ACTIVE = new Color(0xF6E614);

	public static final Paint SPLASH_MENU_PLAY_ACTIVE = new ThrobbingColor(0x20D972/*0x00C3FF*/);
	public static final Color SPLASH_MENU_PLAY_SELECTED = new Color(0x20D972/*0x00C3FF*/);

	public static final Color LEAP_WARNING_OVERLAY = new Color(150, 0, 0, 150);
	public static final Color LEAP_WARNING_TEXT = Color.white;

	public static final Color TUTORIAL_TEXT = Color.white;

	public static Color interpolateColor(Color a, Color c, double progress) {

		double r = (c.getRed() - a.getRed()) * progress + a.getRed();
		double g = (c.getGreen() - a.getGreen()) * progress + a.getGreen();
		double b = (c.getBlue() - a.getBlue()) * progress + a.getBlue();

		return new Color((int)r, (int)g, (int)b);

	}

}

class ThrobbingColor implements Paint, PaintContext {

	private float[] hsb = new float[3];
	
	final static int THROB_PERIOD = 1500;
	final static float BRIGHTNESS_MODULATION = 0.1f;

	public ThrobbingColor(int c) {
		this(new Color(c));
	}

	public ThrobbingColor(Color c) {
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return this;
	}

	public void dispose() {}

	public ColorModel getColorModel() {
		return ColorModel.getRGBdefault();
	}

	public Raster getRaster(int x, int y, int w, int h) {

		WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);

		float mod = (float)Math.sin((System.currentTimeMillis() % THROB_PERIOD) / (float)THROB_PERIOD * 2f * (float)Math.PI);
		mod = (mod + 1f) / 2f;
		mod = (mod * BRIGHTNESS_MODULATION) + (1f - BRIGHTNESS_MODULATION);

		Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]*mod);
		float[] arr = { c.getRed(), c.getGreen(), c.getBlue(), 255 };

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				raster.setPixel(i, j, arr);
			}
		}

		return raster;

	}

	public int getTransparency() {
		return Transparency.OPAQUE;
	}

}