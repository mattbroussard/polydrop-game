import java.awt.Polygon;
public abstract class Block extends Polygon {
	public abstract Vector2 getCenterOfMass();
	public abstract Vector2 addForce();
	public abstract Vector2 getNetForce();
	public abstract void rotate(double theta);
}