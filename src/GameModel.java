
import java.util.ArrayList;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class GameModel {
	
	static final Vec2 GRAVITY = new Vec2(0.0f, -9.81f);

//	ArrayList<Block> blockList = new ArrayList<Block>();
	Body platform;
//	double x = 50, y = 50, w = 50, h = 10;
	
	World world;
	Body body;
	
	public GameModel() {
		world = new World(GRAVITY);
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.STATIC;
		bdef.position.set(4,1);
		platform = world.createBody(bdef);

		platform.setTransform(new Vec2(-4, 4), 0);
		
		PolygonShape platformShape = new PolygonShape();
		platformShape.setAsBox(4, 1);
		FixtureDef platformFixtureDef = new FixtureDef();
		platformFixtureDef.shape = platformShape;
		platformFixtureDef.density=1;
		platformFixtureDef.friction=0.3f;
		Fixture f = platform.createFixture(platformFixtureDef);
		platform.setUserData(f); //make this better
		

/*		// Dynamic Body
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
		body.createFixture(fixtureDef);*/
		
	}
	
	public Body getPlatform(){
		return platform;
	}
	

}