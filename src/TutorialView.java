import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


/*class Instruction{
	float x, y;
	String name;
	
	public Instruction(float x, float y, String name){
		this.x = x;
		this.y = y;
		this.name = name;
	}
}*/


public class TutorialView extends View  implements RadialMenuListener{
	
	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	
	
	static final int TUTORIAL_MENU_PLAY = 0;
	static final int TUTORIAL_MENU_ABOUT = 1;
	static final int TUTORIAL_MENU_EXIT_GAME = 2;
	
	
	World world;
	Platform p;

	private int level = 4;
	
	long time;
	long squareSpawnTime;
	
	RadialMenu menu;
	
	private boolean paused;
	
	private String[] instructions = {	
			"Move your hand to move the platform!",
			"Line the platform up here",
			"Hold the poly until it dissapears!",
			"Make a fist to pause the game",
			"Now stick out your pointer finger\n to select a menu item!"};
	

	
	public TutorialView(){
		world = new World(new Vec2(0.0f, -20f));
		
		menu = new RadialMenu(16.5f, 5f, this);

//		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_TUTORIAL, "Tutorial", "tutorial", 123, 20, RadialMenuItem.ORIENT_LEFT));
//		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_LEADERBOARD, "Leaderboard", "leaderboard", 143, 20, RadialMenuItem.ORIENT_LEFT));
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
		
		if(!isPaused()){
			physicsUpdate(System.currentTimeMillis());
		}else if(level > 2){
			menu.draw(g2);
		}
		instructionUpdate(System.currentTimeMillis());

//		g2.drawStringCentered(instructions[level].name, .5f, Colors.PLATFORM, instructions[level].x, instructions[level].y);
		//TODO: clearly, this is temporary
		//g2.drawStringCentered("tutorial, page "+(page+1), 0.5f, Colors.SCORE, 8.0f, 5.0f);
		
		switch(level){
		case 0:
			g2.drawStringCentered(instructions[level], .5f, Colors.PLATFORM, 8f, 5f);			
			break;
		case 1:
			g2.fillRect(.5f, 5, 8, .5f, isPaused() ? Colors.BACKGROUND : Colors.PAUSED);
			g2.drawStringCentered(instructions[level], .5f, Colors.PLATFORM, 4.5f, 6.25f);
			break;
		case 2:
			g2.drawStringCentered(instructions[level], .5f, Colors.PLATFORM, 8f, 5f);
			break;
		case 3:
			g2.drawStringCentered(instructions[level], .5f, Colors.PLATFORM, 8f, 5f);
			break;
		case 4:
			g2.drawStringCentered(instructions[level], .5f, Colors.PLATFORM, 6.25f, 5f);
			break;

	}
		
		
		BodyRenderer.drawBody(p, g2, !active);
		
		for(DrawableBody db : blockList){
			BodyRenderer.drawBody(db, g2, isPaused());
		}
		
		g2.prepare();

	}
	
	public void pause(boolean paused){
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
			case 0:
				System.out.println(p.getBody().getLinearVelocity().x + p.getBody().getLinearVelocity().y);
				if(p.getBody().getLinearVelocity().x > 4.5f){
					level++;
				}
				break;
			case 1:
				
				if(	Math.abs(p.getBody().getPosition().x + 4 - .5) < 1
				 && Math.abs(10 - p.getBody().getPosition().y + .5 - 5) < 1){
					level++;
				}
				break;
			case 2:
				if(blockList.size() < 1){
					blockList.add(spawn(-8 + 4.5f));
					blockList.get(0).reduceLifetime(5);
				}

				break;
				
			case 3:
				if(isPaused()){
					level++;
				}
				break;
			case 4:
				
				break;

		}
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
		if(level > 2){
			menu.pointerUpdate(cursorX, cursorY);			
		}
	}

}