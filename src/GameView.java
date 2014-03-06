
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

import org.jbox2d.common.*;

/* Matt TODO:

 - refactor out view statefulness (particularly with red flash)
 - menu implementation

*/

public class GameView extends JComponent implements KeyListener{
	
	GameModel model;
	GameController controller;
	LinkedList<Notification> notifs;
	RadialMenu pausedMenu;
	
	boolean recentPointLoss = false;
	int pointLossAlpha = 0;
	
	//I don't think these are used anymore except code commented by dallas in paintComponent?
	boolean startUnpause = false;
	int countdown = 3;
	float pointLossX = 0;
	

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;
		notifs = new LinkedList<Notification>();

		pausedMenu = new RadialMenu(8, 5.5f, this);
		pausedMenu.addItem(new RadialMenuItem(0, "freeMode", 60, 20));
		pausedMenu.addItem(new RadialMenuItem(1, "dualMode", 80, 20));
		pausedMenu.addItem(new RadialMenuItem(2, "singleMode", 100, 20));
		pausedMenu.addItem(new RadialMenuItem(3, "exit", 250, 20));
		pausedMenu.addItem(new RadialMenuItem(4, "leaderboard", 270, 20));

		/* pausedMenu = new RadialMenu(8, 5);
		int w = 360/30;
		for (int i = 0; i < 30; i++) {
			pausedMenu.addItem(new RadialMenuItem(i, 110 + i*w, w));
		} */

	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
		if(e.getKeyCode() == KeyEvent.VK_SPACE) controller.newGame();
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void notifyScore(DrawableBody db, int scoreDelta) {
		
		if (scoreDelta<0)
			recentPointLoss = true;

		Vec2 pos = db.getBody().getPosition();
		if (pos.y < 0) pos.set(pos.x, 0);
		if (pos.x < -7.0f) pos.set(-7.0f, pos.y);
		if (pos.x > 7.0f) pos.set(7.0f, pos.y);

		Color c = scoreDelta >= 0 ? Colors.REWARD : Colors.PENALTY;
		String msg = String.format("%s%d", (scoreDelta>=0?"+":""), scoreDelta);

		Notification n = new Notification((float)pos.x, (float)pos.y, System.currentTimeMillis(), msg, 0.35f, c);
		synchronized (notifs) { notifs.addFirst(n); }

	}
	
	public void unPaused(){
		startUnpause = true;
		countdown = 3;
	}

	public void notifyLevel() {

		Notification n = new Notification(0.0f, 5.0f, System.currentTimeMillis(), "Level Up!", 2.0f, Colors.REWARD);
		synchronized (notifs) { notifs.addFirst(n); }

	}

	public void setPointLossX(float pointLossX){
		this.pointLossX = pointLossX;
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

		//Draw FPS, if requested with --fps command line option
		paintFPS(g2);

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
		float gradientStep = 0.05f;
		for(float i = 0; i < heightOfRedBar; i += gradientStep){
			float alpha = (1 - i/heightOfRedBar)*pointLossTimeFactor;
//			g2.fillRect(0, 10.0f-i, 16.0f, gradientStep, new Color(1,0,0,alpha));
			g2.fillCircle((float)(pointLossX+8), 11.3f, 1+i, new Color(1,0,0,alpha));
		}




		//Draw platform
		Platform rp = model.getRightPlatform();
		Platform lp = model.getLeftPlatform();
		BodyRenderer.drawBody(lp, g2, paused);
		BodyRenderer.drawBody(rp, g2, paused);
		
		//Draw blocks
		ArrayList<DrawableBody> blocks = model.getBlocks();
		synchronized (blocks) {
			for (DrawableBody b : blocks)
				BodyRenderer.drawBody(b, g2, paused);
		}
		
		//draw paused message and radial menu if paused
		if (paused && !gameOver)
			TextRenderer.drawPaused(g2);

		//Draw score
		TextRenderer.drawScore(g2, model.getScore());

		//Draw radial level indicator
		LevelRenderer.drawLevelIndicator(g2, model.getLevel(), controller.calculateLevelProgress(), paused);

		
		//Draw health bar
		HealthRenderer.drawHealthBar(g2, model.getHealth());

		//Handle notifications -- unfortunately, one thing that gives the view a bit of statefulness...
		synchronized (notifs) {

			//Prune old score notifications
			while (notifs.peekLast() != null && notifs.peekLast().expirationProgress() > 1.0f)
				notifs.removeLast();

			//Draw score and level-up notifications
			for (Notification n : notifs)
				TextRenderer.drawNotification(g2, n);

		}

		//draw game over message if game over
		if (gameOver)
			TextRenderer.drawGameOver(g2);

		//draw the active menu, if there is one
		RadialMenu menu = getActiveMenu();
		if (menu != null)
			menu.draw(g2);
		
	}

	public void menuItemSelected(int id) {

		//Temporary. TODO: actually handle menu events here.
		SoundManager.play("pointGain");
		model.addPoints(1000*id);

	}

	public RadialMenu getActiveMenu() {

		if (false)
			return null; //In the future, this will be for a leaderboard menu.
		if (model.isGameOver())
			return null; //In the future, this will be for a game over menu.
		if (controller.isPaused())
			return pausedMenu;

		return null;

	}

	//called by LeapController to indicate position of pointer.
	public void pointerUpdate(double x, double y) {

		RadialMenu m = getActiveMenu();
		if (m == null)
			return;

		m.pointerUpdate((float)x, (float)y);

	}

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

	long lastPaint = 0;
	int paintId = 0;
	float fps = 0;
	boolean showFPS = false; /* set from Main */
	static final int FPS_SAMPLE = 10;
	public void paintFPS(GraphicsWrapper g2) {

		if (!showFPS) return;

		paintId = (paintId + 1) % FPS_SAMPLE;
		if (paintId == 0) {
			long now = System.currentTimeMillis();
			fps = FPS_SAMPLE * 1000f / (now - lastPaint);
			lastPaint = now;
		}
		g2.drawString(String.format("%.1f fps", fps), 0.2f, Color.white, 15f, 9.6f);

	}

}