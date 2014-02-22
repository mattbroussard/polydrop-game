
import java.awt.*;
import javax.swing.*;

public class View extends JComponent {
	
	Model model;

	public View(Model m) {

		super();
		model = m;

	}

	public void paintComponent(Graphics g) {

		g.drawString("Hello World", 20, 20);

	}

}