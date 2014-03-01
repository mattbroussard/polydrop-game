
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Main {

	public static void main(String[] args) {

		//Parse any command-line arguments
		boolean fullScreen = true;
		for (String a : args) {
			if (a.equals("--windowed"))
				fullScreen = false;
			//can add more options here
		}

		//Setup model
		GameModel model = new GameModel();

		//Setup controllers
		GameController game = new GameController(model);
		LeapController leap = new LeapController(game);

		//Setup view and frame
		GameView view = new GameView(model, game);
		game.addView(view);
		JFrame frame = new JFrame("PolyDrop");
		frame.setContentPane(view);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(view);

		//Set cursor to invisible
		view.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "null"));

		//Setup full screen window
		if (fullScreen) {

			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);

		} else {
			
			frame.setSize(800,600);
			frame.setLocation(100,100);

		}

		//Make window visible
		frame.setVisible(true);

	}

}
