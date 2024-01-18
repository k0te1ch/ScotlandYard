package solution;

import solution.controllers.GameController;
import solution.helpers.SoundHelper;
import solution.views.MainFrame;

/**
 * This is the main entry point to the app.
 * I thought it would be nice to have this - it shouldn't be too big
 */
public class ScotlandYardApplication {

	public static void main(String[] args) {

        GameController gameController = new GameController();

        //we setup the ui here
		MainFrame mainFrame = new MainFrame(gameController);

        //Set up sound
        SoundHelper.loadClips();
        //now we're good to go!


	}

}
