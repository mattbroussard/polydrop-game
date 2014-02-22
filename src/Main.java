
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
		GameView view = new GameView(model);
		JFrame frame = new JFrame("Derp");
		frame.setSize(640, 480);
		frame.setLocation(100, 100);
		frame.setContentPane(view);
		frame.setVisible(true);

	}

}
