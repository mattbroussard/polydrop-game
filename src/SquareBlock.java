
public class SquareBlock extends Block {
	
	private double upperLeft_x;
	private double upperLeft_y;
	private double sideLength;
	private Vector2 center;
	private double theta;

	private Vector2 centerOfMass;
	private Vector2 velocity;

	private Vector2 netForce;

	public SquareBlock(double upperLeft_x, double sideLength) {
		this.upperLeft_x = upperLeft_x;
		this.sideLength = sideLength;
		this.theta = 0.0;
		//this.center = new Vector2(up)
	}

	public Vector2 getCenterOfMass() {
		return new Vector2(0.0, 0.0);
	}
	public Vector2 addForce() {
		return new Vector2(0.0, 0.0);
	}
	/*
	public Vector2 getNetForce() {
		return new Vector2(0.0, 0.0);
	} */
	public void applyNetForce() {
		
	}

	@Override
	public Vector2 getNetForce() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rotate(double theta) {
		// TODO Auto-generated method stub
		
	}
}