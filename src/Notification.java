
import java.awt.Color;

public class Notification {

	public final static int NOTIFICATION_TIME = 1250;

	float x;
	float y;
	long creationTime;
	String msg;
	float size;
	Color color;

	public Notification(float x, float y, long creationTime, String msg, float size, Color color) {
		this.x = x;
		this.y = y;
		this.creationTime = creationTime;
		this.msg = msg;
		this.size = size;
		this.color = color;
	}

	public float expirationProgress() {

		long now = System.currentTimeMillis();
		float progress = (float)(now-creationTime) / (float)NOTIFICATION_TIME;
		
		//this shouldn't happen, but let's not let it
		if (progress < 0.0f) progress = 0.0f;

		return progress;

	}

}