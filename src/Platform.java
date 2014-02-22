
public class Platform implements DrawableBody {

	private Body body;
	private Color color;
	
	public Platform(int x, int y, int w, int h) {
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

	public getFixture() {
		return this.body.getUserData();
	}
}
