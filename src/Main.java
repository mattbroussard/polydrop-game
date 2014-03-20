
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
		GameController game = new GameController(model);

		//Setup frame
		JFrame frame = new JFrame("PolyDrop");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Setup view and frame's layout
		GameView view = new GameView(model, game);
		view.showFPS = showFPS;
		Container lay = frame.getContentPane();
		lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
		lay.setBackground(Colors.LETTERBOX);
		lay.add(Box.createVerticalGlue());
		lay.add(view);
		lay.add(Box.createVerticalGlue());
		lay.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "null"));
		frame.addKeyListener(view);
		game.addView(view);

		//Setup full screen window
		if (fullScreen) {

			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);

		} else {
			
			frame.setSize(1280,824);
			frame.setLocation(100,100);

		}

		//Setup Leap controller
		LeapController leap = new LeapController(game, view);

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
