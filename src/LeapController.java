
import org.jbox2d.common.Vec2;

import com.leapmotion.leap.*;

//TODO Matt (or someone else?): Further refactoring on this, it's gotten to be quite a mess and longs for the simple days of February 21

public class LeapController extends Listener implements Runnable {
	
	GameController game;
	GameView view;
	Controller leap;

	Thread t;

	static final double SPACE_WIDTH = 500f;
	static final double SPACE_HEIGHT = 500f;
	static final double FIST_THRESHOLD = Math.PI / 4f;
	static final double THUMB_YAW_THRESHOLD = 65f / 180f * Math.PI;
	static final double THUMB_LENGTH_THRESHOLD = 30;
	static final double Z_PAUSE_THRESHOLD = 175f;
	
	double handResumeX = 0, handResumeY = 0;
	double handPauseX = 0, handPauseY = 0;
	
	Vector pauseLocation = new Vector(0.0f,0.0f,0.0f);
	Vector resumeLocation = new Vector(0.0f,0.0f,0.0f);
	
	double dx = 0, dy = 0;
	
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

		game.pause(false);

	}

	int lastHand = -1;
	public void processFrameForPlatform(Frame frame) {

		//HandList hands = frame.hands();
		Hand rightHand = getPreferredHand(frame, HAND_RIGHT);
		Hand leftHand = getPreferredHand(frame, HAND_LEFT);
		Hand primaryHand = getPrimaryHand(leftHand, rightHand);
		
		//System.out.printf("processFrameForPlatform: %d hands, left=%d, right=%d, primary=%d\n", nHands, leftHand!=null?leftHand.id():-1, rightHand!=null?rightHand.id():-1, primaryHand!=null?primaryHand.id():-1);

		long now = System.currentTimeMillis();
		long dt = (lastUpdate < 0) ? 0 : now-lastUpdate;

		if (primaryHand == null) {
			game.pause(true);
			return;
		} else {
			game.unpause(true);
		}

		int mode = game.getGameMode();
		switch (mode) {

			case GameController.FREE_PLAY:
			case GameController.ONE_HAND:

				Vector handPos = primaryHand.palmPosition();
				double handX = normalize(handPos.getX(), -SPACE_WIDTH/2.0f, SPACE_WIDTH/2.0f); 
				double handY = normalize(handPos.getY(), 0f, SPACE_HEIGHT);
				double handRoll = primaryHand.palmNormal().roll();

				game.updatePlatformPosition(handX, handY, handRoll, dt);
				
				break;

			case GameController.TWO_HANDS:
				
				Vector leftHandPos = leftHand == null ? null : leftHand.palmPosition();
				double leftHandX = leftHand == null ? -1 : normalize(leftHandPos.getX(), -SPACE_WIDTH/2.0f, SPACE_WIDTH/2.0f); 
				double leftHandY = leftHand == null ? -1 : normalize(leftHandPos.getY(), 0f, SPACE_HEIGHT);
				double leftHandRoll = leftHand == null ? -1 : leftHand.palmNormal().roll();

				Vector rightHandPos = rightHand == null ? null : rightHand.palmPosition();
				double rightHandX = rightHand == null ? -1 : normalize(rightHandPos.getX(), -SPACE_WIDTH/2.0f, SPACE_WIDTH/2.0f); 
				double rightHandY = rightHand == null ? -1 : normalize(rightHandPos.getY(), 0f, SPACE_HEIGHT);
				double rightHandRoll = rightHand == null ? -1 : rightHand.palmNormal().roll();

				game.updatePlatformPosition(rightHandX, rightHandY, rightHandRoll, leftHandX, leftHandY, leftHandRoll, dt);

				break;
			
			default:
				return; 
		
		}

		lastUpdate = now;

	}

	public boolean handUnusable(Hand h) {

		//reject hands that are too far back
		if (h.palmPosition().getZ() > Z_PAUSE_THRESHOLD) {
			////System.out.printf("Hand id=%d unusable! Failed Z threshold test.\n", h.id());
			return true;
		}
		
		//reject hands with 2 or fewer fingers (fists)
		if (h.fingers().count() <= 2) {
			////System.out.printf("Hand id=%d unusable! Failed nFingers test.\n", h.id());
			return true;
		}
		
		//reject hands that are too slanted (they're likely to be fists)
		if (Math.abs(h.palmNormal().roll()) > FIST_THRESHOLD) {
			////System.out.printf("Hand id=%d unusable! Failed fist slant test.\n", h.id());
			return true;
		}

		return false;

	}

	//keep track of the most recently tracked hands and always track them.
	//if we lose one or both, pick new ones using a simple heuristic.
	static final int HAND_LEFT = 0;
	static final int HAND_RIGHT = 1;
	int[] preferredHandIDs = { -1, -1 };
	public Hand getPreferredHand(Frame frame, int which) {
		
		if (which != 0 && which != 1)
			return null;

		Hand pref = frame.hand(preferredHandIDs[which]);
		if (pref.isValid() && !handUnusable(pref))
			return pref;
		preferredHandIDs[which] = -1;

		Hand best = null;
		for (Hand h : frame.hands()) {
			
			//if hand is designated unusable (fist, etc.) eliminate from consideration immediately
			if (handUnusable(h)) {
				continue;
			}

			//don't allow right and left to be the same hand
			if (h.id() == preferredHandIDs[1-which]) {
				////System.out.printf("Hand id=%d failed uniqueness test for hand %d.\n", h.id(), which);
				continue;
			}

			//prefer leftmost hand if which=0
			if (which == 0 && best != null && best.palmPosition().getX() < h.palmPosition().getX()) {
				////System.out.printf("Hand id=%d failed leftmost hand test.\n", h.id());
				continue;
			}
			
			//prefer rightmost hand if which=1
			if (which == 1 && best != null && best.palmPosition().getX() > h.palmPosition().getX()) {
				////System.out.printf("Hand id=%d failed rightmost hand test.\n", h.id());
				continue;
			}
			
			//prefer frontmost hand
			if (best != null && best.palmPosition().getZ() < h.palmPosition().getZ()) {
				////System.out.printf("Hand id=%d failed frontmost hand test.\n", h.id());
				continue;
			}

			best = h;

		}

		if (best==null)
			return null;

		preferredHandIDs[which] = best.id();
		return best;

	}

	//pick one preferred hand; prefer right (sorry lefties) if both are seen
	public Hand getPrimaryHand(Hand left, Hand right) {

		return right != null ? right : left;

	}

	//keep track of the most recently tracked pointable and always track that one.
	//if we lose it, pick a new one using a simple heuristic
	int preferredPointableID = -1;
	public Pointable getPreferredPointable(Frame frame) {

		Pointable pref = frame.pointable(preferredPointableID);
		if (pref.isValid())
			return pref;
		preferredPointableID = -1;

		Pointable best = null;
		for (Pointable p : frame.pointables()) {

			//don't take non-finger pointables
			if (!p.isFinger()) {
				////System.out.printf("Pointable id=%d failed finger/tool test.\n", p.id());
				continue;
			}
			
			//don't take pointables in ZONE_NONE
			if (p.touchZone() == Pointable.Zone.ZONE_NONE) {
				//System.out.printf("Pointable id=%d failed ZONE_NONE test.\n", p.id());
				//continue;
			}
			
			//don't take thumbs if we can avoid it -- you can see them even when making a fist.
			if (Math.abs(p.stabilizedTipPosition().yaw()) > THUMB_YAW_THRESHOLD || p.length() < THUMB_LENGTH_THRESHOLD) {
				////System.out.printf("Pointable id=%d failed thumb yaw/length test.\n", p.id());
				continue;
			}
			
			//if there are multiple hands, take the pointable on the hand closest to the x center
			if (best != null && Math.abs(best.hand().palmPosition().getX()) < Math.abs(p.hand().palmPosition().getX())) {
				////System.out.printf("Pointable id=%d failed hand x-centrality test test.\n", p.id());
				continue;
			}
			
			//take the forward-most pointing pointable
			if (best != null && Math.abs(best.stabilizedTipPosition().yaw()) < Math.abs(p.stabilizedTipPosition().yaw())) {
				System.out.printf("Pointable id=%d failed forward-most pointing test.\n", p.id());
				continue;
			}
			
			//take the longest pointable
			if (best != null && best.length() > p.length()) {
				////System.out.printf("Pointable id=%d failed length test.\n", p.id());
				continue;
			}

			//take the pointable we've seen the longest
			if (best != null && best.timeVisible() > p.timeVisible()) {
				////System.out.printf("Pointable id=%d failed time visible test.\n", p.id());
				continue;
			}

			best = p;

		}

		if (best==null)
			return null;

		preferredPointableID = best.id();
		return best;

	}

	public void processFrameForMenu(Frame frame) {

		Pointable pointer = getPreferredPointable(frame);
		if (pointer==null) return;

		Vector pos = pointer.stabilizedTipPosition();
		//System.out.printf("have preferred pointable id=%d with yaw=%.2fdeg, pitch=%.2fdeg\n", pointer.id(), pos.yaw()/Math.PI*180f, pos.pitch()/Math.PI*180f);

		//use a slightly narrower window
		double x = normalize(pos.getX(), -SPACE_WIDTH/3.0f, SPACE_WIDTH/3.0f); 
		double y = normalize(pos.getY(), 50f, SPACE_HEIGHT*0.75f);

		//if we ran off the edge, don't consider this pointable
		if (x == 0f || y == 0f || x == 1f || y == 1f) {
			////System.out.printf("rejecting preferred pointable id=%d because over/underflow\n", pointer.id());
			return;
		}

		view.pointerUpdate(x, y);

	}

	public void processFrame(Frame frame) {

		processFrameForPlatform(frame);
		processFrameForMenu(frame);

	}

	public void run() {

		while (true) {

			processFrame(leap.frame());
			try { Thread.sleep(50); } catch (Exception e) {}

		}

	}

	//The LeapController is used to:
	// - actuate change in the position of the platform(s) on the model (via the GameController)
	// - pause and unpause the game (via the GameController)
	// - drive UI interactions to change game modes (via the GameView directly)
	public LeapController(GameController game, GameView view) {

		this.game = game;
		this.view = view;

		//setup Leap listener/controller
		leap = new Controller();
		leap.addListener(this);
		
		t = new Thread(this);
		t.start();

	}

}