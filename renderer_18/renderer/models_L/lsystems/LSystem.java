package renderer.models_L.lsystems;

import renderer.models_L.lsystem.*;
import renderer.models_L.turtlegraphics.*;
import renderer.scene.util.ModelShading;
import renderer.framebuffer.*;
import renderer.scene.*;
import renderer.pipeline.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.ArrayList;

public class LSystem {
    private Model lSystem;
    private String axiom;
    private int stepSize;
    private int delta;
    private HashMap<char, String> productions;

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distnace the turtle will walk with each step foreward
      @param delta       the angle that the turtle will turn, pitch or roll
      @param productions the productionst that each character will map to
    */
    public LSystem(final String axiom, final double stepSize, final double delta, final ArrayList<Production> productions) {
        this.axiom = axiom;
        this.stepSize = stepSize;
        this.delta = delta;

        for (Production p : productions) {
            this.productions.put(p.predecessor, p.successor);
        }
    }

    /**
      Add a {@link Production} to this {@code LSystem}

      @param pArray  array of {@link Production} objects to add to this {@code LSystem}
    */
    public final void addProduction(final Production... pArray) {
        for (Production p : pArray) {
            this.productions.put(p.predecessor, p.successor);
        }
    }

    /**
      Update a given production

      @param p1  the {@link Production} that will be updated
      @param p2  the new {@link Production} to update it to
    */
    public final void updateProduction(final Production p1, final Production p2) {
        this.productions.put(p1.predecessor, p2.successor);
    }

    /**
      Rewrite the string using the {@code LSystem}'s productions
    */
    public void expand() {
        String newStr = "";

        for (int i = 0; i < this.axiom.length(); ++i) {
            if (this.productions.containsKey(axiom.charAt(i))) {
                newStr += this.productions.containsKey(axiom.charAt(i));
            } else {
                newStr += axiom.charAt(i);
            }
        }

        this.axiom = newStr;
    }

    public void draw() {
        double branchX;
        double branchY;

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch(axiom.charAt(i)) {
                case 'F':                                   // move foreward by this.stepSize
                    this.turtle.forward(this.stepSize);
                    break;
                case 'f':                                   // move forward without drawing a line
                    this.turtle.penUp();
                    this.turtle.forward(this.stepSize);
                    this.turtle.penDown();
                    break;
                case '+':                                   // turn the turtle in the positive direction (counter-clokwise)
                    this.turtle.turn(-this.delta);
                    break;
                case '-':                                   // turn the turtle in the negative direction (clockwise)
                    this.turtle.turn(this.delta);
                    break;
                case '^':                                   // pitch up
                    break;
                case '&':                                   // pitch down
                    break;
                case '\\':                                  // roll left
                    break;
                case '/':                                   // roll right
                    break;
                case '|':                                   // turn around
                    this.turtle.turn(180.0);
                    break;
                case '$':                                   // Rotate the turtle to vertical
                    this.turtle.setHeading(0.0);
                    break;
                case '[':                                   // start a branch
                    branchX = this.turtle.getX();
                    branchY = this.turtle.getY();
                    break;
                case ']':                                   // move turtle back to start of branch
                    this.turtle.penUp();
                    this.turtle.moveTo(branchX, branchY);
                    this.turtle.penDown();
                    break;
                case '{':
                    break;
                case 'G':
                    break;
                case '.':
                    break;
                case '}':
                    break;
                case '~':                                   // encorporate a predefined surface
                    break;
                case '!':                                   // decrement the diameter of a segment
                    break;
                case '`':                                   // increment the current color index
                    break;
                case '%':                                   // cut off the remainder of the branch
                    break;
                default:
                    break;
            }
        }
    }
}
