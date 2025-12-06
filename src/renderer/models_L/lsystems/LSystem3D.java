package renderer.models_L.lsystems;

import renderer.models_L.lsystems.Polygon;
import renderer.models_L.turtlegraphics.Turtle3D;
import renderer.models_L.turtlegraphics.TurtleState3D;
import renderer.scene.Model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.Color;

//TODO: Make this extend Model

public class LSystem3D {
    private String axiom;
    private final double stepSize;
    private final double delta;
    private double xHome;
    private double yHome;
    private double zHome;
    private final HashMap<Character, String> productions = new HashMap<>();
    private final ArrayList<Color> colorList = new ArrayList<>();

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
      Add a {@link Production} to this {@code LSystem3D}

      @param pArray  array of {@link Production} objects to add to this {@code LSystem3D}
    */
    public final void addColor(final Color... cArray) {
        for (Color c : cArray) {
            colorList.add(c);
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
    public Model draw() {
        Model lSystem = new Model("lSystem");
        Turtle3D turtle = new Turtle3D(lSystem, "lSystem", this.xHome, this.yHome, this.zHome);
        Stack<TurtleState3D> branchStack = new Stack<>();
        String polygon = "";
        ArrayList<String> polygons = new ArrayList<>();

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch(axiom.charAt(i)) {
                case 'F' -> turtle.forward(this.stepSize);
                case 'f' -> {
                    turtle.penUp();
                    turtle.forward(this.stepSize);
                    turtle.penDown();
                }
                case '+' -> turtle.yaw(this.delta);
                case '-' -> turtle.yaw(-this.delta);
                case '^' -> turtle.pitch(this.delta);
                case '&' -> turtle.pitch(-this.delta);
                case '\\' -> turtle.roll(this.delta);
                case '/' -> turtle.roll(-this.delta);
                case '|' -> turtle.yaw(180.0);
                case '$' -> turtle.resetAxes();
                case '[' -> {
                    double startBranchX = turtle.getXPos();
                    double startBranchY = turtle.getYPos();
                    double startBranchZ = turtle.getZPos();
                    branchStack.push(new TurtleState3D(startBranchX, startBranchY, startBranchZ, turtle.getLeft(), turtle.getHeading(), turtle.getUp()));
                }
                case ']' -> {
                    turtle.penUp();
                    TurtleState3D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY(), startOfBranch.getZ());
                    turtle.setHeading(startOfBranch.getHeading());
                    turtle.setLeft(startOfBranch.getLeft());
                    turtle.setUp(startOfBranch.getUp());
                    turtle.penDown();
                }
                case '{' -> {
                    i++;
                    while (axiom.charAt(i) != '}') {
                        polygon += axiom.charAt(i);
                        i++;
                    }
                    String name = "polygon" + polygons.size();
                    Model p = new Polygon(name, polygon, delta);

                }
                default -> {}
            }
        }

        return lSystem;
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
     Change the starting Z of the Turtle

     @param newZHome  a double that sets the Z corrdinate
     */
    public final void setZHome(final double newZHome) {
        this.zHome = newZHome;
    }
}

