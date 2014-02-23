
import java.awt.*;
import javax.swing.*;

public class Main {

	static final boolean FULL_SCREEN = true;

	public static void main(String[] args) {

		//Setup model
		GameModel model = new GameModel();

		//Setup controllers
		GameController game = new GameController(model);
		LeapController leap = new LeapController(game);

		//Setup view
		GameView view = new GameView(model, game);
		game.addView(view);
		JFrame frame = new JFrame("Derp");

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

		frame.setContentPane(view);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(view);
		frame.setVisible(true);

	}

}
