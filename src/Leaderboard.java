
import javax.swing.JOptionPane;
import java.awt.Color;
import java.util.*;

public class Leaderboard {
	
	static final int N_ENTRIES = 10;

	ArrayList<Entry> topList = new ArrayList<Entry>();
	
	GameController controller;
	GameView view;
	
	private boolean allowedHighScore = true;

	public Leaderboard(GameController c) {
		this.controller = c;
		topList = new ArrayList<Entry>();
		readPrefs();
	}
	
	public void setAllowedHighScore(boolean b){
		allowedHighScore = b;
	}
	
	public boolean isAllowedHighScore(){
		return allowedHighScore;
	}

	public String promptForName(int score, int place) {
		controller.setUsingUI(true);
		String msg = String.format("New high score of %d (placed #%d)!\nWhat is your name?", score, place);
		String s = (String)JOptionPane.showInputDialog(view, msg, "New High Score", JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("leaderboard"), null, "");
		controller.setUsingUI(false);
		return s;
	}

	public void writePrefs() {

		for (int i = 0; i < topList.size(); i++) {
			Entry e = topList.get(i);
			Prefs.putString("name_"+(i+1), e.name);
			Prefs.putInt("score_"+(i+1), e.score);
		}

		Prefs.writeOut();

	}

	public void readPrefs() {

		for (int i = 1; i <= N_ENTRIES; i++) {

			String name = Prefs.getString("name_"+i, null);
			int score = Prefs.getInt("score_"+i, Integer.MIN_VALUE);
			
			if (name == null || score == Integer.MIN_VALUE)
				break;

			Entry e = new Entry(name, score);
			topList.add(e);

		}

		//should already be sorted, but maybe someone tampered with the prefs file?
		Collections.sort(topList);

	}

	public void reportScore(int score) {
		
		System.out.printf("reportScore(%d) called.\n", score);

		if (!isAllowedHighScore()) {
			System.out.println("...but not allowed to be on leaderboard!");
			SoundManager.play("gameOver");
			return;
		}

		Entry entry = new Entry(null, score);

		int insertIndex = -Collections.binarySearch(topList, entry)-1;
		if (insertIndex < N_ENTRIES) {
			
			SoundManager.play("highScore");

			String name = promptForName(score, insertIndex+1);
			entry.name = name;

			topList.add(insertIndex, entry);
			while (topList.size() > N_ENTRIES)
				topList.remove(topList.size()-1);

			writePrefs();

		} else {
			SoundManager.play("gameOver");
		}

		System.out.println("end reportScore");

	}

	public void clearLeaderboard() {

		//prevent accidental clear
		controller.setUsingUI(true);
		int confirmation = JOptionPane.showConfirmDialog(view, "Are you sure you want to clear all high scores?", "Clear High Scores", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("clearLeaderboard"));
		controller.setUsingUI(false);
		if (confirmation != JOptionPane.OK_OPTION)
			return;

		Prefs.reset();
		Prefs.writeOut();
		topList = new ArrayList<Entry>();

	}

	public void draw(GraphicsWrapper g2) {
		
		if (topList.size()>0) {
			for (int i = 0; i < topList.size(); i++) {
				Entry e = topList.get(i);
				String msg = String.format("#%d: %s:  %d points", i+1, e.name, e.score);
				g2.drawStringCentered(msg, 0.5f, Colors.LEADERBOARD, 8, 1.8f + 0.6f*i);
			}
		} else {
			g2.drawStringCentered("No High Scores", 1, Colors.LEADERBOARD, 8, 5);
		}

	}
	private class Entry implements Comparable<Entry> {
		String name;
		int score;
		public Entry(String name, int score) {
			this.name = name;
			this.score = score;
		}
		public int compareTo(Entry other) {
			return other.score - this.score; //intentionally backwards comparator
		}
		public boolean equals(Object other) {
			return this == other;
		}
	}

}
