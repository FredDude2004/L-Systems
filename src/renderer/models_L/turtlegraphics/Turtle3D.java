/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2025 rlkraft@pnw.edu kduchesn@pnw.edu ahmed208@pnw.edu
 * See LICENSE for details.
*/

/*
 * To implement a 3D turtle we need to keep track of three unit vectors. Which will
 * represent the local 3D axes of the Turtle. The turtle has three different rotations
 * that it can perform.
 *
 *      Yaw:    which is the standard turn that the turtle performs in the 2D version
 *              and is around the turtle's Z-axis.
 *
 *      Pitch:  which can be thought of as the turtle pointing his nose up or down and
 *              is around the turtle's X-axis.
 *
 *      Roll:   which is the rotation around the turtle's Y-axis and is like a plane
 *              doing a barrel-roll.
 *
 * To perform these rotations we need to keep in mind that the axes of the turtle are
 * local, not the global X, Y and Z axes. To perform a rotation around an arbitrary
 * axis in space you can use
 *
 *      Rodrigues' Equation: v' = v*cos(α) + (k x v)*sin(α) + k*(k•v)(1-cos(α))
 *
 * This formula allows us to rotate a vector v around an arbitrary vector k.
 * To avoid confusion from here on we will give the turtle's local axes a different name
 *
 *      X-axis -> LeftVector      (L)
 *      X-axis -> HeadingVector   (H)
 *      Z-axis -> UpVector        (U)
 *
 * Yaw, for example, we would need to rotate both L and H vectors around U.
 *
 *      L' = L*cos(α) + (U x L)*sin(α) + U*(U•L)(1-cos(α))
 *      H' = H*cos(α) + (U x H)*sin(α) + U*(U•H)(1-cos(α))
 *
 * For Pitch you would rotate H and U around L.
 *
 *      H' = H*cos(α) + (L x H)*sin(α) + L*(L•H)(1-cos(α))
 *      U' = U*cos(α) + (L x U)*sin(α) + L*(L•U)(1-cos(α))
 *
 * And for Roll you would rotate U and L around H.
 *
 *      U' = U*cos(α) + (H x U)*sin(α) + H*(H•U)(1-cos(α))
 *      L' = L*cos(α) + (H x L)*sin(α) + H*(H•L)(1-cos(α))
 *
 * This now allows us to tell the turtle to Yaw, Pitch and Roll with a set angle.
 * The next function we need to add to the turtle is the ability to move foreward
 * and backward. This is much simpler than the rotations. We simply multiply Heading
 * by the stepSize and add each entry to the turtle's x, y and z respectively.
 *
 *      x' = x + stepSize * H.x
 *      y' = y + stepSize * H.y
 *      z' = z + stepSize * H.z
 *
 * With all of that we now have a funcitoning 3D turtle. Of course there are the other
 * functions that it should have, like penUp and penDown, but that's much less interesting
*/

package renderer.models_L.turtlegraphics;

import renderer.models_L.turtlegraphics.TurtleState3D;
import renderer.scene.*;
import renderer.scene.Vector;
import renderer.scene.primitives.*;

/**
 * https://www.clear.rice.edu/comp360/lectures/K10188_C001.pdf
 */
public class Turtle3D {
    public final Model model;
    public final String name;

    private double xHome;
    private double yHome;
    private double zHome;

    private double xPos;
    private double yPos;
    private double zPos;

    private Vector left;
    private Vector heading;
    private Vector up;

    private boolean penDown;
    private double stepSize; // see the resize() method

