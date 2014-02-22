
import java.util.ArrayList;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class GameModel {
	
	static final Vec2 GRAVITY = new Vec2(0.0f, 9.81f);

//	ArrayList<Block> blockList = new ArrayList<Block>();
	Platform p;
//	double x = 50, y = 50, w = 50, h = 10;
	
	World world;
	Body body;
	
	public GameModel() {
		p = new Platform(50,50,50,10);
		
		world = new World(GRAVITY);

		//dummy body
		Body b = world.createBody(new BodyDef());

		// Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(0, 4);
		body = world.createBody(bodyDef);
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(1, 1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;
		fixtureDef.density=1;
		fixtureDef.friction=0.3f;
		body.createFixture(fixtureDef);
		
	}
	
	public Platform getPlatform(){
		return p;
	}
	

}