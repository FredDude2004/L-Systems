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
import renderer.scene.util.ModelShading;
import renderer.models_L.turtlegraphics.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;

public class SystemDOL {
    public static String axiom = "F-F-F-F";
    public static int l = 1;
    public static int delta = 90;

    public static void expand() {
        String newStr = "";
        // expand the string using the predefined productions

        for (int i = 0; i < axiom.length(); ++i) {
            switch (axiom.charAt(i)) {
                case 'F': // the only production in this system
                    newStr += "F-F+F+FF-F-F+F";
                    break;
                default:
                    break;
            }
        }

        axiom = newStr;
    }

    public static void main(String[] args) {
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width  = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        final Model turtleModel = new Model("DOL_System");
        final Turtle turtle = new Turtle(turtleModel, 0.0, 0.0, -3.0);
        final int expansions = 3;

        for (int i = 0; i < expansions; ++i) {
            for (int j = 0; j < axiom.length(); ++j) {
                switch (axiom.charAt(j)) {
                    case 'F':
                    turtle.forward(l);
                    break;
                    case '+':
                    turtle.turn(delta);
                    break;
                    case '-':
                    turtle.turn(-delta);
                    break;
                    default:
                    break;
                }
            }
            expand(); // expand the axiom
            turtle.moveTo(0.0, 0.0); // Reset turtle position

            ModelShading.setRandomColors(turtleModel);
            scene.getPosition(0).setModel(turtleModel);
            // Render
            fb.clearFB(); 
            Pipeline.render(scene, fb);
            fb.dumpFB2File("DOL_System.ppm");
        }


    }
}
