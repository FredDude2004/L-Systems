package renderer.models_L.lsystems;

import renderer.models_L.lsystems.Polygon;
import renderer.models_L.turtlegraphics.Turtle3D;
import renderer.models_L.turtlegraphics.TurtleState3D;
import renderer.scene.Model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.Color;

public class LSystem3D extends Model {
    private String axiom;
    private final double stepSize;
    private final double delta;
    private double xHome;
    private double yHome;
    private double zHome;
    private final HashMap<Character, String> productions = new HashMap<>();
    public double minX, minY, maxX, maxY, minZ, maxZ; // bounding box
    private double leafScaler = 0.25; // make the lines that define a leaf 1/4 of the size of regular lines
    private int colorIdx = 0;

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distance the turtle will walk with each step forward
      @param delta       the angle that the turtle will turn, pitch or roll
    */
    public LSystem3D(final String axiom, final double stepSize, final double delta) {
        this(axiom, stepSize, delta, 0.0, 0.0, 0.0, new ArrayList<>());
    }

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distance the turtle will walk with each step forward
      @param delta       the angle that the turtle will turn, pitch or roll
      @param xHome       the X coordinate of the L-Systems Turtle
      @param yHome       the Y coordinate of the L-Systems Turtle
    */
    public LSystem3D(final String axiom, final double stepSize, final double delta, final double xHome, final double yHome) {
        this(axiom, stepSize, delta, xHome, yHome, 0.0, new ArrayList<>());
    }

    /**
      @param axiom       the starting {@link String} that the productions will expand
      @param stepSize    the distance the turtle will walk with each step forward
      @param delta       the angle that the turtle will turn, pitch or roll
      @param xHome       the X coordinate of the L-Systems Turtle
      @param yHome       the Y coordinate of the L-Systems Turtle
      @param zHome       the Z coordinate of the L-Systems Turtle
      @param productions the productionst that each character will map to
    */
    public LSystem3D(final String axiom, final double stepSize, final double delta, final double xHome, final double yHome, final double zHome, final ArrayList<Production> productions) {
        this.axiom = axiom;
        this.stepSize = stepSize;
        this.delta = delta;
        this.xHome = xHome;
        this.yHome = yHome;
        this.zHome = zHome;


        for (Production p : productions) {
            this.productions.put(p.predecessor, p.successor);
        }
    }


    /**
      Add a {@link Production} to this {@code LSystem3D}

      @param pArray  array of {@link Production} objects to add to this {@code LSystem3D}
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
      Rewrite the string using the {@code LSystem3D}'s productions

      @param iterations  the amount of expansions that will happen to the lSystem
    */
    public void expand(int iterations) {
       for (int i = 0; i < iterations; ++i) {
        StringBuilder newStr = new StringBuilder();  // reset each iteration

        for (int j = 0; j < axiom.length(); ++j) {
            char c = axiom.charAt(j);
            if (productions.containsKey(c)) {
                newStr.append(productions.get(c));  // replace F (or any matching char)
            } else {
                newStr.append(c);  // keep +, -, etc.
            }
        }

        this.axiom = newStr.toString();  // update the axiom for the next iteration
      }
    }

    /**
        Draws the {@code LSystem3D} according to these rules:

        F Move forward and draw a line.
        f Move forward without drawing a line
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
        G Move forward and draw a line. Do not record a vertex.
        . Record a vertex in the current polygon.
        } Complete a polygon.
        ~ Incorporate a predefined surface.
        ! Decrement the diameter of segments.
        ` Increment the current color index.
        % Cut off the remainder of the branch.

        Returns a {@link Model}
    */
    public void build() {
        Turtle3D turtle = new Turtle3D(this, "lSystem", this.xHome, this.yHome, this.zHome);
        Stack<TurtleState3D> branchStack = new Stack<>();
        String polygonAxiom = "";

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch(axiom.charAt(i)) {
                case 'F' -> {
                    turtle.forward(this.stepSize);
                    updateBounds(turtle.getXPos(), turtle.getYPos(), turtle.getZPos());
                }
                case 'f' -> {
                    turtle.penUp();
                    turtle.forward(this.stepSize);
                    turtle.penDown();
                }
                case '+' -> turtle.yaw(-this.delta);
                case '-' -> turtle.yaw(this.delta);
                case '^' -> turtle.pitch(this.delta);
                case '&' -> turtle.pitch(-this.delta);
                case '\\' -> turtle.roll(this.delta);
                case '/' -> turtle.roll(-this.delta);
                case '|' -> turtle.yaw(180.0);
                case '$' -> turtle.resetAxes();
                case '[' -> branchStack.push(turtle.getTurtleState());
                case ']' -> {
                    turtle.penUp();
                    TurtleState3D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY(), startOfBranch.getZ());
                    turtle.setOrientation(startOfBranch.getHeading(), startOfBranch.getLeft(), startOfBranch.getUp());
                    turtle.penDown();
                }
                case '%' -> {
                    while (axiom.charAt(i) != ']') {
                        i++;
                    }

                    turtle.penUp();
                    TurtleState3D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY(), startOfBranch.getZ());
                    turtle.setOrientation(startOfBranch.getHeading(), startOfBranch.getLeft(), startOfBranch.getUp());
                    turtle.penDown();
                }
                case '`' -> incrementColorIdx();
                case '{' -> {
                    String polygonName = "Polygon at axiom.charAt(" + i + ")";
                    polygonAxiom += '{';
                    while (axiom.charAt(++i) != '}') {
                        polygonAxiom += axiom.charAt(i);
                    }
                    polygonAxiom += '}';

                    TurtleState3D state = turtle.getTurtleState();
                    Color c = Color.black;
                    if (colorList.size() > 0)
                        c = colorList.get(colorIdx);
                    Model p = new Polygon(polygonName,
                                          polygonAxiom,
                                          delta,
                                          stepSize * leafScaler,
                                          state.buildMatrixFromTurtleState(),
                                          c);
                    super.addNestedModel(p);
                    polygonAxiom = "";
                }
                default -> {/*System.out.println("Unimplemented Character: " + axiom.charAt(i));*/ }
            }
        }
    }

    private void incrementColorIdx() {
        if (colorIdx < colorList.size() - 1)
            colorIdx++;
        else
            colorIdx = 0;
    }

    private void updateBounds(double x, double y, double z) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        minZ = Math.min(minZ, z);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        maxZ = Math.max(maxZ, z);
    }

    // public

    public final void setXHome(final double newXHome) { this.xHome = newXHome; }
    public final void setYHome(final double newYHome) { this.yHome = newYHome; }
    public final void setZHome(final double newZHome) { this.zHome = newZHome; }
    public final void setLeafScaler(final double newLeafScaler) { this.leafScaler = newLeafScaler; }
}

