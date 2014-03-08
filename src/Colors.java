
import java.awt.Color;

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

	public static final Color MENU_GAP = new Color(20,20,20,150);
	public static final Color MENU_ITEM = new Color(180,180,180,150);
	public static final Color MENU_ITEM_SELECTED = new Color(180,0,0,255);
	public static final Color MENU_TOOLTIP = new Color(255,255,255);
	public static final Color MENU_CURSOR = new Color(255,0,0,180);

	public static final Color MENU_MODE_SINGLE_SELECTED = new Color(255,200,200);
	public static final Color MENU_MODE_DUAL_SELECTED = new Color(200,255,200);
	public static final Color MENU_MODE_FREE_SELECTED = new Color(200,200,255);
	public static final Color MENU_MODE_SINGLE_ACTIVE = new Color(255,150,150, 180);
	public static final Color MENU_MODE_DUAL_ACTIVE = new Color(150,255,150, 180);
	public static final Color MENU_MODE_FREE_ACTIVE = new Color(150,150,255, 180);

	public static Color interpolateColor(Color a, Color c, double progress) {

		double r = (c.getRed() - a.getRed()) * progress + a.getRed();
		double g = (c.getGreen() - a.getGreen()) * progress + a.getGreen();
		double b = (c.getBlue() - a.getBlue()) * progress + a.getBlue();

		return new Color((int)r, (int)g, (int)b);

	}

}