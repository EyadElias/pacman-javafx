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
package de.amr.games.pacman.ui.fx.util;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

/**
 * Shows coordinates axes (x-axis=red, y-axis=green, z-axis=blue).
 * 
 * @author Armin Reichert
 */
public class CoordinateSystem extends Group {

	public CoordinateSystem(double axisLength) {
		Sphere origin = new Sphere(1);
		origin.setMaterial(new PhongMaterial(Color.CHOCOLATE));

		Cylinder xAxis = createAxis(Color.RED.brighter(), axisLength);
		Cylinder yAxis = createAxis(Color.GREEN.brighter(), axisLength);
		Cylinder zAxis = createAxis(Color.BLUE.brighter(), axisLength / 2);

		xAxis.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
		zAxis.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

		getChildren().addAll(origin, xAxis, yAxis, zAxis);
	}

	// Cylinder height points to y-direction
	private Cylinder createAxis(Color color, double height) {
		Cylinder axis = new Cylinder(0.25, height);
		axis.setMaterial(new PhongMaterial(color));
		return axis;
	}
}