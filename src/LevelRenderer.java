
import java.awt.Color;

public class LevelRenderer {

	public static void drawLevelIndicator(GraphicsWrapper g2, int level, float progress, boolean paused) {

		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		Color levelFGColor = paused ? Colors.PAUSED : Colors.SHAPES[(level+1)%(Colors.SHAPES.length)];
		Color levelBGColor = paused ? Colors.BACKGROUND : Colors.PAUSED;
		float levelAngle = progress * 360f;

		g2.fillCircle(15f, 0.75f, 0.625f, levelBGColor);
		g2.fillArc(15f, 0.75f, 0.5f, 0.0f, 360.0f, levelFGColor);
		g2.fillCircle(15f, 0.75f, 0.5f, new Color(39,40,34,150));
		g2.fillArc(15f, 0.75f, 0.5f, 90.0f, -levelAngle, levelFGColor);
		g2.fillCircle(15f, 0.75f, 0.45f, levelBGColor);

		g2.drawStringCentered(""+level, 0.5f, levelFGColor, 15f, 0.9f);

	}

}