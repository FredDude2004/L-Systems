public class Coordinate {
    private double x;
    private double y;
    private double z;

    public Coordinate() {
        Coordinate(0, 0, 0);
    }

    public Coordinate(final double x, final double y) {
        Coordinate(x, y, 0);
    }

    public Coordinate(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}
