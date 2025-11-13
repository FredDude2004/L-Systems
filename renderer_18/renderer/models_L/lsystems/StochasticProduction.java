package renderer.models_L.lsystems;

public class StochasticProduction 
{
   public Character predecessor;
    public String successor;
    public double probability; // in (0, 1]; caller ensures sums per predecessor ~= 1

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

    /** Update just the predecessor. */
    public void setPredecessor(final Character predecessor) {
        this.predecessor = predecessor;
    }

    /** Update just the successor. */
    public void setSuccessor(final String successor) {
        this.successor = successor;
    }

    /** Update just the probability. */
    public void setProbability(final double probability) {
        this.probability = probability;
    }

    /** Update all fields. */
    public void setProduction(final Character predecessor,
                              final String successor,
                              final double probability) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.probability = probability;
    } 
}
