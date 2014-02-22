import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.Color;

public class Square implements DrawableBody {

	private Body body;
	private Fixture fixture;
	private Color color;
	
	public Square(World world) {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DYNAMIC;
		bdef.position.set(x,10);
		body = model.world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(.5f, .5f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;
		this.fixture = body.createFixture(fixtureDef);

		this.color = Color.red;
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
