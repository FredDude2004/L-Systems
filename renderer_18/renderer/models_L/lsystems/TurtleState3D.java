package renderer.models_L.lsystems;
import renderer.scene.*;

public class TurtleState3D {
    private double x;
    private double y;
    private double z;

    private Vector left;
    private Vector heading;
    private Vector up;

    public TurtleState3D(final double x, final double y, final double z, final Vector left, final Vector heading, final Vector up) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.left = left;
        this.heading = heading;
        this.up = up;
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

    public Vector getLeft() {
        return this.left;
    }

    public Vector getHeading() {
        return this.heading;
    }

    public Vector getUp() {
        return this.up;
    }

    @Override
    public String toString() {
        String turtleState3D = "(" + this.x + ", " + this.y + ", " + this.z + ")" + "\n" +
                               "Left Vector: " + this.left+ "\n" + 
                               "Heading Vector: " + this.heading + "\n" + 
                               "Up Vector: " + this.up + "\n";
        return turtleState3D;
    }

}
