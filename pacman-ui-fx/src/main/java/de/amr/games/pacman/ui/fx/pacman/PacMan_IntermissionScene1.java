package de.amr.games.pacman.ui.fx.pacman;

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.ui.fx.common.AbstractGameScene2D;
import de.amr.games.pacman.ui.fx.rendering.FXRendering;
import de.amr.games.pacman.ui.pacman.PacMan_IntermissionScene1_Controller;
import de.amr.games.pacman.ui.pacman.PacMan_IntermissionScene1_Controller.Phase;
import de.amr.games.pacman.ui.sound.SoundManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * First intermission scene: Blinky chases Pac-Man and is then chased by a huge Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene1 extends AbstractGameScene2D {

	private PacMan_IntermissionScene1_Controller animation;

	public PacMan_IntermissionScene1(PacManGameController controller, FXRendering rendering, SoundManager sounds) {
		super(controller, rendering, sounds);
	}

	@Override
	public void start() {
		animation = new PacMan_IntermissionScene1_Controller(controller, rendering, sounds);
		animation.start();
	}

	@Override
	public void end() {
	}

	@Override
	public void update() {
		animation.update();
		clearCanvas();
		GraphicsContext g = canvas.getGraphicsContext2D();
		rendering.drawGhost(g, animation.blinky, false);
		if (animation.phase == Phase.BLINKY_CHASING_PACMAN) {
			rendering.drawPlayer(g, animation.pac);
		} else {
			g.translate(0, -10);
			rendering.drawBigPacMan(g, animation.pac);
			g.translate(0, 10);
		}
		rendering.drawLevelCounter(g, controller.getGame(), t(25), t(34));
	}
}