
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;

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

	public DrawableBody spawnSquare(float x) {		
		return new Square(model.world, x);
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
		Color[] colors = { Color.BLUE, Color.RED, Color.MAGENTA, Color.YELLOW, Color.ORANGE, Color.BLACK };
		return sides == 4 ? new Square(model.world, 0) : new PolyBody(model.world, 0, sides, colors[sides-3]);

	}

	public void run() {
		long squareSpawnTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		while (true) {
			
			// Just spin if we're paused
			if (isPaused()) {
				time = System.currentTimeMillis();
				continue;
			}

			// drop square every 2 seconds
			long now = System.currentTimeMillis();
			if(now - squareSpawnTime >= 2*1000) {
				DrawableBody db = spawn();
				model.blockList.add(db);
				model.addPoints(db.getValue());
				System.out.println("creating new square");
				squareSpawnTime = now;
			}
			
			// physics update
			now = System.currentTimeMillis();			
			model.world.step((now-time)/1000f, 6, 2);
			if (view!=null) view.repaint();
			time = now;

			try { Thread.sleep(50); } catch (Exception e) {}

		}

	}

	public synchronized void updatePlatformPosition(double x, double y, double theta) {

		if (isPaused()) return;
		model.platform.getBody().setTransform(new Vec2((float)(16*x-8), (float)(10*y)), (float) theta);
		
	}



}