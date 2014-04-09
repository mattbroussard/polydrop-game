
import java.awt.Color;
import java.awt.geom.*;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.net.URL;

public class AboutView extends PaginatedView implements HitTestListener {

	HitTester[] hitTesters;
	HitTester currentHitTester = null;

	private final static int ABOUT_LINK_POLYDROP = 0;
	private final static int ABOUT_LINK_JAVA = 1;
	private final static int ABOUT_LINK_LEAP = 2;
	private final static int ABOUT_LINK_JBOX2D = 3;
	private final static int ABOUT_LINK_FONTAWESOME = 4;
	private final static int ABOUT_LINK_LAUNCH4J = 5;
	private final static int ABOUT_LINK_APPBUNDLER = 6;
	private final static int ABOUT_LINK_GPL = 7;

	private final static int HIT_TEST_DELAY = 2000;

	public AboutView() {
		super();

		final float ox = 2.5f;
		final float oy = 1.5f;

		//setup hit test for page 1
		HitTester p1 = new HitTester(HIT_TEST_DELAY, this);
		p1.addRegion(ABOUT_LINK_POLYDROP, new Rectangle2D.Float(ox+3.178f, oy+5.597f, 4.7f, 0.49f));

		//setup hit test for page 2
		HitTester p2 = new HitTester(HIT_TEST_DELAY, this);
		p2.addRegion(ABOUT_LINK_JAVA, new Rectangle2D.Float(ox+0.7f, oy+3.4f, 2.11f, 0.4f));
		p2.addRegion(ABOUT_LINK_LEAP, new Rectangle2D.Float(ox+0.7f, oy+4.445f, 3.0f, 0.4f));
		p2.addRegion(ABOUT_LINK_JBOX2D, new Rectangle2D.Float(ox+0.7f, oy+5.5f, 2.256f, 0.4f));
		p2.addRegion(ABOUT_LINK_FONTAWESOME, new Rectangle2D.Float(ox+5.89f, oy+3.416f, 2.94f, 0.4f));
		p2.addRegion(ABOUT_LINK_LAUNCH4J, new Rectangle2D.Float(ox+5.89f, oy+4.422f, 4.1f, 0.4f));
		p2.addRegion(ABOUT_LINK_APPBUNDLER, new Rectangle2D.Float(ox+5.89f, oy+5.465f, 4.452f, 0.4f));

		//setup hit test for page 3
		HitTester p3 = new HitTester(HIT_TEST_DELAY, this);
		p3.addRegion(ABOUT_LINK_GPL, new Rectangle2D.Float(ox+3.67f, oy+4.554f, 3.7f, 0.4f));

		hitTesters = new HitTester[] { p1, p2, p3 };

	}

	public void draw(GraphicsWrapper g2, int page) {

		currentHitTester = hitTesters[page];

		g2.drawImage("about"+(page+1), 8f, 5f);

		if (page == 0) {
			g2.drawStringCentered("Version "+Main.getVersion(), 0.25f, Color.WHITE, 8f, 8.5f);
		}

		if (currentHitTester != null)
			currentHitTester.drawSelectingRegion(g2);

	}

	public int pageCount() {
		return 3;
	}

	public void openLink(String url) {

		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception e) {}

	}

	public void onHitTestSelection(int id) {

		switch (id) {
			case ABOUT_LINK_POLYDROP:
				openLink("http://mattb.name/polydrop");
				break;
			case ABOUT_LINK_JAVA:
				openLink("http://java.com/");
				break;
			case ABOUT_LINK_LEAP:
				openLink("http://leapmotion.com/");
				break;
			case ABOUT_LINK_JBOX2D:
				openLink("http://www.jbox2d.org/");
				break;
			case ABOUT_LINK_FONTAWESOME:
				openLink("http://fontawesome.io/");
				break;
			case ABOUT_LINK_LAUNCH4J:
				openLink("http://launch4j.sourceforge.net/");
				break;
			case ABOUT_LINK_APPBUNDLER:
				openLink("http://java.net/projects/appbundler");
				break;
			case ABOUT_LINK_GPL:
				openLink("http://www.gnu.org/licenses/");
				break;
		}

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		super.pointerUpdate(cursorX, cursorY);
		if (currentHitTester != null)
			currentHitTester.pointerUpdate(cursorX, cursorY);
	}

}