    /*
     * @param model a reference to the {@link Model} that this {@code Turtle3D} is
     * builing
     *
     * @param name a {@link String} that is a name for this {@code Turtle3D}
     *
     * @param xHome the intial x-coordinate for this {@code Turtle3D}
     *
     * @param yHome the intial y-coordinate for this {@code Turtle3D}
     *
     * @param zHome the intial z-coordinate for this {@code Turtle3D}
     *
     * @throws NullPointerException if {@code model} is {@code null}
     *
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public Turtle3D(final Model model, final String name, final double xHome, final double yHome, final double zHome) {
        if (model == null)
            throw new NullPointerException("Turtle3D's Model must not be null");
        if (name == null)
            throw new NullPointerException("Turtle3D's name must not be null");

        this.model = model;
        this.name = name;

        this.xHome = xHome;
        this.yHome = yHome;
        this.zHome = zHome;

        this.xPos = xHome;
        this.yPos = yHome;
        this.zPos = zHome;

        /*
         * Local Axis Initialization: H, U, and L define the turtle's local,
         * right-handed coordinate frame. This orientation is independent of the
         * turtle's starting position (x, y, z).
         * * Convention:
         * H (Heading) = (0, 0, 1) -> Forward along the global Z-axis
         * U (Up) = (0, 1, 0) -> Up along the global Y-axis
         * L (Left) = (1, 0, 0) -> Left along the global X-axis
         * * Check: L must equal U x H to satisfy the Right-Hand Rule.
         */
        this.heading = new Vector(0.0, 0.0, -1.0);
        this.left = new Vector(1.0, 0.0, 0.0);
        this.up = new Vector(0.0, 1.0, 0.0);

