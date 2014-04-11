
public class CheatManager {
	
	public static boolean limes = false;
	public static boolean failfast = false;
	public static boolean dropfast = false;
	public static boolean leaderboardForbidden = false;

	public static void applyCheat(String x) {

		if (x.equals("limes")) {
			limes = !limes;
		} else if (x.equals("failfast")) {
			failfast = !failfast;
		} else if (x.equals("skyfalling")) {
			dropfast = !dropfast;
			leaderboardForbidden = true;
		} else {
			return; //invalid cheat
		}

		SoundManager.play("cheat");

	}

}