
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.geom.*;

import java.util.*;

/* Matt TODO:

 - letterboxing
 - move render layers into separate classes
 - refactor out view statefulness
 - new font
 - menu implementation

*/

public class GameView extends JComponent implements KeyListener{
	
	GameModel model;
	GameController controller;
	LinkedList<Notification> notifs;

	final static int EXPIRATION_PERIOD = 2500;
	final static int PRECISION_FACTOR = 1000;
	final static String FONT_NAME = "Arial";
	final static int NOTIFICATION_TIME = 1250;
	final static float NOTIFICATION_DISTANCE = 2f;
	
	boolean recentPointLoss = false;
	int pointLossAlpha = 0;
	
	//I don't think these are used anymore except code commented by dallas in paintComponent?
	boolean startUnpause = false;
	int countdown = 3;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;
		notifs = new LinkedList<Notification>();

	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
		if(e.getKeyCode() == KeyEvent.VK_SPACE) controller.newGame();
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public Color interpolateColor(Color a, Color c, double progress) {

		double r = (c.getRed() - a.getRed()) * progress + a.getRed();
		double g = (c.getGreen() - a.getGreen()) * progress + a.getGreen();
		double b = (c.getBlue() - a.getBlue()) * progress + a.getBlue();

		return new Color((int)r, (int)g, (int)b);

	}

	public Color expireColor(Color c, long expiry) {

		if (expiry > EXPIRATION_PERIOD) return c;
		if (expiry < 0) return c;
		if (expiry == 0) return null;

		double progress = (double)(EXPIRATION_PERIOD-expiry) / (double)EXPIRATION_PERIOD;
		Color interp = interpolateColor(c, Colors.BACKGROUND, progress);

		double blink = Math.sin(50.0f * progress);
		return blink >= 0 ? interp : null;

	}

	public void drawBody(DrawableBody db, GraphicsWrapper g2, boolean paused) {

		g2.prepare(GraphicsWrapper.TRANSFORM_BODIES);

		Fixture fix = db.getFixture();
		Body body = db.getBody();
		PolygonShape shape = (PolygonShape) fix.getShape();

		Path2D.Float poly = new Path2D.Float();
		for (int i = 0; i < shape.getVertexCount(); i++) {
			Vec2 vertex = shape.getVertex(i);
			Vec2 wv = body.getWorldPoint(vertex);
			if (i==0) poly.moveTo(wv.x, wv.y);
			else poly.lineTo(wv.x, wv.y);
		}

		Color c = paused ? Colors.BACKGROUND : expireColor(db.getColor(), db.getExpiration());

		if (c!=null)
			g2.fillPath(poly, c);

	}

	public void notifyScore(DrawableBody db, int scoreDelta) {
		
		if (scoreDelta<0)
			recentPointLoss = true;

		Vec2 pos = db.getBody().getPosition();
		if (pos.y < 0) pos.set(pos.x, 0);
		if (pos.x < -7.0f) pos.set(-7.0f, pos.y);
		if (pos.x > 7.0f) pos.set(7.0f, pos.y);

		Color c = scoreDelta >= 0 ? Colors.REWARD : Colors.PENALTY;
		long exp = System.currentTimeMillis() + NOTIFICATION_TIME;
		String msg = String.format("%s%d", (scoreDelta>=0?"+":""), scoreDelta);

		Notification n = new Notification((float)pos.x, (float)pos.y, exp, msg, 0.35f, c);
		synchronized (notifs) { notifs.addFirst(n); }

	}
	
	public void unPaused(){
		startUnpause = true;
		countdown = 3;
	}

