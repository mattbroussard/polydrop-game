
import java.awt.*;
import javax.swing.*;

public class GameView extends JComponent {
	
	GameModel model;
	

	public GameView(GameModel m) {

		super();
		model = m;

	}

	public void paintComponent(Graphics g) {

		g.drawString("Hello World", 20, 20);
		
		g.setColor(Color.GRAY);
		g.fill3DRect((int)(model.x), (int)(model.y), (int)(model.w), (int)(model.h), true);
		
	}

}