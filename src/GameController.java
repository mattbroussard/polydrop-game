
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;
import org.jbox2d.callbacks.*;

import java.util.Arrays;
import java.util.Iterator;

import java.awt.Color;
import java.util.ArrayList;

public class GameController implements Runnable {
	
	static final int PAUSE_DELAY = 100;
	long pauseTimer = 0;

	GameModel model;
	GameView view;
	Leaderboard leaderboard;
	boolean paused = false;
	
	//The existence of this flag is a bit annoying, but we need it to ensure the game won't unpause while a different view is active
	//So, it starts true and is set to false when the GameView/PausedView is entered via the SplashView, then true again if any other view is entered via those views.
	boolean usingUI = true;
	
    //final int timesToSpawn[] = {0,1000,600,400,300,250,200,150,150};
	final float timesToGainHealth[] = {0,1.5f,1,.75f,.5f,.4f,.35f,.3f,.25f}; // in seconds
	final int timesToSpawn[] = {0,1000,800,650,500,450,375,300,250};
	final int scoreNeededToLevel[] = {0,80,200,500,1000,2000,3500,5500};
	final int distributions[][] = {	{0}, //this will never run. Never on level '0'
									{3,4,5},
							 		{3,4,5,6,6},
							 		{3,4,5,6,7,7},
							 		{3,4,5,6,7,8,8},
							 		{3,4,5,6,7,8},
							 		{3,4,5,6,7,8},
							 		{3,4,5,6,7,8},
							 		{3,4,5,6,7,8}};

	
	private ArrayList<Double> dxList = new ArrayList<Double>();

	public GameController(GameModel m) {
		model = m;
		Thread t = new Thread(this);
		t.start();
	}
	
	public void exitGame() {
		//For now, just exit. In the future, we may have cleanup things to do first.
		System.exit(0);
	}
	
	public GameModel getModel() {
		return model;
	}

	public void addGameView(GameView v) {
		view = v;
	}

	public void addLeaderboard(Leaderboard l) {
		leaderboard = l;
	}
	
	public void setUsingUI(boolean using) {
		usingUI = using;
		if (using && !isPaused() && !model.isGameOver())
			pause(false);
	}

	public synchronized void pause(boolean delay) {

		//don't allow accidentally falling into the paused view if the user is using UI things (such as leaderboard)
		if (usingUI) {
			paused = true;
			pauseTimer = 0;
			return;
		}

		//don't pause until a short delay without unpause
		if (!delay) {
			pauseTimer = 0;
		} else if (!isPaused() && pauseTimer == 0) {
			pauseTimer = System.currentTimeMillis();
			System.out.println("Started pause timer");
			return;
		} else if (pauseTimer < 0) {
			pauseTimer = 0;
			System.out.println("Cancelled unpause timer");
			return;
		} else if (System.currentTimeMillis()-pauseTimer < PAUSE_DELAY) {
			return;
		}

		if (isPaused())
			return;
		pauseTimer = 0;

		System.out.println("Actually pausing.");
		paused = true;
		view.switchToPaused();
		if (view != null)
			view.repaint();

	}

	public synchronized void unpause(boolean delay) {
		
		//don't allow accidental unpauses if the user is using UI things (such as leaderboard)
		if (usingUI) {
			paused = true;
			pauseTimer = 0;
			return;
		}

		//don't unpause until a short delay without pause
		if (!delay) {
			pauseTimer = 0;
		} else if (isPaused() && pauseTimer == 0) {
			pauseTimer = -System.currentTimeMillis();
			System.out.println("Started unpause timer");
			return;
		} else if (pauseTimer > 0) {
			pauseTimer = 0;
			System.out.println("Cancelled pause timer");
			return;
		} else if (System.currentTimeMillis()+pauseTimer < PAUSE_DELAY) {
			return;
		}

		if (!isPaused())
			return;
		pauseTimer = 0;

		System.out.println("Actually unpausing.");
		paused = false;
		view.switchToUnpaused();

	}

	public synchronized boolean isPaused() {
		return paused;
	}

