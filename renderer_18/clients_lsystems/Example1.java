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

import renderer.scene.*;
import renderer.framebuffer.*;
import renderer.pipeline.*;
import renderer.models_L.lsystems.*;
import renderer.models_L.lsystems.Production;
import renderer.scene.util.ModelShading;
import renderer.scene.util.Vector3D;

import java.awt.Color;

public class Example1 {
    public static void main(String[] args) {
        // --- Scene / framebuffer ---
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width  = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

        // --- L-system definition (axiom, step length, angleDeg) ---
        // step length ~0.28-0.35 works well at 1024x1024
        final double step = 0.30;
        final double angleDeg = 22.5;

        LSystem lsys = new LSystem("F", step, angleDeg);

        // F -> FF+[+F-F-F]-[-F+F+F]
        lsys.addProduction(new Production('F', "FF+[+F-F-F]-[-F+F+F]"));

        // IMPORTANT: tell the interpreter what each symbol means
        // (Without this, '[' and ']' are ignored -> the "skinny vine" you saw.)
        lsys.setForward('F');     // draw forward
        lsys.setTurnLeft('+');    // +angle
        lsys.setTurnRight('-');   // -angle
        lsys.setPush('[');        // push state
        lsys.setPop(']');         // pop state

        // Expand enough times to get the dense bushy look
        lsys.expand(5);

        // Draw to a 2D model using the turtle interpreter
        Model model = lsys.draw();

        // Aesthetics
        ModelShading.setColor(model, new Color(220, 220, 220));
        scene.getPosition(0).setModel(model);

        // Optional: nudge into frame if your origin differs
        scene.getPosition(0).setTranslation(new Vector3D(-0.5, -1.0, 0));

        // Render
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Example1.ppm");
        System.out.println("Wrote Example1.ppm");
    }
}
