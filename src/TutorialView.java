
public class TutorialView extends View implements RadialMenuListener {

	RadialMenu menu;

	static final int TUTORIAL_MENU_BACK = 0;

	public TutorialView() {

		menu = new RadialMenu(8, 11.5f, this);
		menu.addItem(new RadialMenuItem(TUTORIAL_MENU_BACK, "Back", "menuReturn", 80, 20, RadialMenuItem.ORIENT_TOP));

	}

	public void draw(GraphicsWrapper g2, boolean active) {
		if (!active) return;
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		g2.fillRect(0f, 0f, 16f, 10f, Colors.PAUSED);

		//TODO: clearly, this is temporary
		g2.drawStringCentered("let me learn you a thing", 0.5f, Colors.SCORE, 8.0f, 5.0f);

		menu.draw(g2);

	}

	public void onMenuSelection(int id) {

		switch (id) {
			case TUTORIAL_MENU_BACK:
				getViewManager().popView();
				break;
			default:
				return;
		}

		SoundManager.play("menuChoice");

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		menu.pointerUpdate(cursorX, cursorY);
	}

}