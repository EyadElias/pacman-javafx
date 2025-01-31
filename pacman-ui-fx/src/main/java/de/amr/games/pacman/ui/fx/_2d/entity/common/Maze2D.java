/*
MIT License

Copyright (c) 2021 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package de.amr.games.pacman.ui.fx._2d.entity.common;

import static de.amr.games.pacman.model.world.PacManGameWorld.TS;
import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.util.List;
import java.util.stream.Collectors;

import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameLevel;
import de.amr.games.pacman.ui.fx._2d.rendering.common.Rendering2D;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * 2D representation of the maze for a given game level. Implements the flashing animation played
 * when the game level is complete.
 * 
 * @author Armin Reichert
 */
public class Maze2D implements Renderable2D {

	private final int x;
	private final int y;
	private final Rendering2D rendering;
	private GameLevel gameLevel;
	private Timeline flashingAnimation;
	private boolean flashing;
	private List<Energizer2D> energizers2D;
	private TimedSequence<Boolean> energizerBlinking = TimedSequence.pulse().frameDuration(10);

	public Maze2D(V2i leftUpperCorner, Rendering2D rendering) {
		x = t(leftUpperCorner.x);
		y = t(leftUpperCorner.y);
		this.rendering = rendering;
		KeyFrame switchImage = new KeyFrame(Duration.millis(150), e -> flashing = !flashing);
		flashingAnimation = new Timeline(switchImage);
		flashing = false;
	}

	public void setGameLevel(GameLevel gameLevel) {
		this.gameLevel = gameLevel;
		energizers2D = gameLevel.world.energizerTiles().map(energizerTile -> {
			Energizer2D energizer2D = new Energizer2D();
			energizer2D.x = t(energizerTile.x);
			energizer2D.y = t(energizerTile.y);
			energizer2D.blinkingAnimation = energizerBlinking;
			return energizer2D;
		}).collect(Collectors.toList());
		flashingAnimation.setCycleCount(2 * gameLevel.numFlashes);
	}

	public boolean isFlashing() {
		return flashingAnimation.getStatus() == Status.RUNNING;
	}

	public Timeline getFlashingAnimation() {
		return flashingAnimation;
	}

	public TimedSequence<Boolean> getEnergizerBlinking() {
		return energizerBlinking;
	}

	@Override
	public void render(GraphicsContext g) {
		if (flashingAnimation.getStatus() == Status.RUNNING) {
			if (flashing) {
				rendering.renderMazeFlashing(g, gameLevel.mazeNumber, x, y);
			} else {
				rendering.renderMazeEmpty(g, gameLevel.mazeNumber, x, y);
			}
		} else {
			rendering.renderMazeFull(g, gameLevel.mazeNumber, x, y);
			energizers2D.forEach(energizer2D -> energizer2D.render(g));
			energizerBlinking.animate();
			g.setFill(Color.BLACK);
			gameLevel.world.tiles().filter(gameLevel::isFoodRemoved).forEach(emptyFoodTile -> {
				g.fillRect(t(emptyFoodTile.x), t(emptyFoodTile.y), TS, TS);
			});
		}
	}
}