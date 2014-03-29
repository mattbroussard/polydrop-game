
//This is an abstract class rather than an interface because all of the methods are optional
public abstract class View {

	//the parent ViewManager... used because Views may be asked to repaint and now need to ask ViewManager to do this.
	//note: this starts null but is never set back to null, even when the view becomes inactive... this is to avoid an issue where a repaint is requested just as the view is being removed (a NullPointerException could result).
	private ViewManager viewManager = null;

	//called by the ViewManager if the view is on the view stack.
	//If the view is the topmost one that the user intends to interact with, it is considered active.
	//There is a special exception to this relationship for for GameView, PausedView, and GameOverView: PausedView/GameOverView call the GameView's draw directly when the GameView is not on the stack at all so that the presence of blocks, etc. can be controlled more finely.
	public void draw(GraphicsWrapper g2, boolean active) {}
	
	//called when the view becomes active, either:
	// - by being added to the view stack for the first time, or
	// - by another view being removed from the view stack
	public void onActive() {}

	//called to update the position of the pointer, if this view uses one, when the view is active.
	//coordinates are in the 16x10 standard coordinate space
	public void pointerUpdate(float cursorX, float cursorY) {}

	//called when a key event is received, when this view is active.
	public void onKey(int keyCode) {}

	//called by ViewManager to notify this View of who its parent is, so it knows who to ask to repaint.
	public final void setViewManager(ViewManager vm) {
		viewManager = vm;
	}

	//this exists only so Views can use JOptionPane and similar
	public final ViewManager getViewManager() {
		return viewManager;
	}

	//called by the View or anyone who interacts with it that wants a repaint
	public final void repaint() {
		if (viewManager != null)
			viewManager.repaint();
	}

	//if draw(GraphicsWrapper) is called without indication of being active, assume it's not.
	public final void draw(GraphicsWrapper g2) {
		draw(g2, false);
	}

}