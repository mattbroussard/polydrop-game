import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


public class SplashView extends View implements RadialMenuListener {
	
	RadialMenu menu;

	static final int SPLASH_MENU_PLAY = 0;
	static final int SPLASH_MENU_TUTORIAL = 1;
	static final int SPLASH_MENU_LEADERBOARD = 2;
	static final int SPLASH_MENU_ABOUT = 3;
	static final int SPLASH_MENU_EXIT_GAME = 4;
	
	long prevTime;
	World world;

	long prevSpawnTime; 
	double dropRate = 0.5*1000; // milliseconds
	
	Platform rp, lp;
	float leftPlatformX = -4.5f;
	float rightPlatformX = 1.5f;
	float amplitude = 1.5f; // amplitude
	float platformSpeed = (float)Math.PI/2000f; // rad/s
	float platformAngleSpeed = (float)Math.PI/3000f;
	
	long accumulatedTime = 0;
	float maxdt = 50f;
	
	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();

	public SplashView() {

		menu = new RadialMenu(16.5f, 5f, this);

		menu.addItem(new RadialMenuItem(SPLASH_MENU_TUTORIAL, "Tutorial", "tutorial", 123, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_LEADERBOARD, "Leaderboard", "leaderboard", 143, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_PLAY, "Play", "singleMode", 163, 34, RadialMenuItem.ORIENT_LEFT, Colors.SPLASH_MENU_PLAY_SELECTED, Colors.SPLASH_MENU_PLAY_ACTIVE));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_ABOUT, "About", "about", 197, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_EXIT_GAME, "Exit Game", "exit", 217, 20, RadialMenuItem.ORIENT_LEFT));

		menu.setActiveItem(SPLASH_MENU_PLAY);
		world = new World(new Vec2(0.0f, -20f));
		
		rp = new Platform(world, Platform.RIGHT);
		lp = new Platform(world, Platform.LEFT);
		
		rp.getBody().setTransform(new Vec2(rightPlatformX, 4), 0);
		lp.getBody().setTransform(new Vec2(leftPlatformX, 2), 0);

		prevTime = System.currentTimeMillis();
	}

	public void draw(GraphicsWrapper g2, boolean active) {
		update();
		g2.prepare();

		g2.fillRect(0f, 0f, 16f, 10f, Colors.PAUSED);
		if (!active) return;

		
		BodyRenderer.drawBody(rp, g2, false);
		BodyRenderer.drawBody(lp, g2, false);
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, false);
		}

		g2.drawImage("splashLogo", 4f, 2f);

		menu.draw(g2);

	}
	
	public void update(){ 
		long now = System.currentTimeMillis();
		long dt = Math.min(now - prevTime, (long)maxdt); // accounts for pausing

		// Spawn a new block
		if(now - prevSpawnTime >= dropRate) {
			DrawableBody db = spawn();
			synchronized (blockList) {
				blockList.add(db);
			}
			prevSpawnTime = now;
		}		

		// advance physics
		updatePlatforms(dt);
		world.step(dt/1000f, 6, 2);
		prevTime = System.currentTimeMillis();
		
		// Reduce lifetimes
		Iterator<DrawableBody> itr = blockList.iterator();
		while( itr.hasNext() ) {
			DrawableBody b = itr.next();
			b.reduceLifetime(dt);
			if( b.getExpiration() <= 0 ) {
				// Block expires
				itr.remove();
				world.destroyBody(b.getBody());
			}			

			Vec2 pos = b.getBody().getPosition();
			if(pos.y < -2) {
				// Block falls off screen
				itr.remove();
				world.destroyBody(b.getBody());				
			}
		}

		prevTime = now;
	}

	private float platformPositionFromTime(long time) {
		float x = (float)(amplitude*Math.sin(platformSpeed*time));
		return x;
	}

	private float platformAngleFromTime(long time) {
		float x = (float)(0.1*Math.sin(platformAngleSpeed*time));
		return x;
	}
	
	public void updatePlatforms(long dt){
		accumulatedTime += dt;

		float curX = rp.getBody().getPosition().x - rightPlatformX;
		float nextX = platformPositionFromTime(accumulatedTime);
		float v = (nextX - curX);

		rp.getBody().setLinearVelocity(new Vec2(   v,0));
		lp.getBody().setLinearVelocity(new Vec2(-1*v,0));

		float curAngle  = rp.getBody().getAngle();
		float nextAngle = platformAngleFromTime(accumulatedTime);
		v = nextAngle - curAngle;
		
		rp.getBody().setAngularVelocity(   v);
		lp.getBody().setAngularVelocity(-1*v);
	}
	
	public DrawableBody spawn() {
		int sides = (int)Math.round(Math.random()*5) + 3;
		float x;

        // int level = Arrays.binarySearch(scoreNeededToLevel, model.getMaxScore());
		int newPoly = (int)(Math.random()*5)+3;
		//return distributions[newPoly] == 4 ? new Square(model.world, x) : new PolyBody(model.world, x, distributions[newPoly], Colors.SHAPES[distributions[newPoly]-3]);
		//long now = System.currentTimeMillis();
		
		x = (float)(Math.random() * 10 - 8 );
		
		System.out.println("spawing at "+x);
		return new PolyBody(world, x, newPoly, GameModel.FREE_PLAY);
	}

	public void onMenuSelection(int id) {

		switch (id) {
			case SPLASH_MENU_TUTORIAL:
				getViewManager().pushView("tutorial");
				break;

			case SPLASH_MENU_LEADERBOARD:
				getViewManager().pushView("leaderboard");
				break;

			case SPLASH_MENU_PLAY:
				getViewManager().pushView("paused");
				break;

			case SPLASH_MENU_ABOUT:
				getViewManager().pushView("about");
				break;

			case SPLASH_MENU_EXIT_GAME:
				System.exit(0);
				break;

			default:
				break;
		}

		SoundManager.play("menuChoice");

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		menu.pointerUpdate(cursorX, cursorY);
	}

}