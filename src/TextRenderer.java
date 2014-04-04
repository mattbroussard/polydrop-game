
import java.awt.Color;

public class TextRenderer {
	
	public final static float NOTIFICATION_DISTANCE = 2f;

	public static void drawScore(GraphicsWrapper g2, int score) {

		g2.prepare();

		g2.drawString(
			"Score: "+score,
			0.7f,
			Colors.SCORE,
			0.5f,
			1.0f
		);

	}

	public static void drawNotification(GraphicsWrapper g2, Notification n) {

		g2.prepare();

		float progress = n.expirationProgress();
		float dy = progress * NOTIFICATION_DISTANCE;
		Color color = Colors.interpolateColor(n.color, Colors.BACKGROUND, progress);

		g2.drawStringCentered(n.msg, n.size, color, n.x + 8.0f, 10.0f - n.y - dy);

	}

	public static void drawGameOver(GraphicsWrapper g2) {

		g2.prepare();

		g2.drawStringCentered(
			"GAME OVER",
			2.0f,
			Colors.HEALTH_BAD,
			8,
			5
		);

		/* g2.drawStringCentered(
			"Press [space] to play again",
			0.3f,
			Colors.PAUSED_TEXT,
			8,
			7.5f
		); */

	}

	public static void drawPaused(GraphicsWrapper g2) {

		g2.prepare();

		g2.drawStringCentered(
			"PAUSED",
			1.0f,
			Colors.PAUSED_TEXT,
			8.0f,
			1.125f
		);

	}

}