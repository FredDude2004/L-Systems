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

import renderer.scene.*;
import renderer.framebuffer.*;
import renderer.pipeline.*;
import renderer.models_L.lsystems.*;
import renderer.models_L.lsystems.Production;
import renderer.scene.util.ModelShading;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class Example1 {
    public static void main(String[] args) {
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width  = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

        LSystem lSystem = new LSystem("F", 0.5, 22.5);
        Production p1 = new Production('F', "FF+[+F-F-F]-[-F+F+F]");
        lSystem.addProduction(p1);

        lSystem.expand(3);
        Model lSystemModel = lSystem.draw();

        ModelShading.setColor(lSystemModel, Color.white);
        scene.getPosition(0).setModel(lSystemModel);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Example1.ppm");
    }
}
