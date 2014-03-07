
import javax.swing.JOptionPane;
import java.awt.Color;

public class Leaderboard {
	
	GameController controller;
	String lastPlayerName = null;

	public Leaderboard(GameController c) {
		this.controller = c;
		//TODO Dallas: implement
	}

	public String promptForName(String defaultName) {
		controller.setUsingUI(true);
		String s = JOptionPane.showInputDialog("Congratulations on your high score! What is your name?", defaultName);
		controller.setUsingUI(false);
		return s;
	}

	public void reportScore(int score) {

		String name = lastPlayerName == null ? promptForName("") : lastPlayerName;
		//TODO Dallas: implement

	}

	public void clearLeaderboard() {

		//TODO Dallas: implement

	}

	public void draw(GraphicsWrapper g2) {

		//TODO Dallas: implement
		g2.drawStringCentered("Placeholder!", 1, Color.white, 8, 5);

	}

}