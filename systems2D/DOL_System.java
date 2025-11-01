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
import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DOL_System {
    private static final Logger logger = Logger.getLogger(DOL_System.class.getName());

    public static void initLogger() {
        try {
            FileHandler fileHandler = new FileHandler("DOL_System.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up file logger", e);
        }
    }

    public static String axiom = "F-F-F-F";
    public static final double l = 0.1;
    public static final int delta = 90;

    public static void expand() {
        String newStr = "";
        // expand the string using the predefined productions

        for (int i = 0; i < axiom.length(); ++i) {
            switch (axiom.charAt(i)) {
                case 'F': // the only production in this system
                    newStr += "F-F+F+FF-F-F+F";
                    break;
                case '+':
                    newStr += "+";
                    break;
                case '-':
                    newStr += "-";
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
        initLogger();

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        final Model turtleModel = new Model("DOL_System");
        final Turtle turtle = new Turtle(turtleModel, 0.75, -0.75, -2.0);
        final int expansions = 2;

        for (int i = 0; i < expansions; ++i) {
            expand();
            logger.info("Current axiom: " + axiom);
            logger.info("Current expansion: " + (i + 1) + "\n");
        }

        for (int i = 0; i < axiom.length(); ++i) {
            switch (axiom.charAt(i)) {
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

        ModelShading.setColor(turtleModel, Color.black);
        scene.getPosition(0).setModel(turtleModel);
        // Render
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("DOL_System.png", "png");
        double superLongVarName = 1.0;
    }
}
