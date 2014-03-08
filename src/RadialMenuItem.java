
import java.awt.Color;

public class RadialMenuItem implements Comparable<RadialMenuItem> {
	
	int id;
	float startAngle;
	float arcAngle;
	String title;
	String icon;
	Color selectedColor;
	Color activeColor;

	public RadialMenuItem(int id, String title, String icon, float startAngle, float arcAngle, Color selectedColor, Color activeColor) {

		this.id = id;
		this.title = title;
		this.icon = icon;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
		this.selectedColor = selectedColor;
		this.activeColor = activeColor;

	}

	public RadialMenuItem(int id, String title, String icon, float startAngle, float arcAngle) {
		this(id, title, icon, startAngle, arcAngle, Colors.MENU_ITEM_SELECTED, Colors.MENU_ITEM);
	}

	public void drawLabel(GraphicsWrapper g2, boolean selected) {

		boolean flip = startAngle > 180;

		if (flip) g2.rotate(180);
		
		g2.drawImage(icon, 0, 0);
		
		if (selected)
			g2.drawStringCentered(title, 0.2f, Colors.MENU_TOOLTIP, 0, (flip?0.75f:-0.75f));
		
		if (flip) g2.restore();

	}
	
	public void changeIcon(String icon){
		this.icon = icon;
	}

	//this was added to allow the list of items to be sorted by startAngle
	public int compareTo(RadialMenuItem other) {
		return (int)Math.round(10000*(this.startAngle - other.startAngle));
	}

	public boolean equals(Object other) {
		return other == this;
	}

}