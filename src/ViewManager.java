
import java.awt.Graphics;
import javax.swing.JComponent;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Iterator;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Container;
import java.util.HashMap;

public class ViewManager extends JComponent implements KeyListener {
	
	private ConcurrentLinkedDeque<View> views = new ConcurrentLinkedDeque<View>();
	private HashMap<String, View> registry = new HashMap<String, View>();

	private LeapWarningView leapWarning = new LeapWarningView();
	private boolean leapWarningVisible = false;

	//stuff used to draw FPS counter if --fps command-line argument was given
	private static final int FPS_SAMPLE = 10;
	private boolean showFPS = false;
	private long lastPaint = 0;
	private int paintId = 0;
	private float fps = 0;

	public ViewManager(boolean showFPS) {

		super();
		this.showFPS = showFPS;

	}

	public void registerView(View view, String viewName) {

		registry.put(viewName, view);
		view.setViewManager(this);

	}

	public View getView(String viewName) {

		return registry.get(viewName);

	}

	public void pushView(View view) {

		view.setViewManager(this);
		view.onActive();
		views.push(view);

	}

	public void pushView(String viewName) {

		View view = getView(viewName);
		if (view==null) return;

		pushView(view);

	}

	public View popView() {

		View popped = views.pop();
		
		View activeView = views.peek();
		if (activeView != null)
			activeView.onActive();

		return popped;

	}

	public View swapView(View view) {

		View temp = popView();
		pushView(view);
		return temp;

	}

	public View swapView(String viewName) {

		View view = getView(viewName);
		if (view == null) return null;

		return swapView(view);

	}

	public void setLeapWarningVisible(boolean vis) {
		leapWarningVisible = vis;
	}

	public void pointerUpdate(double x, double y) {

		float cursorX = (float)(x * 16.0f);
		float cursorY = (float)(10.0f - (y * 10.0f));

		View activeView = views.peek();
		if (activeView != null)
			activeView.pointerUpdate(cursorX, cursorY);

	}

	public void paintComponent(Graphics g) {

		GraphicsWrapper g2 = new GraphicsWrapper(g, this);

		Iterator<View> it = views.descendingIterator();
		while (it.hasNext()) {

			View v = it.next();
			boolean active = !it.hasNext();
			v.draw(g2, active);

		}

		//This View gets drawn outside of the normal view stack on top of everything if the LeapController says it should be visible
		if (leapWarningVisible)
			leapWarning.draw(g2, true);

		paintFPS(g2);

	}

	private void paintFPS(GraphicsWrapper g2) {

		if (!showFPS) return;
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		paintId = (paintId + 1) % FPS_SAMPLE;
		if (paintId == 0) {

			long now = System.currentTimeMillis();
			fps = FPS_SAMPLE * 1000f / (now - lastPaint);
			lastPaint = now;

		}

		g2.drawString(String.format("%.1f fps", fps), 0.2f, Color.white, 15f, 9.6f);

	}

	public void keyReleased(KeyEvent e) {

		View activeView = views.peek();
		if (activeView != null)
			activeView.onKey(e.getKeyCode());

	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	//To implement letterboxing and avoid unpleasant stretching, we tell the parent container what size we'd like to be
	public Dimension getPreferredSize() {

		Container parent = this.getParent();
		if (parent == null) return null;

		float parentRatio = (float)parent.getWidth() / (float)parent.getHeight();

		if (parentRatio > 1.6f) {

			//container is too wide, take its height
			float ourWidth = 1.6f * parent.getHeight();
			return new Dimension((int)Math.round(ourWidth), parent.getHeight());

		} else {

			//container is too tall, take its width
			float ourHeight = (float)parent.getWidth() / 1.6f;
			return new Dimension(parent.getWidth(), (int)Math.round(ourHeight));

		}

	}

	//we always get what we want
	public Dimension getMaximumSize() { return getPreferredSize(); }
	public Dimension getMinimumSize() { return getPreferredSize(); }

}