	public float calculateLevelProgress() {
		int curLevel = model.getLevel();

		//this prevents ArrayIndexOutOfBoundsExceptions
		if (curLevel >= scoreNeededToLevel.length)
			return 1;

		float progress = (float)(model.getScore()          - scoreNeededToLevel[curLevel-1]);
		float total = (float)(scoreNeededToLevel[curLevel] - scoreNeededToLevel[curLevel-1]);
		return Math.max(progress/total, 0);
	}

	public DrawableBody spawn() {
		float x;

        // int level = Arrays.binarySearch(scoreNeededToLevel, model.getMaxScore());
		int level = model.getLevel();
		if(level >= distributions.length) level = distributions.length-1;
		int newPoly = (int)(Math.random()*(distributions[level].length));
		//return distributions[newPoly] == 4 ? new Square(model.world, x) : new PolyBody(model.world, x, distributions[newPoly], Colors.SHAPES[distributions[newPoly]-3]);
		//long now = System.currentTimeMillis();
		
		if(getDx() <  .03 && Math.random() < .5) //Player is not moving that much
		{
			//Find the locations of the platform(s)
			//While loops are inefficient, but okay for now
			/// if(hands > 1) {
			if(model.getGameMode() == GameModel.TWO_HANDS) {
				float rpx = model.getRightPlatform().getBody().getPosition().x;
				float lpx = model.getLeftPlatform().getBody().getPosition().x;
				//Dont pick any numbers within rpx +- 2 or lpx +- 2
				x = (float)(Math.random() * 14 - 7);
				while(Math.abs(x-rpx) < 2 || Math.abs(x-lpx) < 2) {
					x = (float)(Math.random() * 14 - 7);
				}
			} else {
				// ONE_HAND or FREE_PLAY gameMode
				float platformX = model.getPlatform().getBody().getPosition().x - 2;
				x = (float)(Math.random() * 14 - 7);
				while(Math.abs(platformX - x) < 4) {
					x = (float)(Math.random() * 14 - 7);
				}
			}
		} else {			
			x = (float)(Math.random() * 12 - 6 );
		}
		System.out.println("spawing at "+x);
		int sides = !CheatManager.dropfast ? distributions[level][newPoly] : (int)Math.round(Math.random()*5)+3;
		return new PolyBody(model.world, x, sides, model.gameMode);
	}

