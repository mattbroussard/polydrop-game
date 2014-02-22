import java.awt.Polygon;

public class Platform extends Block{
	
	public Platform(int x, int y, int w, int h){
		super();
		this.addPoint(x  , y  );
		this.addPoint(x+w, y  );
		this.addPoint(x+w, y+h);
		this.addPoint(x  , y+h);
	
	}
	
	public void moveLeft(double d){
		for(int i = 0; i < xpoints.length; i++)
			xpoints[i] -= d;
	}
	public void moveRight(double d){
		moveLeft(d*-1);		
	}
	public void tilt(double theta){
		
	}

	public Vector2 getCenterOfMass() {
		return new Vector2(0.0, 0.0);
	}
	public Vector2 addForce() {
		return new Vector2(0.0, 0.0);
	}
	public Vector2 getNetForce() {
		return new Vector2(0.0, 0.0);
	}
	public void applyNetForce() {
		// do this
	}

	@Override
	public void rotate(double theta) {
		// TODO Auto-generated method stub
		
	}
}
