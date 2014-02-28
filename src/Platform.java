
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.Color;

public class Platform implements DrawableBody {

	private Body body;
	private Fixture fixture;
	private Color color;
	
	public Platform(World world) {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.KINEMATIC;
		bdef.position.set(0,2);
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2, 0.25f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 2.0f;
		this.fixture = body.createFixture(fixtureDef);

		this.color = Colors.PLATFORM;
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
}
