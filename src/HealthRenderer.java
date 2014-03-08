
import java.awt.Color;

public class HealthRenderer {
	
	public static void drawHealthBar(GraphicsWrapper g2, int health, boolean paused) {

		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		Color healthColor = Colors.HEALTH_GOOD;
		if (health < 60) healthColor = Colors.HEALTH_MID;
		if (health < 30) healthColor = Colors.HEALTH_BAD;
		if (paused) healthColor = Colors.HEALTH_PAUSED;

		float healthWidth = 2.0f;
		float healthHeight = 0.4f;
		float healthX = 12.0f;
		float healthY = 0.55f;

		g2.fillRect(healthX, healthY, healthWidth, healthHeight, Colors.HEALTH);
		g2.fillRect(healthX, healthY, healthWidth * health / 100f, healthHeight, healthColor);

	}

}