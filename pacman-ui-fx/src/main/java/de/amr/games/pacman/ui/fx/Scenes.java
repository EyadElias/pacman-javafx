package de.amr.games.pacman.ui.fx;

import de.amr.games.pacman.ui.fx._2d.rendering.mspacman.Rendering2D_MsPacMan;
import de.amr.games.pacman.ui.fx._2d.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.fx._2d.scene.common.PlayScene2D;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntermissionScene1;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntermissionScene2;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntermissionScene3;
import de.amr.games.pacman.ui.fx._2d.scene.mspacman.MsPacMan_IntroScene;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntermissionScene1;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntermissionScene2;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntermissionScene3;
import de.amr.games.pacman.ui.fx._2d.scene.pacman.PacMan_IntroScene;
import de.amr.games.pacman.ui.fx._3d.entity.GianmarcosPacManModel3D;
import de.amr.games.pacman.ui.fx._3d.scene.PlayScene3DWithAnimations;
import de.amr.games.pacman.ui.fx.sound.PacManGameSounds;
import de.amr.games.pacman.ui.fx.sound.SoundManager;

/**
 * Scenes.
 * 
 * @author Armin Reichert
 */
public class Scenes {

	public static final GameScene PAC_MAN_SCENES[][] = new GameScene[5][2];
	public static final Rendering2D_PacMan PACMAN_RENDERING = new Rendering2D_PacMan();
	public static final SoundManager PACMAN_SOUNDS = new SoundManager(PacManGameSounds::pacManSoundURL);

	static {
		//@formatter:off
		PAC_MAN_SCENES[0][0] = 
		PAC_MAN_SCENES[0][1] = new PacMan_IntroScene();
		PAC_MAN_SCENES[1][0] = 
		PAC_MAN_SCENES[1][1] = new PacMan_IntermissionScene1();
		PAC_MAN_SCENES[2][0] = 
		PAC_MAN_SCENES[2][1] = new PacMan_IntermissionScene2();
		PAC_MAN_SCENES[3][0] = 
		PAC_MAN_SCENES[3][1] = new PacMan_IntermissionScene3();
		PAC_MAN_SCENES[4][0] = new PlayScene2D(PACMAN_RENDERING, PACMAN_SOUNDS);
		PAC_MAN_SCENES[4][1] = new PlayScene3DWithAnimations(GianmarcosPacManModel3D.get(), PACMAN_SOUNDS);
		//@formatter:on
	}

	public static final GameScene MS_PACMAN_SCENES[][] = new GameScene[5][2];
	public static final Rendering2D_MsPacMan MS_PACMAN_RENDERING = new Rendering2D_MsPacMan();
	public static final SoundManager MS_PACMAN_SOUNDS = new SoundManager(PacManGameSounds::msPacManSoundURL);

	static {
		//@formatter:off
		MS_PACMAN_SCENES[0][0] = 
		MS_PACMAN_SCENES[0][1] = new MsPacMan_IntroScene();
		MS_PACMAN_SCENES[1][0] = 
		MS_PACMAN_SCENES[1][1] = new MsPacMan_IntermissionScene1();
		MS_PACMAN_SCENES[2][0] = 
		MS_PACMAN_SCENES[2][1] = new MsPacMan_IntermissionScene2();
		MS_PACMAN_SCENES[3][0] = 
		MS_PACMAN_SCENES[3][1] = new MsPacMan_IntermissionScene3();
		MS_PACMAN_SCENES[4][0] = new PlayScene2D(MS_PACMAN_RENDERING, MS_PACMAN_SOUNDS);
		MS_PACMAN_SCENES[4][1] = new PlayScene3DWithAnimations(GianmarcosPacManModel3D.get(), MS_PACMAN_SOUNDS);
		//@formatter:on
	}

}