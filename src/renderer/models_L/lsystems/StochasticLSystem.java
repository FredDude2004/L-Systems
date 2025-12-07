package renderer.models_L.lsystems;

import renderer.scene.Model;
import renderer.models_L.turtlegraphics.Turtle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StochasticLSystem {
    private String axiom;
    private final double stepSize;
    private final double delta;
    private final double xHome;
    private final double yHome;

    private final HashMap<Character, ArrayList<StochasticProduction>> prods = new HashMap<>();
    private final Random rng = new Random();

    /**
     * @param axiom a {@link String} which will be rewritten with the systems productions
     * @param stepSize a double of how far the {@link Turtle} will step when drawing the system
     * @param delta a double representing an angle in degrees that the {@link Turtle} will turn
     *
     * Create a StochasticLSystem with the above parameters and xHome and yHome set to (0,0).
     */
    public StochasticLSystem(final String axiom, final double stepSize, final double delta) {
        this(axiom, stepSize, delta, 0.0, 0.0);
    }

    /**
     * @param axiom a {@link String} which will be rewritten with the systems productions
     * @param stepSize a double of how far the {@link Turtle} will step when drawing the system
     * @param delta a double representing an angle in degrees that the {@link Turtle} will turn
     * @param xHome a double representing the {@link Turtle}'s starting x-coordinate
     * @param yHome a double representing the {@link Turtle}'s home/starting y-coordinate
     *
     * Create a StochasticLSystem with the above parameters.
     */
    public StochasticLSystem(final String axiom, final double stepSize, final double delta,
                             final double xHome, final double yHome) {
        this.axiom = axiom;
        this.stepSize = stepSize;
        this.delta = delta;
        this.xHome = xHome;
        this.yHome = yHome;
    }

    /**
     * @param arr an array of {@code StochasticProduction}'s to add to the L-System.
     *
     * Add one or more stochastic productions.
     */
    public final void addProduction(final StochasticProduction... arr) {
        for (StochasticProduction p : arr) {
            prods.computeIfAbsent(p.predecessor, k -> new ArrayList<>()).add(p);
        }
    }

    /**
     * @param iterations an integer representing the number of times the L-System should be expanded.
     *
     * Expand the axiom 'iterations' times using probabilistic production selection.
     */
    public void expand(final int iterations) {
        for (int it = 0; it < iterations; ++it) {
            final String s = this.axiom;
            final StringBuilder sb = new StringBuilder(Math.max(s.length() * 2, 32));
            for (int i = 0; i < s.length(); ++i) {
                final char c = s.charAt(i);
                final ArrayList<StochasticProduction> list = prods.get(c);
                if (list == null || list.isEmpty()) {
                    sb.append(c); // pass-through for terminals like + - [ ]
                } else {
                    sb.append(randomSample(list));
                }
            }
            this.axiom = sb.toString();
        }
    }

    /**
     * @param list {@link ArrayList} of {@link StochasticProduction}'s
     * Generates a random number and uses that number to determine what the
     * successor of the production will be.
     *
     * @return {@link String} the successor of the production
     */
    private String randomSample(final ArrayList<StochasticProduction> list) {
        double r = rng.nextDouble();
        double cum = 0.0;
        for (StochasticProduction sp : list) {
            cum += sp.probability;
            if (r < cum) return sp.successor;
        }
        // Fallback in case of tiny rounding error
        return list.get(list.size() - 1).successor;
    }

    /**
     * Draws the {@link LSystem2D} according to these rules:
     *
     * <br>
     * 'F' Move forward and draw a line. <br>
     * 'f' Move forward without drawing a line <br>
     * '+' Turn left. <br>
     * '-' Turn right. <br>
     * '^' Pitch up. <br>
     * '&' Pitch down. <br>
     * '\' Roll left. <br>
     * '/' Roll right. <br>
     * '|' Turn around. <br>
     * '$' Rotate the turtle to vertical. <br>
     * '[' Start a branch. <br>
     * ']' Complete a branch. <br>
     * '{' Start a polygon. <br>
     * 'G' Move forward and draw a line. Do not record a vertex. <br>
     * '.' Record a vertex in the current polygon. <br>
     * '}' Complete a polygon. <br>
     * '~' Incorporate a predefined surface. <br>
     * '!' Decrement the diameter of segments. <br>
     * '`' Increment the current color index. <br>
     * '%' Cut off the remainder of the branch. <br>
     * <br>
     * <p>
     * Returns a {@link Model}
     */
    public Model draw() {
        // Hand the final expanded string to a deterministic LSystem and reuse its draw()
        LSystem2D base = new LSystem2D(this.axiom, this.stepSize, this.delta, this.xHome, this.yHome);
        return base.build();
    }

    /**
     * Normalize per-predecessor probabilities so they sum to 1.
     */
    public final void normalizeProbabilities() {
        for (Map.Entry<Character, ArrayList<StochasticProduction>> e : prods.entrySet()) {
            double sum = 0.0;
            for (StochasticProduction sp : e.getValue()) sum += sp.probability;
            if (sum > 0.0) {
                for (StochasticProduction sp : e.getValue()) sp.probability /= sum;
            }
        }
    }

    /**
     * Strict check that probabilities per predecessor sum ~ 1.0.
     */
    public final void validateProbabilities(final double eps) {
        for (Map.Entry<Character, ArrayList<StochasticProduction>> e : prods.entrySet()) {
            double sum = 0.0;
            for (StochasticProduction sp : e.getValue()) sum += sp.probability;
            if (Math.abs(sum - 1.0) > eps) {
                throw new IllegalArgumentException(
                        "Probabilities for '" + e.getKey() + "' sum to " + sum + " (expected ~1)");
            }
        }
    }

    public static Model binaryTree(int expansions) {
	StochasticLSystem binTree = new StochasticLSystem("F", 0.25, 25);
	StochasticProduction binTreeP1 = new StochasticProduction('F', "F[+F]F", 0.55);
	StochasticProduction binTreeP2 = new StochasticProduction('F', "F[-F]F", 0.55);
	StochasticProduction binTreeP3 = new StochasticProduction('F', "F[+F]F[-F]F", 0.55);

	binTree.addProduction(binTreeP1, binTreeP2, binTreeP3);
	binTree.expand(expansions);
	return binTree.draw();
    }

    public static Model fern(int expansions) {
	StochasticLSystem fern = new StochasticLSystem("X", 0.5, 22.5);
	StochasticProduction fernP1 = new StochasticProduction('X', "F-[[X]+X]+F[+FX]-X", 0.6);
	StochasticProduction fernP2 = new StochasticProduction('X', "F[+X]-X", 0.4);
	StochasticProduction fernP3 = new StochasticProduction('X', "FF");

	fern.addProduction(fernP1, fernP2, fernP3);
	fern.expand(expansions);
	return fern.draw();
    }

    public static Model flower(int expansions) {
	StochasticLSystem flower = new StochasticLSystem("X", 0.5, 15.0);
	StochasticProduction flowP1 = new StochasticProduction('X', "F[+X]F[-X]+X", 0.5);
	StochasticProduction flowP2 = new StochasticProduction('X', "F[-X]+X", 0.3);
	StochasticProduction flowP3 = new StochasticProduction('X', "Ff", 0.2);

	flower.addProduction(flowP1, flowP2, flowP3);
	flower.expand(expansions);
	return flower.draw();
    }


    public static Model coral(int expansions) {
	StochasticLSystem coral = new StochasticLSystem("F", 0.75, 20.0);
	StochasticProduction coralP1 = new StochasticProduction('F', "F[+F]F", 0.5);
	StochasticProduction coralP2 = new StochasticProduction('F', "F[-F]F", 0.25);
	StochasticProduction coralP3 = new StochasticProduction('F', "F[+F][-F]", 0.25);

	coral.addProduction(coralP1, coralP2, coralP3);
	coral.expand(expansions);
	return coral.draw();
    }

    public String getAxiom() {
        return axiom;
    }

    public double getStepSize() {
        return stepSize;
    }

    public double getDelta() {
        return delta;
    }

    public double getXHome() {
        return xHome;
    }

    public double getYHome() {
        return yHome;
    }

}
