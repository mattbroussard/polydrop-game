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
	
	long squareSpawnTime; 
	
	Platform rp, lp;
	float a;
	float v;
	float terminalv = 3;
	long time;
	World world;
	
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
		
		rp.getBody().setTransform(new Vec2(-2,6), 0);
		lp.getBody().setTransform(new Vec2(-2,3), 0);
		v = terminalv;

	}

	public void draw(GraphicsWrapper g2, boolean active) {
		update(); 
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		g2.fillRect(0f, 0f, 16f, 10f, Colors.PAUSED);
		if (!active) return;

		
		BodyRenderer.drawBody(rp, g2, false);
		BodyRenderer.drawBody(lp, g2, false);
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, false);
		}
		
		 g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);
		 	
		//TODO: clearly, this is temporary
		g2.drawStringCentered("le splash~", 1.5f, Colors.SCORE, 8.0f, 5.0f);
		g2.drawStringCentered("build " + Main.getVersion(), 0.2f, Colors.SCORE, 8.0f, 6.5f);

		menu.draw(g2);

	}
	
	public void update(){ 
		long now = System.currentTimeMillis();
//		long time = System.currentTimeMillis();
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
		a = (lp.getBody().getPosition().x + 2) * -.1f;
		updatePlatforms();
		time = System.currentTimeMillis();
	}
	
	public void updatePlatforms(){
		rp.getBody().setLinearVelocity(new Vec2(-1*v,0));
		lp.getBody().setLinearVelocity(new Vec2(v,0));
		
	//	rp.getBody().setLinearVelocity(new Vec2(0,0));
	//	lp.getBody().setLinearVelocity(new Vec2(0,0));
			
		v += a;
		if(v > terminalv ) v = terminalv;
		if( v < -1 * terminalv) v = -1 * terminalv;
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