	public void run() {
		long squareSpawnTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		long healthTime = System.currentTimeMillis();
		long platformPositionTime = System.currentTimeMillis();
		while (true) {
			
			try { Thread.sleep(50); } catch (Exception e) {}

			// Just spin if we're paused
			if (isPaused() || model.isGameOver()) {
				//System.out.printf("*/*/*/*/*/ controller thread spinning. isPaused()=%s, model.isGameOver()=%s\n", isPaused(), model.isGameOver());
				time = System.currentTimeMillis();
				if (view!=null)
					view.repaint();
				continue;
			}

			// drop block every 2 seconds
			long now = System.currentTimeMillis();
			long spawnTime = CheatManager.dropfast ? 50 : timesToSpawn[model.getLevel()];
			if(now - squareSpawnTime >= 2*spawnTime) {
				DrawableBody db = spawn();
				synchronized (model.blockList) {
					model.blockList.add(db);
				}
				squareSpawnTime = now;
			}
			
			// restore health
			now = System.currentTimeMillis();
			if(model.getGameMode() != GameModel.FREE_PLAY) {
				if(now - healthTime >= 1000 * timesToGainHealth[model.getLevel()]) {
					model.increaseHealth();
					healthTime = now;
				}
			}

			// physics update
			now = System.currentTimeMillis();
			model.world.step((now-time)/1000f, 6, 2);
			/*model.getRightPlatform().getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
			model.getRightPlatform().getBody().setAngularVelocity(0);*/
			
			//update lifetimes and points
			synchronized (model.blockList) {
				Iterator<DrawableBody> itr = model.blockList.iterator();
				while( itr.hasNext() ) {
					DrawableBody b = itr.next();
					long dt = now - time;
					b.reduceLifetime(dt);
					if( b.getExpiration() <= 0 ) {
						// yay, points!
						model.addPoints(b.getReward());
						SoundManager.play("pointGain");
						if (view != null)
							view.notifyScore(b, b.getReward());
						
						// level up
						if(model.getLevel() < scoreNeededToLevel.length && model.getScore() >= scoreNeededToLevel[model.getLevel()]) {
							model.levelUp();
							if (view != null)
								view.notifyLevel();
							SoundManager.play("levelup");
						}

						// remove block
						itr.remove();
						model.world.destroyBody(b.getBody());
					}
				}
			}

			// remove blocks that have fallen
			synchronized (model.blockList) {
				Iterator<DrawableBody> itr = model.blockList.iterator();
				while( itr.hasNext() ) {
					DrawableBody b = itr.next();
					Vec2 pos = b.getBody().getPosition();
					if(pos.y < -2) {
						// Oh no! Lose points. :(
						if(pos.x < -8) {
							view.setPointLossX(-8);
						} else if(pos.x > 8) {
							view.setPointLossX(8);
						} else {
							view.setPointLossX(pos.x);
						}
						System.out.println("pos: "+pos.x);
						itr.remove();
						if(model.getGameMode() != GameModel.FREE_PLAY) {
							model.reduceHealth();
							if (CheatManager.failfast) {
								for (int i = 0; i < 4; i++)
									model.reduceHealth();
							}
						}
						if (view != null)
							view.notifyScore(b, b.getPenalty());
						model.addPoints(b.getPenalty());
						model.world.destroyBody(b.getBody());
						SoundManager.play("pointLoss");						
					}
				}
			}

			if(model.isGameOver()){
				//reentrancy prevented by isGameOver spin check at top of loop
				setUsingUI(true);
				view.switchToGameOver();
				if (leaderboard != null)
					leaderboard.reportScore(model.getScore());
			}

			if (view!=null)
				view.repaint();
			time = now;
		}
	} 
	
	public synchronized void updatePlatformPosition(double rhandx, double rhandy, double rtheta, double lhandx, double lhandy, double ltheta, double dt) {
		if (isPaused() || model.isGameOver()) {
			return;
		}

		if (rhandx != -1) {
			movePlatform(model.getRightPlatform(), rhandx, rhandy, rtheta, dt);
		} else {
			model.getRightPlatform().getBody().setLinearVelocity(new Vec2(0f, 0f));
			model.getRightPlatform().getBody().setAngularVelocity(0f);
		}
		
		if (lhandx != -1) {
			movePlatform(model.getLeftPlatform(), lhandx, lhandy, ltheta, dt);
		} else {
			model.getLeftPlatform().getBody().setLinearVelocity(new Vec2(0f, 0f));
			model.getLeftPlatform().getBody().setAngularVelocity(0f);
		}
	}

	public synchronized void updatePlatformPosition(double handx, double handy, double theta, double dt) {
		if (isPaused() || model.isGameOver()) {
			return;
		}
		
		movePlatform(model.platform, handx, handy, theta, dt);
	}

	private synchronized void movePlatform(Platform p, double handx, double handy, double theta, double dt) {
		double dtheta = theta - p.getBody().getAngle();
		double dx = (16*(float)handx - 8) - p.getBody().getPosition().x;
		double dy = (10*(float)handy)     - p.getBody().getPosition().y;
		p.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
		p.getBody().setAngularVelocity((float)(dtheta/dt*1000));		
		
		dxList.add((double) Math.abs(dx));
		if(dxList.size() > 10)
			dxList.remove(0);
	}
	
	public synchronized double getDx(){
		double dx = 0;
		for(Double d : dxList){
			dx += d;
		}
		return dx/dxList.size();
	}

	public void newGame() {
		if (!model.isGameOver())
			return;
		
		int oldMode = model.getGameMode();
		model = new GameModel();
		model.setGameMode(oldMode);
		
		if (view != null) {
			view.model = model;
		}

		paused = false;
		pauseTimer = 0;
		view.switchToUnpaused();

	}
}