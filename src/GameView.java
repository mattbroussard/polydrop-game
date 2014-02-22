
import java.awt.*;
import javax.swing.*;

public class GameView extends JComponent {
	
	GameModel model;
	
	int x, y, w, h;

	public GameView(GameModel m) {

		super();
		model = m;

	}

	public void paintComponent(Graphics g) {

		g.drawString("Hello World", 20, 20);
		
		g.setColor(Color.GRAY);
		g.fill3DRect(x, y, w, h, true);
		
	}

}