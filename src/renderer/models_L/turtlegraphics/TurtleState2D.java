package renderer.models_L.turtlegraphics;

import renderer.scene.Matrix;
import renderer.scene.Vector;

public class TurtleState2D {
    private double x;
    private double y;
    private double z;
    private double heading;

    public TurtleState2D() {
        this(0, 0, 0, 0);
    }

    public TurtleState2D(final double x, final double y) {
        this(x, y, 0, 0);
    }

    public TurtleState2D(final double x, final double y, final double heading) {
        this(x, y, 0, heading);
    }

    public TurtleState2D(final double x, final double y, final double z, final double heading) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.heading = heading;
    }

    public Matrix buildMatrixFromTurtleState() {
        double rad = Math.toRadians(heading);

        double c = Math.cos(rad);
        double s = Math.sin(rad);

        // Columns of the matrix
        Vector c1 = new Vector( c,  s, 0.0, 0.0);   // x-axis direction
        Vector c2 = new Vector(-s,  c, 0.0, 0.0);   // y-axis direction
        Vector c3 = new Vector(0.0, 0.0, 1.0, 0.0);  // z-axis
        Vector c4 = new Vector(  x,   y, 0.0, 1.0);  // translation

        return Matrix.buildFromColumns(c1, c2, c3, c4);
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getZ() { return this.z; }
    public double getHeading() { return this.heading; }

    @Override
    public String toString() {
        return "Heading: " + this.heading + "\nPosition: (" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}

/*

    public static Matrix getTranslationMatrixFromTurtleState(double x, double y, double z,
                                   double pitch, double roll, double yaw)
    {
        double cx = (double)Math.cos(roll);
        double sx = (double)Math.sin(roll);

        double cy = (double)Math.cos(pitch);
        double sy = (double)Math.sin(pitch);

        double cz = (double)Math.cos(yaw);
        double sz = (double)Math.sin(yaw);

        // Yaw (Z) * Pitch (Y) * Roll (X)
        double r00 = cz*cy;
        double r01 = cz*sy*sx - sz*cx;
        double r02 = cz*sy*cx + sz*sx;

        double r10 = sz*cy;
        double r11 = sz*sy*sx + cz*cx;
        double r12 = sz*sy*cx - cz*sx;

        double r20 = -sy;
        double r21 = cy*sx;
        double r22 = cy*cx;

        Matrix M = new Matrix();

        // Rotation part
        M.m[0] = r00;  M.m[1] = r01;  M.m[2] = r02;  M.m[3] = x;
        M.m[4] = r10;  M.m[5] = r11;  M.m[6] = r12;  M.m[7] = y;
        M.m[8] = r20;  M.m[9] = r21;  M.m[10] = r22; M.m[11] = z;

        // Last row
        M.m[12] = 0;
        M.m[13] = 0;
        M.m[14] = 0;
        M.m[15] = 1;

        return M;
    }

*/
