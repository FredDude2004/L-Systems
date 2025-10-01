package renderer.models_L.lsystems;

public class Production {
    public Character predecessor;
    public String successor;

    /**
      @param predecessor a character that will be changed in a production
    */
    public Production(final Character predecessor) {
        this(predecessor, Character.toString(predecessor));
    }

    /**
      @param predecessor a Characteracter that will be changed in a production
      @param successor what the predecessor will be changed to in the production
    */
    public Production(final Character predecessor, final String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    /**
      @param predecessor a Characteracter that will be changed in a production

      update the predecessor to a new Characteracter
    */
    public void setPredecessor(final Character predecessor) {
        this.predecessor = predecessor;
    }

    /**
      @param successor what the predecessor will be changed to in the production

      update the successor to a new String
    */
    public void setSuccessor(final String successor) {
        this.successor = successor;
    }

    /**
      @param predecessor a Characteracter that will be changed in a production
      @param successor what the predecessor will be changed to in the production
    */
    public void setProduction(final Character predecessor, final String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

}
