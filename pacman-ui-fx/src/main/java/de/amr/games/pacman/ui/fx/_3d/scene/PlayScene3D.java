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
package de.amr.games.pacman.ui.fx._3d.scene;

import static de.amr.games.pacman.lib.Logging.log;
import static de.amr.games.pacman.model.world.PacManGameWorld.TS;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.ui.fx.Env;
import de.amr.games.pacman.ui.fx._2d.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.fx._3d.entity.Bonus3D;
import de.amr.games.pacman.ui.fx._3d.entity.Ghost3D;
import de.amr.games.pacman.ui.fx._3d.entity.LevelCounter3D;
import de.amr.games.pacman.ui.fx._3d.entity.LivesCounter3D;
import de.amr.games.pacman.ui.fx._3d.entity.Maze3D;
import de.amr.games.pacman.ui.fx._3d.entity.PacManModel3D;
import de.amr.games.pacman.ui.fx._3d.entity.Player3D;
import de.amr.games.pacman.ui.fx._3d.entity.ScoreNotReally3D;
import de.amr.games.pacman.ui.fx.scene.GameScene;
import de.amr.games.pacman.ui.fx.util.CoordinateSystem;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;

/**
 * 3D-scene displaying the maze and the game play. Used in both game variants.
 * 
 * @author Armin Reichert
 */
public class PlayScene3D implements GameScene {

	static final int CAM_TOTAL = 0, CAM_FOLLOWING_PLAYER = 1, CAM_NEAR_PLAYER = 2;

	private final SubScene subSceneFX;
	private final PlaySceneCam[] cams;
	private int selectedCamIndex;

	protected PacManGameController gameController;

	protected final PacManModel3D model3D;
	protected Maze3D maze3D;
	protected Player3D player3D;
	protected List<Ghost3D> ghosts3D;
	protected Bonus3D bonus3D;
	protected ScoreNotReally3D score3D;
	protected LevelCounter3D levelCounter3D;
	protected LivesCounter3D livesCounter3D;

	public PlayScene3D(PacManModel3D model3D) {
		this.model3D = model3D;
		Camera cam = new PerspectiveCamera(true);
		subSceneFX = new SubScene(new Group(), 1, 1, true, SceneAntialiasing.BALANCED);
		subSceneFX.setCamera(cam);
		subSceneFX.addEventHandler(KeyEvent.KEY_PRESSED, event -> selectedCam().handle(event));
		cams = new PlaySceneCam[] { //
				new Cam_Total(cam), //
				new Cam_FollowingPlayer(cam), //
				new Cam_NearPlayer(cam), //
//				new POVPerspective(cam), //
		};
		selectCam(CAM_FOLLOWING_PLAYER);
		Env.$mazeResolution.addListener((resolution, oldValue, newValue) -> buildMazeWalls(newValue.intValue()));
	}

	@Override
	public void init() {
		log("%s: init", this);

		final var width = game().level().world.numCols() * TS;
		final var height = game().level().world.numRows() * TS;

		maze3D = new Maze3D(width, height);
		maze3D.setFloorTexture(new Image(getClass().getResourceAsStream("/common/escher-texture.jpg")));
		maze3D.setWallBaseColor(rendering2D().getMazeSideColor(game().level().mazeNumber));
		maze3D.setWallTopColor(rendering2D().getMazeTopColor(game().level().mazeNumber));
		maze3D.$wallHeight.bind(Env.$mazeWallHeight);
		buildMaze();

		player3D = new Player3D(game().player(), model3D);
		ghosts3D = game().ghosts().map(ghost -> new Ghost3D(ghost, model3D, rendering2D())).collect(Collectors.toList());
		bonus3D = new Bonus3D(rendering2D());
		score3D = new ScoreNotReally3D(rendering2D().getScoreFont());

		livesCounter3D = new LivesCounter3D(model3D);
		livesCounter3D.setTranslateX(TS);
		livesCounter3D.setTranslateY(TS);
		livesCounter3D.setTranslateZ(-4); // TODO
		livesCounter3D.setVisible(!gameController.isAttractMode());

		levelCounter3D = new LevelCounter3D(rendering2D());
		levelCounter3D.setRightPosition(26 * TS, TS);
		levelCounter3D.setTranslateZ(-4); // TODO
		levelCounter3D.rebuild(game());

		var playground = new Group(maze3D, score3D, livesCounter3D, levelCounter3D, player3D, bonus3D);
		playground.getChildren().addAll(ghosts3D);
		playground.setTranslateX(-0.5 * width);
		playground.setTranslateY(-0.5 * height);

		var coordinateSystem = new CoordinateSystem(subSceneFX.getWidth());
		coordinateSystem.visibleProperty().bind(Env.$axesVisible);

		subSceneFX.setRoot(new Group(new AmbientLight(), playground, coordinateSystem));
	}

	@Override
	public void update() {
		player3D.update();
		ghosts3D.forEach(Ghost3D::update);
		bonus3D.update(game().bonus());
		score3D.update(game(), gameController.isAttractMode());
		// TODO: is this the recommended way to do keep the score in plain view?
		score3D.setRotationAxis(Rotate.X_AXIS);
		score3D.setRotate(subSceneFX.getCamera().getRotate());
		livesCounter3D.setVisibleItems(game().lives());
		selectedCam().follow(player3D);
	}

	@Override
	public void end() {
		log("%s: end", this);
	}

	public PlaySceneCam selectedCam() {
		return cams[selectedCamIndex];
	}

	public void selectCam(int i) {
		selectedCamIndex = i;
		selectedCam().reset();
	}

	public void nextCam() {
		selectCam((selectedCamIndex + 1) % cams.length);
	}

	@Override
	public PacManGameController getGameController() {
		return gameController;
	}

	@Override
	public void setGameController(PacManGameController gameController) {
		this.gameController = gameController;
	}

	@Override
	public OptionalDouble aspectRatio() {
		return OptionalDouble.empty();
	}

	@Override
	public SubScene getSubSceneFX() {
		return subSceneFX;
	}

	@Override
	public void resize(double width, double height) {
		// data binding does the job
	}

	protected Rendering2D rendering2D() {
		return gameController.gameVariant() == GameVariant.MS_PACMAN
				? de.amr.games.pacman.ui.fx.scene.Scenes.MS_PACMAN_RENDERING
				: de.amr.games.pacman.ui.fx.scene.Scenes.PACMAN_RENDERING;
	}

	protected void buildMaze() {
		var foodColor = rendering2D().getFoodColor(game().level().mazeNumber);
		maze3D.buildWallsAndAddFood(game().level().world, Env.$mazeResolution.get(), Env.$mazeWallHeight.get(), foodColor);
	}

	protected void buildMazeWalls(int resolution) {
		maze3D.buildWalls(game().level().world, resolution, Env.$mazeWallHeight.get());
	}
}