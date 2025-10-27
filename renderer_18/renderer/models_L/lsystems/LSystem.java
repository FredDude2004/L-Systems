package renderer.models_L.lsystems;

import renderer.models_L.lsystems.*;
import renderer.models_L.turtlegraphics.*;
import renderer.scene.util.ModelShading;
import renderer.framebuffer.*;
import renderer.scene.*;
import renderer.pipeline.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.ArrayList;

public class LSystem {
    private String axiom;
    private double stepSize;
    private double delta;
    private HashMap<Character, String> productions = new HashMap<>();

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distnace the turtle will walk with each step foreward
      @param delta       the angle that the turtle will turn, pitch or roll
    */
    public LSystem(final String axiom, final double stepSize, final double delta) {
        this(axiom, stepSize, delta, new ArrayList<Production>());
    }

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

      @param iterations   the amount of expansions that will happen to the lSystem
    */
    public void expand(int iterations) 
    {
       //String newStr = "";  // reset each iteration
       for (int i = 0; i < iterations; ++i) 
       {
        String newStr = "";  // reset each iteration
        for (int j = 0; j < axiom.length(); ++j) 
        {
            char c = axiom.charAt(j);
            if (productions.containsKey(c)) 
            {
                newStr += productions.get(c);  // replace F (or any matching char)
            } 
            else 
            {
                newStr += c;  // keep +, -, etc.
            }
        }
        axiom = newStr;  // update the axiom for the next iteration
        System.out.println("Iteration " + (i + 1) + " length: " + axiom.length());
    }
}

    /**
      Draws the current l-system to a Model and returns it
    */
    public Model draw() {
        Model lSystem = new Model("lSystem");
        Turtle turtle = new Turtle(lSystem, "lSystem", 0.0, 0.0, -25.0);
        double startBranchX = 0.0;
        double startBranchY = 0.0;

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch(axiom.charAt(i)) {
                case 'F':                                   // move foreward by this.stepSize
                    turtle.forward(this.stepSize);
                    break;
                case 'f':                                   // move forward without drawing a line
                    turtle.penUp();
                    turtle.forward(this.stepSize);
                    turtle.penDown();
                    break;
                case '+':                                   // turn the turtle in the positive direction (counter-clokwise)
                    turtle.turn(-this.delta);
                    break;
                case '-':                                   // turn the turtle in the negative direction (clockwise)
                    turtle.turn(this.delta);
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
                    turtle.turn(180.0);
                    break;
                case '$':                                   // Rotate the turtle to vertical
                    turtle.setHeading(0.0);
                    break;
                case '[':                                   // start a branch
                    startBranchX = turtle.getXPos();
                    startBranchY = turtle.getYPos();
                    break;
                case ']':                                   // move turtle back to start of branch
                    turtle.penUp();
                    turtle.moveTo(startBranchX, startBranchY);
                    turtle.penDown();
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

        return lSystem;
    }
}
