
public class GameOverView extends View implements RadialMenuListener {
	
	GameController controller;
	GameView gameView;

	RadialMenu menu;

	static final int GAMEOVER_MENU_NEWGAME = 1;
	static final int GAMEOVER_MENU_EXIT_GAME = 2;
	static final int GAMEOVER_MENU_LEADERBOARD = 3;
	static final int GAMEOVER_MENU_TUTORIAL = 4;

	public GameOverView(GameController controller, GameView gameView) {

		this.controller = controller;
		this.gameView = gameView;

		menu = new RadialMenu(8, 11.5f, this);
		menu.addItem(new RadialMenuItem(GAMEOVER_MENU_NEWGAME, "New Game", "newGame", 110, 20, RadialMenuItem.ORIENT_TOP));
		menu.addItem(new RadialMenuItem(GAMEOVER_MENU_LEADERBOARD, "High Scores", "leaderboard", 90, 20, RadialMenuItem.ORIENT_TOP));
		menu.addItem(new RadialMenuItem(GAMEOVER_MENU_TUTORIAL, "Tutorial", "tutorial", 70, 20, RadialMenuItem.ORIENT_TOP));
		menu.addItem(new RadialMenuItem(GAMEOVER_MENU_EXIT_GAME, "Exit Game", "exit", 50, 20, RadialMenuItem.ORIENT_TOP));

	}

	public void draw(GraphicsWrapper g2, boolean active) {

		gameView.draw(g2, false, active);

		if (active) {
			TextRenderer.drawGameOver(g2);
			menu.draw(g2);
		}

	}

	public void onMenuSelection(int id) {

		switch (id) {
			case GAMEOVER_MENU_EXIT_GAME:
				controller.exitGame();
				break;
			case GAMEOVER_MENU_LEADERBOARD:
				getViewManager().pushView("leaderboard");
				break;
			case GAMEOVER_MENU_NEWGAME:
				controller.newGame();
				break;
			case GAMEOVER_MENU_TUTORIAL:
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

}