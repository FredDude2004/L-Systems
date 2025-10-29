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
import java.util.Stack;

public class LSystem {
    private String axiom;
    private double stepSize;
    private double delta;
    private double xHome;
    private double yHome;
    private HashMap<Character, String> productions = new HashMap<>();

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distnace the turtle will walk with each step foreward
      @param delta       the angle that the turtle will turn, pitch or roll
    */
    public LSystem(final String axiom, final double stepSize, final double delta) {
        this(axiom, stepSize, delta, 0.0, 0.0, new ArrayList<Production>());
    }

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distnace the turtle will walk with each step foreward
      @param delta       the angle that the turtle will turn, pitch or roll
      @param xHome       the X coordinate of the L-Systems Turtle
      @param YHome       the Y coordinate of the L-Systems Turtle
    */
    public LSystem(final String axiom, final double stepSize, final double delta, final double xHome, final double yHome) {
        this(axiom, stepSize, delta, xHome, yHome, new ArrayList<Production>());
    }

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distnace the turtle will walk with each step foreward
      @param delta       the angle that the turtle will turn, pitch or roll
      @param xHome       the X coordinate of the L-Systems Turtle
      @param YHome       the Y coordinate of the L-Systems Turtle
      @param productions the productionst that each character will map to
    */
    public LSystem(final String axiom, final double stepSize, final double delta, final double xHome, final double yHome, final ArrayList<Production> productions) {
        this.axiom = axiom;
        this.stepSize = stepSize;
        this.delta = delta;
        this.xHome = xHome;
        this.yHome = yHome;

        for (Production p : productions) {
            this.productions.put(p.predecessor, p.successor);
        }
    }

    /**
      Change the starting X of the Turtle

      @param newXHome  a double that sets the X corrdinate
    */
    public final void setXHome(final double newXHome) {
        this.xHome = newXHome;
    }

    /**
      Change the starting Y of the Turtle

      @param newYHome  a double that sets the Y corrdinate
    */
    public final void setYHome(final double newYHome) {
        this.yHome = newYHome;
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

      @param iterations  the amount of expansions that will happen to the lSystem
    */
    public void expand(int iterations) {
       for (int i = 0; i < iterations; ++i) {
        String newStr = "";  // reset each iteration

        for (int j = 0; j < axiom.length(); ++j) {
            char c = axiom.charAt(j);
            if (productions.containsKey(c)) {
                newStr += productions.get(c);  // replace F (or any matching char)
            } else {
                newStr += c;  // keep +, -, etc.
            }
        }

        this.axiom = newStr;  // update the axiom for the next iteration
      }
    }

    /**
        Draws the {@code LSystem} according to these rules:

        F Move foreward and draw a line.
        f Move foreward without drawing a line
        + Turn left.
        - Turn right.
        ^ Pitch up.
        & Pitch down.
        \ Roll left.
        / Roll right.
        | Turn around.
        $ Rotate the turtle to vertical.
        [ Start a branch.
        ] Complete a branch.
        { Start a polygon.
        G Move foreward and draw a line. Do not record a vertex.
        . Record a vertex in the current polygon.
        } Complete a polygon.
        ~ Incorporate a predefined surface.
        ! Decrement the diameter of segments.
        ` Increment the current color index.
        % Cut off the remainder of the branch.

        Returns a {@link Model}
    */
    public Model draw() {
        Model lSystem = new Model("lSystem");
        Turtle turtle = new Turtle(lSystem,  this.xHome, this.yHome, -25.0);
        Stack<TurtleState> branchStack = new Stack<>();

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch(axiom.charAt(i)) {
                case 'F':
                    turtle.forward(this.stepSize);
                    break;
                case 'f':
                    turtle.penUp();
                    turtle.forward(this.stepSize);
                    turtle.penDown();
                    break;
                case '+':
                    turtle.turn(-this.delta);
                    break;
                case '-':
                    turtle.turn(this.delta);
                    break;
                case '^':
                    break;
                case '&':
                    break;
                case '\\':
                    break;
                case '/':
                    break;
                case '|':
                    turtle.turn(180.0);
                    break;
                case '$':
                    turtle.setHeading(0.0);
                    break;
                case '[':
                    double startBranchX = turtle.getXPos();
                    double startBranchY = turtle.getYPos();
                    branchStack.push(new TurtleState(startBranchX, startBranchY, -25.0, turtle.getHeading()));
                    break;
                case ']':
                    turtle.penUp();
                    TurtleState startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY());
                    turtle.setHeading(startOfBranch.getHeading());
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
                case '~':
                    break;
                case '!':
                    break;
                case '`':
                    break;
                case '%':
                    break;
                default:
                    break;
            }
        }

        return lSystem;
    }
}
