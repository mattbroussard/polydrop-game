
import java.awt.*;
import javax.swing.*;

public class Main {

	public static void main(String[] args) {

		//Setup model
		Model model = new Model();

		//Setup controller

		//Setup view
		View view = new View(model);
		JFrame frame = new JFrame("Derp");
		frame.setSize(640, 480);
		frame.setLocation(100, 100);
		frame.setContentPane(view);
		frame.setVisible(true);

	}

}
