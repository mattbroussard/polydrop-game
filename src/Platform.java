import java.awt.Polygon;

public class Platform extends Polygon{
	
	public Platform(int x, int y, int w, int h){
		super();
		this.addPoint(x, y);
		this.addPoint(x+w, y);
		this.addPoint(x+w, y+h);
		this.addPoint(x, y+h);
	
	}
	
	public void moveLeft(double d){
		for(int i = 0; i < xpoints.length; i++)
			xpoints[i] -= d;

		//x -= d;
	}
	public void moveRight(double d){
		//x += d;
		moveLeft(d*-1);
		
	}
	public void tilt(double theta){
		
	}

}
