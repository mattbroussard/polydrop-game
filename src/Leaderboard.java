
import javax.swing.JOptionPane;
import java.awt.Color;
import java.util.*;

public class Leaderboard extends View implements RadialMenuListener {
	
	static final int N_ENTRIES = 10;


	ArrayList<Entry> topList = new ArrayList<Entry>();
	
	GameController controller;
	
	RadialMenu menu;
	static final int LEADERBOARD_MENU_CLEAR = 1;
	static final int LEADERBOARD_MENU_EXIT = 2;

	public Leaderboard(GameController c) {

		this.controller = c;
		topList = new ArrayList<Entry>();
		readPrefs();

		menu = new RadialMenu(8, 11.5f, this);
		menu.addItem(new RadialMenuItem(LEADERBOARD_MENU_EXIT, "Back", "menuReturn", 90, 20, RadialMenuItem.ORIENT_TOP));
		menu.addItem(new RadialMenuItem(LEADERBOARD_MENU_CLEAR, "Reset Scores", "clearLeaderboard", 70, 20, RadialMenuItem.ORIENT_TOP));

	}

	public String promptForName(int score, int place) {
		String msg = String.format("New high score of %d (placed #%d)!\nWhat is your name?", score, place);
		String s = (String)JOptionPane.showInputDialog(getViewManager(), msg, "New High Score", JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("leaderboard"), null, "");
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

		Entry entry = new Entry(null, score);

		int binSearch = Collections.binarySearch(topList, entry);
		int insertIndex = binSearch < 0 ? -binSearch-1 : binSearch+1; //binarySearch can say it found the entry if score comparator returns 0
		if (insertIndex < N_ENTRIES && !CheatManager.leaderboardForbidden) {
			
			SoundManager.play("highScore");

			String name = promptForName(score, insertIndex+1);
			if (name==null)
				return;
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
		int confirmation = JOptionPane.showConfirmDialog(getViewManager(), "Are you sure you want to clear all high scores?", "Clear High Scores", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, ImageManager.getIcon("clearLeaderboard"));
		if (confirmation != JOptionPane.OK_OPTION)
			return;

		Prefs.reset();
		Prefs.writeOut();
		topList = new ArrayList<Entry>();

	}

	public void draw(GraphicsWrapper g2, boolean active) {
		
		if (!active) return;

		if (topList.size()>0) {
			for (int i = 0; i < topList.size(); i++) {
				Entry e = topList.get(i);
				String msg = String.format("#%d: %s:  %d points", i+1, e.name, e.score);
				g2.drawStringCentered(msg, 0.5f, Colors.LEADERBOARD, 8, 1.8f + 0.6f*i);
			}
		} else {
			g2.drawStringCentered("No High Scores", 1, Colors.LEADERBOARD, 8, 5);
		}

		menu.draw(g2);

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		menu.pointerUpdate(cursorX, cursorY);
	}

	public void onMenuSelection(int id) {

		switch (id) {
			case LEADERBOARD_MENU_CLEAR:
				clearLeaderboard();
				break;

			case LEADERBOARD_MENU_EXIT:
				getViewManager().popView();
				break;

			default:
				return;
		}

		SoundManager.play("menuChoice");

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
