import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;
import java.awt.Color;

public class PolyBody implements DrawableBody {

	private Body body;
	private Fixture fixture;
	private Color color;
	private int sides;
	private long lifetime = 10*1000; // milliseconds
	private float size;
	private int reward;
	private int penalty;

	public PolyBody(World world, float x, int sides, int gameMode) {		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DYNAMIC;
		bdef.position.set(x,10);
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		Vec2[] verts = new Vec2[sides];
		float r = (float)(Math.random()*.3+.25);
		size = r;
		for (int i = 0; i < sides; i++) {
			double theta = 2 * Math.PI / sides * i;
			
			//float r = 0.50f;
			verts[i] = new Vec2((float)Math.cos(theta) * r, (float)Math.sin(theta) * r);
		}
		shape.set(verts, sides);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.9f;
		this.fixture = body.createFixture(fixtureDef);

		this.color = Colors.SHAPES[sides-3];
		this.sides = sides;
		
		this.reward = (gameMode == GameModel.FREE_PLAY) ? 0 : 10 * sides;
		this.penalty = (gameMode == GameModel.FREE_PLAY) ? 0 : -20;
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

	public int getValue() {
		return sides*10;		
	}
	
	public float getSize(){
		return size;
	}
	
	public void reduceLifetime(long dt) {
		this.lifetime -= dt;
	}

	public long getExpiration() {
		return this.lifetime;
	}

	public int getNumSides() {
		return this.sides;
	}
	public int getReward(){
		return reward;
	}
	public int getPenalty(){
		return penalty;
	}

}