import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.StochasticLSystem;
import renderer.models_L.lsystems.StochasticProduction;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;
import renderer.scene.Matrix;

import java.awt.*;
import java.awt.Rectangle;

public class StochasticSystems {
	public static void main(String[] args) {
		final Scene scene = new Scene("Stochastic L-Systems");
		scene.addPosition(new Position(new Model(), "p0"));
		scene.camera.viewTranslate(5, 0, 15);

		final int width = 1024;
		final int height = 1024;
		final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

		for (int i = 0; i < 100; i++) {
		    Model binTree = StochasticLSystem.binaryTree(5);
		    ModelShading.setColor(binTree, Color.black);
		    scene.getPosition(0).setModel(binTree);
		    fb.clearFB();
		    Pipeline.render(scene, fb);
		    fb.dumpFB2File(String.format("A%03d", i + 1));
		}
		
		for (int i = 0; i < 100; i++) {
		    Model fern = StochasticLSystem.fern(5);
		    ModelShading.setColor(fern, Color.black);
		    scene.getPosition(0).setModel(fern);
		    fb.clearFB();
		    Pipeline.render(scene, fb);
		    fb.dumpFB2File(String.format("B%03d", i + 1));
		}
		
		for (int i = 0; i < 100; i++) {
		    Model flower = StochasticLSystem.flower(3);
		    ModelShading.setColor(flower, Color.black);
		    scene.getPosition(0).setModel(flower);
		    fb.clearFB();
		    Pipeline.render(scene, fb);
		    fb.dumpFB2File(String.format("D%03d", i + 1));
		}
		
		for (int i = 0; i < 100; i++) {
		    Model coral = StochasticLSystem.coral(5);
		    ModelShading.setColor(coral, Color.black);
		    scene.getPosition(0).setModel(coral);
		    fb.clearFB();
		    Pipeline.render(scene, fb);
		    fb.dumpFB2File(String.format("E%03d", i + 1));
		}
		
	}
}
