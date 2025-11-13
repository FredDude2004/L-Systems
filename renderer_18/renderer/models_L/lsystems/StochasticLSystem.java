package renderer.models_L.lsystems;

import renderer.models_L.turtlegraphics.*;
import renderer.scene.*;
import renderer.pipeline.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map; 
import java.util.Random;
import java.util.Stack;

public class StochasticLSystem
{
    private String axiom;
    private final double stepSize;
    private final double delta;
    private final double xHome;
    private final double yHome;

    // Multiple productions per predecessor
    private final HashMap<Character, ArrayList<StochasticProduction>> prods = new HashMap<>();
    private Random rng = new Random();

    /* ===== Constructors ===== */

    public StochasticLSystem(final String axiom, final double stepSize, final double delta) 
    {
        this(axiom, stepSize, delta, 0.0, 0.0);
    }

    public StochasticLSystem(final String axiom, final double stepSize, final double delta,
                             final double xHome, final double yHome) 
    {
        this.axiom = axiom;
        this.stepSize = stepSize;
        this.delta = delta;
        this.xHome = xHome;
        this.yHome = yHome;
    }

    /* ===== Setup helpers ===== */

    /** Add one or more stochastic productions. */
    public final void addProduction(final StochasticProduction... arr) 
    {
        for (StochasticProduction p : arr) 
        {
            prods.computeIfAbsent(p.predecessor, k -> new ArrayList<>()).add(p);
        }
    }

    /** Optional: set a fixed seed for reproducible runs. */
    public final void setSeed(final long seed) {
        this.rng = new Random(seed);
    }

    /* ===== Core: stochastic expand ===== */

    /** Expand the axiom 'iterations' times using probabilistic production selection. */
    public void expand(final int iterations) 
    {
        for (int it = 0; it < iterations; ++it) 
        {
            final String s = this.axiom;
            final StringBuilder sb = new StringBuilder(Math.max(s.length() * 2, 32));
            for (int i = 0; i < s.length(); ++i) 
            {
                final char c = s.charAt(i);
                final ArrayList<StochasticProduction> list = prods.get(c);
                if (list == null || list.isEmpty()) 
                {
                    sb.append(c); // pass-through for terminals like + - [ ]
                } 
                else 
                {
                    sb.append(sample(list));
                }
            }
            this.axiom = sb.toString();
        }
    }

    private String sample(final ArrayList<StochasticProduction> list) {
        double r = rng.nextDouble();
        double cum = 0.0;
        for (int i = 0; i < list.size(); ++i) 
        {
            cum += list.get(i).probability;
            if (r < cum) return list.get(i).successor;
        }
        // Fallback in case of tiny rounding error
        return list.get(list.size() - 1).successor;
    }

    /* ===== Draw: reuse your existing LSystem.draw() ===== */

    public Model draw() 
    {
        // Hand the final expanded string to a deterministic LSystem and reuse its draw()
        LSystem base = new LSystem(this.axiom, this.stepSize, this.delta, this.xHome, this.yHome);
        return base.draw();
    }

    /* ===== (Optional) lightweight validation/normalization ===== */

    /** Normalize per-predecessor probabilities so they sum to 1 (if caller passed weights). */
    public final void normalizeProbabilities() 
    {
        for (Map.Entry<Character, ArrayList<StochasticProduction>> e : prods.entrySet()) 
        {
            double sum = 0.0;
            for (StochasticProduction sp : e.getValue()) sum += sp.probability;
            if (sum > 0.0) 
            {
                for (StochasticProduction sp : e.getValue()) sp.probability /= sum;
            }
        }
    }

    /** Strict check that probabilities per predecessor sum ~ 1.0 (optional). */
    public final void validateProbabilities(final double eps) 
    {
        for (Map.Entry<Character, ArrayList<StochasticProduction>> e : prods.entrySet()) 
        {
            double sum = 0.0;
            for (StochasticProduction sp : e.getValue()) sum += sp.probability;
            if (Math.abs(sum - 1.0) > eps) 
            {
                throw new IllegalArgumentException(
                    "Probabilities for '" + e.getKey() + "' sum to " + sum + " (expected ~1)");
            }
        }
    }

    /* ===== Getters (handy if you need to inspect) ===== */
    public String getAxiom() { return axiom; }
    public double getStepSize() { return stepSize; }
    public double getDelta() { return delta; }
    public double getXHome() { return xHome; }
    public double getYHome() { return yHome; }

}