	public void notifyLevel() {

		Notification n = new Notification(0.0f, 5.0f, System.currentTimeMillis() + NOTIFICATION_TIME, "Level Up!", 2.0f, Colors.REWARD);
		synchronized (notifs) { notifs.addFirst(n); }

	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		GraphicsWrapper g2 = new GraphicsWrapper(graphics, this);

		boolean gameOver = model.isGameOver();
		boolean paused = controller.isPaused() || gameOver;

		//Draw background
		Color bg = paused ? Colors.PAUSED : Colors.BACKGROUND;
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		g2.fillRect(0, 0, 16, 10, bg);

		//draw paused message if paused
		if (paused && !gameOver) {
			g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
			g2.drawStringCentered(
				"PAUSED",
				1.0f,
				Colors.PAUSED_TEXT,
				8.0f,
				1.125f
			);
		}

		/*
		//some code dallas added but commented? not ported to new graphics scheme.
		if(startUnpause){

			System.out.println("Starting unpause");
			g.setFont(new Font("Monospace", 0, (int) fontSize));
			System.out.println("Color: " +(int)(255 - (fontSize-200)*10));
			g.setColor(new Color(255, 0,0,(int)(255 - (fontSize-200)*10)));
			g.drawString(countdown+"", this.getWidth()/2, this.getHeight()/2);
			//drawStringCentered(countdown+"",new Font("Monospace", 0, fontSize),new Color(256,0,0,(100+fontSize)), g2, (int)(this.getWidth()/2), (int)(this.getHeight()/2) );
			fontSize += 1.3;
			if(255 - (fontSize-200)*10 <= 1){
				countdown--;
				fontSize = 200;
				if(countdown <= 0){
					countdown = 3;
					startUnpause = false;
					paused = false;
				}
			}
		}
		*/

		//red flash/gradient on bottom
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		float maxPointLossAlpha = 80;
		if(recentPointLoss) {
			pointLossAlpha = (int)maxPointLossAlpha;
			recentPointLoss = false;
		}
		else {
			pointLossAlpha = Math.max(pointLossAlpha - 5, 0);
		}
		float pointLossTimeFactor = pointLossAlpha/maxPointLossAlpha;
		float heightOfRedBar = 1.0f;
		float gradientStep = 0.01f;
		for(float i = 0; i < heightOfRedBar; i += gradientStep){
			float alpha = (1 - i/heightOfRedBar)*pointLossTimeFactor;
			g2.fillRect(0, 10.0f-i, 16.0f, gradientStep, new Color(1,0,0,alpha));
		}

		//Draw platform
		Platform rp = model.getRightPlatform();
		Platform lp = model.getLeftPlatform();
		drawBody(lp, g2, paused);
		drawBody(rp, g2, paused);
		
		//Draw blocks
		ArrayList<DrawableBody> blocks = model.getBlocks();
		synchronized (blocks) {
			for (DrawableBody b : blocks)
				drawBody(b, g2, paused);
		}
		
		//Draw score
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		String score = "Score: "+model.getScore();
		g2.drawString(
			score,
			0.7f,
			Colors.SCORE,
			0.5f,
			1.0f
		);

		//Draw radial level indicator
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		int level = controller.calculateLevel(model.getMaxScore());
		//Dallas added this if, is this condition even possible?
		if(level >= controller.scoreNeededToLevel.length)
			level = controller.scoreNeededToLevel.length;
		float pointsHave = model.getMaxScore();
		float pointsNeeded = controller.scoreNeededToLevel[level];
		float pointsForCurLevel = controller.scoreNeededToLevel[level-1];
		Color levelFGColor = paused ? Colors.PAUSED : Colors.SHAPES[(level+1)%(Colors.SHAPES.length)];
		Color levelBGColor = paused ? Colors.BACKGROUND : Colors.PAUSED;
		float levelAngle = (float)(pointsHave - pointsForCurLevel) / (float)(pointsNeeded - pointsForCurLevel) * 360f;
		g2.fillOval(14.375f, 0.125f, 1.25f, 1.25f, levelBGColor);
		g2.fillArc(14.5f, 0.25f, 1.0f, 1.0f, 90.0f, 360, levelFGColor);
		g2.fillOval(14.5f, 0.25f, 1.0f, 1.0f, new Color(39,40,34,150));
		g2.fillArc(14.5f, 0.25f, 1.0f, 1.0f, 90.0f, -levelAngle, levelFGColor);
		g2.fillOval(14.55f, 0.3f, 0.9f, 0.9f, levelBGColor);
		g2.drawStringCentered(""+level, 0.5f, levelFGColor, 15f, 0.9f);
		
		//Draw health bar
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		float health = (float)model.getHealth() / 100.0f;
		float healthWidth = 2.0f;
		float healthHeight = 0.4f;
		float healthX = 12.0f;
		float healthY = 0.55f;
		g2.fillRect(healthX, healthY, healthWidth, healthHeight, Colors.HEALTH);
		Color healthColor = Colors.HEALTH_GOOD;
		if (health < 0.6f) healthColor = Colors.HEALTH_MID;
		if (health < 0.3f) healthColor = Colors.HEALTH_BAD;
		g2.fillRect(healthX, healthY, healthWidth * health, healthHeight, healthColor);

		//Handle notifications -- unfortunately, one thing that gives the view a bit of statefulness...
		synchronized (notifs) {

			//Prune old score notifications
			long now = System.currentTimeMillis();
			while (notifs.peekLast() != null && notifs.peekLast().expiry < now)
				notifs.removeLast();

			//Draw score and level-up notifications
			g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
			for (GameView.Notification n : notifs) {
				float progress = ((float)(NOTIFICATION_TIME-n.expiry+now) / (float)NOTIFICATION_TIME);
				float dy = progress * NOTIFICATION_DISTANCE;
				Color color = interpolateColor(n.color, Colors.BACKGROUND, progress);
				g2.drawStringCentered(n.msg, n.size, color, n.x + 8.0f, 10.0f - n.y - dy);
			}

		}

		//draw game over message if game over
		if (gameOver) {
			g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
			g2.drawStringCentered(
				"GAME OVER",
				2.0f,
				Colors.HEALTH_BAD,
				8,
				5
			);
			g2.drawStringCentered(
				"Press [space] to play again",
				0.3f,
				Colors.PAUSED_TEXT,
				8,
				7.5f
			);
		}
		
	}

	//This class was made to avoid lots of places where we had:
	// - awkward int/float casting
	// - PRECISION_FACTOR multiply/divides
	// - graphics transformations for drawing different things
	private class GraphicsWrapper {

		static final int TRANSFORM_RAW = 0;
		static final int TRANSFORM_STANDARD = 1;
		static final int TRANSFORM_BODIES = 2;

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

	private class Notification {
		float x;
		float y;
		long expiry;
		String msg;
		float size;
		Color color;
		public Notification(float x, float y, long expiry, String msg, float size, Color color) {
			this.x = x;
			this.y = y;
			this.expiry = expiry;
			this.msg = msg;
			this.size = size;
			this.color = color;
		}
	}

}