import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.LSystem2D;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;
import renderer.scene.Matrix;

import java.awt.*;
import java.awt.Rectangle;

public class SierpinskiTriangle {
	public static void main(String[] args) {
		final Scene scene = new Scene("Sierpinski Triangle");
		scene.addPosition(new Position(new Model(), "p0"));

		final int width = 1024;
		final int height = 1024;
		final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

		Model sierpinskiTriangle = LSystem2D.sierpinskiTriangle(8);
		Rectangle r = sierpinskiTriangle.getBoundingBox();

		ModelShading.setColor(sierpinskiTriangle, Color.black);
		scene.setPosition(0, new Position(sierpinskiTriangle,
				Matrix.translate(-(r.width / 2),
						-(r.height / 2),
						(-width / 2))));
		fb.clearFB();
		Pipeline.render(scene, fb);
		fb.dumpFB2File("SierpinskiTriangle.ppm");
	}
}
