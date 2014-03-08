
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

import org.jbox2d.common.*;

public class GameView extends JComponent implements KeyListener{
	
	GameModel model;
	GameController controller;
	Leaderboard leaderboard;
	LinkedList<Notification> notifs;
	
	
	boolean recentPointLoss = false;
	int pointLossAlpha = 0;
	
	//I don't think these are used anymore except code commented by dallas in paintComponent?
	boolean startUnpause = false;
	int countdown = 3;
	float pointLossX = 0;

	boolean usingLeaderboard = false;

	RadialMenu pausedMenu;
	RadialMenu gameOverMenu;
	RadialMenu leaderboardMenu;
	RadialMenuItem muteMenuItem;

	static final int PAUSE_MENU_MODE_FREE = 0;
	static final int PAUSE_MENU_MODE_DUAL = 1;
	static final int PAUSE_MENU_MODE_SINGLE = 2;
	static final int PAUSE_MENU_EXIT_GAME = 3;
	static final int PAUSE_MENU_LEADERBOARD = 4;
	static final int PAUSE_MENU_MUTE = 5;

	static final int LEADERBOARD_MENU_CLEAR = 6;
	static final int LEADERBOARD_MENU_EXIT = 7;

	static final int GAMEOVER_MENU_NEWGAME = 8;
	static final int GAMEOVER_MENU_EXIT_GAME = 9;
	static final int GAMEOVER_MENU_LEADERBOARD = 10;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;
		notifs = new LinkedList<Notification>();

		//Construct paused menu
		pausedMenu = new RadialMenu(8, 5.5f, this);
		pausedMenu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_FREE, "Free Play", "freeMode", 60, 20, Colors.MENU_MODE_FREE_SELECTED, Colors.MENU_MODE_FREE_ACTIVE));
		pausedMenu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_DUAL, "Two Hands", "dualMode", 80, 20, Colors.MENU_MODE_DUAL_SELECTED, Colors.MENU_MODE_DUAL_ACTIVE));
		pausedMenu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_SINGLE, "One Hand", "singleMode", 100, 20, Colors.MENU_MODE_SINGLE_SELECTED, Colors.MENU_MODE_SINGLE_ACTIVE));
		pausedMenu.addItem(new RadialMenuItem(PAUSE_MENU_EXIT_GAME, "Exit Game", "exit", 240, 20));
		pausedMenu.addItem(new RadialMenuItem(PAUSE_MENU_LEADERBOARD, "High Scores", "leaderboard", 260, 20));
		muteMenuItem = new RadialMenuItem(PAUSE_MENU_MUTE, "Mute", "mute", 280, 20);
		pausedMenu.addItem(muteMenuItem);

		//Constuct game over menu
		gameOverMenu = new RadialMenu(8, 11.5f, this);
		gameOverMenu.addItem(new RadialMenuItem(GAMEOVER_MENU_NEWGAME, "New Game", "newGame", 100, 20));
		gameOverMenu.addItem(new RadialMenuItem(GAMEOVER_MENU_EXIT_GAME, "Exit Game", "exit", 80, 20));
		gameOverMenu.addItem(new RadialMenuItem(GAMEOVER_MENU_LEADERBOARD, "High Scores", "leaderboard", 60, 20));

		//Construct leaderboard menu
		leaderboardMenu = new RadialMenu(8, 11.5f, this);
		leaderboardMenu.addItem(new RadialMenuItem(LEADERBOARD_MENU_EXIT, "Back", "menuReturn", 90, 20));
		leaderboardMenu.addItem(new RadialMenuItem(LEADERBOARD_MENU_CLEAR, "Reset Scores", "clearLeaderboard", 70, 20));

	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) controller.exitGame();
		if(e.getKeyCode() == KeyEvent.VK_SPACE) controller.newGame();
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void addLeaderboard(Leaderboard l) {
		leaderboard = l;
		leaderboard.view = this;
	}

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

		//TODO: in the future, it might be nice if this were in its own Renderer class like the other components.
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
			//g2.fillRect(0, 10.0f-i, 16.0f, gradientStep, new Color(1,0,0,alpha));
			g2.fillCircle((float)(pointLossX+8), 11.3f, 1+i, new Color(1,0,0,alpha));
		}

		if (!usingLeaderboard) {

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
			
			//draw paused message if paused
			if (paused && !gameOver)
				TextRenderer.drawPaused(g2);
			
			//draw game over message if game over
			if (gameOver)
				TextRenderer.drawGameOver(g2);

		}

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

		//draw the leaderboard UI if we're using it
		if (leaderboard != null && usingLeaderboard)
			leaderboard.draw(g2);

		//draw the active menu, if there is one
		RadialMenu menu = getActiveMenu();
		if (menu != null)
			menu.draw(g2);
		
	}

	public void menuItemSelected(int id) {

		

		switch (id) {
			case PAUSE_MENU_MODE_FREE:
				pausedMenu.setActiveItem(PAUSE_MENU_MODE_FREE);

				//temp
				SoundManager.play("pointGain");
				model.addPoints(10000);
				
				break;

			case PAUSE_MENU_MODE_DUAL:
				pausedMenu.setActiveItem(PAUSE_MENU_MODE_DUAL);

				//temp
				SoundManager.play("pointGain");
				model.addPoints(10000);
				
				break;

			case PAUSE_MENU_MODE_SINGLE:
				pausedMenu.setActiveItem(PAUSE_MENU_MODE_SINGLE);

				//temp
				SoundManager.play("pointGain");
				model.addPoints(10000);
				
				break;
			case PAUSE_MENU_MUTE:
				SoundManager.toggleMuted();
				muteMenuItem.setIcon(SoundManager.isMuted() ? "unmute" : "mute");
				muteMenuItem.setTitle(SoundManager.isMuted() ? "Unmute" : "Mute");
				
				break;

			case PAUSE_MENU_EXIT_GAME:
			case GAMEOVER_MENU_EXIT_GAME:
				controller.exitGame();
				break;

			case PAUSE_MENU_LEADERBOARD:
			case GAMEOVER_MENU_LEADERBOARD:
				controller.setUsingUI(true);
				usingLeaderboard = true;
				break;

			case LEADERBOARD_MENU_CLEAR:
				if (leaderboard != null)
					leaderboard.clearLeaderboard();
				break;

			case LEADERBOARD_MENU_EXIT:
				usingLeaderboard = false;
				controller.setUsingUI(false);
				break;

			case GAMEOVER_MENU_NEWGAME:
				controller.newGame();
				break;

			default:
				return;
		}
		
		SoundManager.play("menuChoice");

	}

	public RadialMenu getActiveMenu() {

		if (usingLeaderboard)
			return leaderboardMenu;
		if (model.isGameOver())
			return gameOverMenu;
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