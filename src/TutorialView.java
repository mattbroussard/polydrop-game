import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;



class Instruction{
	float x, y;
	String description;
	
	public Instruction(float x, float y, String description){
		this.x = x;
		this.y = y;
		this.description = description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
}
public class TutorialView extends View  implements RadialMenuListener{
	
	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	
	
	static final int TUTORIAL_MENU_EXIT = 0;
	
	static final int MOVE_PLATFORM = 0;
	static final int LINE_UP_PLATFORM = 1;
	static final int HOLD_POLY = 2;
	static final int SCORE_EXPLANATION_ONE = 3;
	static final int SCORE_EXPLANATION_TWO = 4;
	static final int HEALTH_EXPLANATION_ONE = 5;
	static final int HEALTH_EXPLANATION_TWO = 6;
	static final int LEVEL_INDICATOR_EXPLANATION_ONE = 7;
	static final int LEVEL_INDICATOR_EXPLANATION_TWO = 8;
	static final int RECEIVED_POINTS = 9;
	static final int PAUSE = 10;
	static final int UNPAUSE = 11;
	static final int PAUSE_AGAIN = 12;
	static final int MOVE_CURSOR = 13;
	static final int LINE_UP_CURSOR = 14;
	static final int SELECT_PLAY = 15;
	static final int GAMEOVER = 16;
	
	static final int PAUSE_DELAY = 100;
	
	private int score = 0;
	private int health = 35;
	
	long pauseTimer = 0;
	long time;
	long healthIncrease;
	long startedLevel;
	
	World world;
	Platform platform;

	private int instructionNumber = PAUSE;
	
	float dx = 0;
	float fontSize = .5f;
	
	Color stringColor = Colors.PLATFORM;
	
	RadialMenu menu;
	
	private boolean paused;

	
	private Instruction[] instructions = {	
			new Instruction(8f, 5f, "Move your hand to move the platform!"),
			new Instruction(4.5f, 6.25f, "Line the platform up here"),							
			new Instruction(8f, 5f, "Catch the poly!"),
			new Instruction(8f, 5f, "If you hold it long enough, it will increase your score"),
			new Instruction(8f, 5f, "But if you drop it, it will decrease your score and your health"),
			new Instruction(9.25f, .825f, "This is your health bar"),
			new Instruction(8f,5f,"Your health will regenerate with time"),
			new Instruction(8f,2.5f,"This tells you how close you are to leveling up based on your score"),
			new Instruction(8f,5f,"When you level up, shapes begin to fall faster"), //Thought about saying 'more frequently', but I think people will understand, and this sounds better
			new Instruction(8f,5f, "Yay! You got points!"),
			new Instruction(8f,5f, "Now, make a fist to pause the game"),							
			new Instruction(8f,5f,"Unclench your fist to unpause"),							
			new Instruction(8f,5f,"Pause again"),												
			new Instruction(6.25f,5f, "Now stick out your pointer finger to control the cursor"),	
			new Instruction(12f,1.5f,"Move the cursor here"),										
			new Instruction(6.25f,5f,"Drag the cursor to the left to go back!"),
			new Instruction(8f, 6f,"Don't be that guy") };	
	

	
	public TutorialView(){
		world = new World(new Vec2(0.0f, -20f));
		
		menu = new RadialMenu(16.5f, 5f, this);

		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_EXIT, "Back", "menuReturn", 163, 34, RadialMenuItem.ORIENT_LEFT));

		menu.setActiveItem(TUTORIAL_MENU_EXIT);
		
