
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import java.awt.geom.*;

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
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void resetTrans(Graphics2D g2) {
		AffineTransform at = new AffineTransform();
		at.setToIdentity();
		g2.setTransform(at);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;

		g.drawString("Hello World", 20, 20);
		
		g.setColor(Color.GRAY);
		Body p = model.getPlatform();
		//g.fillPolygon(p);

		Body body = model.platform;
		resetTrans(g2);
		g2.translate(body.getWorldCenter().x, body.getWorldCenter().y);
		g2.rotate(body.getAngle());
		g2.drawRect(-10, -10, 20, 20);
		resetTrans(g2);
		
	}

}