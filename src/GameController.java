
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

	static final int FREE_PLAY = 0;
	static final int ONE_HAND  = 1;
	static final int TWO_HANDS = 2;

	GameModel model;
	GameView view;
	Leaderboard leaderboard;
	boolean paused = false;
	boolean usingUI = false;
	
	int hands;
	
    //final int timesToSpawn[] = {0,1000,600,400,300,250,200,150,150};
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

	Thread t;
	
	private ArrayList<Double> dxList = new ArrayList<Double>();

	public GameController(GameModel m) {
		model = m;
		leaderboard = new Leaderboard(this);
		t = new Thread(this);
		t.start();
	}
	
	public void exitGame() {

		//For now, just exit. In the future, we may have cleanup things to do first.
		System.exit(0);

	}

	public void setHands(int h){
		hands = h;
	}
	
	public void addView(GameView v) {
		view = v;
		v.addLeaderboard(leaderboard);
	}
	
	public void setUsingUI(boolean using) {
		usingUI = using;
		if (using && !isPaused() && !model.isGameOver())
			pause(false);
	}

	public synchronized void pause(boolean delay) {

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
		if (view != null)
			view.repaint();

	}

	public synchronized void unpause(boolean delay) {
		
		//don't allow accidental unpauses if the user is using UI things (such as leaderboard)
		if (usingUI) {
			if (!isPaused())
				pause(false);
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
		if (view != null)
			view.unPaused();
		paused = false;

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
		int sides = (int)Math.round(Math.random()*5) + 3;
		float x;

        // int level = Arrays.binarySearch(scoreNeededToLevel, model.getMaxScore());
		int level = model.getLevel();
		if(level >= distributions.length) level = distributions.length-1;
		int newPoly = (int)(Math.random()*(distributions[level].length));
		//return distributions[newPoly] == 4 ? new Square(model.world, x) : new PolyBody(model.world, x, distributions[newPoly], Colors.SHAPES[distributions[newPoly]-3]);
		//long now = System.currentTimeMillis();
		
		if(getDx() <  .03 && Math.random() < .5 ) //Player is not moving that much
		{
			//Find the locations of the platform(s)
			//While loops are inefficient, but okay for now
			if(hands > 1 ){
				float rpx = model.getRightPlatform().getBody().getPosition().x;
				float lpx = model.getLeftPlatform().getBody().getPosition().x;
				//Dont pick any numbers within rpx +- 2 or lpx +- 2
				x = (float)(Math.random() * 14 - 7);
				while(Math.abs(x-rpx) < 2 || Math.abs(x-lpx) < 2) x = (float)(Math.random() * 14 - 7);
			}else{
				float rpx = model.getRightPlatform().getBody().getPosition().x - 2;
				x = (float)(Math.random() * 14 - 7);
				while(Math.abs(rpx - x) < 4) x = (float)(Math.random() * 14 - 7);
			}
		}else{
			
			x = (float)(Math.random() * 12 - 6 );
		}
		System.out.println("spawing at "+x);
		return new PolyBody(model.world, x, distributions[level][newPoly]);
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
				time = System.currentTimeMillis();
				if (view!=null)
					view.repaint();
				continue;
			}

			// drop block every 2 seconds
			long now = System.currentTimeMillis();
			if(now - squareSpawnTime >= 2*timesToSpawn[model.getLevel()]) {
				DrawableBody db = spawn();
				synchronized (model.blockList) {
					model.blockList.add(db);
				}
				squareSpawnTime = now;
			}
			
			// restore health
			now = System.currentTimeMillis();
			if(now - healthTime >= 0.5*1000) {
				model.increaseHealth();
				healthTime = now;
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
						model.addPoints(b.getValue());
						SoundManager.play("pointGain");
						if (view != null)
							view.notifyScore(b, b.getValue());
						
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
						view.setPointLossX(pos.x);
						System.out.println("pos: "+pos.x);
						itr.remove();
						model.reduceHealth();
						if (view != null)
							view.notifyScore(b, -20);
						model.world.destroyBody(b.getBody());
						model.addPoints(-20);
						SoundManager.play("pointLoss");
					}
				}
			}
			
			if (view!=null)
				view.repaint();
			time = now;
		}
	}
	
	public synchronized void updatePlatformPosition(double rhandx, double rhandy, double rtheta, double lhandx, double lhandy, double ltheta, double dt) {
		if (isPaused() || model.isGameOver()) return;
		//model.platform.getBody().setTransform(model.platform.getBody().getPosition(), (float) theta);
		double dtheta = rtheta - model.rp.getBody().getAngle();
		float dx = (16*(float)rhandx - 8) - model.rp.getBody().getPosition().x;
		float dy = (10*(float)rhandy)     - model.rp.getBody().getPosition().y;
		model.rp.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
		model.rp.getBody().setAngularVelocity((float)(dtheta/dt*1000));	
		dxList.add((double) Math.abs(dx));
		if(dxList.size() > 10)
			dxList.remove(0);
		
		if(lhandx == 0 && lhandy == 0 && ltheta == 0) {
			dx = (float) ((16*(float)rhandx - 8) - (4*Math.cos(rtheta)) - model.getLeftPlatform().getBody().getPosition().x);
			dy = (float)((10*(float)rhandy - 4*Math.sin(rtheta)) - model.getLeftPlatform().getBody().getPosition().y);
			dtheta = rtheta - model.getLeftPlatform().getBody().getAngle();
			model.getLeftPlatform().getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
			model.getLeftPlatform() .getBody().setAngularVelocity((float)(dtheta/dt*1000));	
		} else {
			dtheta = ltheta - model.lp.getBody().getAngle();
			dx = (16*(float)lhandx - 8) - model.lp.getBody().getPosition().x;
			dy = (10*(float)lhandy)     - model.lp.getBody().getPosition().y;
			model.lp.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
			model.lp.getBody().setAngularVelocity((float)(dtheta/dt*1000));		
			dxList.add((double) Math.abs(dx));
			if(dxList.size() > 10)
				dxList.remove(0);
		}
	}
	
	public synchronized double getDx(){
		double dx = 0;
		for(Double d : dxList){
			dx += d;
		}
		//System.out.println("DX: "+dx/dxList.size());
		return dx/dxList.size();
	}

	public void newGame() {
		if (!model.isGameOver())
			return;
		model = new GameModel();
		if (view != null)
			view.model = model;
	}
}