
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;

public class GameController implements Runnable {
	
	GameModel model;
	GameView view;
	boolean paused;

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
	
	public synchronized void movePlatformRight(double d) {
	//	Platform p = model.getPlatform();
	//	p.moveRight(d);
		view.repaint();
	}
	
	public synchronized void movePlatformLeft(double d) {
		//model.getPlatform().moveLeft(d);
		view.repaint();
	}
	
	public synchronized void pause(){
		paused = !paused;
	}
	

	//temp?
	public void run() {
		long squareSpawnTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		while (true) {
			
			// drop square every 2 seconds
			long now = System.currentTimeMillis();
			if(now - squareSpawnTime >= 2*1000) {
				model.blockList.add(spawnSquare(0f));
				System.out.println("creating new square");
				squareSpawnTime = now;
			}
			
			// physics update
			now = System.currentTimeMillis();			
			model.world.step((now-time)/1000f, 6, 2);
			if (view!=null) view.repaint();
			time = now;

			// remove blocks that have fallen
			itr = model.blockList.Iterator();
			while( itr.hasNext() ) {
				block = itr.next();
				Vec2 pos = block.getBody().getPosition();
				if(pos.y < -10) {
					itr.remove();
				}
			}
			System.out.println("Number of blocks = " + model.blockList.size());
			
			try { Thread.sleep(50); } catch (Exception e) {}

		}

	}

	//stub
	public synchronized void updatePlatformPosition(double x, double y, double theta) {
//		model.platform.setLinearVelocity(new Vec2((float)(16*x-8)/50, (float)(10*y)));
		model.platform.getBody().setTransform(new Vec2((float)(16*x-8), (float)(10*y)), (float) theta);
		//theta = theta * (180f / Math.PI);
		//System.out.printf("x=%.3f, y=%.3f, theta=%.3f\n", x, y, theta);
	}

	public synchronized boolean collides(Block a, Block b) {
		for(int i = 0; i < a.npoints; i++)
			if(b.contains(a.xpoints[i], a.ypoints[i])){
				System.out.println("Collision!");
				return true;				
			}

		return false;
	}
	//public void pause() {}
	//public void unpause() {}

}