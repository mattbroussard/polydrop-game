
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		//Print current build version
		System.out.printf("PolyDrop version %s starting up...\n", getVersion());

		//Parse any command-line arguments
		boolean fullScreen = true;
		boolean showFPS = false;
		for (String a : args) {
			if (a.equals("--windowed"))
				fullScreen = false;
			if (a.equals("--fps"))
				showFPS = true;
		}

		//Setup model
		GameModel model = new GameModel();

		//Setup game controller
		GameController gameController = new GameController(model);

		//Setup ViewManager
		ViewManager vm = new ViewManager(showFPS);

		//Setup views
		GameView gameView = new GameView(model, gameController);
		gameController.addGameView(gameView);
		vm.registerView(gameView, "game");
		PausedView pausedView = new PausedView(gameController, gameView);
		vm.registerView(pausedView, "paused");
		GameOverView gameOverView = new GameOverView(gameController, gameView);
		vm.registerView(gameOverView, "gameover");
		TutorialView tutorial = new TutorialView();
		vm.registerView(tutorial, "tutorial");
		AboutView about = new AboutView();
		vm.registerView(about, "about");
		Leaderboard leaderboard = new Leaderboard(gameController);
		vm.registerView(leaderboard, "leaderboard");
		gameController.addLeaderboard(leaderboard);
		SplashView splash = new SplashView();
		vm.registerView(splash, "splash");
		vm.pushView(splash);

		//Setup frame
		JFrame frame = new JFrame("PolyDrop");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container lay = frame.getContentPane();
		lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
		lay.setBackground(Colors.LETTERBOX);
		lay.add(Box.createVerticalGlue());
		lay.add(vm);
		lay.add(Box.createVerticalGlue());
		lay.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "null"));
		frame.addKeyListener(vm);

		//Setup full screen window
		if (fullScreen) {

			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);

		} else {
			
			frame.setSize(1280,824);
			frame.setLocation(100,100);

		}

		//Setup Leap controller
		LeapController leap = new LeapController(gameController, vm);

		//Make window visible
		frame.setVisible(true);

	}

	private static String version = null;
	public static String getVersion() {

		if (version == null) {

			try {
				InputStream is = Main.class.getResource("BUILD_VERSION").openStream();
				Scanner scan = new Scanner(is);
				version = scan.useDelimiter("\\A").next().trim();
				scan.close();
				is.close();
			} catch (Exception e) {
				if (version == null)
					version = "Unknown";
			}

		}

		return version;

	}

}
