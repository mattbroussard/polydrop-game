
import java.awt.Color;

public class RadialMenuItem implements Comparable<RadialMenuItem> {
	
	int id;
	float startAngle;
	float arcAngle;
	String icon;

	public RadialMenuItem(int id, String icon, float startAngle, float arcAngle) {

		this.id = id;
		this.icon = icon;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;

	}

	public void drawLabel(GraphicsWrapper g2) {

		boolean flip = startAngle > 180;

		if (flip) g2.rotate(180);
		g2.drawImage(icon, 0, 0);
		if (flip) g2.restore();

	}

	//this was added to allow the list of items to be sorted by startAngle
	public int compareTo(RadialMenuItem other) {
		return (int)Math.round(10000*(this.startAngle - other.startAngle));
	}

	public boolean equals(Object other) {
		return other == this;
	}

}