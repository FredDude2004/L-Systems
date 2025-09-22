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
    String axiom = "F-F-F-F";
    int delta = 90;

    void expand() {
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
        System.out.println("Hello World!");
    }
}
