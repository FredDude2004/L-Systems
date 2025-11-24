package renderer.models_L.lsystems;

import renderer.models_L.turtlegraphics.Turtle;
import renderer.models_L.turtlegraphics.TurtleState2D;
import renderer.scene.Model;
import renderer.scene.Vertex;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.Rectangle;

public class LSystem2D {
    private String axiom;
    private final double stepSize;
    private final double delta;
    private double xHome;
    private double yHome;
    private final HashMap<Character, String> productions = new HashMap<>();

    // variables for the bounding box of the final L-System
    private double minX, minY, maxX, maxY;

    /**
     * @param axiom    the starting {@link String} that the productions will expand
     * @param stepSize the distance the turtle will walk with each step forward
     * @param delta    the angle that the turtle will turn, pitch or roll
     */
    public LSystem2D(final String axiom, final double stepSize, final double delta) {
        this(axiom, stepSize, delta, 0.0, 0.0, new ArrayList<>());
    }

    /**
     * @param axiom    the starting {@link String} that the productions will expand
     * @param stepSize the distance the turtle will walk with each step forward
     * @param delta    the angle that the turtle will turn, pitch or roll
     * @param xHome    the X coordinate of the L-Systems Turtle
     * @param yHome    the Y coordinate of the L-Systems Turtle
     */
    public LSystem2D(final String axiom, final double stepSize, final double delta, final double xHome, final double yHome) {
        this(axiom, stepSize, delta, xHome, yHome, new ArrayList<>());
    }

    /**
     * @param axiom       the starting {@link String} that the productions will expand
     * @param stepSize    the distance the turtle will walk with each step forward
     * @param delta       the angle that the turtle will turn, pitch or roll
     * @param xHome       the X coordinate of the L-Systems Turtle
     * @param yHome       the Y coordinate of the L-Systems Turtle
     * @param productions the productions that each character will map to
     */
    public LSystem2D(final String axiom, final double stepSize, final double delta, final double xHome, final double yHome, final ArrayList<Production> productions) {
        this.axiom = axiom;
        this.stepSize = stepSize;
        this.delta = delta;
        this.xHome = xHome;
        this.yHome = yHome;

        this.minX = xHome;
        this.maxX = xHome;
        this.minY = yHome;
        this.maxY = yHome;

        for (Production p : productions) {
            this.productions.put(p.predecessor, p.successor);
        }
    }

