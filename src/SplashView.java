
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import java.awt.event.KeyEvent;

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
	TranslationFunction lpFunc = new TranslationFunction(new SplashPlatformPath(), -4.5f, 2.25f);
	TranslationFunction rpFunc = new TranslationFunction(new SplashPlatformPath(), 1f, 5f);
	
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
		
		rp.getBody().setTransform(new Vec2(rpFunc.getOriginX(), rpFunc.getOriginY()), 0);
		lp.getBody().setTransform(new Vec2(lpFunc.getOriginX(), lpFunc.getOriginY()), 0);

		prevTime = System.currentTimeMillis();

	}

	public void draw(GraphicsWrapper g2, boolean active) {

		update();
		g2.prepare();

		g2.fillRect(0f, 0f, 16f, 10f, Colors.PAUSED);
		if (!active) return;

		BodyRenderer.drawBody(rp, g2, false);
		BodyRenderer.drawBody(lp, g2, false);
		
		//debug
		//lpFunc.debugDraw(g2);
		//rpFunc.debugDraw(g2);
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, false);
		}

		g2.drawImage("splashLogo", 4f, 2f);

		g2.drawStringCentered("Press the spacebar for a tutorial", 0.25f, Color.WHITE, 8f, 9.7f);

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

	public void updatePlatforms(long dt){

		accumulatedTime += dt;

		lpFunc.updateBody(lp, accumulatedTime, dt);
		rpFunc.updateBody(rp, accumulatedTime + lpFunc.getPeriod()/3, dt);

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

	public void onKey(int keyCode) {
		if (keyCode == KeyEvent.VK_SPACE) {
			getViewManager().pushView("tutorial");
		}
	}

	public void pointerUpdate(float cursorX, float cursorY) {
		menu.pointerUpdate(cursorX, cursorY);
	}

}

abstract class ParametricFunction {

	public final void debugDraw(GraphicsWrapper g2) {

		//draw path of matt's platform
		float animTime = getPeriod();
		for (float i = 0; i < 1; i += 0.01f) {
			Color c = Color.getHSBColor(i, 1f, 0.7f);
			g2.fillCircle(
				getX((long)Math.round(animTime*i))+8f,
				10f-getY((long)Math.round(animTime*i)),
				0.05f,
				c
			);
		}

	}

	public final void updateBody(DrawableBody db, long time, long dt) {

		float xv = (getX(time) - db.getBody().getPosition().x) / dt * 1000f;
		float yv = (getY(time) - db.getBody().getPosition().y) / dt * 1000f;
		float av = (getTheta(time) - db.getBody().getAngle()) / dt * 1000f;
		db.getBody().setLinearVelocity(new Vec2(xv, yv));
		db.getBody().setAngularVelocity(av);

	}

	//overridable functions
	public abstract long getPeriod();
	public abstract float getX(long time);
	public abstract float getY(long time);
	public float getTheta(long time) { return 0; }

}

class SplashPlatformPath extends ParametricFunction {

	final long period = 6000;
	final long anglePeriod = 3000;
	final float xAmp = 1.0f;
	final float yAmp = 0.4f;
	final float angleAmp = 0.15f;

	public SplashPlatformPath() {}

	public long getPeriod() {
		return period;
	}

	public float getX(long time) {
		return xAmp * (float)Math.cos(2*Math.PI/period*time);
	}

	public float getY(long time) {
		return yAmp * (float)Math.sin(2*Math.PI/period*time*2);
	}

	public float getTheta(long time) {
		return (float)(angleAmp*Math.sin(2*Math.PI/anglePeriod*time));
	}

}

class TranslationFunction extends ParametricFunction {

	ParametricFunction func;
	float x;
	float y;

	public TranslationFunction(ParametricFunction func, float x, float y) {
		this.func = func;
		this.x = x;
		this.y = y;
	}

	public long getPeriod() { return func.getPeriod(); }
	public float getX(long time) { return func.getX(time) + x; }
	public float getY(long time) { return func.getY(time) + y; }
	public float getTheta(long time) { return func.getTheta(time); }

	public float getOriginX() { return x; }
	public float getOriginY() { return y; }

}
