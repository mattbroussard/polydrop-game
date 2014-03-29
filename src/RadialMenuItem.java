
import java.awt.Paint;

public class RadialMenuItem implements Comparable<RadialMenuItem> {
	
	public static final int ORIENT_TOP = 0;
	public static final int ORIENT_BOTTOM = 1;
	public static final int ORIENT_LEFT = 2;
	public static final int ORIENT_RIGHT = 3;

	int id;
	float startAngle;
	float arcAngle;
	String title;
	String icon;
	Paint selectedColor;
	Paint activeColor;
	int orient;

	public RadialMenuItem(int id, String title, String icon, float startAngle, float arcAngle, int orient, Paint selectedColor, Paint activeColor) {

		this.id = id;
		this.title = title;
		this.icon = icon;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
		this.selectedColor = selectedColor;
		this.activeColor = activeColor;
		this.orient = orient;

	}

	public RadialMenuItem(int id, String title, String icon, float startAngle, float arcAngle, int orient) {
		this(id, title, icon, startAngle, arcAngle, orient, Colors.MENU_ITEM_SELECTED, Colors.MENU_ITEM);
	}

	//text = false for icons; true for labels
	private float getRotation(boolean text) {

		float angle = startAngle + arcAngle/2f;

		switch (orient) {
			case ORIENT_TOP:
				return 90f - angle;
			case ORIENT_BOTTOM:
				return 270f - angle;
			case ORIENT_LEFT:
				return text ? 180f - angle : 0f;
			case ORIENT_RIGHT:
				return text ? -angle : 0f;
			default:
				return 0f;
		}

	}

	public void drawLabel(GraphicsWrapper g2, boolean selected) {

		g2.rotate(getRotation(false));
		g2.drawImage(icon, 0, 0);
		g2.restore();

		if (selected) {
		
			float labelX = 0f;
			switch (orient) {
				case ORIENT_LEFT:
					labelX = -1.2f;
					break;
				case ORIENT_RIGHT:
					labelX = 1.2f;
					break;
			}
			
			float labelY = 0.1f;
			switch (orient) {
				case ORIENT_TOP:
					labelY = -0.75f;
					break;
				case ORIENT_BOTTOM:
					labelY = 0.75f;
					break;
			}

			g2.rotate(getRotation(true));
			g2.drawStringCentered(title, 0.2f, Colors.MENU_TOOLTIP, labelX, labelY);
			g2.restore();
		
		}
		
	}
	
	public void setIcon(String icon){
		this.icon = icon;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	//this was added to allow the list of items to be sorted by startAngle
	public int compareTo(RadialMenuItem other) {
		return (int)Math.round(10000*(this.startAngle - other.startAngle));
	}

	public boolean equals(Object other) {
		return other == this;
	}

}