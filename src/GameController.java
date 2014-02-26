
import org.jbox2d.dynamics.*;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;

import java.util.Arrays;
import java.util.Iterator;

import java.awt.Color;
import java.util.ArrayList;

public class GameController implements Runnable {
	
	GameModel model;
	GameView view;
	boolean paused = false;

	double platformPrevx = 0;
	double platformPrevy = 0; // used to un-pause smoothly

	double platformDeltax = 0;
	double platformDeltay = 0;
	
//	final int timesToSpawn[] = {0,1000,600,400,300,250,200,150,150};
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
		t = new Thread(this);
		t.start();
	}
	
	public void addView(GameView v) {
		view = v;
	}
	
	public synchronized void pause() {
		paused = true;
		if (view != null) view.repaint();
	}

	public synchronized void pause(double handx, double handy) {
		platformPrevx = (16*handx - 8);
		platformPrevy = (10*handy);
		paused = true;
		if (view != null) view.repaint();
	}

	public synchronized void unpause() {
		view.unPaused();
		paused = false;
	}

	public synchronized void unpause(double  handx, double handy) {
		platformDeltax = platformPrevx - (16*handx - 8);
		platformDeltay = platformPrevy - (10*handy);
		paused = false;
		System.out.println("unpaused.");
	}

	public synchronized boolean isPaused() {
		return paused;
	}
	
	public int calculateLevel(int score) {
		int level = Arrays.binarySearch(scoreNeededToLevel, score);
		if(level >= 0) return level + 1;
		else{
			level += 1;
			level *= -1;
			level -= 1;
		}
		return level + 1;
	}

	public DrawableBody spawn() {
		int sides = (int)Math.round(Math.random()*5) + 3;
		float x;

//		int level = Arrays.binarySearch(scoreNeededToLevel, model.getMaxScore());
		int level = calculateLevel(model.getMaxScore());
		if(level >= distributions.length) level = distributions.length-1;
		int newPoly = (int)(Math.random()*(distributions[level].length));
		//return distributions[newPoly] == 4 ? new Square(model.world, x) : new PolyBody(model.world, x, distributions[newPoly], Colors.SHAPES[distributions[newPoly]-3]);
		//long now = System.currentTimeMillis();
		
		if(getDx() <  .05 ) //Player is not moving that much
		{
			x = (float)(Math.random() * 2 - 6);
			if(Math.random() > .5) x *= -1;
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
				if (view!=null) view.repaint();
				continue;
			}

			// drop block every 2 seconds
			long now = System.currentTimeMillis();
			if(now - squareSpawnTime >= 2*timesToSpawn[calculateLevel(model.getMaxScore())]) {
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
			model.platform.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
			model.platform.getBody().setAngularVelocity(0);
			
			synchronized (model.blockList) {

				//update lifetimes and points
				Iterator<DrawableBody> itr = model.blockList.iterator();
				while( itr.hasNext() ) {
					DrawableBody b = itr.next();
					long dt = now - time;
					b.reduceLifetime(dt);
					if( b.getExpiration() <= 0 ) {
						// yay, points!

						int oldLevel = calculateLevel(model.getMaxScore());
						model.addPoints(b.getValue());
						int newLevel = calculateLevel(model.getMaxScore());
						if (newLevel > oldLevel) {
							view.notifyLevel();
						}
						
						view.notifyScore(b, b.getValue());
						itr.remove();
						model.world.destroyBody(b.getBody());
					}
				}

			}

			synchronized (model.blockList) {
				// remove blocks that have fallen
				Iterator<DrawableBody> itr = model.blockList.iterator();
				while( itr.hasNext() ) {
					DrawableBody b = itr.next();
					Vec2 pos = b.getBody().getPosition();
					if(pos.y < -2) {
						// Oh no! Lose points. :(
						itr.remove();
						model.reduceHealth();
						view.notifyScore(b, -20);
						model.world.destroyBody(b.getBody());
						model.addPoints(-20);
					}
				}
			}
			
			if (view!=null) {
				view.repaint();
			}
			time = now;

		}

	}

	public synchronized void updatePlatformPosition(double handx, double handy, double theta, double dt) {
		if (isPaused() || model.isGameOver()) return;
		//model.platform.getBody().setTransform(model.platform.getBody().getPosition(), (float) theta);
		double dtheta = theta - model.platform.getBody().getAngle();
		float dx = (16*(float)handx - 8) - model.platform.getBody().getPosition().x;
		float dy = (10*(float)handy)     - model.platform.getBody().getPosition().y;
		model.platform.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
		model.platform.getBody().setAngularVelocity((float)(dtheta/dt*1000));	
		dxList.add((double) Math.abs(dx));
		if(dxList.size() > 10) dxList.remove(0);
	}
	
	public double getDx(){
		double dx = 0;
		for(Double d : dxList){
			dx += d;
		}
		System.out.println("DX: "+dx/dxList.size());
		return dx/dxList.size();
	}

	public void newGame() {

		if (!model.isGameOver()) return;

		model = new GameModel();
		view.model = model;

	}

}