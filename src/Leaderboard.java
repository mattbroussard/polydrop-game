
import javax.swing.JOptionPane;
import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.*;

public class Leaderboard {
	
	static final int N_ENTRIES = 10;

	Preferences prefs = Preferences.userRoot();
	ArrayList<Entry> topList = new ArrayList<Entry>();
	
	GameController controller;
	GameView view;

	public Leaderboard(GameController c) {
		this.controller = c;
		topList = new ArrayList<Entry>();
		readPrefs();
	}

	public String promptForName(int score, int place) {
		controller.setUsingUI(true);
		String msg = String.format("New high score of %d (placed #%d)!\nWhat is your name?", score, place);
		String s = JOptionPane.showInputDialog(view, msg, "");
		controller.setUsingUI(false);
		return s;
	}

	public void writePrefs() {

		for (int i = 0; i < topList.size(); i++) {
			Entry e = topList.get(i);
			prefs.put("name_"+(i+1), e.name);
			prefs.putInt("score_"+(i+1), e.score);
		}

	}

	public void readPrefs() {

		for (int i = 1; i <= N_ENTRIES; i++) {

			String name = prefs.get("name_"+i, null);
			int score = prefs.getInt("score_"+i, Integer.MIN_VALUE);
			
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

		int insertIndex = -Collections.binarySearch(topList, entry)-1;
		if (insertIndex < N_ENTRIES) {
			
			String name = promptForName(score, insertIndex+1);
			entry.name = name;

			topList.add(insertIndex, entry);
			while (topList.size() > N_ENTRIES)
				topList.remove(topList.size()-1);

		}

		writePrefs();
		System.out.println("end reportScore");

	}

	public void clearLeaderboard() {
		//TODO Dallas: implement
		try {
			prefs.clear();
			topList = new ArrayList<Entry>();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}

	public void draw(GraphicsWrapper g2) {
		
		if (topList.size()>0) {
			for (int i = 0; i < topList.size(); i++) {
				Entry e = topList.get(i);
				String msg = String.format("#%d: %s - %d points", i+1, e.name, e.score);
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