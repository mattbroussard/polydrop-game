
public class CheatManager {
	
	public static boolean limes = false;
	public static boolean failfast = false;

	public static void applyCheat(String x) {

		if (x.equals("limes")) {
			limes = !limes;
		} else if (x.equals("failfast")) {
			failfast = !failfast;
		} else {
			return; //invalid cheat
		}

		SoundManager.play("cheat");

	}

}