        this.penDown = true;
        this.stepSize = 1;
    }

    /**
     * Check if this {@code Turtle3D}'s pen is down.
     *
     * @return true if down else false
     */
    public boolean isPenDown() {
        return penDown;
    }

    /**
     * Set this {@code Turtle3D}'s penDown variable.
     *
     * @param value value for this {@code Turtle3D}'s penDown variable
     */
    public void setPenDown(final boolean value) {
        penDown = value;
    }

    /**
     * Set this {@code Turtle3D}'s pen down.
     */
    public void penDown() {
        penDown = true;
    }

    /**
     * Lift this {@code Turtle3D}'s pen up.
     */
    public void penUp() {
        penDown = false;
    }

    public void resetAxes() {
        this.heading = new Vector(0.0, 0.0, -1.0);
        this.left = new Vector(1.0, 0.0, 0.0);
        this.up = new Vector(0.0, 1.0, 0.0);
    }

    /**
     * Perform Rodrigues' Equation rotating {@link Vector} v around {@link Vector} k
     * and returning the rotated vector
     *
     * @param v     Vector that is to be rotated
     * @param k     Vector that is rotated about
     * @param alpha the angle to rotate the vector v
     * @return vPrime the rotated {@link Vector}
     */
    private static Vector rodriguesEquation(final Vector v, final Vector k, final double alphaDegrees) {
        final Vector k_unit = k.normalize();
        final double alpha = Math.toRadians(alphaDegrees);

        // Rodrigues' Equation: v' = v*cos(α) + (k x v)*sin(α) + k*(k•v)(1-cos(α))
        Vector term1 = v.times(Math.cos(alpha));
        Vector term2 = (k_unit.crossProduct(v)).times(Math.sin(alpha));
        Vector term3 = k_unit.times(k_unit.dotProduct(v)).times(1 - Math.cos(alpha));

        return term1.plus(term2).plus(term3);
    }

    /**
     * turn the turtle's Yaw by angle alpha
     *
     * @param alpha double of the rotation angle
     */
    public void yaw(final double alpha) {
        // L' = L*cos(α) + (U x L)*sin(α) + U*(U•L)(1-cos(α))
        // H' = H*cos(α) + (U x H)*sin(α) + U*(U•H)(1-cos(α))
        Vector leftPrime = Turtle3D.rodriguesEquation(this.left, this.up, alpha % 360);
        Vector headingPrime = Turtle3D.rodriguesEquation(this.heading, this.up, alpha % 360);

        this.left = leftPrime;
        this.heading = headingPrime;
    }

    /**
     * turn the turtle's Pitch by angle alpha
     *
     * @param alpha double of the rotation angle
     */
    public void pitch(final double alpha) {
        // H' = H*cos(α) + (L x H)*sin(α) + L*(L•H)(1-cos(α))
        // U' = U*cos(α) + (L x U)*sin(α) + L*(L•U)(1-cos(α))
        Vector headingPrime = Turtle3D.rodriguesEquation(this.heading, this.left, alpha % 360);
        Vector upPrime = Turtle3D.rodriguesEquation(this.up, this.left, alpha % 360);

        this.heading = headingPrime;
        this.up = upPrime;
    }

    /**
     * turn the turtle's Roll by angle alpha
     *
     * @param alpha double of the rotation angle
     */
    public void roll(final double alpha) {
        // U' = U*cos(α) + (H x U)*sin(α) + H*(H•U)(1-cos(α))
        // L' = L*cos(α) + (H x L)*sin(α) + H*(H•L)(1-cos(α))
        Vector upPrime = Turtle3D.rodriguesEquation(this.up, this.heading, alpha % 360);
        Vector leftPrime = Turtle3D.rodriguesEquation(this.left, this.heading, alpha % 360);

        this.up = upPrime;
        this.left = leftPrime;
    }

    /**
     * calculate and return the magnitude of a given {@link Vector}
     *
     * @param v {@link Vector} to calculate the magnitude
     */
    public static double magnitude(Vector v) {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    /**
     * Turn this {@code Turtle3D} to face another {@code Turtle3D}.
     *
     * @param turtle the {@code Turtle3D} to turn towards
     */
    public void turnToFace(final Turtle3D turtle) {
        turnToFace(turtle.getXPos(), turtle.getYPos(), turtle.getZPos());
    }

    public void turnToFace(final double x, final double y, final double z) {
        final Vector targetPoint = new Vector(x, y, z);
        final Vector currentPos = new Vector(this.getXPos(), this.getYPos(), this.getZPos());

        Vector direction = targetPoint.minus(currentPos);

        // Avoid calculating rotation if the target is the turtle's current position.
        if (Turtle3D.magnitude(direction) < 1e-6) {
            return;
        }

        final Vector hPrime = direction.normalize();
        final Vector rotationAxis = this.heading.crossProduct(hPrime).normalize();
        double cosTheta = this.heading.dotProduct(hPrime);

        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
        final double angle = Math.acos(cosTheta);

        // If the angle is very small, the vectors are already aligned, so skip rotation
        if (angle < 1e-6) {
            return;
        }

        // Apply the rotation to all three local vectors
        final Vector newLeft = Turtle3D.rodriguesEquation(this.left, rotationAxis, angle);
        final Vector newUp = Turtle3D.rodriguesEquation(this.up, rotationAxis, angle);

        this.heading = hPrime;
        this.left = newLeft.normalize(); // Re-normalize to fix floating point drift
        this.up = newUp.normalize();

        // Re-verify the Right-Hand Rule after the rotation to ensure consistency
        this.left = this.up.crossProduct(this.heading).normalize();
    }

    /**
     * Move this {@code Turtle3D} to the coordinates (0, 0, 0) and give it the default
     * heading
     */
    public void home() {
        this.xPos = xHome;
        this.yPos = yHome;
        this.zPos = zHome;

        this.left = new Vector(1.0, 0.0, 0.0);
        this.up = new Vector(0.0, 1.0, 0.0);
        this.heading = new Vector(0.0, 0.0, 1.0);
    }

    /**
     * Move this {@code Turtle3D} to the given (x, y, z) location.
     *
     * @param x the x-coordinate to move this {@code Turtle3D} to
     * @param y the y-coordinate to move this {@code Turtle3D} to
     * @param z the z-coordinate to move this {@code Turtle3D} to
     */
    public void moveTo(final double x, final double y, final double z) {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
    }

    /**
     * Move this {@code Turtle3D} foward one unit in the heading direction.
     */
    public void forward() {
        forward(1);
    }

    /**
     * Move this {@code Turtle3D} backward one unit.
     */
    public void backward() {
        backward(1);
    }


    /**
     * Move this {@code Turtle3D} backward the given number of units.
     *
     * @param distance the distance to walk this {@code Turtle3D} backward
     */
    public void backward(final double distance) {
        forward(-distance);
    }

    /**
     * Move this {@code Turtle3D} forward the given number of units
     * in the heading direction. If the pen is down, then add two
     * {@link Vertex} objects and a {@link LineSegment} object to
     * the underlying {@code Turtle3D}.
     *
     * @param distance the distance to walk this {@code Turtle3D} forward in the
     *                 heading direction
     */
    public void forward(final double distance) {
        final double xOld = this.xPos;
        final double yOld = this.yPos;
        final double zOld = this.zPos;

        // change the current position
        this.xPos = xOld + (stepSize * distance * this.heading.x);
        this.yPos = yOld + (stepSize * distance * this.heading.y);
        this.zPos = zOld + (stepSize * distance * this.heading.z);

        if (penDown) {
            final int index = this.model.vertexList.size();

            final Vertex oldVertex = new Vertex(xOld, yOld, zOld);
            final Vertex newVertex = new Vertex(xPos, yPos, zPos);

            this.model.addVertex(oldVertex, newVertex);
            this.model.addPrimitive(new LineSegment(index, index + 1));
        }
    }

    /**
     * Same as the forward() method but without building a {@link LineSegment}.
     * <p>
     * This is part of "Turtle Geometry" as defined by Ronald Goldman.
     * <p>
     * https://www.clear.rice.edu/comp360/lectures/old/TurtlesGraphicL1New.pdf
     * https://people.engr.tamu.edu/schaefer/research/TurtlesforCADRevised.pdf
     * https://www.routledge.com/An-Integrated-Introduction-to-Computer-Graphics-and-Geometric-Modeling/Goldman/p/book/9781138381476
     *
     * @param distance the distance to walk this {@code Turtle3D} forward in the
     *                 heading direction
     */
    public void move(final double distance) {
        this.xPos = xPos + (stepSize * distance * this.heading.x);
        this.yPos = yPos + (stepSize * distance * this.heading.y);
        this.zPos = zPos + (stepSize * distance * this.heading.z);
    }

    /**
     * Change the length of the step size by the factor {@code s}.
     * <p>
     * This is part of "Turtle Geometry" as defined by Ronald Goldman.
     *
     * @param s scaling factor for the new {@code stepSize}
     */
    public void resize(final double s) {
        stepSize = s * stepSize;
    }

    /**
     * get the xPos, yPos, zPos, left {@link Vector}, heading {@link Vector} and up {@link Vector}
     * encapsulated in a {@link TurtleState3D} Object.
     *
     * @return TurtleState3D
     */
    public TurtleState3D getTurtleState() {
        return new TurtleState3D(xPos, yPos, zPos, left, heading, up);
    }

    /**
     * Set each of the local axis {@link Vector}s
     *
     * @param heading
     * @param left
     * @param up
     */
    public void setOrientation(Vector heading, Vector left, Vector up) {
        this.heading = heading;
        this.left = left;
        this.up = up;
    }

    public double getXPos() { return this.xPos; }
    public double getYPos() { return this.yPos; }
    public double getZPos() { return this.zPos; }
    public Vector getLeft() { return this.left; }
    public Vector getHeading() { return this.heading; }
    public Vector getUp() { return this.up; }
    public void setLeft(Vector left) { this.left = left; }
    public void setHeading(Vector heading) { this.heading = heading; }
    public void setUp(Vector up) { this.up = up; }

    /**
     * For debugging.
     *
     * @return {@link String} representation of this {@code Turtle3D} object
     */
    @Override
    public String toString() {
        String result = "";
        result += "Turtle3D: " + this.name + "\n";
        result += "origin: (" + this.xPos + ", " + yPos + ", " + zPos + ")\n";
        result += model.toString() + "\n";
        return result;
    }
}// Turtle3D