		platform = new Platform(world, Platform.FULL);
		time = System.currentTimeMillis();
	}

	public void draw(GraphicsWrapper g2, boolean active) {
		
		if(!active){
			return;
		}
		
		Color bg = isPaused() ? Colors.PAUSED : Colors.BACKGROUND;
		g2.fillRect(0f, 0f, 16f, 10f, bg);
		
		switch(instructionNumber){
		case MOVE_PLATFORM:
			g2.drawStringCentered("And make sure your fingers are spread!", .5f, stringColor, 8f, 6f);
			break;
		case LINE_UP_PLATFORM:
			g2.fillRect(.5f, 5, 8, .5f, isPaused() ? Colors.BACKGROUND : Colors.PAUSED);
			break;
/*		case SCORE_ONE:
			g2.fillRect(.5f, 1.25f, 3.5f, .125f, Colors.HEALTH_GOOD);
			break;
		case SCORE_TWO:
			g2.fillRect(.5f, 1.25f, 3.5f, .125f, Colors.HEALTH_GOOD);
			break;*/
		case HEALTH_EXPLANATION_ONE:
		case HEALTH_EXPLANATION_TWO:
			//g2.fillRect(11.5f, 1.5f, 3, .125f, Colors.HEALTH_GOOD);
			break;
		case LEVEL_INDICATOR_EXPLANATION_ONE:
		case LEVEL_INDICATOR_EXPLANATION_TWO:
			//g2.fillRect(14.25f,1.75f,1.5f,.125f, Colors.HEALTH_GOOD);
			break;
		case LINE_UP_CURSOR:
			g2.maskCircle(15, 5, .25f);
			g2.fillCircle(15, 5, .5f, Colors.LEAP_WARNING_OVERLAY);
			break;
		case SELECT_PLAY:
			g2.fillRect(12, 5, 3, .125f, Colors.LEAP_WARNING_OVERLAY);
			break;
		case GAMEOVER:
			g2.drawStringCentered("GAME OVER", 2.55f, Colors.HEALTH_BAD, 8f, 4f);
			break;
			default:
				break;
		}
		
		if(instructionNumber >= HOLD_POLY){
			//Draw score
			TextRenderer.drawScore(g2, score);

			//Draw radial level indicator
			LevelRenderer.drawLevelIndicator(g2, 1, .75f, isPaused());		

			//Draw health bar
			HealthRenderer.drawHealthBar(g2, health, isPaused());
			
		}
		
		g2.drawStringCentered(instructions[instructionNumber].description, fontSize, stringColor, instructions[instructionNumber].x, instructions[instructionNumber].y);
		
		if(!isPaused()){
			physicsUpdate(System.currentTimeMillis());
		}else{
			TextRenderer.drawPaused(g2);
			if(instructionNumber > PAUSE_AGAIN && instructionNumber != GAMEOVER){
				menu.draw(g2);
			}
		}
		instructionUpdate(System.currentTimeMillis());
		
		BodyRenderer.drawBody(platform, g2, isPaused());
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, isPaused());
		}
		
		g2.prepare();

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
		if(instructionNumber >= LINE_UP_CURSOR){
			return;
		}
		this.paused = paused;

		
	}
	
	public void physicsUpdate(long now){	

		world.step((now-time)/1000f, 6, 2);
		
		if(now - healthIncrease > 3 * 1000 && instructionNumber != GAMEOVER){
			health = Math.min(health+3, 100);
			healthIncrease = now;
		}
		
		Iterator<DrawableBody> itr = blockList.iterator();
		while( itr.hasNext() ) {
			DrawableBody b = itr.next();
			long dt = now - time;
			b.reduceLifetime((long) (dt*(10/(3.5*7)))); // this is to increase the amount of time it takes the block to dissapear, so the user is still doing something when the explanation for the radial level indicator is being explained
			if( b.getExpiration() <= 0 ) {
				// remove block
				itr.remove();
				world.destroyBody(b.getBody());
				score += b.getReward();
				SoundManager.play("pointGain");
				//levelUp(now);
			}
			Vec2 pos = b.getBody().getPosition();
			if(pos.y < -2) {
				//Try again
				score += b.getPenalty();
				health = Math.max(0, health-5);
				if(health == 0){
					setInstructionNumber(GAMEOVER, now);
				}else{
					setInstructionNumber(HOLD_POLY, now);
					instructions[instructionNumber].setDescription("Try again"); 
				}
				itr.remove();
				SoundManager.play("pointLoss");	
				world.destroyBody(b.getBody());

			}
		}

		time = System.currentTimeMillis();
	}
	
	public void setInstructionNumber(int num, long now){
		startedLevel = now;
		instructionNumber = num;
	}
	
	public void instructionUpdate(long now){
		switch(instructionNumber){
			case MOVE_PLATFORM:
				if(dx > 15f){
					levelUp(now);
					dx = 0;
				}
				break;
			case LINE_UP_PLATFORM:
				
				if(	Math.abs(platform.getBody().getPosition().x + 4 - .5) < 1
				 && Math.abs(10 - platform.getBody().getPosition().y + .5 - 5) < 1){
					levelUp(now);
				}
				break;
			case HOLD_POLY:
				if(blockList.size() < 1){
					blockList.add(spawn(-8 + 4.5f));
				}
			case SCORE_EXPLANATION_ONE:
			case SCORE_EXPLANATION_TWO:
			case HEALTH_EXPLANATION_ONE:
			case HEALTH_EXPLANATION_TWO:
			case LEVEL_INDICATOR_EXPLANATION_ONE:
			case LEVEL_INDICATOR_EXPLANATION_TWO:
			case RECEIVED_POINTS:
				if(now - startedLevel > 3.5 * 1000){
					levelUp(now);
				}
				break;
			case PAUSE:
			case PAUSE_AGAIN:
				if(isPaused()){
					levelUp(now);
				}
				break;
			case UNPAUSE:
				if(!isPaused()){
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
					instructionNumber = 0;
					score = 0;
					health = 35;
					instructions[HOLD_POLY].setDescription("Hold the poly until it dissapears!"); 
				}
				break;


		}
	}
	
	public void levelUp(long now){
		startedLevel = now;
		instructionNumber++;
	}
	
	public void updatePlatform(double handx, double handy, double theta, long dt){

		if (handx != -1) {
			System.out.println("dx: "+this.dx);
			double dtheta = theta - platform.getBody().getAngle();
			double dx = (16*(float)handx - 8) - platform.getBody().getPosition().x;
			double dy = (10*(float)handy)     - platform.getBody().getPosition().y;
			platform.getBody().setLinearVelocity(new Vec2((float)(dx/dt*1000), (float)(dy/dt*1000)));
			platform.getBody().setAngularVelocity((float)(dtheta/dt*1000));
			if(instructionNumber == MOVE_PLATFORM){
				this.dx  += handx;
			}
		} else {
			platform.getBody().setLinearVelocity(new Vec2(0f, 0f));
			platform.getBody().setAngularVelocity(0f);
		}
	
	}
	
	public boolean isPaused(){
		return paused;
	}
	
	public DrawableBody spawn(float x) {
		int numberOfSides = (int)(Math.random()*5)+3;
		return new PolyBody(world, x, numberOfSides, GameModel.ONE_HAND);
	}

	public void onMenuSelection(int id) {

		switch (id) {
			case TUTORIAL_MENU_EXIT:
				getViewManager().popView();
				break;

			default:
				break;
		}

		SoundManager.play("menuChoice");

	}
	
	public void pointerUpdate(float cursorX, float cursorY) {
		if(instructionNumber > PAUSE_AGAIN){
			menu.pointerUpdate(cursorX, cursorY);
			if(instructionNumber == MOVE_CURSOR){
				this.dx += cursorX;
			}
		}
	}

}