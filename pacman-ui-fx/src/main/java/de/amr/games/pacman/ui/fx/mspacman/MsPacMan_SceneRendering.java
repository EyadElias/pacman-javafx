package de.amr.games.pacman.ui.fx.mspacman;

import static de.amr.games.pacman.lib.Direction.LEFT;
import static de.amr.games.pacman.lib.Direction.RIGHT;
import static de.amr.games.pacman.lib.Direction.UP;
import static de.amr.games.pacman.model.GhostState.DEAD;
import static de.amr.games.pacman.model.GhostState.ENTERING_HOUSE;
import static de.amr.games.pacman.model.GhostState.FRIGHTENED;
import static de.amr.games.pacman.model.GhostState.LOCKED;
import static de.amr.games.pacman.world.PacManGameWorld.TS;
import static de.amr.games.pacman.world.PacManGameWorld.t;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.PacManGameState;
import de.amr.games.pacman.lib.Animation;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.Bonus;
import de.amr.games.pacman.model.Creature;
import de.amr.games.pacman.model.Ghost;
import de.amr.games.pacman.model.Pac;
import de.amr.games.pacman.model.PacManGameModel;
import de.amr.games.pacman.ui.PacManGameAnimation;
import de.amr.games.pacman.ui.fx.common.SceneRendering;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Rendering for the scenes of the Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class MsPacMan_SceneRendering implements SceneRendering, PacManGameAnimation {

	private final Image sheet = new Image("/mspacman/graphics/sprites.png", false);

	private final Rectangle2D[] symbols;
	private final Map<Integer, Rectangle2D> bonusValues;
	private final Map<Integer, Rectangle2D> bountyValues;
	private final Map<Direction, Animation<Rectangle2D>> msPacMunching;
	private final Map<Direction, Animation<Rectangle2D>> pacManMunching; // used in intermission scene
	private final Animation<Rectangle2D> msPacSpinning;
	private final List<EnumMap<Direction, Animation<Rectangle2D>>> ghostsKicking;
	private final EnumMap<Direction, Animation<Rectangle2D>> ghostEyes;
	private final Animation<Rectangle2D> ghostBlue;
	private final Animation<Rectangle2D> ghostFlashing;
	private final Animation<Integer> bonusJumps;
	private final List<Animation<Image>> mazesFlashing;
	private final Animation<Boolean> energizerBlinking;
	private final Animation<Rectangle2D> flapAnim;
	private final Animation<Rectangle2D> birdAnim;

	private final Font scoreFont;

	private int index(Direction dir) {
		return dir == RIGHT ? 0 : dir == LEFT ? 1 : dir == UP ? 2 : 3;
	}

	private Rectangle2D r(double x, double y, int tileX, int tileY, double xTiles, double yTiles) {
		return new Rectangle2D(x + tileX * 16, y + tileY * 16, xTiles * 16, yTiles * 16);
	}

	private Rectangle2D s(int tileX, int tileY) {
		return r(456, 0, tileX, tileY, 1, 1);
	}

	public MsPacMan_SceneRendering() {

		scoreFont = Font.loadFont(getClass().getResource("/emulogic.ttf").toExternalForm(), 8);

		symbols = new Rectangle2D[] { s(3, 0), s(4, 0), s(5, 0), s(6, 0), s(7, 0), s(8, 0), s(9, 0) };

		//@formatter:off

		bonusValues = new HashMap<>();
		bonusValues.put(100,  s(3, 1));
		bonusValues.put(200,  s(4, 1));
		bonusValues.put(500,  s(5, 1));
		bonusValues.put(700,  s(6, 1));
		bonusValues.put(1000, s(7, 1));
		bonusValues.put(2000, s(8, 1));
		bonusValues.put(5000, s(9, 1));
		
		bountyValues = new HashMap<>();
		bountyValues.put(200,  s(0, 8));
		bountyValues.put(400,  s(1, 8));
		bountyValues.put(800,  s(2, 8));
		bountyValues.put(1600, s(3, 8));
		//@formatter:on

		// Animations

		energizerBlinking = Animation.pulse().frameDuration(10);

		mazesFlashing = new ArrayList<>(6);
		for (int mazeIndex = 0; mazeIndex < 6; ++mazeIndex) {
			Map<Color, Color> exchanges = Map.of(getMazeWallBorderColor(mazeIndex), Color.WHITE, getMazeWallColor(mazeIndex),
					Color.BLACK);
			WritableImage mazeEmpty = new WritableImage(226, 248);
			mazeEmpty.getPixelWriter().setPixels(0, 0, 226, 248, sheet.getPixelReader(), 226, 248 * mazeIndex);
			Image mazeEmptyBright = SceneRendering.exchangeColors(mazeEmpty, exchanges);
			mazesFlashing.add(Animation.of(mazeEmptyBright, mazeEmpty).frameDuration(15));
		}

		msPacMunching = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			Animation<Rectangle2D> munching = Animation.of(s(1, d), s(1, d), s(2, d), s(0, d));
			munching.frameDuration(2).endless();
			msPacMunching.put(dir, munching);
		}

		msPacSpinning = Animation.of(s(0, 3), s(0, 0), s(0, 1), s(0, 2));
		msPacSpinning.frameDuration(10).repetitions(2);

		pacManMunching = new EnumMap<>(Direction.class);
		pacManMunching.put(Direction.RIGHT, Animation.of(s(0, 9), s(1, 9), s(2, 9)).endless().frameDuration(2));
		pacManMunching.put(Direction.LEFT, Animation.of(s(0, 10), s(1, 10), s(2, 9)).endless().frameDuration(2));
		pacManMunching.put(Direction.UP, Animation.of(s(0, 11), s(1, 11), s(2, 9)).endless().frameDuration(2));
		pacManMunching.put(Direction.DOWN, Animation.of(s(0, 12), s(1, 12), s(2, 9)).endless().frameDuration(2));

		ghostsKicking = new ArrayList<>(4);
		for (int id = 0; id < 4; ++id) {
			EnumMap<Direction, Animation<Rectangle2D>> walkingTo = new EnumMap<>(Direction.class);
			for (Direction dir : Direction.values()) {
				int d = index(dir);
				Animation<Rectangle2D> walking = Animation.of(s(2 * d, 4 + id), s(2 * d + 1, 4 + id));
				walking.frameDuration(4).endless();
				walkingTo.put(dir, walking);
			}
			ghostsKicking.add(walkingTo);
		}

		ghostEyes = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			ghostEyes.put(dir, Animation.ofSingle(s(8 + index(dir), 5)));
		}

		ghostBlue = Animation.of(s(8, 4), s(9, 4));
		ghostBlue.frameDuration(20).endless().run();

		ghostFlashing = Animation.of(s(8, 4), s(9, 4), s(10, 4), s(11, 4));
		ghostFlashing.frameDuration(5).endless();

		bonusJumps = Animation.of(0, 2, 0, -2).frameDuration(20).endless().run();

		flapAnim = Animation.of( //
				new Rectangle2D(456, 208, 32, 32), //
				new Rectangle2D(488, 208, 32, 32), //
				new Rectangle2D(520, 208, 32, 32), //
				new Rectangle2D(488, 208, 32, 32), //
				new Rectangle2D(456, 208, 32, 32)//
		);
		flapAnim.repetitions(1).frameDuration(4);

		birdAnim = Animation.of(//
				new Rectangle2D(489, 176, 32, 16), //
				new Rectangle2D(521, 176, 32, 16));
		birdAnim.endless().frameDuration(10).restart();
	}

	@Override
	public Image spritesheet() {
		return sheet;
	}

	@Override
	public Font getScoreFont() {
		return scoreFont;
	}

	public Map<Direction, Animation<Rectangle2D>> pacManMunching() {
		return pacManMunching;
	}

	public Rectangle2D getHeart() {
		return s(2, 10);
	}

	public Animation<Rectangle2D> getFlapAnim() {
		return flapAnim;
	}

	/**
	 * Note: maze numbers are 1-based, maze index as stored here is 0-based.
	 * 
	 * @param mazeIndex
	 * @return
	 */
	public Color getMazeWallColor(int mazeIndex) {
		switch (mazeIndex) {
		case 0:
			return Color.rgb(255, 183, 174);
		case 1:
			return Color.rgb(71, 183, 255);
		case 2:
			return Color.rgb(222, 151, 81);
		case 3:
			return Color.rgb(33, 33, 255);
		case 4:
			return Color.rgb(255, 183, 255);
		case 5:
			return Color.rgb(255, 183, 174);
		default:
			return Color.WHITE;
		}
	}

	/**
	 * Note: maze numbers are 1-based, maze index as stored here is 0-based.
	 * 
	 * @param mazeIndex
	 * @return
	 */
	public Color getMazeWallBorderColor(int mazeIndex) {
		switch (mazeIndex) {
		case 0:
			return Color.rgb(255, 0, 0);
		case 1:
			return Color.rgb(222, 222, 255);
		case 2:
			return Color.rgb(222, 222, 255);
		case 3:
			return Color.rgb(255, 183, 81);
		case 4:
			return Color.rgb(255, 255, 0);
		case 5:
			return Color.rgb(255, 0, 0);
		default:
			return Color.WHITE;
		}
	}

	private Direction ensureNotNull(Direction dir) {
		return dir != null ? dir : Direction.RIGHT;
	}

	@Override
	public void signalGameState(GraphicsContext g, PacManGameModel game) {
		if (game.state == PacManGameState.GAME_OVER || game.attractMode) {
			g.setFont(scoreFont);
			g.setFill(Color.RED);
			g.fillText("GAME", t(9), t(21));
			g.fillText("OVER", t(15), t(21));
		} else if (game.state == PacManGameState.READY) {
			g.setFont(scoreFont);
			g.setFill(Color.YELLOW);
			g.fillText("READY", t(11), t(21));
		}
	}

	@Override
	public void drawMaze(GraphicsContext g, int mazeNumber, int x, int y, boolean flashing) {
		int index = mazeNumber - 1;
		if (flashing) {
			g.drawImage(mazeFlashing(mazeNumber).animate(), x, y);
		} else {
			Rectangle2D fullMazeRegion = new Rectangle2D(0, 248 * index, 226, 248);
			g.drawImage(sheet, fullMazeRegion.getMinX(), fullMazeRegion.getMinY(), fullMazeRegion.getWidth(),
					fullMazeRegion.getHeight(), x, y, fullMazeRegion.getWidth(), fullMazeRegion.getHeight());
		}
	}

	@Override
	public void drawFoodTiles(GraphicsContext g, Stream<V2i> tiles, Predicate<V2i> eaten) {
		tiles.filter(eaten).forEach(tile -> hideTile(g, tile));
	}

	@Override
	public void drawEnergizerTiles(GraphicsContext g, Stream<V2i> energizerTiles) {
		if (energizerBlinking.animate()) {
			energizerTiles.forEach(tile -> hideTile(g, tile));
		}
	}

	@Override
	public void drawLevelCounter(GraphicsContext g, PacManGameModel game, int rightX, int y) {
		int x = rightX;
		int firstLevel = Math.max(1, game.currentLevelNumber - 6);
		for (int level = firstLevel; level <= game.currentLevelNumber; ++level) {
			byte symbol = game.levelSymbols.get(level - 1);
			// TODO how can an IndexOutOfBoundsException occur here?
			Rectangle2D region = symbols[symbol];
			g.drawImage(sheet, region.getMinX(), region.getMinY(), 16, 16, x, y, 16, 16);
			x -= t(2);
		}
	}

	@Override
	public void drawLivesCounter(GraphicsContext g, PacManGameModel game, int x, int y) {
		int maxLivesDisplayed = 5;
		int livesDisplayed = game.started ? game.lives - 1 : game.lives;
		for (int i = 0; i < Math.min(livesDisplayed, maxLivesDisplayed); ++i) {
			g.drawImage(sheet, 456 + 16, 0, 16, 16, x + t(2 * i), y, 16, 16);
		}
	}

	@Override
	public void drawScore(GraphicsContext g, PacManGameModel game, boolean titleOnly) {
		g.setFont(scoreFont);
		g.translate(0, 2);
		g.setFill(Color.WHITE);
		g.fillText("SCORE", t(1), t(1));
		g.fillText("HIGHSCORE", t(15), t(1));
		g.translate(0, 1);
		if (!titleOnly) {
			g.setFill(getMazeWallColor(game.level.mazeNumber - 1));
			g.fillText(String.format("%08d", game.score), t(1), t(2));
			g.setFill(Color.LIGHTGRAY);
			g.fillText(String.format("L%02d", game.currentLevelNumber), t(9), t(2));
			g.setFill(getMazeWallColor(game.level.mazeNumber - 1));
			g.fillText(String.format("%08d", game.highscorePoints), t(15), t(2));
			g.setFill(Color.LIGHTGRAY);
			g.fillText(String.format("L%02d", game.highscoreLevel), t(23), t(2));
		}
		g.translate(0, -3);
	}

	@Override
	public void hideTile(GraphicsContext g, V2i tile) {
		g.setFill(Color.BLACK);
		g.fillRect(tile.x * TS, tile.y * TS, TS, TS);
	}

	// draw creature sprite centered over creature collision box
	private void drawCreature(GraphicsContext g, Creature guy, Rectangle2D region) {
		if (guy.visible && region != null) {
			g.drawImage(sheet, region.getMinX(), region.getMinY(), region.getWidth(), region.getHeight(),
					guy.position.x - region.getWidth() / 2 + 4, guy.position.y - region.getHeight() / 2 + 4, region.getWidth(),
					region.getHeight());
		}
	}

	@Override
	public void drawPac(GraphicsContext g, Pac pac) {
		drawCreature(g, pac, pacSprite(pac));
	}

	@Override
	public void drawGhost(GraphicsContext g, Ghost ghost, boolean frightened) {
		drawCreature(g, ghost, ghostSprite(ghost, frightened));
	}

	@Override
	public void drawBonus(GraphicsContext g, Bonus bonus) {
		g.save();
		g.translate(0, bonusJumps.animate());
		drawCreature(g, bonus, bonusSprite(bonus));
		g.restore();
	}

	public void drawFlapAnimation(GraphicsContext g, double x, double y, String flapNumber, String sceneTitle) {
		drawRegion(g, getFlapAnim().animate(), x, y);
		g.setFill(Color.rgb(222, 222, 225));
		g.setFont(scoreFont);
		g.fillText(flapNumber, x + 20, y + 30);
		if (getFlapAnim().isRunning()) {
			g.fillText(sceneTitle, x + 40, y + 20);
		}
	}

	public void drawMrPacMan(GraphicsContext g, Pac pacMan) {
		if (pacMan.visible) {
			Animation<Rectangle2D> munching = pacManMunching().get(pacMan.dir);
			drawRegion(g, pacMan.speed > 0 ? munching.animate() : munching.frame(1), pacMan.position.x - 4,
					pacMan.position.y - 4);
		}
	}

	public void drawBirdAnim(GraphicsContext g, double x, double y) {
		birdAnim.animate();
		drawRegion(g, birdAnim.frame(), x + 4 - birdAnim.frame().getWidth() / 2, y + 4 - birdAnim.frame().getHeight() / 2);
	}

	public void drawBlueBag(GraphicsContext g, double x, double y) {
		drawRegion(g, new Rectangle2D(488, 199, 8, 8), x, y);
	}

	public void drawJunior(GraphicsContext g, double x, double y) {
		drawRegion(g, new Rectangle2D(509, 200, 8, 8), x, y);
	}

	@Override
	public Rectangle2D bonusSprite(Bonus bonus) {
		if (bonus.edibleTicksLeft > 0) {
			return symbols[bonus.symbol];
		}
		if (bonus.eatenTicksLeft > 0) {
			return bonusValues.get(bonus.points);
		}
		return null;
	}

	@Override
	public Rectangle2D pacSprite(Pac pac) {
		if (pac.dead) {
			return pacDying().hasStarted() ? pacDying().animate() : pacMunchingToDir(pac, pac.dir).frame();
		}
		if (pac.speed == 0) {
			return pacMunchingToDir(pac, pac.dir).frame(0);
		}
		if (!pac.couldMove) {
			return pacMunchingToDir(pac, pac.dir).frame(1);
		}
		return pacMunchingToDir(pac, pac.dir).animate();
	}

	@Override
	public Rectangle2D ghostSprite(Ghost ghost, boolean frightened) {
		if (ghost.bounty > 0) {
			return bountyValues.get(ghost.bounty);
		}
		if (ghost.is(DEAD) || ghost.is(ENTERING_HOUSE)) {
			return ghostReturningHomeToDir(ghost, ghost.dir).animate();
		}
		if (ghost.is(FRIGHTENED)) {
			return ghostFlashing().isRunning() ? ghostFlashing().frame() : ghostFrightenedToDir(ghost, ghost.dir).animate();
		}
		if (ghost.is(LOCKED) && frightened) {
			return ghostFrightenedToDir(ghost, ghost.dir).animate();
		}
		return ghostKickingToDir(ghost, ghost.wishDir).animate(); // Looks towards wish dir!
	}

	@Override
	public Animation<Rectangle2D> pacMunchingToDir(Pac pac, Direction dir) {
		return msPacMunching.get(ensureNotNull(dir));
	}

	@Override
	public Animation<Rectangle2D> pacDying() {
		return msPacSpinning;
	}

	@Override
	public Animation<Rectangle2D> ghostKickingToDir(Ghost ghost, Direction dir) {
		return ghostsKicking.get(ghost.id).get(ensureNotNull(dir));
	}

	@Override
	public Animation<Rectangle2D> ghostFrightenedToDir(Ghost ghost, Direction dir) {
		return ghostBlue;
	}

	@Override
	public Animation<Rectangle2D> ghostFlashing() {
		return ghostFlashing;
	}

	@Override
	public Animation<Rectangle2D> ghostReturningHomeToDir(Ghost ghost, Direction dir) {
		return ghostEyes.get(ensureNotNull(dir));
	}

	@Override
	public Animation<Image> mazeFlashing(int mazeNumber) {
		return mazesFlashing.get(mazeNumber - 1);
	}

	@Override
	public Stream<Animation<?>> mazeFlashings() {
		return mazesFlashing.stream().map(Animation.class::cast);
	}

	@Override
	public Animation<Boolean> energizerBlinking() {
		return energizerBlinking;
	}
}