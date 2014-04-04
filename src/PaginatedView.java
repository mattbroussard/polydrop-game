
import java.awt.Color;

public abstract class PaginatedView extends View implements RadialMenuListener {

	RadialMenu prevMenu;
	RadialMenu nextMenu;
	int page = 0;

	static final int PAGINATED_MENU_PREV = -1;
	static final int PAGINATED_MENU_NEXT = 1;

	//candidates to be overridden
	public abstract void draw(GraphicsWrapper g2, int page);
	public abstract int pageCount();
	public void pageChanged() {}

	public PaginatedView() {

		nextMenu = new RadialMenu(17.5f, 5f, this);
		nextMenu.addItem(new RadialMenuItem(PAGINATED_MENU_NEXT, "Next", "menuForward", 163, 34, RadialMenuItem.ORIENT_LEFT));
		prevMenu = new RadialMenu(-1.5f, 5f, this);
		prevMenu.addItem(new RadialMenuItem(PAGINATED_MENU_PREV, "Previous", "menuReturn", -17, 34, RadialMenuItem.ORIENT_RIGHT));

	}

	public int getCurrentPage() {
		return page;
	}

	public void draw(GraphicsWrapper g2, boolean active) {
		if (!active) return;
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		draw(g2, getCurrentPage());

		String progress = "";
		for (int i = 0; i < getCurrentPage(); i++) progress += "\u25E6";
		progress += "\u2022";
		for (int i = getCurrentPage()+1; i < pageCount(); i++) progress += "\u25E6";
		g2.drawStringCentered(progress, 0.75f, Color.WHITE, 8f, 9.5f);

		prevMenu.draw(g2);
		nextMenu.draw(g2);

	}

	public void onMenuSelection(int id) {

		page += id;
		if (page < 0 || page >= pageCount()) {
			getViewManager().popView();
			page = 0;
		} else {
			pageChanged();
		}

		SoundManager.play("menuChoice");

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		prevMenu.pointerUpdate(cursorX, cursorY);
		nextMenu.pointerUpdate(cursorX, cursorY);
	}

}