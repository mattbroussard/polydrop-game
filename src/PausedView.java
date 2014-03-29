
public class PausedView extends View implements RadialMenuListener {
	
	GameView gameView;
	GameController controller;
	
	RadialMenu menu;
	RadialMenuItem muteMenuItem;

	static final int PAUSE_MENU_MODE_FREE = 0;
	static final int PAUSE_MENU_MODE_DUAL = 1;
	static final int PAUSE_MENU_MODE_SINGLE = 2;
	static final int PAUSE_MENU_EXIT_GAME = 3;
	static final int PAUSE_MENU_LEADERBOARD = 4;
	static final int PAUSE_MENU_MUTE = 5;

	public PausedView(GameController controller, GameView gameView) {

		this.controller = controller;
		this.gameView = gameView;

		menu = new RadialMenu(8, 5.5f, this);
		menu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_FREE, "Free Play", "freeMode", 60, 20, RadialMenuItem.ORIENT_TOP, Colors.MENU_MODE_FREE_SELECTED, Colors.MENU_MODE_FREE_ACTIVE));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_DUAL, "Two Hands", "dualMode", 80, 20, RadialMenuItem.ORIENT_TOP, Colors.MENU_MODE_DUAL_SELECTED, Colors.MENU_MODE_DUAL_ACTIVE));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_SINGLE, "One Hand", "singleMode", 100, 20, RadialMenuItem.ORIENT_TOP, Colors.MENU_MODE_SINGLE_SELECTED, Colors.MENU_MODE_SINGLE_ACTIVE));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_EXIT_GAME, "Exit Game", "exit", 240, 20, RadialMenuItem.ORIENT_BOTTOM));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_LEADERBOARD, "High Scores", "leaderboard", 260, 20, RadialMenuItem.ORIENT_BOTTOM));
		
		muteMenuItem = new RadialMenuItem(PAUSE_MENU_MUTE, "Mute", "mute", 280, 20, RadialMenuItem.ORIENT_BOTTOM);
		menu.addItem(muteMenuItem);

	}

	public void draw(GraphicsWrapper g2, boolean active) {

		gameView.draw(g2, false, active);

		if (active) {
			TextRenderer.drawPaused(g2);
			menu.draw(g2);
		}

	}

	public void onActive() {

		controller.setUsingUI(false);

		GameModel model = controller.getModel();

		switch (model.getGameMode()) {
			case GameModel.ONE_HAND:
				menu.setActiveItem(PAUSE_MENU_MODE_SINGLE);
				break;
			case GameModel.TWO_HANDS:
				menu.setActiveItem(PAUSE_MENU_MODE_DUAL);
				break;
			case GameModel.FREE_PLAY:
				menu.setActiveItem(PAUSE_MENU_MODE_FREE);
				break;
		}

	}

	public void onMenuSelection(int id) {

		GameModel model = controller.getModel();

		switch (id) {
			case PAUSE_MENU_MODE_FREE:
				menu.setActiveItem(PAUSE_MENU_MODE_FREE);
				if( model.getGameMode() != GameModel.FREE_PLAY ) {
					model.setGameMode(GameModel.FREE_PLAY);
					gameView.clearBlockList();
				}
				
				break;

			case PAUSE_MENU_MODE_DUAL:
				menu.setActiveItem(PAUSE_MENU_MODE_DUAL);
				if( model.getGameMode() != GameModel.TWO_HANDS ) {
					model.setGameMode(GameModel.TWO_HANDS);
				}
				
				break;

			case PAUSE_MENU_MODE_SINGLE:
				menu.setActiveItem(PAUSE_MENU_MODE_SINGLE);
				if( model.getGameMode() != GameModel.ONE_HAND ) {
					model.setGameMode(GameModel.ONE_HAND);
				}

				break;

			case PAUSE_MENU_MUTE:
				SoundManager.toggleMuted();
				muteMenuItem.setIcon(SoundManager.isMuted() ? "unmute" : "mute");
				muteMenuItem.setTitle(SoundManager.isMuted() ? "Unmute" : "Mute");
				
				break;

			case PAUSE_MENU_EXIT_GAME:
				controller.exitGame();
				break;

			case PAUSE_MENU_LEADERBOARD:
				controller.setUsingUI(true);
				getViewManager().pushView("leaderboard");
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