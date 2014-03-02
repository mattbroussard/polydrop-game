
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

//This class was made to avoid lots of places where we had:
// - awkward int/float casting
// - PRECISION_FACTOR multiply/divides
// - graphics transformations for drawing different things
public class GraphicsWrapper {

	static final int TRANSFORM_RAW = 0;
	static final int TRANSFORM_STANDARD = 1;
	static final int TRANSFORM_BODIES = 2;

	static final int PRECISION_FACTOR = 1000;
	static final String FONT_NAME = "Arial";

	private Graphics2D g2;
	private JComponent canvas;
	private int lastPrepare = -1;

	public GraphicsWrapper(Graphics g, JComponent canvas) {
	
		this.g2 = (Graphics2D)g;
		this.canvas = canvas;

		//enable antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		//reset transformation
		prepare(TRANSFORM_STANDARD);
	
	}

	public void prepare(int transformType) {

		//don't waste time resetting the transform if we don't have to
		if (transformType == lastPrepare)
			return;
		else
			lastPrepare = transformType;

		AffineTransform id = new AffineTransform();
		id.setToIdentity();
		g2.setTransform(id);

		float scale = getCurrentScale();

		if (transformType == TRANSFORM_STANDARD || transformType == TRANSFORM_BODIES) {

			float xScale = ((float)canvas.getWidth()) / (16.0f * scale);
			float yScale = ((float)canvas.getHeight()) / (10.0f * scale);
			g2.transform(AffineTransform.getScaleInstance(xScale, yScale));

		}

		if (transformType == TRANSFORM_BODIES) {

			g2.transform(AffineTransform.getScaleInstance(1.0, -1.0));
			g2.translate(8.0f * scale, -10.0f * scale);

		}

	}

	public Graphics2D getUnderlyingGraphics() {
		return g2;
	}

	public float getCurrentScale() {
		return lastPrepare == TRANSFORM_RAW ? 1.0f : PRECISION_FACTOR;
	}

	public void drawString(String s, float fontSize, Color c, float x, float y) {
		
		float scale = getCurrentScale();

		Font f = new Font(FONT_NAME, 0, (int)Math.round(fontSize*scale));

		g2.setColor(c);
		g2.setFont(f);
		g2.drawString(s, x * scale, y * scale);
	
	}

	public void drawStringCentered(String s, float fontSize, Color c, float x, float y) {
		
		float scale = getCurrentScale();

		Font f = new Font(FONT_NAME, 0, (int)Math.round(fontSize*scale));
		g2.setFont(f);
		g2.setColor(c);

		float w = g2.getFontMetrics(f).stringWidth(s);
		g2.drawString(s, x*scale - w/2.0f, y*scale);

	}

	//Note: this mutates the polygon
	public void fillPath(Path2D.Float poly, Color c) {
		
		float scale = getCurrentScale();

		poly.transform(AffineTransform.getScaleInstance(scale, scale));
		g2.setColor(c);
		g2.fill(poly);

	}

	public void fillRect(float x, float y, float w, float h, Color c) {
		
		float scale = getCurrentScale();

		g2.setColor(c);
		g2.fillRect(
			(int)Math.round(x*scale),
			(int)Math.round(y*scale),
			(int)Math.round(w*scale),
			(int)Math.round(h*scale)
		);		

	}

	public void fillOval(float x, float y, float w, float h, Color c) {
		
		float scale = getCurrentScale();

		g2.setColor(c);
		g2.fillOval(
			(int)Math.round(x*scale),
			(int)Math.round(y*scale),
			(int)Math.round(w*scale),
			(int)Math.round(h*scale)
		);


	}

	//angles in degrees
	public void fillArc(float x, float y, float w, float h, float startAngle, float endAngle, Color c) {
		
		float scale = getCurrentScale();

		g2.setColor(c);
		g2.fillArc(
			(int)Math.round(x*scale),
			(int)Math.round(y*scale),
			(int)Math.round(w*scale),
			(int)Math.round(h*scale),
			(int)Math.round(startAngle),
			(int)Math.round(endAngle)
		);

	}

}