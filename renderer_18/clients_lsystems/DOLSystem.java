/*
 * L-Systems will start with an axiom and is rewritten with a set of rules
 *
 * This is a simple example of a DOL-System which are the simplest class of
 * L-Systems, which are deterministic and context free.
 *
 * The System in this program has these properties
 * axiom: F-F-F-F
 * p: F -> F-F+F+FF-F-F+F
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
import java.io.IOException;
import java.util.ArrayList;

public class DOLSystem {
    public static void main(String[] args) {
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

        LSystem2D lSystem = new LSystem2D("F-F-F-F", 1.0, 90.0);
        Production productionOne = new Production('F', "F-F+F+FF-F-F+F");
        lSystem.addProduction(productionOne);

        lSystem.expand(2);
       // lSystem.expand(1);
        Model lSystemModel = lSystem.draw();

        ModelShading.setColor(lSystemModel, Color.white);
        scene.getPosition(0).setModel(lSystemModel);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("DOL_System.ppm");
    }
}
