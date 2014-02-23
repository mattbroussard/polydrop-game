
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.geom.*;

import java.util.*;

public class GameView extends JComponent implements KeyListener{
	
	GameModel model;
	GameController controller;
	LinkedList<GameView.Notification> notifs;

	final static int EXPIRATION_PERIOD = 2500;
	final static double PRECISION_FACTOR = 1000f;
	final static int NOTIFICATION_TIME = 1250;
	final static double NOTIFICATION_DISTANCE = 2f;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;
		notifs = new LinkedList<GameView.Notification>();

	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public void resetTrans(Graphics2D g2) {
		AffineTransform at = new AffineTransform();
		at.setToIdentity();
		g2.setTransform(at);
	}

	public void transformForBodies(Graphics2D g2) {
		resetTrans(g2);
		double xScale = this.getWidth() / (16.0 * PRECISION_FACTOR);
		double yScale = this.getHeight() / (-10.0 * PRECISION_FACTOR);
		g2.transform(AffineTransform.getScaleInstance(xScale, yScale));
		g2.translate(8.0f * PRECISION_FACTOR, -10.0f * PRECISION_FACTOR);
	}

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

		//double blink = Math.sin(5.0f / progress);
		double blink = Math.sin(50.0f * progress);
		return blink >= 0 ? interp : null;

	}

	public void drawBody(DrawableBody db, Graphics2D g2) {

		Fixture fix = db.getFixture();
		Body body = db.getBody();
		PolygonShape shape = (PolygonShape) fix.getShape();

		//System.out.printf("world vector of body origin is (%.3f, %.3f)\n", body.getPosition().x, body.getPosition().y);

		//System.out.println("start poly");
		Polygon poly = new Polygon();
		for (int i = 0; i < shape.getVertexCount(); i++) {
			Vec2 vertex = shape.getVertex(i);
			Vec2 wv = body.getWorldPoint(vertex);
			poly.addPoint((int)(wv.x * PRECISION_FACTOR), (int)(wv.y * PRECISION_FACTOR));
			//System.out.printf("vertex: x=%.3f, y=%.3f\n", wv.x, wv.y);
		}
		//System.out.println("end poly");

		Color c = controller.isPaused() ? Colors.BACKGROUND : expireColor(db.getColor(), db.getExpiration());

		if (c!=null) {
			g2.setColor(c);
			g2.fillPolygon(poly);
		}

	}

	public void notifyScore(DrawableBody db, int scoreDelta) {

		Vec2 pos = db.getBody().getPosition();
		if (pos.y < 0) pos.set(pos.x, 0);
		if (pos.x < -7.0f) pos.set(-7.0f, pos.y);
		if (pos.x > 7.0f) pos.set(7.0f, pos.y);

		Color c = scoreDelta >= 0 ? Colors.REWARD : Colors.PENALTY;
		long exp = System.currentTimeMillis() + NOTIFICATION_TIME;
		String msg = String.format("%s%d", (scoreDelta>=0?"+":""), scoreDelta);

		Notification n = new Notification(pos.x, pos.y, exp, msg, 30, c);
		synchronized (notifs) { notifs.addFirst(n); }

	}

	public void notifyLevel() {

		Notification n = new Notification(-1.0f, 5.0f, System.currentTimeMillis() + NOTIFICATION_TIME, "Level Up!", 60, Colors.REWARD);
		synchronized (notifs) { notifs.addFirst(n); }

	}

	public double convertGameX(double x) {

		return ((x + 8.0f) / 16.0f) * this.getWidth();

	}

	public double convertGameY(double y) {

		return ((y / -10.0f) * this.getHeight()) + this.getHeight();

	}

	public void drawStringCentered(String s, Font f, Color c, Graphics2D g2, int x, int y) {

		g2.setFont(f);
		g2.setColor(c);
		int w = getFontMetrics(f).stringWidth(s);
		g2.drawString(s, x - w/2, y);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		//enable antialiasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		boolean paused = controller.isPaused();

		//Draw background
		Color bg = paused ? Colors.PAUSED : Colors.BACKGROUND;
		resetTrans(g2);
		g2.setColor(bg);
		g2.fillRect(0,0,this.getWidth(),this.getHeight());

		//draw paused message if paused
		if (paused) {
			drawStringCentered(	"PAUSED",
								new Font("Monospace", 0, 250),
								Colors.PAUSED_TEXT,
								g2,
								this.getWidth()/2,
								this.getHeight()/2);
		}

		//Prepare to draw bodies
		transformForBodies(g2);

		//Draw platform
		Platform p = model.getPlatform();
		drawBody(p, g2);
		
		//Draw blocks
		ArrayList<DrawableBody> blocks = model.getBlocks();
		for(DrawableBody b : blocks) drawBody(b, g2);

		//Draw score
		resetTrans(g2);
		String score = "Score: "+model.getScore();
		g2.setFont(new Font("Monospace", 0, 80));
		g2.setColor(Colors.SCORE);
		g2.drawString(score, 40, 80);

		synchronized (notifs) {

			//Prune old score notifications
			long now = System.currentTimeMillis();
			while (notifs.peekLast() != null && notifs.peekLast().expiry < now)
				notifs.removeLast();

			//Draw score and level-up notifications
			resetTrans(g2);
			for (GameView.Notification n : notifs) {
				double x = convertGameX(n.x);
				double progress = ((double)(NOTIFICATION_TIME-n.expiry+now) / (double)NOTIFICATION_TIME);
				double dy = progress * NOTIFICATION_DISTANCE;
				double y = convertGameY(n.y + dy);
				Color color = interpolateColor(n.color, Colors.BACKGROUND, progress);
				drawStringCentered(n.msg, new Font("Monospace", 0, n.size), color, g2, (int)x, (int)y);
				//System.out.printf("Drawing notif \"%s\" at (%d,%d).\n", n.msg, (int)x, (int)y);
			}

		}
		
	}

	private class Notification {
		double x;
		double y;
		long expiry;
		String msg;
		int size;
		Color color;
		public Notification(double x, double y, long expiry, String msg, int size, Color color) {
			this.x = x;
			this.y = y;
			this.expiry = expiry;
			this.msg = msg;
			this.size = size;
			this.color = color;
		}
	}

}