    /**
     * @param expansions an int of expansions for snowflake system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Snowflake
     * @return {@link Model} a Koch Snowflake
     */
    public static Model kochSnowflake(int expansions) {
        final LSystem2D kochSnow = new LSystem2D("F++F++F", 1.0, 60.0);
        final Production p1 = new Production('F', "F-F++F-F");

        kochSnow.addProduction(p1);
        kochSnow.expand(expansions);

        return kochSnow.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveA(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "F-F+F+FF-F-F+F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveB(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "F+FF-FF-F-F+F+FF-F-F+F+FF+FF-F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveC(int expansions) {
        final LSystem2D curve = new LSystem2D("-F", 1.0, 90.0);
        final Production p1 = new Production('F', "F+F-F-F+F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveD(int expansions) {
        final LSystem2D curve = new LSystem2D("F+F+F+F", 1.0, 90.0);
        final Production p1 = new Production('F', "F+f-FF+F+FF+Ff+FF-f+FF-F-FF-Ff-FFF");
        final Production p2 = new Production('f', "ffffff");

        curve.addProduction(p1);
        curve.addProduction(p2);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveE(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "FF-F-F-F-F-F+F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveF(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "FF-F-F-F-FF");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveG(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "FF-F+F-F-FF");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveH(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "FF-F--F-F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveI(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "F-FF--F-F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Koch Island
     * @return {@link Model} a Koch Island
     */
    public static Model kochCurveJ(int expansions) {
        final LSystem2D curve = new LSystem2D("F-F-F-F", 1.0, 90.0);
        final Production p1 = new Production('F', "F-F+F-F-F");

        curve.addProduction(p1);
        curve.expand(expansions);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for island system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Dragon Curve
     * @return {@link Model} a Dragon Curve
     */
    public static Model dragonCurve(int expansions) {
        final LSystem2D curve = new LSystem2D("A", 1.0, 90.0);
        final Production p1 = new Production('A', "A+B+");
        final Production p2 = new Production('B', "-A-B");

        curve.addProduction(p1, p2);
        curve.expand(expansions);
        String axiom = curve.getAxiom();
        axiom = axiom.replace('A', 'F').replace('B', 'F');
        curve.setAxiom(axiom);

        return curve.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Sierpinski Triangle
     * @return {@link Model} a sierpinski triangle
     */
    public static Model sierpinskiTriangle(int expansions) {
        final LSystem2D sierpinTri = new LSystem2D("B", 1.0, 60);
        final Production p1 = new Production('A', "B+A+B");
        final Production p2 = new Production('B', "A-B-A");

        sierpinTri.addProduction(p1, p2);
        sierpinTri.expand(expansions);
        String axiom = sierpinTri.getAxiom();
        axiom = axiom.replace('A', 'F').replace('B', 'F');
        sierpinTri.setAxiom(axiom);

        return sierpinTri.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Tree
     * @return {@link Model} a Tree
     */
    public static Model treeA(int expansions) {
        final LSystem2D tree = new LSystem2D("F", 1.0, 25.7);
        final Production p1 = new Production('F', "F[+F]F[-F]F");

        tree.addProduction(p1);
        tree.expand(expansions);

        return tree.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Tree
     * @return {@link Model} a Tree
     */
    public static Model treeB(int expansions) {
        final LSystem2D tree = new LSystem2D("F", 1.0, 20);
        final Production p1 = new Production('F', "F[+F]F[-F]F");

        tree.addProduction(p1);
        tree.expand(expansions);

        return tree.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Tree
     * @return {@link Model} a Tree
     */
    public static Model treeC(int expansions) {
        final LSystem2D tree = new LSystem2D("F", 1.0, 22.5);
        final Production p1 = new Production('F', "FF-[-F+F+F]+[+F-F-F]");

        tree.addProduction(p1);
        tree.expand(expansions);

        return tree.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Tree
     * @return {@link Model} a Tree
     */
    public static Model treeD(int expansions) {
        final LSystem2D tree = new LSystem2D("X", 1.0, 20);
        final Production p1 = new Production('X', "F[+X]F[-X]+X");
        final Production p2 = new Production('F', "FF");

        tree.addProduction(p1, p2);
        tree.expand(expansions);

        return tree.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Tree
     * @return {@link Model} a Tree
     */
    public static Model treeE(int expansions) {
        final LSystem2D tree = new LSystem2D("X", 1.0, 25.7);
        final Production p1 = new Production('X', "F[+X][-X]FX");
        final Production p2 = new Production('F', "FF");

        tree.addProduction(p1, p2);
        tree.expand(expansions);

        return tree.draw();
    }

    /**
     * @param expansions an int of expansions for the triangle system to do
     *                   a static factory method that creates a {@link LSystem2D}
     *                   of a Tree
     * @return {@link Model} a Tree
     */
    public static Model treeF(int expansions) {
        final LSystem2D tree = new LSystem2D("X", 1.0, 25.7);
        final Production p1 = new Production('X', "F-[[X]+X]+F[+FX]-X");
        final Production p2 = new Production('F', "FF");

        tree.addProduction(p1, p2);
        tree.expand(expansions);

        return tree.draw();
    }

    /**
     * Change the starting X of the Turtle
     *
     * @param newXHome a double that sets the X corrdinate
     */
    public final void setXHome(final double newXHome) {
        this.xHome = newXHome;
    }

    /**
     * Change the starting Y of the Turtle
     *
     * @param newYHome a double that sets the Y corrdinate
     */
    public final void setYHome(final double newYHome) {
        this.yHome = newYHome;
    }

    /**
     * Add a {@link Production} to this {@code LSystem}
     *
     * @param pArray array of {@link Production} objects to add to this {@code LSystem}
     */
    public final void addProduction(final Production... pArray) {
        for (Production p : pArray) {
            this.productions.put(p.predecessor, p.successor);
        }
    }

    /**
     * Update a given production
     *
     * @param p1 the {@link Production} that will be updated
     * @param p2 the new {@link Production} to update it to
     */
    public final void updateProduction(final Production p1, final Production p2) {
        this.productions.put(p1.predecessor, p2.successor);
    }

    /**
     * Rewrite the string using the {@code LSystem}'s productions
     *
     * @param iterations the amount of expansions that will happen to the lSystem
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
     * Draws the {@code LSystem} according to these rules:
     *
     * <br>
     * 'F' Move forward and draw a line. <br>
     * 'f' Move forward without drawing a line <br>
     * '+' Turn left. <br>
     * '-' Turn right. <br>
     * '|' Turn around. <br>
     * '$' Rotate the turtle to vertical. <br>
     * '[' Start a branch. <br>
     * ']' Complete a branch. <br>
     * <br>
     *
     * saves the minX, maxX, minY and maxY to position the model
     * in Model space to be completely visible after rendering.
     * Returns a {@link Model}
     */
    public Model draw() {
        Model lSystem = new Model("lSystem");
        Turtle turtle = new Turtle(lSystem, this.xHome, this.yHome, 0.0);
        Stack<TurtleState2D> branchStack = new Stack<>();

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch (axiom.charAt(i)) {
                case 'F' -> {
                    turtle.forward(this.stepSize);
                    updateBounds(turtle.getXPos(),  turtle.getYPos());
                }
                case 'f' -> {
                    turtle.penUp();
                    turtle.forward(this.stepSize);
                    turtle.penDown();
                }
                case '+' -> turtle.turn(-this.delta);
                case '-' -> turtle.turn(this.delta);
                case '|' -> turtle.turn(180.0);
                case '$' -> turtle.setHeading(0.0);
                case '[' -> {
                    double startBranchX = turtle.getXPos();
                    double startBranchY = turtle.getYPos();
                    branchStack.push(new TurtleState2D(startBranchX, startBranchY, 0.0, turtle.getHeading()));
                }
                case ']' -> {
                    turtle.penUp();
                    TurtleState2D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY());
                    turtle.setHeading(startOfBranch.getHeading());
                    turtle.penDown();
                }
                default -> {}
            }
        }

        return lSystem;
    }

    private void updateBounds(double x, double y) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
    }

    public Rectangle getBoundingBox() {
	int width = (int) Math.round(maxX - minX);
	int height = (int) Math.round(maxY - minY);
	int x = (int) Math.round(minX);
	int y = (int) Math.round(minY);
	return new Rectangle(x, y, width, height);
    }

    public String getAxiom() {
        return axiom;
    }

    public void setAxiom(final String axiom) {
        this.axiom = axiom;
    }
}
