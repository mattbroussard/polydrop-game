import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.util.HashMap;

public class SoundManager {
	private static HashMap<String, Clip> clips = new HashMap<String, Clip>();

	private static void load(String clipName) throws Exception {
		try {
			File file = new File(clipName + ".wav");
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			
			// specify what kind of line we want to create
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			// create the line
			Clip clip = (Clip)AudioSystem.getLine(info);
			// load the samples from the stream
			clip.open(stream);
			clips.put(clipName, clip);
			System.out.println("Loaded "+ clipName);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void play(String clipName) {
		if(!clips.containsKey(clipName)) {
			try {
				load(clipName);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		Clip clip = clips.get(clipName);
		clip.setFramePosition(0);
		clip.start();
	}

	public static void main(String[] args) {
		play("test");
		try { Thread.sleep(1000); } catch (Exception e) {}
		play("test");
		//play("coin_sound");
		//play("coin_sound");
		try { Thread.sleep(10000); } catch (Exception e) {}
	}
}
