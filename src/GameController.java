
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

	//stub
	public synchronized void updatePlatformPosition(double x, double y, double theta) {
		theta = theta * (180f / Math.PI);
		System.out.printf("x=%.3f, y=%.3f, theta=%.3f\n", x, y, theta);
	}

	public synchronized boolean collides(Block a, Block b) {
		for(int i = 0; i < a.npoints; i++)
			if(b.contains(a.xpoints[i], a.ypoints[i])){
				System.out.println("Collision!");
				return true;				
			}

		return false;
	}
	//public void pause() {}
	//public void unpause() {}

}