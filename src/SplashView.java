
public class SplashView extends View implements RadialMenuListener {
	
	RadialMenu menu;

	static final int SPLASH_MENU_PLAY = 0;
	static final int SPLASH_MENU_TUTORIAL = 1;
	static final int SPLASH_MENU_LEADERBOARD = 2;
	static final int SPLASH_MENU_ABOUT = 3;
	static final int SPLASH_MENU_EXIT_GAME = 4;

	public SplashView() {

		menu = new RadialMenu(16.5f, 5f, this);

		menu.addItem(new RadialMenuItem(SPLASH_MENU_TUTORIAL, "Tutorial", "tutorial", 123, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_LEADERBOARD, "Leaderboard", "leaderboard", 143, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_PLAY, "Play", "singleMode", 163, 34, RadialMenuItem.ORIENT_LEFT, Colors.SPLASH_MENU_PLAY_SELECTED, Colors.SPLASH_MENU_PLAY_ACTIVE));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_ABOUT, "About", "about", 197, 20, RadialMenuItem.ORIENT_LEFT));
		menu.addItem(new RadialMenuItem(SPLASH_MENU_EXIT_GAME, "Exit Game", "exit", 217, 20, RadialMenuItem.ORIENT_LEFT));

		menu.setActiveItem(SPLASH_MENU_PLAY);

	}

	public void draw(GraphicsWrapper g2, boolean active) {
		g2.prepare(GraphicsWrapper.TRANSFORM_STANDARD);

		g2.fillRect(0f, 0f, 16f, 10f, Colors.PAUSED);
		if (!active) return;

		//TODO: clearly, this is temporary
		g2.drawStringCentered("le splash~", 1.5f, Colors.SCORE, 8.0f, 5.0f);
		g2.drawStringCentered("build " + Main.getVersion(), 0.2f, Colors.SCORE, 8.0f, 6.5f);

		menu.draw(g2);

	}

	public void onMenuSelection(int id) {

		switch (id) {
			case SPLASH_MENU_TUTORIAL:
				getViewManager().pushView("tutorial");
				break;

			case SPLASH_MENU_LEADERBOARD:
				getViewManager().pushView("leaderboard");
				break;

			case SPLASH_MENU_PLAY:
				getViewManager().pushView("paused");
				break;

			case SPLASH_MENU_ABOUT:
				getViewManager().pushView("about");
				break;

			case SPLASH_MENU_EXIT_GAME:
				System.exit(0);
				break;

			default:
				break;
		}

		SoundManager.play("menuChoice");

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		menu.pointerUpdate(cursorX, cursorY);
	}

}