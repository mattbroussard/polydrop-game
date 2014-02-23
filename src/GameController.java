
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;
import java.util.Iterator;

import java.awt.Color;

public class GameController implements Runnable {
	
	GameModel model;
	GameView view;

	boolean paused = false;

	Thread t;

	public GameController(GameModel m) {
		model = m;

		t = new Thread(this);
		t.start();

	}
	
	public void addView(GameView v) {
		view = v;
	}
	
	public synchronized void pause(){
		paused = true;
		if (view != null) view.repaint();
	}

	public synchronized void unpause() {
		paused = false;
	}

	public synchronized boolean isPaused() {
		return paused;
	}
	
	public DrawableBody spawn() {

		int sides = (int)Math.round(Math.random()*5) + 3;
		float x = (float)(Math.random() * 10 - 5);

		int distributions[] = {3,3,3,3,3,4,4,5,5,6,6,7,8,8,8};
		int newPoly = (int)(Math.random()*(distributions.length));
		//return distributions[newPoly] == 4 ? new Square(model.world, x) : new PolyBody(model.world, x, distributions[newPoly], Colors.SHAPES[distributions[newPoly]-3]);
		//long now = System.currentTimeMillis();
		return new PolyBody(model.world, x, distributions[newPoly]);

	}

	public void run() {
		long squareSpawnTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		long platformPositionTime = System.currentTimeMillis();
		while (true) {
			
			// Just spin if we're paused
			if (isPaused()) {
				time = System.currentTimeMillis();
				continue;
			}

			// drop block every 2 seconds
			long now = System.currentTimeMillis();
			if(now - squareSpawnTime >= 2*1000) {
				DrawableBody db = spawn();
				model.blockList.add(db);
				//model.addPoints(db.getValue());
				squareSpawnTime = now;
			}
			
			// physics update
			now = System.currentTimeMillis();
			model.world.step((now-time)/1000f, 6, 2);
			model.platform.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
			model.platform.getBody().setAngularVelocity(0);
			
			Iterator<DrawableBody> itr = model.blockList.iterator();
			while( itr.hasNext() ) {
				DrawableBody b = itr.next();
				long dt = now - time;
				b.reduceLifetime(dt);
				if( b.getExpiration() <= 0 ) {
					// yay, points!
					model.addPoints(b.getValue());
					view.notifyScore(b, b.getValue());
					itr.remove();
					model.world.destroyBody(b.getBody());
				}
			}
			if (view!=null) {
				view.repaint();
			}
			time = now;

			// remove blocks that have fallen
			itr = model.blockList.iterator();
			while( itr.hasNext() ) {
				DrawableBody b = itr.next();
				Vec2 pos = b.getBody().getPosition();
				if(pos.y < -2) {
					// Oh no! Lose points. :(
					itr.remove();
					model.world.destroyBody(b.getBody());
					model.addPoints(-50);
					view.notifyScore(b, -50);
				}
			}
			
			try { Thread.sleep(50); } catch (Exception e) {}

		}

	}

	public synchronized void updatePlatformPosition(double handx, double handy, double theta, double dt) {
		if (isPaused()) return;
		//model.platform.getBody().setTransform(model.platform.getBody().getPosition(), (float) theta);
		double dtheta = theta - model.platform.getBody().getAngle();
		double dx = (16*handx - 8) - model.platform.getBody().getPosition().x;
		double dy = (10*handy)     - model.platform.getBody().getPosition().y;
		model.platform.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
		model.platform.getBody().setAngularVelocity((float)(dtheta/dt*1000));		
	}


}