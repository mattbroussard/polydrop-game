
import java.awt.event.KeyEvent;
import java.awt.Color;

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
	static final int PAUSE_MENU_TUTORIAL = 6;

	public PausedView(GameController controller, GameView gameView) {

		this.controller = controller;
		this.gameView = gameView;

		menu = new RadialMenu(8, 5.5f, this);
		menu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_FREE, "Free Play", "freeMode", 60, 20, RadialMenuItem.ORIENT_TOP, Colors.MENU_MODE_FREE_SELECTED, Colors.MENU_MODE_FREE_ACTIVE));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_DUAL, "Two Hands", "dualMode", 80, 20, RadialMenuItem.ORIENT_TOP, Colors.MENU_MODE_DUAL_SELECTED, Colors.MENU_MODE_DUAL_ACTIVE));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_MODE_SINGLE, "One Hand", "singleMode", 100, 20, RadialMenuItem.ORIENT_TOP, Colors.MENU_MODE_SINGLE_SELECTED, Colors.MENU_MODE_SINGLE_ACTIVE));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_EXIT_GAME, "Exit Game", "exit", 230, 20, RadialMenuItem.ORIENT_BOTTOM));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_LEADERBOARD, "High Scores", "leaderboard", 250, 20, RadialMenuItem.ORIENT_BOTTOM));
		menu.addItem(new RadialMenuItem(PAUSE_MENU_TUTORIAL, "Tutorial", "tutorial", 270, 20, RadialMenuItem.ORIENT_BOTTOM));

		muteMenuItem = new RadialMenuItem(PAUSE_MENU_MUTE, "Mute", "mute", 290, 20, RadialMenuItem.ORIENT_BOTTOM);
		menu.addItem(muteMenuItem);

	}

	public void draw(GraphicsWrapper g2, boolean active) {

		gameView.draw(g2, false, active);

		if (active) {
			TextRenderer.drawPaused(g2);
			menu.draw(g2);

			//draw text and image to guide user how to unpause
			g2.drawImage("tutorialHand", 8f, 5f);
			g2.drawStringCentered("Open your hand to unpause the game.", 0.25f, Color.WHITE, 8f, 6.5f);

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

			case PAUSE_MENU_TUTORIAL:
				controller.setUsingUI(true);
				getViewManager().pushView("tutorial");
				break;

			default:
				return;
		}
		
		SoundManager.play("menuChoice");

	}

	public void pointerUpdate(float cursorX, float cursorY) {
		menu.pointerUpdate(cursorX, cursorY);
	}

	StringBuilder cheat = new StringBuilder();
	public void onKey(int keyCode) {

		//this is the vector through which cheats are entered
		if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
			char c = (char)(keyCode - KeyEvent.VK_A + (char)'a');
			cheat.append(c);
			if (cheat.length() > 100)
				cheat.delete(0, 50);
		} else if (keyCode == KeyEvent.VK_ENTER) {
			String s = cheat.toString();
			cheat = new StringBuilder();
			CheatManager.applyCheat(s);
		}

	}

}