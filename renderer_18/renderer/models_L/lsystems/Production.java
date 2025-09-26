package renderer.models_L.lsystems;

class Production {
    private char predecessor;
    private String successor;

    /**
      @param predecessor a character that will be changed in a production
    */
    public Production(final char predecessor) {
        this(predecessor, Character.toString(predecessor));
    }

    /**
      @param predecessor a character that will be changed in a production
      @param successor what the predecessor will be changed to in the production
    */
    public Production(final char predecessor, final String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    /**
      @param predecessor a character that will be changed in a production

      update the predecessor to a new character
    */
    public void updatePredecessor(final char predecessor) {
        this.predecessor = predecessor;
    }

    /**
      @param successor what the predecessor will be changed to in the production

      update the successor to a new String
    */
    public void updateSuccessor(final String successor) {
        this.successor = successor;
    }

    /**
      @param predecessor a character that will be changed in a production
      @param successor what the predecessor will be changed to in the production
    */
    public void updateProduction(final char predecessor, final String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

}
