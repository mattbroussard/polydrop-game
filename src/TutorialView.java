
public class TutorialView extends PaginatedView {

	public void draw(GraphicsWrapper g2, int page) {

		//TODO: clearly, this is temporary
		g2.drawStringCentered("tutorial, page "+(page+1), 0.5f, Colors.SCORE, 8.0f, 5.0f);

	}

	public int pageCount() { return 5; }

}