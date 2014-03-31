
import java.util.ArrayList;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class GameModel {
	
	static final Vec2 GRAVITY = new Vec2(0.0f, -20f);

	public static final int ONE_HAND  = 1;
	public static final int TWO_HANDS = 2;
	public static final int FREE_PLAY = 3;
	public static final int SPLASH_SCREEN = 4;

	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	Platform platform;
	Platform rp;
	Platform lp;

	World world;
	Body body;
	
	int gameMode;
	int score = 0;
	int maxScore = score;
	int health = 100;
	int level = 1;

	boolean gameOver = false;

	public GameModel() {
		world = new World(GRAVITY);
		setGameMode(ONE_HAND);
	}

	public int getGameMode() {
		return gameMode;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	public void setLevel(int level){
		this.level = level;
	}

	public void setGameMode(int newGameMode) {
		if( gameMode == newGameMode ) {
			return;
		}
		synchronized (world) {

			// Just set gameMode variable. Done't mess with Platform objects.
			if( gameMode    == ONE_HAND && 
				newGameMode == FREE_PLAY ) {
				gameMode = newGameMode;
				return;
			}

			// Just set gameMode variable. Done't mess with Platform objects.
			if( gameMode    == FREE_PLAY && 
				newGameMode == ONE_HAND ) {
				gameMode = newGameMode;
				return;
			}

			gameMode = newGameMode;
			if( gameMode == TWO_HANDS ) {
				if( platform != null ) {
					world.destroyBody(platform.getBody());
				}
				rp = new Platform(world, Platform.RIGHT);
				lp = new Platform(world, Platform.LEFT);
			}
			else {
				// ONE_HAND or FREE_PLAY
				if( rp != null ) {
					world.destroyBody(rp.getBody());
				}
				if( lp != null ) {
					world.destroyBody(lp.getBody());
				}
				platform = new Platform(world, Platform.FULL);
			}
		}
	}
	
	public int getScore() {
		return score;
	}
	
	public int getMaxScore(){
		return maxScore;
	}

	public int getHealth() {
		return health;
	}

	public void reduceHealth() {
		if(getGameMode() != GameModel.FREE_PLAY){
			this.health -= 5;
			this.gameOver = (this.health <= 0);
		}
	}

	public void increaseHealth() {
		if(getGameMode() != GameModel.FREE_PLAY){
			this.health = Math.min(health + 1, 100);

		}
	}
	public void restoreHealth(){
		this.health = 100;
	}

	public void addPoints(int p) {
		score += p;
		if(score > maxScore)
			maxScore = score;
	}

	public Platform getPlatform() {
		return platform;
	}
	public Platform getRightPlatform(){
		return rp;
	}
	public Platform getLeftPlatform(){
		return lp;
	}

	public ArrayList<DrawableBody> getBlocks() {
		return blockList;
	}
	
	public boolean isGameOver() {
		return this.gameOver;
	}

	public void levelUp() {
		this.level++;
	}

	public int getLevel() {
		return this.level;
	}
}