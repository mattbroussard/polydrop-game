import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;
import java.util.HashMap;

public class SoundManager {
	
	private static boolean muted = false;
	
	private static HashMap<String, Clip> clips = new HashMap<String, Clip>();

	private static void load(String clipName) throws Exception {
		URL url = SoundManager.class.getResource(clipName+".wav");
		AudioInputStream stream = AudioSystem.getAudioInputStream(url);
		AudioFormat format = stream.getFormat();
		
		// specify what kind of line we want to create
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		// create the line
		Clip clip = (Clip)AudioSystem.getLine(info);
		// load the samples from the stream
		clip.open(stream);
		clips.put(clipName, clip);
		System.out.println("Loaded audio resource "+ clipName);
	}

	public static void play(String clipName) {
		if(muted){
			return;
		}
		if(!clips.containsKey(clipName)) {
			try {
				load(clipName);
			} catch (Exception e) {
				System.out.println("Couldn't load audio resource "+ clipName);
				return;
			}
		}
		Clip clip = clips.get(clipName);
		clip.setFramePosition(0);
		clip.start();
	}
	
	public static void mute(){
		muted = !muted;
	}
	public static boolean isMute(){
		return muted;
	}

}
