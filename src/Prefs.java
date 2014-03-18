
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Prefs {
	
	private static Properties store;

	public static String getString(String key, String defaultValue) {
		if (store==null) init();

		return store.getProperty(key, defaultValue);

	}

	public static int getInt(String key, int defaultValue) {
		if (store==null) init();

		try {
			return Integer.parseInt(store.getProperty(key, Integer.toString(defaultValue)));
		} catch (Exception e) {
			return defaultValue;
		}

	}

	public static void putString(String key, String val) {
		if (store==null) init();

		store.setProperty(key, val);

	}

	public static void putInt(String key, int val) {
		if (store==null) init();

		store.setProperty(key, Integer.toString(val));

	}

	public static void reset() {

		store = new Properties();

	}

	public static void writeOut() {
		if (store==null) init();

		File f = getFile();

		//if running from Airspace the folders should exist. But they might not on our dev machines, or if we allow non-Airspace distribution in the future.
		new File(f.getParent()).mkdirs();

		try {
			FileOutputStream fos = new FileOutputStream(f);
			store.store(fos, "PolyDrop settings file... do not tamper with this!");
			fos.close();
		} catch (Exception e) {}

	}

	private static void init() {
		if (store!=null) return;

		File f = getFile();

		store = new Properties();

		if (f.exists()) {
			try {
				FileInputStream fis = new FileInputStream(f);
				store.load(fis);
				fis.close();
			} catch (Exception e) {}
		}

	}

	private static File getFile() {

		String os = System.getProperty("os.name", "generic").toLowerCase();
		String sep = System.getProperty("file.separator", "/");

		String folder = null;
		if (os.indexOf("mac") >= 0) {
			
			//Required prefs path for Mac: ~/Library/Application Support/AirspaceApps/PolyDrop
			folder = System.getProperty("user.home", "~") + sep + "Library" + sep + "Application Support" + sep + "AirspaceApps" + sep + "PolyDrop";

		} else if (os.indexOf("win") >= 0) {

			//Required prefs path for Windows: %AppData%\AirspaceApps\PolyDrop\prefs.txt
			folder = System.getenv("APPDATA") + sep + "AirspaceApps" + sep + "PolyDrop";

		} else {

			//We're on some unknown platform, store the preferences in the home folder
			folder = System.getProperty("user.home", "~");
		
		}

		return new File(folder + sep + "PolyDrop_prefs.txt");

	}

}