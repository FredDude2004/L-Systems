package renderer.models_L.lsystems;

public class StochasticProduction {
    public Character predecessor;
    public String successor;
    public double probability;

    /**
     * @param predecessor the symbol that is rewritten
     * @param successor   the replacement string for this predecessor
     * @param probability the probability for selecting this production among
     *                    all productions with the same predecessor (in (0,1])
     */
    public StochasticProduction(final Character predecessor,
                                final String successor,
                                final double probability) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.probability = probability;
    }

    /**
     * @param predecessor a Character that will be rewritten with the successor
     *
     * Set the predecessor.
     */
    public void setPredecessor(final Character predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @param successor a String of what the predecessor will be rewritten to
     *
     * Set the successor.
     */
    public void setSuccessor(final String successor) {
        this.successor = successor;
    }

    /**
     * @param probability a double value representing the new probability
     *
     * Set just the probability.
     */
    public void setProbability(final double probability) {
        this.probability = probability;
    }

    /**
     * @param predecessor a Character that will be rewritten with the successor
     * @param successor a String of what the predecessor will be rewritten to
     * @param probability a double value representing the new probability
     *
     * Update all fields.
     */
    public void setProduction(final Character predecessor,
                              final String successor,
                              final double probability) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.probability = probability;
    }
}