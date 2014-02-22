
public interface Block extends Polygon {
	public Vector2 getCenterOfMass();
	public Vector2 addForce();
	public Vector2 getNetForce();
	public void rotate(double theta);
}