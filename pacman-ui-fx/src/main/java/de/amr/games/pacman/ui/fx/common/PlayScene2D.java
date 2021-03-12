package de.amr.games.pacman.ui.fx.common;

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.util.Optional;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.PacManGameState;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.animation.Animation;
import de.amr.games.pacman.ui.animation.PacManGameAnimations;
import de.amr.games.pacman.ui.fx.rendering.PacManGameRendering2D;
import de.amr.games.pacman.ui.sound.SoundManager;

/**
 * This is where the action is.
 * 
 * @author Armin Reichert
 */
public class PlayScene2D extends AbstractGameScene2D {

	private Animation<?> mazeFlashing;

	public PlayScene2D(PacManGameController controller, PacManGameRendering2D rendering, SoundManager sounds) {
		super(controller, rendering, sounds);
		controller.fsm.addStateEntryListener(PacManGameState.HUNTING, this::onHuntingStarted);
		controller.fsm.addStateEntryListener(PacManGameState.CHANGING_LEVEL, this::onChangingGameLevel);
		controller.fsm.addStateEntryListener(PacManGameState.PACMAN_DYING, this::onPacManDying);
	}

	private void onHuntingStarted(PacManGameState state) {
		rendering.mazeAnimations().energizerBlinking().restart();
		rendering.playerAnimations().playerMunching(controller.game.player).forEach(Animation::restart);
		controller.game.ghosts().flatMap(rendering.ghostAnimations()::ghostKicking).forEach(Animation::restart);
	}

	private void onPacManDying(PacManGameState state) {
		controller.game.ghosts().flatMap(rendering.ghostAnimations()::ghostKicking).forEach(Animation::reset);
	}

	private void onChangingGameLevel(PacManGameState state) {
		GameModel game = controller.game;
		mazeFlashing = rendering.mazeAnimations().mazeFlashing(game.level.mazeNumber);
	}

	private void runChangingGameLevel(PacManGameState state) {
		GameModel game = controller.game;
		if (state.timer.isRunningSeconds(2)) {
			game.ghosts().forEach(ghost -> ghost.visible = false);
		}
		if (state.timer.isRunningSeconds(3)) {
			mazeFlashing.restart();
		}
		mazeFlashing.animate();
		if (mazeFlashing.isComplete()) {
			controller.letCurrentGameStateExpire();
		}
	}

	@Override
	public Optional<PacManGameAnimations> animations() {
		return Optional.of(rendering);
	}

	@Override
	public void start() {
		GameModel game = controller.game;
		mazeFlashing = rendering.mazeAnimations().mazeFlashing(game.level.mazeNumber).repetitions(game.level.numFlashes);
		mazeFlashing.reset();
	}

	@Override
	public void end() {
	}

	@Override
	public void update() {
		if (controller.fsm.state == PacManGameState.CHANGING_LEVEL) {
			runChangingGameLevel(controller.fsm.state);
		}
		render();
	}

	public void render() {
		GameModel game = controller.game;
		rendering.drawMaze(gc, game.level.mazeNumber, 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			rendering.drawFoodTiles(gc, game.level.world.tiles().filter(game.level.world::isFoodTile),
					game.level::containsEatenFood);
			rendering.drawEnergizerTiles(gc, game.level.world.energizerTiles());
		}
		if (controller.attractMode) {
			rendering.drawGameState(gc, game, PacManGameState.GAME_OVER);
		} else {
			rendering.drawGameState(gc, game, controller.fsm.state);
		}
		rendering.drawBonus(gc, game.bonus);
		rendering.drawPlayer(gc, game.player);
		game.ghosts().forEach(ghost -> rendering.drawGhost(gc, ghost, game.player.powerTimer.isRunning()));
		rendering.drawScore(gc, game, controller.fsm.state == PacManGameState.INTRO || controller.attractMode);
		if (!controller.attractMode) {
			rendering.drawLivesCounter(gc, game, t(2), t(34));
		}
		rendering.drawLevelCounter(gc, game, t(25), t(34));
	}
}