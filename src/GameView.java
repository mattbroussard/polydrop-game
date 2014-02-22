
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.geom.*;

public class GameView extends JComponent implements KeyListener{
	
	GameModel model;
	GameController controller;
	boolean pressedRight;
	boolean pressedPause;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT && !pressedRight){
			pressedRight = true;
			controller.movePlatformRight(10);
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE && !pressedPause){
			controller.pause();
			pressedPause = true;
		}
			
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) pressedRight = false;
		if(e.getKeyCode() == KeyEvent.VK_SPACE) pressedPause = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
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

	public void drawBody(Body body, Graphics2D g2) {

		Fixture fix = (Fixture)body.getUserData();
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

		g2.setColor(Color.GRAY);
		g2.fillPolygon(poly);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		transformForBodies(g2);

		//Draw platform
		Body p = model.getPlatform();
		drawBody(p, g2);

		
	}

}