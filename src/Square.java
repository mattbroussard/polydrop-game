import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.Color;

public class Square implements DrawableBody {

	private Body body;
	private Fixture fixture;
	private Color color;
	private long lifetime = 5; // seconds
	private long expirationTime;
	
	public Square(World world, float x, long timeCreated) {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DYNAMIC;
		bdef.position.set(x,10);
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(.40f, .40f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.9f;
		this.fixture = body.createFixture(fixtureDef);

		this.color = Colors.SHAPES[1];
		this.expirationTime = timeCreated + this.lifetime*1000;
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

	public int getValue() { return 1; }

	public long getExpiration() {
		return this.expirationTime;
	}
}
