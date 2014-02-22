
public class GameController {
	
	GameModel model;
	GameView view;
	boolean paused;

	public GameController(GameModel m) {
		model = m;

	}
	
	public void addView(GameView v) {
		view = v;
	}
	
	public synchronized void movePlatformRight(double d) {
		Platform p = model.getPlatform();
		p.moveRight(d);
		view.repaint();
	}
	
	public synchronized void movePlatformLeft(double d) {
		model.getPlatform().moveLeft(d);
		view.repaint();
	}
	
	public synchronized void pause(){
		paused = !paused;
	}

}