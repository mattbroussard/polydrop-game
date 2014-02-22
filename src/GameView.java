
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class GameView extends JComponent implements KeyListener{
	
	GameModel model;
	GameController controller;
	boolean pressedRight;
	boolean pressedPause;

	public GameView(GameModel m, GameController c) {

		super();
		model = m;
		controller = c;
	}

	

	@Override
	public void keyPressed(KeyEvent e) {		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT && !pressedRight){
			pressedRight = true;
			controller.movePlatformRight(10);
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE && !pressedPause){
			controller.pause();
			pressedPause = true;
		}
			
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) pressedRight = false;
		if(e.getKeyCode() == KeyEvent.VK_SPACE) pressedPause = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawString("Hello World", 20, 20);
		
		g.setColor(Color.GRAY);
		Platform p = model.getPlatform();
		g.fillPolygon(p);
		
	}

}