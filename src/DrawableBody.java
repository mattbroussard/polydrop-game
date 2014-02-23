
import org.jbox2d.dynamics.*;
import java.awt.Color;

public interface DrawableBody {
	
	public Body getBody();
	public Fixture getFixture();
	public Color getColor();
	public int getValue();
	public long getExpiration();

}