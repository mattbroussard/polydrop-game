
import java.awt.Color;

public class LeapWarningView extends View {
	
	public void draw(GraphicsWrapper g2, boolean active) {
		if (!active) return;
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		g2.fillRect(0, 0, 16, 10, Colors.LEAP_WARNING_OVERLAY);

		g2.drawStringCentered("The Leap Motion Controller is disconnected or", 0.5f, Colors.LEAP_WARNING_TEXT, 8f, 7f);
		g2.drawStringCentered("the Leap Motion Controller service is not running.", 0.5f, Colors.LEAP_WARNING_TEXT, 8f, 7.6f);
		g2.drawStringCentered("Please correct this before continuing.", 0.5f, Colors.LEAP_WARNING_TEXT, 8f, 8.2f);

		g2.drawImage("leapWarn", 8f, 4f);

	}

}