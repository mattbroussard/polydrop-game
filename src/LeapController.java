
import com.leapmotion.leap.*;

public class LeapController extends Listener {
	
	GameController game;
	Controller leap;

	@Override
	public void onInit(Controller c) {

		System.out.println("onInit");

	}

	public LeapController(GameController gc) {

		game = gc;

		//setup Leap listener/controller
		leap = new Controller();
		leap.addListener(this);

	}

}