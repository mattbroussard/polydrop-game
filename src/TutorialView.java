import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


public class TutorialView extends View  {
	
	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	
	World world;
	Platform p;
	
	long time;
	long squareSpawnTime;
	
	private boolean paused;
	
	public TutorialView(){
		world = new World(new Vec2(0.0f, -20f));
		
		System.out.println("Tutorial view being created");
		
		p = new Platform(world, Platform.FULL);
		time = System.currentTimeMillis();
	}

	public void draw(GraphicsWrapper g2, boolean active) {
		if(!isPaused()){
			update();
		}
		
		Color bg = isPaused() ? Colors.PAUSED : Colors.BACKGROUND;
		g2.fillRect(0f, 0f, 16f, 10f, bg);
		
		//TODO: clearly, this is temporary
		//g2.drawStringCentered("tutorial, page "+(page+1), 0.5f, Colors.SCORE, 8.0f, 5.0f);
		
		BodyRenderer.drawBody(p, g2, !active);
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, isPaused());
		}
		
		g2.prepare();

	}
	
	public void pause(boolean paused){
		this.paused = paused;
	}
	
	public void update(){
		long now = System.currentTimeMillis();
		if(now - squareSpawnTime >= 2.5*1000) {
			DrawableBody db = spawn();
			synchronized (blockList) {
				blockList.add(db);
			}
			squareSpawnTime = now;
		}		

		world.step((now-time)/1000f, 6, 2);
		
		//Reduce lifetimes
		Iterator<DrawableBody> itr = blockList.iterator();
		while( itr.hasNext() ) {
			DrawableBody b = itr.next();
			long dt = now - time;
			b.reduceLifetime(dt);
			if( b.getExpiration() <= 0 ) {
				// remove block
				itr.remove();
				world.destroyBody(b.getBody());
			}
			

			Vec2 pos = b.getBody().getPosition();
			if(pos.y < -2) {
				// Oh no! Lose points. :(
				itr.remove();
				world.destroyBody(b.getBody());				
			}
		}

		time = System.currentTimeMillis();
	}
	
	public void updatePlatform(double handx, double handy, double theta, long dt){

		if (handx != -1) {
			double dtheta = theta - p.getBody().getAngle();
			double dx = (16*(float)handx - 8) - p.getBody().getPosition().x;
			double dy = (10*(float)handy)     - p.getBody().getPosition().y;
			p.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
			p.getBody().setAngularVelocity((float)(dtheta/dt*1000));
		} else {
			p.getBody().setLinearVelocity(new Vec2(0f, 0f));
			p.getBody().setAngularVelocity(0f);
		}
	
	}
	
	public boolean isPaused(){
		return paused;
	}
	
	public DrawableBody spawn() {
		int sides = (int)Math.round(Math.random()*5) + 3;
		float x;

		int newPoly = (int)(Math.random()*5)+3;
		
		x = (float)(Math.random() * 10 - 8 );
		
		System.out.println("spawing at "+x);
		return new PolyBody(world, x, newPoly, GameModel.FREE_PLAY);
	}

}