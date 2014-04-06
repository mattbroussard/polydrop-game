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
	static final int SCORE_ONE = 3;
	static final int SCORE_TWO = 4;
	static final int HEALTH_ONE = 5;
	static final int HEALTH_TWO = 6;
	static final int HEALTH_THREE = 7;
	static final int LEVEL_INDICATOR = 8;
	static final int PAUSE = 9;
	static final int UNPAUSE = 10;
	static final int PAUSE_AGAIN = 11;
	static final int MOVE_CURSOR = 12;
	static final int LINE_UP_CURSOR = 13;
	static final int SELECT_PLAY = 14;
	static final int GAMEOVER = 15;
	
	static final int PAUSE_DELAY = 100;
	
	private int score = 0;
	private int health = 35;
	
	long pauseTimer = 0;
	long time;
	long squareSpawnTime;
	long healthIncrease;
	long startedLevel;
	
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
			"This will tell you your score",
			"Your score will increase everytime you catch a poly",
			"This will tell you how much health you have left",
			"Your health will decrease every time you drop a poly",
			"Your health regenerates with time",
			"This tells you how close you are to leveling up based on your score",
			"Make a fist to pause the game",							
			"Unclench your fist to unpause",							
			"Pause again",												
			"Now stick out your pointer finger to control the cursor",	
			"Move the cursor here",										
			"Drag the cursor to the left until you start the game!",
			"Don't be that guy"};	
	

	
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
		case SCORE_ONE:
			setStringCoord(3f,2.5f);
			g2.fillRect(.5f, 1.25f, 3.5f, .125f, Colors.HEALTH_GOOD);
			break;
		case SCORE_TWO:
			setStringCoord(5.75f,2.5f);
			g2.fillRect(.5f, 1.25f, 3.5f, .125f, Colors.HEALTH_GOOD);
			break;
		case HEALTH_ONE:
			setStringCoord(10f,2.5f);
			g2.fillRect(11.5f, 1.5f, 3, .125f, Colors.HEALTH_GOOD);
			break;
		case HEALTH_TWO:
			setStringCoord(10f,2.5f);
			g2.fillRect(11.5f, 1.5f, 3, .125f, Colors.HEALTH_GOOD);
			break;
		case HEALTH_THREE:
			setStringCoord(11f,2.5f);
			g2.fillRect(11.5f, 1.5f, 3, .125f, Colors.HEALTH_GOOD);
			break;
		case LEVEL_INDICATOR:
			setStringCoord(8f,2.5f);
			g2.fillRect(14.25f,1.75f,1.5f,.125f, Colors.HEALTH_GOOD);
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
		case GAMEOVER:
			setStringCoord(8f, 6f);
			g2.drawStringCentered("GAME OVER", 2.55f, Colors.HEALTH_BAD, 8f, 4f);
		}
		
		if(level >= HOLD_POLY){
			//Draw score
			TextRenderer.drawScore(g2, score);

			//Draw radial level indicator
			LevelRenderer.drawLevelIndicator(g2, 1, .75f, isPaused());		

			//Draw health bar
			HealthRenderer.drawHealthBar(g2, health, isPaused());
			
		}
		
		g2.drawStringCentered(instructions[level], fontSize, stringColor, stringX, stringY);
		
		if(!isPaused()){
			physicsUpdate(System.currentTimeMillis());
		}else if(level > PAUSE_AGAIN && level != GAMEOVER){
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
		if(level == SELECT_PLAY){
			return;
		}
		this.paused = paused;

		
	}
	
	public void physicsUpdate(long now){	

		world.step((now-time)/1000f, 6, 2);
		
		if(now - healthIncrease > 3 * 1000 && level != GAMEOVER){
			health = Math.min(health+3, 100);
			healthIncrease = now;
		}
		
		Iterator<DrawableBody> itr = blockList.iterator();
		while( itr.hasNext() ) {
			DrawableBody b = itr.next();
			long dt = now - time;
			b.reduceLifetime(dt);
			if( b.getExpiration() <= 0 ) {
				// remove block
				itr.remove();
				world.destroyBody(b.getBody());
				score += b.getReward();
				levelUp(now);
			}
			Vec2 pos = b.getBody().getPosition();
			if(pos.y < -2) {
				//Try again
				score += b.getPenalty();
				health = Math.max(0, health-5);
				if(health == 0){
					level = GAMEOVER;
					startedLevel = now;
				}else{
					instructions[level] = "Try again!";
				}
				itr.remove();
				world.destroyBody(b.getBody());

			}
		}

		time = System.currentTimeMillis();
	}
	
	public void instructionUpdate(long now){
		switch(level){
			case MOVE_PLATFORM:
				if(dx > 15f){
					levelUp(now);
					dx = 0;
				}
				break;
			case LINE_UP_PLATFORM:
				
				if(	Math.abs(p.getBody().getPosition().x + 4 - .5) < 1
				 && Math.abs(10 - p.getBody().getPosition().y + .5 - 5) < 1){
					levelUp(now);
				}
				break;
			case HOLD_POLY:
				if(blockList.size() < 1){
					blockList.add(spawn(-8 + 4.5f));
					blockList.get(0).reduceLifetime(5);
				}

				break;	
			case SCORE_ONE:
				if(now - startedLevel > 5 * 1000){
					levelUp(now);
				}
				break;
			case SCORE_TWO:
				if(now - startedLevel > 5 * 1000){
					levelUp(now);
				}
				break;
			case HEALTH_ONE:
				if(now - startedLevel > 5 * 1000){
					levelUp(now);
				}
				break;
			case HEALTH_TWO:
				if(now - startedLevel > 5 * 1000){
					levelUp(now);
				}
				break;
			case HEALTH_THREE:
				if(now - startedLevel > 5 * 1000){
					levelUp(now);
				}
				break;
			case LEVEL_INDICATOR:
				if(now - startedLevel > 5 * 1000){
					levelUp(now);
				}
				break;
			case PAUSE:
				if(isPaused()){
					levelUp(now);
				}
				break;
			case UNPAUSE:
				if(!isPaused()){
					levelUp(now);
				}
				break;
			case PAUSE_AGAIN:
				if(isPaused()){
					levelUp(now);
				}
				break;
			case MOVE_CURSOR:
				if(dx > 275){
					dx = 0;
					levelUp(now);
				}
				break;
			case LINE_UP_CURSOR:
				if( Math.abs(menu.cursorX - 15) < .5f
				 && Math.abs(menu.cursorY -  5) < .5f){
					levelUp(now);
				}
				break;
			case SELECT_PLAY:

				break;
			case GAMEOVER:
				if(now - startedLevel > 5 * 1000){
					level = 0;
					score = 0;
					health = 35;
					instructions[HOLD_POLY] = "Hold the poly until it dissapears!";
				}
				break;


		}
	}
	
	public void levelUp(long now){
		startedLevel = now;
		level++;
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
		return new PolyBody(world, x, newPoly, GameModel.ONE_HAND);
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