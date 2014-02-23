
import java.util.ArrayList;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class GameModel {
	
	static final Vec2 GRAVITY = new Vec2(0.0f, -20f);

	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	Platform platform;

	World world;
	Body body;
	
	int score = 0;
	int maxScore = score;
	int health = 100;

	public GameModel() {
		world = new World(GRAVITY);
		platform = new Platform(world);		
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
		this.health -= 10;
	}

	public void increaseHealth() {
		this.health += 1;
	}

	public void addPoints(int p) {
		score += p;
		if(score > maxScore) maxScore = score;
	}

	public Platform getPlatform(){
		return platform;
	}

	public ArrayList<DrawableBody> getBlocks() {
		return blockList;
	}
	

}