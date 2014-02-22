
public class Vector2 {
	
	private double x;
	private double y;

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 add(Vector2 v) {
		return new Vector2(this.x + v.x, this.y + v.y);
	}

	public Vector2 dot(Vector2 v) {
		return new Vector2(this.x*v.x, this.y*v.y);
	}
}