package renderer.models_L.lsystems;

public class TurtleState {
    private double x;
    private double y;
    private double z;
    private double heading;

    public TurtleState() {
        this(0, 0, 0, 0);
    }

    public TurtleState(final double x, final double y) {
        this(x, y, 0, 0);
    }

    public TurtleState(final double x, final double y, final double z, final double heading) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.heading = heading;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getHeading() {
        return this.heading;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}
