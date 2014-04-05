import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class TutorialView extends View  implements RadialMenuListener{
	
	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	
	
	static final int TUTORIAL_MENU_PLAY = 0;
	static final int TUTORIAL_MENU_ABOUT = 1;
	static final int TUTORIAL_MENU_EXIT_GAME = 2;
	
	static final int MOVE_PLATFORM = 0;
	static final int LINE_UP_PLATFORM = 1;
	static final int HOLD_POLY = 2;
	static final int PAUSE = 3;
	static final int UNPAUSE = 4;
	static final int PAUSE_AGAIN = 5;
	static final int MOVE_CURSOR = 6;
	static final int LINE_UP_CURSOR = 7;
	static final int SELECT_PLAY = 8;
	
	static final int PAUSE_DELAY = 100;
	
	long pauseTimer = 0;
	long time;
	long squareSpawnTime;
	
	World world;
	Platform p;

	private int level = 0;
	
	float dx = 0;
	float stringX = 0;
	float stringY = 0;
	float fontSize = .5f;
	
	Color stringColor = Colors.PLATFORM;
	
	RadialMenu menu;
	
	private boolean paused;

	
	private String[] instructions = {	
			"Move your hand to move the platform!",
			"Line the platform up here",							
			"Hold the poly until it dissapears!",					
			"Make a fist to pause the game",							
			"Unclench your fist to unpause",							
			"Pause again",												
			"Now stick out your pointer finger to control the cursor",	
			"Move the cursor here",										
			"Drag the cursor to the left until you start the game!"};	
	

	
	public TutorialView(){
		world = new World(new Vec2(0.0f, -20f));
		
		menu = new RadialMenu(16.5f, 5f, this);

		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_PLAY, "Play", "singleMode", 163, 34, RadialMenuItem.ORIENT_LEFT, Colors.SPLASH_MENU_PLAY_SELECTED, Colors.SPLASH_MENU_PLAY_ACTIVE));
		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_ABOUT, "About", "about", 143, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_EXIT_GAME, "Exit Game", "exit", 197, 20, RadialMenuItem.ORIENT_LEFT));
		
		menu.setActiveItem(TUTORIAL_MENU_PLAY);
		
		p = new Platform(world, Platform.FULL);
		time = System.currentTimeMillis();
	}

	public void draw(GraphicsWrapper g2, boolean active) {
		
		Color bg = isPaused() ? Colors.PAUSED : Colors.BACKGROUND;
		g2.fillRect(0f, 0f, 16f, 10f, bg);
		
		switch(level){
		case MOVE_PLATFORM:	
			setStringCoord(8f,5f);
			break;
		case LINE_UP_PLATFORM:
			setStringCoord(4.5f,6.25f);
			g2.fillRect(.5f, 5, 8, .5f, isPaused() ? Colors.BACKGROUND : Colors.PAUSED);
			break;
		case HOLD_POLY:
			setStringCoord(8f,5f);
			break;
		case PAUSE:
			setStringCoord(8f,5f);
			break;
		case UNPAUSE:
			setStringCoord(8f,5f);
			break;
		case PAUSE_AGAIN:
			setStringCoord(8f,5f);
			break;		
		case MOVE_CURSOR:
			setStringCoord(6.25f,5f);
			break;
		case LINE_UP_CURSOR:
			setStringCoord(12f,1.5f);
			g2.fillCircle(15, 5, .25f, Colors.LEAP_WARNING_OVERLAY);
			break;
		case SELECT_PLAY:
			setStringCoord(6.25f,5f);
			g2.fillRect(12, 5, 3, .125f, Colors.LEAP_WARNING_OVERLAY);
			break;
		}
		
		g2.drawStringCentered(instructions[level], fontSize, stringColor, stringX, stringY);
		
		if(!isPaused()){
			physicsUpdate(System.currentTimeMillis());
		}else if(level > PAUSE_AGAIN){
			menu.draw(g2);
		}
		instructionUpdate(System.currentTimeMillis());
		
		BodyRenderer.drawBody(p, g2, isPaused());
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, isPaused());
		}
		
		g2.prepare();

	}
	
	public void setStringCoord(float x, float y){
		stringX = x;
		stringY = y;
	}
	
	public void pause(boolean paused){
		
		if(paused){
			//don't pause until a short delay without unpause

			if (!isPaused() && pauseTimer == 0) {
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

		}
		if(level == instructions.length-1){
			return;
		}
		this.paused = paused;

		
	}
	
	public void physicsUpdate(long now){	

		world.step((now-time)/1000f, 6, 2);
		
		Iterator<DrawableBody> itr = blockList.iterator();
		while( itr.hasNext() ) {
			DrawableBody b = itr.next();
			long dt = now - time;
			b.reduceLifetime(dt);
			if( b.getExpiration() <= 0 ) {
				// remove block
				itr.remove();
				world.destroyBody(b.getBody());
				level++;
			}
			Vec2 pos = b.getBody().getPosition();
			if(pos.y < -2) {
				//Try again
				itr.remove();
				world.destroyBody(b.getBody());
				instructions[level] = "Try again!";
			}
		}

		time = System.currentTimeMillis();
	}
	
	public void instructionUpdate(long now){
		switch(level){
			case MOVE_PLATFORM:
				if(dx > 7f){
					level++;
					dx = 0;
				}
				break;
			case LINE_UP_PLATFORM:
				
				if(	Math.abs(p.getBody().getPosition().x + 4 - .5) < 1
				 && Math.abs(10 - p.getBody().getPosition().y + .5 - 5) < 1){
					level++;
				}
				break;
			case HOLD_POLY:
				if(blockList.size() < 1){
					blockList.add(spawn(-8 + 4.5f));
					blockList.get(0).reduceLifetime(5);
				}

				break;
				
			case PAUSE:
				if(isPaused()){
					level++;
				}
				break;
			case UNPAUSE:
				if(!isPaused()){
					level++;
				}
				break;
			case PAUSE_AGAIN:
				if(isPaused()){
					level++;
				}
				break;
			case MOVE_CURSOR:
				if(dx > 275){
					dx = 0;
					level++;
				}
				break;
			case LINE_UP_CURSOR:
				if( Math.abs(menu.cursorX - 15) < .5f
				 && Math.abs(menu.cursorY -  5) < .5f){
					level++;
				}
				break;
			case SELECT_PLAY:

				break;


		}
	}
	
	public void updatePlatform(double handx, double handy, double theta, long dt){

		if (handx != -1) {
			System.out.println("dx: "+this.dx);
			double dtheta = theta - p.getBody().getAngle();
			double dx = (16*(float)handx - 8) - p.getBody().getPosition().x;
			double dy = (10*(float)handy)     - p.getBody().getPosition().y;
			p.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
			p.getBody().setAngularVelocity((float)(dtheta/dt*1000));
			if(level == MOVE_PLATFORM){
				this.dx  += handx;
			}
		} else {
			p.getBody().setLinearVelocity(new Vec2(0f, 0f));
			p.getBody().setAngularVelocity(0f);
		}
	
	}
	
	public boolean isPaused(){
		return paused;
	}
	
	public DrawableBody spawn(float x) {
		int sides = (int)Math.round(Math.random()*5) + 3;

		int newPoly = (int)(Math.random()*5)+3;
		
		System.out.println("spawing at "+x);
		return new PolyBody(world, x, newPoly, GameModel.FREE_PLAY);
	}

	public void onMenuSelection(int id) {

		switch (id) {
			case TUTORIAL_MENU_PLAY:
				getViewManager().pushView("paused");
				break;

			case TUTORIAL_MENU_ABOUT:
				getViewManager().pushView("about");
				break;

			case TUTORIAL_MENU_EXIT_GAME:
				System.exit(0);
				break;

			default:
				break;
		}

		SoundManager.play("menuChoice");

	}
	public void pointerUpdate(float cursorX, float cursorY) {
		if(level > PAUSE_AGAIN){
			menu.pointerUpdate(cursorX, cursorY);
			if(level == MOVE_CURSOR){
				this.dx += cursorX;
			}
		}
	}

}