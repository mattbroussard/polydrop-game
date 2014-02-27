
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
	
	GradientPaint pointLossGradient;
	boolean recentPointLoss = false;
	int pointLossAlpha = 0;
	boolean startUnpause = false;
	int countdown = 3;
	double fontSize = 200;

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
		if(e.getKeyCode() == KeyEvent.VK_SPACE) controller.newGame();
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

	public void drawBody(DrawableBody db, Graphics2D g2, boolean paused) {

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

		Color c = paused ? Colors.BACKGROUND : expireColor(db.getColor(), db.getExpiration());

		if (c!=null) {
			g2.setColor(c);
			g2.fillPolygon(poly);
		}

	}

	public void notifyScore(DrawableBody db, int scoreDelta) {
		
		recentPointLoss = true;

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
	
	public void unPaused(){
		startUnpause = true;
		countdown = 3;
		fontSize = 200;
	}

	public void notifyLevel() {

		Notification n = new Notification(0.0f, 5.0f, System.currentTimeMillis() + NOTIFICATION_TIME, "Level Up!", 60, Colors.REWARD);
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

		boolean gameOver = model.isGameOver();
		boolean paused = controller.isPaused() || gameOver;

		//Draw background
		Color bg = paused ? Colors.PAUSED : Colors.BACKGROUND;
		resetTrans(g2);
		g2.setColor(bg);
		g2.fillRect(0,0,this.getWidth(),this.getHeight());

		//draw paused message if paused
		if (paused && !gameOver) {
			drawStringCentered(	"PAUSED",
								new Font("Monospace", 0, 80),
								Colors.PAUSED_TEXT,
								g2,
								this.getWidth()/2,
								(int)(this.getHeight()*0.10));
		}
/*		if(startUnpause){

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
		
		//Draw red flash on bottom of screen if player loses points
		float maxPointLossAlpha = 80; 
		if(recentPointLoss) {
			pointLossAlpha = (int)maxPointLossAlpha;
			recentPointLoss = false;
		}
		else {
			pointLossAlpha = Math.max(pointLossAlpha - 5, 0);
		}
		float pointLossTimeFactor = pointLossAlpha/maxPointLossAlpha;
		float heightOfRedBar = 75;
		for(int i = 1 ; i < heightOfRedBar; i++){
			float alpha = (1 - i/heightOfRedBar)*pointLossTimeFactor;
			g.setColor(new Color(1, 0 , 0, alpha));
			g.fillRect(0, this.getHeight()-i, this.getWidth(), 1);
		}

		//Prepare to draw bodies
		transformForBodies(g2);

		//Draw platform
		Platform p = model.getPlatform();
		drawBody(p, g2, paused);
		
		//Draw blocks
		ArrayList<DrawableBody> blocks = model.getBlocks();
		synchronized (blocks) {
			for(DrawableBody b : blocks) drawBody(b, g2, paused);
		}

		//Draw score
		resetTrans(g2);
		String score = "Score: "+model.getScore();
		g2.setFont(new Font("Monospace", 0, 80));
		g2.setColor(Colors.SCORE);
		g2.drawString(score, 40, 80);
		
		//Draw Level counter
		int level = controller.calculateLevel(model.getMaxScore());
		double pointsHave = model.getMaxScore();
		double pointsNeeded = controller.scoreNeededToLevel[level];
		double pointsForCurLevel = controller.scoreNeededToLevel[level-1];
		if(level >= controller.scoreNeededToLevel.length) level = controller.scoreNeededToLevel.length;
		Color circleColor = (controller.isPaused() || model.isGameOver()) ? Color.black : Color.gray;
		g2.setColor(circleColor);
		g2.fillOval(this.getWidth() - 400 , 20, 100, 100);
		g2.setColor(Colors.SHAPES[(level+1)%(Colors.SHAPES.length)]);
		int arcAngle = (int)((pointsHave - pointsForCurLevel) / (pointsNeeded - pointsForCurLevel) * 360);
		g2.fillArc(this.getWidth() - 395, 25, 90, 90, 90 - arcAngle, arcAngle); 
		g2.setColor(circleColor);
		g2.fillOval(this.getWidth() - 385, 35, 70, 70);
		g2.setColor(Colors.SHAPES[(level+1)%(Colors.SHAPES.length)]);
		g2.setFont(new Font("Monospace", 0, 50));
		g2.drawString(level+"", this.getWidth() - 362, 85);
		

		//Draw health bar
		resetTrans(g2);
		double health = (double)model.getHealth() / 100.0f;
		if (health > 1.0f) health = 1.0f;
		if (health < 0.0f) health = 0.0f;
		int healthWidth = 200;
		int healthHeight = 30;
		int healthX = this.getWidth() - 40 - healthWidth;
		int healthMid = healthX + (int)(health * healthWidth);
		int healthY = 40;
		g2.setColor(Colors.HEALTH);
		g2.fillRect(healthX, healthY, healthWidth, healthHeight);
		Color healthColor = Colors.HEALTH_GOOD;
		if (health < 0.6f) healthColor = Colors.HEALTH_MID;
		if (health < 0.3f) healthColor = Colors.HEALTH_BAD;
		g2.setColor(healthColor);
		g2.fillRect(healthX, healthY, healthMid - healthX, healthHeight);

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

		//draw game over message if game over
		if (gameOver) {
			drawStringCentered(	"GAME OVER",
								new Font("Monospace", 0, 200),
								Colors.HEALTH_BAD,
								g2,
								this.getWidth()/2,
								this.getHeight()/2);
			drawStringCentered(		"Press [space] to play again",
								new Font("Monospace", 0, 40),
								Colors.PAUSED_TEXT,
								g2,
								this.getWidth()/2,
								this.getHeight()*3/4);
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