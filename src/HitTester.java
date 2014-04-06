
import java.util.*;
import java.awt.geom.*;

public class HitTester {
	
	int delay;
	HitTestListener listener;
	LinkedHashMap<Integer,Rectangle2D.Float> regions = new LinkedHashMap<Integer,Rectangle2D.Float>();

	int currentId = -1;
	long currentTime = Long.MAX_VALUE;

	float lastCursorX = -1;
	float lastCursorY = -1;

	public HitTester(int delay, HitTestListener listener) {

		this.delay = delay;
		this.listener = listener;

	}

	public void addRegion(int id, Rectangle2D.Float region) {

		//-1 is special, sorry 'bout it
		if (id == -1)
			return;

		regions.put(id, region);

	}

	//used by something that might want to highlight currently-being-selected hit test regions
	//one such implementation is provided in HitTester.draw() but this one need not specifically be used
	public Rectangle2D.Float getSelectingRegion() {

		if (currentId != (-1))
			return regions.get(currentId);
		else
			return null;

	}

	public void pointerUpdate(float cursorX, float cursorY) {

		long now = System.currentTimeMillis();

		//keep track of the cursor so we can draw a progress indicator
		lastCursorX = cursorX;
		lastCursorY = cursorY;
		
		//if we have a current selection, are we keeping it?
		Rectangle2D.Float current = getSelectingRegion();
		if (current == null) {
			//do nothing here
		} else if (!current.contains(cursorX, cursorY)) {
			currentId = -1;
			currentTime = Long.MAX_VALUE;
		} else if (now - currentTime > delay && currentTime != Long.MAX_VALUE) {
			currentTime = Long.MAX_VALUE;
			if (listener != null)
				listener.onHitTestSelection(currentId);
			return;
		} else {
			return;
		}

		//check other regions to see if we're in any of them
		for (int id : regions.keySet()) {
			Rectangle2D.Float region = regions.get(id);
			if (region.contains(cursorX, cursorY)) {
				currentId = id;
				currentTime = now;
				return;
			}
		}


	}

	public void drawSelectingRegion(GraphicsWrapper g2) {

		Rectangle2D.Float region = getSelectingRegion();
		if (region == null || currentTime == Long.MAX_VALUE)
			return;

		g2.fillRect(region.x, region.y, region.width, region.height, Colors.HIT_TEST_HIGHLIGHT);

		//draw radial progress indicator around the cursor (which is assumed to be drawn elsewhere, probably by a RadialMenu)
		float angle = (System.currentTimeMillis() - currentTime) / (float)delay * 360;
		g2.maskCircle(lastCursorX, lastCursorY, 0.28f);
		g2.fillArc(lastCursorX, lastCursorY, 0.32f, 90f, -angle, Colors.HIT_TEST_PROGRESS);
		g2.restore();

	}

}