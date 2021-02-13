package de.amr.games.pacman.ui.multi.app;

import java.io.IOException;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.ui.fx.PacManGameFXUI;
import de.amr.games.pacman.ui.swing.PacManGameSwingUI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The Pac-Man game app.
 * 
 * @author Armin Reichert
 */
public class PacManGameAppMulti extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		PacManGameController controller = new PacManGameController();
		controller.playMsPacMan();
		controller.addView(new PacManGameFXUI(stage, controller, 2.0));
		controller.addView(new PacManGameSwingUI(controller, 2.0));
		controller.showViews();
		controller.startGame();
	}
}