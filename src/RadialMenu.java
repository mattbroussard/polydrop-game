
import java.awt.Color;
import java.util.*;

public class RadialMenu {
	
	float centerX;
	float centerY;
	GameView parent;
	ArrayList<RadialMenuItem> items;

	RadialMenuItem selected = null;
	int active = -1;
	float selectExtent = 0;

	static final float ITEM_GAP = 0.5f;

	public RadialMenu(float centerX, float centerY, GameView parent) {

		this.centerX = centerX;
		this.centerY = centerY;
		this.parent = parent;
		items = new ArrayList<RadialMenuItem>();

	}

	public void setActiveItem(int id) {
		active = id;
	}

	public void addItem(RadialMenuItem item) {

		items.add(item);
		Collections.sort(items);

	}

	float cursorX = -1;
	float cursorY = -1;
	long lastPointerUpdate = -1;
	public void pointerUpdate(float x, float y) {

		//System.out.printf("menu.pointerUpdate called with x=%.3f, y=%.3f\n", x, y);

		lastPointerUpdate = System.currentTimeMillis();

		cursorX = x * 16.0f;
		cursorY = 10.0f - (y * 10.0f);

		float dx = cursorX - centerX;
		float dy = cursorY - centerY;

		float r = (float)Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
		float theta = (float)Math.atan2(-dy, dx) / (float)Math.PI * 180f;
		if (theta < 0) theta += 360;

		//System.out.printf("pointer has r=%.3f, theta=%.3f\n", r, theta);

		//pointer isn't where there could be a selection
		if (r < 2.8f || r > 4.5f) {

			//System.out.println("-- not selection candidate");
			selectExtent = 0;
			selected = null;
			return;

		}

		//we have a selection already. Is it valid? If so, are we done with it?
		if (selected != null) {
			
			float leeway = (float) ((r - 2.8) * 5);
		
			if (r < x || theta < selected.startAngle - leeway || theta > selected.startAngle+selected.arcAngle + leeway) {
				
				//System.out.println("-- selection cancelled");
				selected = null;
				selectExtent = 0;
				return;

			}

			selectExtent = r - 2.8f;

			if (selectExtent > 1.2f) {

				parent.menuItemSelected(selected.id);
				//System.out.println("-- selection successful, callback would be called.");
				selected = null;
				selectExtent = 0;
				return;

			}

			//System.out.println("-- selection continues");
			return;

		}

		//if we don't currently have a selection but are near the edge, check to see if we should start one
		if (r > 2.8f && r < 3.5f) {

			for (RadialMenuItem candidate : items) {

				if (theta > candidate.startAngle && theta < candidate.startAngle+candidate.arcAngle) {

					//System.out.println("-- found a selection");
					selected = candidate;
					selectExtent = r - 2.8f;
					return;

				}

			}
			
			//System.out.println("-- tried finding a selection, but failed.");

		}

	}

	public void draw(GraphicsWrapper g2) {

		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		//draw menu items + gaps
		g2.maskCircle(centerX, centerY, 2.8f);
		for (int i = 0; i < items.size(); i++) {

			RadialMenuItem item = items.get(i);

			//draw dead space
			float gapStart = item.startAngle + item.arcAngle;
			float gapEnd = i == items.size()-1 ? items.get(0).startAngle : items.get(i+1).startAngle;
			float gapLength = gapEnd-gapStart;
			if (gapLength < 0) gapLength += 360;
			if (gapLength >= 5)
				g2.fillArc(centerX, centerY, 3.8f, gapStart + ITEM_GAP, gapLength - 2*ITEM_GAP, Colors.MENU_GAP);
				
			float extent = item == selected ? selectExtent : 0;
			Color color = item == selected ? item.selectedColor : (active == item.id ? item.activeColor : Colors.MENU_ITEM);

			//draw item slice
			g2.maskCircle(centerX, centerY, 2.8f + extent);
			g2.fillArc(centerX, centerY, 3.8f + extent, item.startAngle + ITEM_GAP, item.arcAngle - 2*ITEM_GAP, color);
			g2.restore();

			//ask item to draw its label
			float labelAngleDeg = item.startAngle + item.arcAngle/2.0f;
			float labelAngleRad = labelAngleDeg / 180.f * (float)Math.PI;
			g2.setOrigin(centerX + (3.3f+extent)*(float)Math.cos(labelAngleRad), centerY - (3.3f+extent)*(float)Math.sin(labelAngleRad));
			g2.rotate(90 - labelAngleDeg);
			item.drawLabel(g2, item == selected);
			g2.restore(2);

		}
		g2.restore();

		//draw cursor
		if (System.currentTimeMillis() - lastPointerUpdate < 300)
			g2.fillCircle(cursorX, cursorY, 0.25f, Colors.MENU_CURSOR);

	}

}