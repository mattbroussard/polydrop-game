import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.Color;

public class PolyBody implements DrawableBody {

	private Body body;
	private Fixture fixture;
	private Color color;
	private int sides;
	
	public PolyBody(World world, float x, int sides, Color c) {
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DYNAMIC;
		bdef.position.set(x,10);
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		Vec2[] verts = new Vec2[sides];
		for (int i = 0; i < sides; i++) {
			double theta = 2 * Math.PI / sides * i;
			float r = 0.25f;
			verts[i] = new Vec2((float)Math.cos(theta) * r, (float)Math.sin(theta) * r);
		}
		shape.set(verts, sides);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		this.fixture = body.createFixture(fixtureDef);

		this.color = c;
		this.sides = sides;

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

	public int getValue() { return sides; }
}