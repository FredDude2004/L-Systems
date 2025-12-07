package renderer.models_L.lsystems;

import renderer.models_L.turtlegraphics.Turtle2D;
import renderer.models_L.turtlegraphics.TurtleState2D;
import renderer.scene.Model;
import renderer.scene.Vertex;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;

import java.awt.Color;
import java.awt.Rectangle;

public class LSystem2D extends Model {
    private String axiom;
    private final double stepSize;
    private final double delta;
    private double xHome;
    private double yHome;
    private final HashMap<Character, String> productions = new HashMap<>();
    private final HashMap<Character, Model> surfaces = new HashMap<>();
    private double minX, minY, maxX, maxY; // bounding box
    private double leafScaler = 0.25; // make the lines that define a leaf 1/4 of the size of regular lines
    private int colorIdx = 0;

    /**
     * @param axiom    the starting {@link String} that the productions will expand
     * @param stepSize the distance the turtle will walk with each step forward
     * @param delta    the angle that the turtle will turn, pitch or roll
     */
    public LSystem2D(final String axiom, final double stepSize, final double delta) {
        this("L-System", axiom, stepSize, delta, 0.0, 0.0, new ArrayList<>());
    }

    /**
     * @param axiom    the starting {@link String} that the productions will expand
     * @param stepSize the distance the turtle will walk with each step forward
     * @param delta    the angle that the turtle will turn, pitch or roll
     * @param xHome    the X coordinate of the L-Systems Turtle
     * @param yHome    the Y coordinate of the L-Systems Turtle
     */
    public LSystem2D(final String axiom,
                     final double stepSize,
                     final double delta,
                     final double xHome,
                     final double yHome)
    {
        this("L-System", axiom, stepSize, delta, xHome, yHome, new ArrayList<>());
    }

    /**
     * @param axiom       the starting {@link String} that the productions will
     *                    expand
     * @param stepSize    the distance the turtle will walk with each step forward
     * @param delta       the angle that the turtle will turn, pitch or roll
     * @param xHome       the X coordinate of the L-Systems Turtle
     * @param yHome       the Y coordinate of the L-Systems Turtle
     * @param productions the productions that each character will map to
     */
    public LSystem2D(final String name, final String axiom, final double stepSize, final double delta, final double xHome,
            final double yHome, final ArrayList<Production> productions) {
        super(name);
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
        kochSnow.build();

        return kochSnow;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        curve.build();

        return curve;
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
        sierpinTri.build();

        return sierpinTri;
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
        tree.build();

        return tree;
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
        tree.build();

        return tree;
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
        tree.build();

        return tree;
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
        tree.build();

        return tree;
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
        tree.build();

        return tree;
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
        tree.build();

        return tree;
    }

    /**
     * Add a {@link Production} to this {@code LSystem}
     *
     * @param pArray array of {@link Production} objects to add to this
     *               {@code LSystem}
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
            StringBuilder newStr = new StringBuilder(); // reset each iteration

            for (int j = 0; j < axiom.length(); ++j) {
                char c = axiom.charAt(j);
                if (productions.containsKey(c)) {
                    newStr.append(productions.get(c)); // replace F (or any matching char)
                } else {
                    newStr.append(c); // keep +, -, etc.
                }
            }

            this.axiom = newStr.toString(); // update the axiom for the next iteration
        }
    }

    /**
     * Builds the {@code LSystem} according to these rules:
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
     * Creates a bounding box that can be retrieved with getBoundingBox()
     *
     */
    public void build() {
        Turtle2D turtle = new Turtle2D(this, this.xHome, this.yHome, 0.0);
        Stack<TurtleState2D> branchStack = new Stack<>();
        String polygonAxiom = "";

        for (int i = 0; i < this.axiom.length(); ++i) {
            switch (axiom.charAt(i)) {
                case 'F' -> {
                    turtle.forward(this.stepSize);
                    updateBounds(turtle.getXPos(), turtle.getYPos());
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
                case '[' -> branchStack.push(turtle.getTurtleState());
                case ']' -> {
                    turtle.penUp();
                    TurtleState2D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY());
                    turtle.setHeading(startOfBranch.getHeading());
                    turtle.penDown();
                }
                case '%' -> {
                    while (axiom.charAt(i) != ']') {
                        i++;
                    }

                    turtle.penUp();
                    TurtleState2D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY());
                    turtle.setHeading(startOfBranch.getHeading());
                    turtle.penDown();
                }
                case '`' -> incrementColorIdx();
                case '{' -> {
                    String polygonName = "Polygon at axiom.charAt(" + i + ")";
                    while (axiom.charAt(i) != '}') {
                        polygonAxiom += axiom.charAt(i);
                        i++;
                    }

                    TurtleState2D state = turtle.getTurtleState();
                    Color c = Color.black;
                    if (colorList.size() > 0)
                        c = colorList.get(colorIdx);
                    Model p = new Polygon(polygonName,
                                          polygonAxiom,
                                          delta,
                                          stepSize * 0.25,
                                          state.buildMatrixFromTurtleState(),
                                          c);
                    super.addNestedModel(p);
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

    public final void setXHome(final double newXHome) { this.xHome = newXHome; }
    public final void setYHome(final double newYHome) { this.yHome = newYHome; }
    public String getAxiom() { return axiom; }
    public void setAxiom(final String axiom) { this.axiom = axiom; }
    public final void setLeafScaler(final double newLeafScaler) { this.leafScaler = newLeafScaler; }
}
