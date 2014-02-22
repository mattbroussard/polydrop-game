
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
		bdef.type = BodyType.STATIC;
		bdef.position.set(0,1);
		body = world.createBody(bdef);

		body.setTransform(new Vec2(-4, 4), 0);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(4, 1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density=1;
		fixtureDef.friction=0.3f;
		Fixture f = body.createFixture(fixtureDef);
		body.setUserData(f); //make this better

		this.color = Color.green;
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
}
