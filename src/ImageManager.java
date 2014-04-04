
import java.awt.Image;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.geom.*;

public class ImageManager {
	
	//set to 1.0 for non-Retina image instances
	public static final float REAL_DENSITY = 2.0f;

	private static HashMap<String,BufferedImage> cache = new HashMap<String,BufferedImage>();
	private static HashMap<String,Image> instanceCache = new HashMap<String,Image>();

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

	public static Image getImageInstance(String name, float xScale, float yScale, float rotation) {

		//check if an instance with given scale and rotation is already in the instanceCache
		String key = String.format("%s@xs=%.5f;ys=%.5f;rot=%.5f", name, xScale, yScale, rotation);
		if (instanceCache.containsKey(key))
			return instanceCache.get(key);

		BufferedImage raw = getImage(name);

		//draw the rotated image at full (raw file pixels) size
		BufferedImage step1 = new BufferedImage(raw.getWidth()*2, raw.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D s1g = (Graphics2D)step1.getGraphics();
		AffineTransform s1tf = new AffineTransform();
		s1tf.concatenate(AffineTransform.getTranslateInstance(raw.getWidth()*0.5f, raw.getHeight()*0.5f));
		s1tf.concatenate(AffineTransform.getRotateInstance(rotation / 180f * Math.PI, raw.getWidth()*0.5f, raw.getHeight()*0.5f));
		s1g.drawImage(raw, s1tf, null);
		s1g.dispose();

		//scale the rotated image to the correct resolution for the screen
		Image step2 = step1.getScaledInstance((int)Math.round(step1.getWidth()*xScale*REAL_DENSITY), (int)Math.round(step1.getHeight()*yScale*REAL_DENSITY), Image.SCALE_SMOOTH);

		//copy the image onto a "compatible image"
		//commented because it didn't seem to have any noticeable performance improvement
		/*
		Image step3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(step2.getWidth(null), step2.getHeight(null), Transparency.TRANSLUCENT);
		Graphics2D s3g = (Graphics2D)step3.getGraphics();
		s3g.drawImage(step2, new AffineTransform(), null);
		s3g.dispose();
		*/

		instanceCache.put(key, step2);
		return step2;

	}

}