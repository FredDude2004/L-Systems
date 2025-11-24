/*
 * L-Systems will start with an axiom and is rewritten with a set of rules
 *
 * This is a simple example of a DOL-System which are the simplest class of
 * L-Systems, which are deterministic and context free.
 *
 * The System in this program has these properties
 * axiom: F
 * p: F -> FF+[+F-F-F]-[-F+F+F]
 *
 * Where: F represents a line with a length of l
 *        + represents a turn in the positive direction by the angle delta
 *        - represent a turn in the negative direction by the angle delta
*/

import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.LSystem2D;
import renderer.models_L.lsystems.Production;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

import java.awt.Color;

public class TreeSystems {
    public static void main(String[] args) {
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));
        scene.camera.viewTranslate(5, 0, 15);

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

        Model treeA = LSystem2D.treeA(5);
        Model treeB = LSystem2D.treeB(5);
        Model treeC = LSystem2D.treeC(4);
        Model treeD = LSystem2D.treeD(7);
        Model treeE = LSystem2D.treeE(7);
        Model treeF = LSystem2D.treeF(5);

        ModelShading.setColor(treeA, Color.white);
        scene.getPosition(0).setModel(treeA);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("TreeA.ppm");

        ModelShading.setColor(treeB, Color.white);
        scene.getPosition(0).setModel(treeB);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("TreeB.ppm");

        ModelShading.setColor(treeC, Color.white);
        scene.getPosition(0).setModel(treeC);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("TreeC.ppm");

        ModelShading.setColor(treeD, Color.white);
        scene.getPosition(0).setModel(treeD);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("TreeD.ppm");

        ModelShading.setColor(treeE, Color.white);
        scene.getPosition(0).setModel(treeE);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("TreeE.ppm");

        ModelShading.setColor(treeF, Color.white);
        scene.getPosition(0).setModel(treeF);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("TreeF.ppm");
    }
}
