
import java.awt.Image;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import javax.swing.ImageIcon;

public class ImageManager {
	
	private static HashMap<String,BufferedImage> cache = new HashMap<String,BufferedImage>();

	private static void load(String name) throws Exception {

		BufferedImage b = ImageIO.read(ImageManager.class.getResource(name+".png"));
		cache.put(name, b);
		System.out.println("Loaded image resource "+name);

	}

	public static BufferedImage getImage(String name) {

		if (!cache.containsKey(name)) {
			try {
				load(name);
			} catch (Exception e) {
				System.out.println("Failed to load image resource "+name);
				return new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
			}
		}

		return cache.get(name);

	}

	public static ImageIcon getIcon(String name) {

		return new ImageIcon(ImageManager.getImage(name).getScaledInstance(64, 64, Image.SCALE_SMOOTH));

	}

}