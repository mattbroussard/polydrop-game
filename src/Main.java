
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Main {

	static final boolean FULL_SCREEN = true;

	public static void main(String[] args) {

		//Setup model
		GameModel model = new GameModel();

		//Setup controllers
		GameController game = new GameController(model);
		LeapController leap = new LeapController(game);

		//Mac thing for full screen mode, apparently has to be set before making the frame
		System.setProperty("apple.awt.fullscreenhidecursor", "true");

		//Setup view and frame
		GameView view = new GameView(model, game);
		game.addView(view);
		JFrame frame = new JFrame("Derp");
		frame.setContentPane(view);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(view);

		//Set cursor to invisible
		view.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "null"));

		//Setup full screen window
		if (FULL_SCREEN) {

			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);

			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

			try {
				gd.setFullScreenWindow(frame);
			} catch (Exception e) {
				//what should I do here?
			}

		} else {
			
			frame.setSize(800,500);
			frame.setLocation(100,100);

		}

		//Make window visible
		frame.setVisible(true);

	}

}
