
import org.jbox2d.common.Vec2;

import com.leapmotion.leap.*;

public class LeapController extends Listener implements Runnable {
	
	GameController game;
	Controller leap;

	Thread t;

	static final double SPACE_WIDTH = 500f;
	static final double SPACE_HEIGHT = 500f;
	static final double FIST_THRESHOLD = Math.PI / 4f;
	
	private long lastUpdate;

	//normalizes a number n between a and b to be between 0 and 1. Clips if necessary.
	private double normalize(double n, double a, double b) {

		//avoid div-by-0
		assert a != b;

		//clip out-of-bounds numbers
		if (n >= b) return 1f;
		if (n <= a) return 0f;

		return (n - a) / (b - a);

	}

	@Override
	public void onFocusLost(Controller c) {

		game.pause();

	}

	int lastHand = -1;
	public void processFrame(Frame frame) {

		HandList hands = frame.hands();
		
		if (hands.count()==0) {
			game.pause();
			return;
		}

		Hand hand = hands.frontmost();
		if (hand.id() != lastHand) {
			for (Hand h : hands) {
				if (h.id() == lastHand) {
					hand = h;
					break;
				}
			}
		}
		lastHand = hand.id();

		Vector handPos = hand.palmPosition();
		double handX = normalize(handPos.getX(), -SPACE_WIDTH/2.0f, SPACE_WIDTH/2.0f);
		double handY = normalize(handPos.getY(), 0f, SPACE_HEIGHT);

		Vector handNorm = hand.palmNormal();
		double handRoll = handNorm.roll();

		if (hand.fingers().count() <= 1 && Math.abs(handRoll) < FIST_THRESHOLD) {
			game.pause(handX, handY);
		} else {
			game.unpause(handX, handY);
		}

		long now = System.currentTimeMillis();
		long dt = (lastUpdate < 0) ? 0 : now-lastUpdate;
		game.updatePlatformPosition(handX, handY, handRoll, dt);
		//game.model.platform.getBody().setLinearVelocity(new Vec2(0.0f, 0.0f));
		lastUpdate = now;

	}

	public void run() {

		while (true) {

			processFrame(leap.frame());
			try { Thread.sleep(50); } catch (Exception e) {}

		}

	}

	public LeapController(GameController gc) {

		game = gc;

		//setup Leap listener/controller
		leap = new Controller();
		leap.addListener(this);
		
		t = new Thread(this);
		t.start();

	}

}