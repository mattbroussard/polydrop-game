
import javax.swing.JOptionPane;
import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Leaderboard {
	
	Preferences prefs = Preferences.userRoot();//What should the parameter be?
	
	
	GameController controller;
	String lastPlayerName = null;

	public Leaderboard(GameController c) {
		this.controller = c;
		//TODO Dallas: implement
	}

	public String promptForName(String defaultName) {
		defaultName = (defaultName == null) ? "" : "defaultName";
		controller.setUsingUI(true);
		String s = JOptionPane.showInputDialog("Congratulations on your high score! What is your name?", defaultName);
		lastPlayerName = defaultName;
		controller.setUsingUI(false);
		return s;
	}

	public void reportScore(int score) {
		for(int i = 0; i < 20; i++){
			System.out.println("REPORT SCORE");
		}

		//final String name = lastPlayerName == null ? promptForName("") : lastPlayerName;
		final String name = promptForName(lastPlayerName);
		//TODO Dallas: implement
		if(prefs.getInt(name, -1) == -1){//check to see if this person doesn't already has a score
			prefs.putInt(lastPlayerName, score);
		}else{
			if(prefs.getInt(name, -1) > score){//replace the players old high score
				prefs.remove(name);
				prefs.putInt(name, score);
			}
		}

	}

	public void clearLeaderboard() {
		//TODO Dallas: implement
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	public void draw(GraphicsWrapper g2) {
		
		int[] topFive = {0,0,0,0,0};
		String[] names = null;
		try {
			names = prefs.keys();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < names.length; i++){
			int score = prefs.getInt(names[i], 0);
			for(int j = 0; j < topFive.length; j++){
				if(score > topFive[j]){		
					topFive[j] = score;
				}
			}
		}
		

		//TODO Dallas: implement
		for(int i = 0; i < topFive.length; i ++){
			//g2.drawStringCentered("Placeholder!", 1, Color.white, 8, 5);
			g2.drawStringCentered(topFive[i]+"", 1, Color.white, 8, (float) (3+.85*i));

		}

	}
	

}