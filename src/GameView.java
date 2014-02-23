
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

	final static int EXPIRATION_PERIOD = 2500;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;

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

	final static double PRECISION_FACTOR = 1000f;

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
		return interpolateColor(c, Colors.BACKGROUND, progress);

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
		String pausedString = paused ? " [paused]" : "";
		String score = String.format("Score: %d%s", model.getScore(), pausedString);
		g2.setFont(new Font("Monospace", 0, 80));
		g2.setColor(Colors.SCORE);
		g2.drawString(score, 40, 80);
		
	}

}