
import java.awt.*;
import javax.swing.*;

public class Main {

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
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setContentPane(view);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(view);
		frame.setVisible(true);

	}

}
