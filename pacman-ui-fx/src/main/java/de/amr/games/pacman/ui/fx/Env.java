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
package de.amr.games.pacman.ui.fx;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import de.amr.games.pacman.ui.fx.util.RandomEntrySelector;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.DrawMode;

/**
 * Globally available properties.
 * 
 * @author Armin Reichert
 */
public class Env {

	public static final String APP_ICON_PATH = "/pacman/graphics/pacman.png";

	// UI messages

	public static final ResourceBundle MESSAGES = ResourceBundle.getBundle("/common/messages");

	public static String message(String pattern, Object... args) {
		return MessageFormat.format(MESSAGES.getString(pattern), args);
	}

	// Trash talk

	public static final RandomEntrySelector<String> CHEAT_TALK = load("/common/cheating_talk");
	public static final RandomEntrySelector<String> LEVEL_COMPLETE_TALK = load("/common/level_complete_talk");
	public static final RandomEntrySelector<String> GAME_OVER_TALK = load("/common/game_over_talk");

	private static RandomEntrySelector<String> load(String bundleName) {
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
		return new RandomEntrySelector<>(bundle.keySet().stream().sorted().map(bundle::getString).toArray(String[]::new));
	}

	// Global properties

	public static final BooleanProperty $axesVisible = new SimpleBooleanProperty(false);
	public static final ObjectProperty<DrawMode> $drawMode3D = new SimpleObjectProperty<DrawMode>(DrawMode.FILL);
	public static final IntegerProperty $fps = new SimpleIntegerProperty();
	public static final BooleanProperty $isHUDVisible = new SimpleBooleanProperty(false);
	public static final BooleanProperty $isTimeMeasured = new SimpleBooleanProperty(false);
	public static final IntegerProperty $mazeResolution = new SimpleIntegerProperty(8);
	public static final DoubleProperty $mazeWallHeight = new SimpleDoubleProperty(3.5);
	public static final IntegerProperty $totalTicks = new SimpleIntegerProperty();
	public static final IntegerProperty $slowDown = new SimpleIntegerProperty(1);
	public static final BooleanProperty $paused = new SimpleBooleanProperty(false);
	public static final BooleanProperty $use3DScenes = new SimpleBooleanProperty(true);
}