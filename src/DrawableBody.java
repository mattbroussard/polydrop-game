
import org.jbox2d.dynamics.*;
import java.awt.Color;

public interface DrawableBody {
	
	public Body getBody();
	public Fixture getFixture();
	public Color getColor();
	public int getValue();
	public long getExpiration();
	public void reduceLifetime(long dt);
	public int getPenalty();
	public int getReward();

}