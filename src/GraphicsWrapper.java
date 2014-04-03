
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;

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
	private Graphics2D originalGraphics;
	private LinkedList<Transform> transformStack;
	private JComponent canvas;
	private int lastPrepare = -1;

	public GraphicsWrapper(Graphics g, JComponent canvas) {
	
		this.originalGraphics = (Graphics2D)g;
		this.canvas = canvas;
		this.g2 = this.originalGraphics;
		this.transformStack = new LinkedList<Transform>();
		addTransform();

		//enable antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		//reset transformation
		prepare(TRANSFORM_STANDARD);
	
	}

	public void prepare(int transformType) {

		//get back to the original state on the transform stack
		restore(Integer.MAX_VALUE);

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

	public void drawImage(String imgName, float x, float y) {

		//TODO Matt: in the future, cache these transformed images, and do smoother scaling/rotating w/ antialiasing

		float scaleX = (float)g2.getTransform().getScaleX();
		float scaleY = (float)g2.getTransform().getScaleY();
		
		float cumTransX = (float)(g2.getTransform().getTranslateX() - originalGraphics.getTransform().getTranslateX());
		float cumTransY = (float)(g2.getTransform().getTranslateY() - originalGraphics.getTransform().getTranslateY());
		float realX = (x / 16f) * canvas.getWidth() + cumTransX;
		float realY = (y / 10f) * canvas.getHeight() + cumTransY;

		BufferedImage image = ImageManager.getImage(imgName);

		AffineTransform tform = new AffineTransform();

		tform.concatenate(AffineTransform.getTranslateInstance(realX, realY));
		tform.concatenate(AffineTransform.getTranslateInstance(-image.getWidth()*scaleX*0.5f, -image.getHeight()*scaleY*0.5f));
		
		float rotation = transformStack.peek().cumRotation;
		if (rotation != 0)
			tform.concatenate(AffineTransform.getRotateInstance(rotation / 180f * Math.PI, image.getWidth()*scaleX*0.5f, image.getHeight()*scaleY*0.5f));

		tform.concatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
		
		originalGraphics.drawImage(image, tform, null);

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

	public void fillCircle(float x, float y, float radius, Color c) {
		
		float scale = getCurrentScale();

		x -= radius;
		y -= radius;

		g2.setColor(c);
		g2.fillOval(
			(int)Math.round(x*scale),
			(int)Math.round(y*scale),
			(int)Math.round(2*radius*scale),
			(int)Math.round(2*radius*scale)
		);


	}

	public void maskCircle(float x, float y, float radius) {

		//Matt TODO: try the soft clipping hack described at https://weblogs.java.net/blog/2006/07/19/java-2d-trickery-soft-clipping

		addTransform();

		float scale = getCurrentScale();

		x -= radius;
		y -= radius;

		Area a = new Area(g2.getClip());
		a.subtract(new Area(new Ellipse2D.Float(x*scale, y*scale, scale*radius*2, scale*radius*2)));
		g2.setClip(a);

	}

	public void restore() {
		restore(1);
	}

	public void restore(int n) {

		for (int i = 0; i < n && transformStack.size() > 1; i++) {

			g2.dispose();
			transformStack.pop();
			g2 = transformStack.peek().graphics;

		}

	}

	public void setOrigin(float x, float y) {

		float scale = getCurrentScale();

		addTransform();
		g2.translate(scale*x, scale*y);

	}

	//angles in degrees
	public void rotate(float angle) {

		Transform t = addTransform();
		g2.rotate(angle / 180f * (float)Math.PI);
		t.cumRotation += angle;

	}

	//angles in degrees
	public void fillArc(float x, float y, float radius, float startAngle, float arcAngle, Paint c) {
		
		float scale = getCurrentScale();

		x -= radius;
		y -= radius;

		g2.setPaint(c);
		g2.fillArc(
			(int)Math.round(x*scale),
			(int)Math.round(y*scale),
			(int)Math.round(2*radius*scale),
			(int)Math.round(2*radius*scale),
			(int)Math.round(startAngle),
			(int)Math.round(arcAngle)
		);

	}

	private Transform addTransform() {

		Transform t = new Transform();
		g2 = (Graphics2D)g2.create();
		t.graphics = g2;

		Transform old = transformStack.peek();
		if (old != null)
			t.cumRotation = old.cumRotation;

		transformStack.push(t);	
		return t;

	}

	private class Transform {
		Graphics2D graphics = null;
		float cumRotation = 0;
		public Transform() {}
	}

}