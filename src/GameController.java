
public class GameController {
	
	GameModel model;

	public GameController(GameModel m) {

		model = m;

	}

	//stub
	public void updatePlatformPosition(double x, double y, double theta) {
		theta = theta * (180f / Math.PI);
		System.out.printf("x=%.3f, y=%.3f, theta=%.3f\n", x, y, theta);
	}
	public void pause() {}
	public void unpause() {}

}