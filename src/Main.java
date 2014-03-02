
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

		//Setup frame
		JFrame frame = new JFrame("PolyDrop");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Setup view and frame's layout
		GameView view = new GameView(model, game);
		Container lay = frame.getContentPane();
		lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
		lay.setBackground(Colors.LETTERBOX);
		lay.add(Box.createVerticalGlue());
		lay.add(view);
		lay.add(Box.createVerticalGlue());
		frame.addKeyListener(view);
		game.addView(view);

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
