
import java.util.ArrayList;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class GameModel {
	
	static final Vec2 GRAVITY = new Vec2(0.0f, -20f);

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
		rp = new Platform(world);
		lp = new Platform(world);
		platform = new Platform(world);
		setGameMode(GameController.ONE_HAND);
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int newGameMode) {
		gameMode = newGameMode;
		if( gameMode == GameController.ONE_HAND ) {
			world.destroyBody(rp.getBody());
			world.destroyBody(lp.getBody());
			world.createBody(platform.getBodyDef());
		}
		else if( gameMode == GameController.TWO_HANDS ) {
			world.destroyBody(platform.getBody());
			world.createBody(rp.getBodyDef());
			world.createBody(lp.getBodyDef());
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
		this.health -= 5;
		this.gameOver = (this.health <= 0);
	}

	public void increaseHealth() {
		this.health = Math.min(health + 1, 100);
	}

	public void addPoints(int p) {
		score += p;
		if(score > maxScore) maxScore = score;
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