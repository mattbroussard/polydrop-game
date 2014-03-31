
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.Color;

public class Platform implements DrawableBody {

	public static final int FULL  = 0;
	public static final int LEFT  = 1;
	public static final int RIGHT = 2;
	
	private Body body;
	private Fixture fixture;
	private Color color;
	private BodyDef bdef;
	
	public Platform(World world, int type, Color debugColor) {
		bdef = new BodyDef();
		bdef.type = BodyType.KINEMATIC;
		if( type == FULL ) {
			// center
			bdef.position.set(0,2);
		} else if( type == LEFT ){
			bdef.position.set(-3,2);
		} else if( type == RIGHT ) {
			bdef.position.set(3, 2);
		}
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		if( type == FULL ) {
			shape.setAsBox(4, 0.25f);
		} else {
			shape.setAsBox(2, 0.25f);
		}
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 2.0f;
		this.fixture = body.createFixture(fixtureDef);

		this.color = debugColor;
	}

	public Platform(World w, int type) {
		this(w, type, Colors.PLATFORM);
	}

	public Color getColor() {
		return this.color;
	}

	public Body getBody() {
		return this.body;
	}

	public Fixture getFixture() {
		return this.fixture;
	}

	public int getValue() { return 0; }

	public void reduceLifetime(long dt) {}

	public long getExpiration() {
		return -1000;
	}

	public BodyDef getBodyDef() {
		return this.bdef;
	}

	public int getPenalty() {
		return 0;
	}

	public int getReward() {
		return 0;
	}
}
