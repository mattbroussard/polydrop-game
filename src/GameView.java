
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

import org.jbox2d.common.*;

public class GameView extends View {
	
	GameModel model;
	GameController controller;
	LinkedList<Notification> notifs;
	
	boolean recentPointLoss = false;
	int pointLossAlpha = 0;
	float pointLossX = 0;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;
		notifs = new LinkedList<Notification>();

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

	public void notifyLevel() {

		Notification n = new Notification(0.0f, 5.0f, System.currentTimeMillis(), "Level Up!", 2.0f, Colors.REWARD);
		synchronized (notifs) { notifs.addFirst(n); }

	}

	public void onActive() {

		controller.setUsingUI(false);

	}

	public void setPointLossX(float pointLossX){
		this.pointLossX = pointLossX;
	}
	
	public void draw(GraphicsWrapper g2, boolean active) {
		draw(g2, active, true);
	}

	public void draw(GraphicsWrapper g2, boolean active, boolean showBodies) {

		//Draw background
		Color bg = !active ? Colors.PAUSED : Colors.BACKGROUND;
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		g2.fillRect(0, 0, 16, 10, bg);

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

		if (showBodies) {

			//Draw platform(s)
			if (model.getGameMode() == GameModel.ONE_HAND ||
				model.getGameMode() == GameModel.FREE_PLAY) {
				BodyRenderer.drawBody(model.getPlatform(), g2, !active);
			}
			else if (model.getGameMode() == GameModel.TWO_HANDS) {
				BodyRenderer.drawBody(model.getRightPlatform(), g2, !active);
				BodyRenderer.drawBody(model.getLeftPlatform(),  g2, !active);
			}
			
			//Draw blocks
			ArrayList<DrawableBody> blocks = model.getBlocks();
			synchronized (blocks) {
				for (DrawableBody b : blocks)
					BodyRenderer.drawBody(b, g2, !active);
			}

		}

		//Draw score
		TextRenderer.drawScore(g2, model.getScore());

		//Draw radial level indicator
		LevelRenderer.drawLevelIndicator(g2, model.getLevel(), controller.calculateLevelProgress(), !active);

		//Draw health bar
		if( model.getGameMode() != GameModel.FREE_PLAY ) {
			HealthRenderer.drawHealthBar(g2, model.getHealth(), !active);
		}

		//Handle notifications
		synchronized (notifs) {

			//Prune old score notifications
			while (notifs.peekLast() != null && notifs.peekLast().expirationProgress() > 1.0f)
				notifs.removeLast();

			//Draw score and level-up notifications
			for (Notification n : notifs)
				TextRenderer.drawNotification(g2, n);

		}
		
	}

	//this is a little annoying, but the controller doesn't know about the ViewManager
	public void switchToPaused() { getViewManager().swapView("paused"); }
	public void switchToUnpaused() { getViewManager().swapView("game"); }
	public void switchToGameOver() { getViewManager().swapView("gameover"); }
	
}