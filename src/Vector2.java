
public class Vector2 {
	
	private double x;
	private double y;

	public Vector2(double x, double y) {
		this.setX(x);
		this.setY(y);
	}

	public Vector2 add(Vector2 v) {
		return new Vector2(this.getX() + v.getX(), this.getY() + v.getY());
	}

	public Vector2 dot(Vector2 v) {
		return new Vector2(this.getX()*v.getX(), this.getY()*v.getY());
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
}