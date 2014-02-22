
import java.util.ArrayList;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class GameModel {
	
	static final Vec2 GRAVITY = new Vec2(0.0f, -9.81f);


	ArrayList<DrawableBody> blockList = new ArrayList<DrawableBody>();
	Platform platform;

	
	World world;
	Body body;
	
	public GameModel() {
		world = new World(GRAVITY);
		platform = new Platform(world);		
	}
	
	public Platform getPlatform(){
		return platform;
	}
	

}