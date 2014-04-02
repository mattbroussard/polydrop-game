
import java.awt.Color;
import java.awt.geom.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class BodyRenderer {

	public final static int EXPIRATION_PERIOD = 2500;

	public static Color expireColor(Color c, long expiry) {

		if (expiry > EXPIRATION_PERIOD) return c;
		if (expiry < 0) return c;
		if (expiry == 0) return null;

		double progress = (double)(EXPIRATION_PERIOD-expiry) / (double)EXPIRATION_PERIOD;
		Color interp = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)Math.round((1-progress)*255)); //previously: Colors.interpolateColor(c, Colors.BACKGROUND, progress);

		double blink = Math.sin(50.0f * progress);
		return blink >= 0 ? interp : null;

	}

	public static void drawBody(DrawableBody db, GraphicsWrapper g2, boolean paused) {

		g2.prepare(GraphicsWrapper.TRANSFORM_BODIES);

		Fixture fix = db.getFixture();
		Body body = db.getBody();
		PolygonShape shape = (PolygonShape) fix.getShape();

		Path2D.Float poly = new Path2D.Float();
		for (int i = 0; i < shape.getVertexCount(); i++) {
			Vec2 vertex = shape.getVertex(i);
			Vec2 wv = body.getWorldPoint(vertex);
			if (i==0) poly.moveTo(wv.x, wv.y);
			else poly.lineTo(wv.x, wv.y);
		}

		Color c = paused ? Colors.BACKGROUND : expireColor(db.getColor(), db.getExpiration());

		if (c!=null)
			g2.fillPath(poly, c);

	}

}