/*
 * Renderer L-Systems. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_L.lsystems;

import renderer.scene.Model;
import renderer.models_L.turtlegraphics.Turtle;
import renderer.scene.Vector;
import renderer.scene.Vertex;
import renderer.scene.PointNormalForm;

import java.util.ArrayList;

public class PolygonHelpers {

    /**
     * @param {@link Vertex} a
     * @param {@link Vertex} b
     * @param {@link Vertex} c
     * @return {@link Vector} the normal {@link Vector} of the three Points
     */
    public static Vector getNormalVectorFromPoints(final Vertex a, final Vertex b, final Vertex c) {
        Vector u = new Vector(a, b);
        Vector v = new Vector(b, c);
        return Vector.crossProduct(u, v);
    }

    /**
     * @param vertices
     * @return the area of the polygon as well as telling you the winding order.
     *         If the winding order is clockwise the area will be positive.
     *         If the winding order is counterclockwise the area will be negative.
     */
    public static double getArea(Vertex[] vertices) {
        double area = 0.0;

        for (int i = 0; i < vertices.length; i++) {
            Vertex a = vertices[i];
            Vertex b = vertices[(i + 1) % vertices.length];

            double width = b.x - a.x;
            double height = (a.y + b.y) / 2;

            area += width * height;
        }

        return area;
    }

    public static Vertex[] reverseVertexArray(Vertex[] vertices) {
        Vertex[] reversedArray = new Vertex[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            reversedArray[i] = vertices[vertices.length - 1 - i];
        }
        return reversedArray;
    }

    public static void reverseArrayInPlace(Vertex[] vertices) {
        int start = 0;
        int end = vertices.length - 1;

        while (start < end) {
            Vertex temp = vertices[start];
            vertices[start] = vertices[end];
            vertices[end] = temp;

            start++;
            end--;
        }
    }

    /**
     * Method to treat array as a circular array
     *
     * @param array an array of type <T>
     * @param idx   an integer index in the array positive, negetive or zero
     * @return <T> the item in the array at the index
     */
    public static <T> T getItem(T[] array, int idx) {
        if (idx >= array.length)
            return array[idx % array.length];
        else if (idx < 0)
            return array[idx % array.length + array.length];
        else
            return array[idx];
    }

    /**
     * Method to treat list as a circular list
     *
     * @param list a list of type <T>
     * @param idx  an integer index in the list positive, negetive or zero
     * @return <T> the item in the list at the index
     */
    public static <T> T getItem(ArrayList<T> list, int idx) {
        if (idx >= list.size())
            return list.get(idx % list.size());
        else if (idx < 0)
            return list.get(idx % list.size() + list.size());
        else
            return list.get(idx);
    }

    /**
     * Calculates the cross product of two vectors that are 2D.
     *
     * @param a the first {@link Vector}
     * @param b the first {@link Vector}
     *
     * @return double the result of the cross product
     */
    public static double crossProduct2D(Vector a, Vector b) {
        // cz = axby − aybx
        return a.x * b.y - a.y * b.x;
    }

    /**
     * Checking for a Planar Polygon (3D):
     * If the {@link Model} is a set of vertices in 3D space, we check if they lie
     * on a single plane.
     *
     * Choose Three Vertices: Select any three non-collinear vertices from the set.
     * These three points define a plane.
     * Plane Equation: Calculate the equation of the plane defined by these three
     * points.
     * Test Remaining Vertices: For every other vertex in the set, substitute its
     * coordinates into the plane equation.
     * If all other vertices satisfy the plane equation (within a small tolerance
     * for floating-point precision),
     * then the polygon is planar.
     */
    public static boolean isPlanar(Vertex[] vertices) {
        if (vertices == null)
            throw new NullPointerException();
        // Naive case: all z coordinates equal
        double z0 = vertices[0].z;
        boolean planar = true;

        for (Vertex v : vertices) {
            if (v.z != z0) {
                planar = false;
                break;
            }
        }

        if (planar)
            return true;

        // Fallback: general point–normal form test
        Vector norm = PolygonHelpers.getNormalVectorFromPoints(vertices[0], vertices[1], vertices[2]);
        Vertex p0 = vertices[0];

        // Lambda expression to create the equation of a plane in Point-Normal form
        PointNormalForm isOnPlane = v -> {
            double dx = v.x - p0.x;
            double dy = v.y - p0.y;
            double dz = v.z - p0.z;

            double dot = dx * norm.x + dy * norm.y + dz * norm.z;
            return Math.abs(dot) < 1e-6;
        };

        // Check each vertex on the equation of the plane
        for (Vertex v : vertices) {
            if (!isOnPlane.test(v))
                return false;
        }

        return true;
    }

    /**
     * @param p the {@code Vertex} to check if it is inside triangle ABC
     * @param a
     * @param b
     * @param c
     * @return boolean whether or not the {@code Vertex} p is in the triangle.
     */
    public static boolean isPointInTriangle(Vertex p, Vertex a, Vertex b, Vertex c) {
        Vector ab = new Vector(a, b);
        Vector bc = new Vector(b, c);
        Vector ca = new Vector(c, a);

        Vector ap = new Vector(a, p);
        Vector bp = new Vector(b, p);
        Vector cp = new Vector(c, p);

        double cross1 = PolygonHelpers.crossProduct2D(ab, ap);
        double cross2 = PolygonHelpers.crossProduct2D(bc, bp);
        double cross3 = PolygonHelpers.crossProduct2D(ca, cp);

        if (cross1 > 0.0 || cross2 > 0.0 || cross3 > 0.0) {
            return false;
        }

        return true;

    }

    /**
     * @param vertices
     * @return an ArrayList of {@code Vertex} of the vertices list in 2D
     *         coordinates.
     */
    public static ArrayList<Vertex> flattenTo2D(ArrayList<Vertex> vertices) {
        // The Origin
        Vertex O = vertices.get(0);

        // Translate Points
        ArrayList<Vector> translatedPoints = new ArrayList<>();
        for (Vertex p : vertices) {
            translatedPoints.add(new Vector(p.x - O.x, p.y - O.y, p.z - O.z));
        }

        // Build the first basis
        Vector a = translatedPoints.get(1);
        Vector b = translatedPoints.get(2);
        Vector cross = Vector.crossProduct(a, b);
        if (Vector.length(cross) < 1e-8) {
            throw new IllegalArgumentException("Points are collinear; cannot flatten.");
        }

        // Build the local coordinates
        Vector n = Vector.normalize(Vector.crossProduct(a, b));
        Vector u = Vector.normalize(a);
        Vector v = Vector.crossProduct(n, u);

        ArrayList<Vertex> result = new ArrayList<>();
        for (Vector t : translatedPoints) {
            double uCoord = Vector.dotProduct(t, u);
            double vCoord = Vector.dotProduct(t, v);
            result.add(new Vertex(uCoord, vCoord, 0.0));
        }

        return result;
    }

    public static boolean isSimplePolygon(Vertex[] vertices) {
        if (vertices.length < 3)
            return false;

        return true;
    }

    public static boolean isSimplePolygon(ArrayList<Vertex> vertices) {
        int n = vertices.size();

        // Must have at least 3 vertices to be a polygon
        if (n < 3)
            return false;

        // Check if any edges intersect
        for (int i = 0; i < n; i++) {
            Vertex a1 = vertices.get(i);
            Vertex a2 = vertices.get((i + 1) % n);

            for (int j = i + 1; j < n; j++) {
                Vertex b1 = vertices.get(j);
                Vertex b2 = vertices.get((j + 1) % n);

                // Skip adjacent edges
                if (i == j)
                    continue;
                if ((i + 1) % n == j)
                    continue;
                if (i == (j + 1) % n)
                    continue;

                // Check if edges intersect
                if (segmentsIntersect(a1, a2, b1, b2))
                    return false;
            }

        }

        return true;
    }

    /**
     * @param a
     * @param b
     * @param c
     * @param d
     * @return a boolean telling us whether line AB intersects with CD
     */
    private static boolean segmentsIntersect(Vertex a, Vertex b, Vertex c, Vertex d) {
        return (counterClockwise(a, c, d) != counterClockwise(b, c, d)) &&
                (counterClockwise(a, b, c) != counterClockwise(a, b, d));
    }

    /**
     * @param a
     * @param b
     * @param c
     * @return a boolean telling us whether or not we have to turn counter-clockwise
     *         after drawing a line from a-b to get to c.
     */
    private static boolean counterClockwise(Vertex a, Vertex b, Vertex c) {
        return (c.y - a.y) * (b.x - a.x) > (b.y - a.y) * (c.x - a.x);
    }

    public static boolean containsColinearEdges(Vertex[] vertices) {
        for (int i = 0; i < vertices.length; i++) {
            if (areCollinear(vertices[i],
                    vertices[(i + 1) % vertices.length],
                    vertices[(i + 2) % vertices.length])) {
                return false;
            }
        }

        return true;
    }

    public static boolean areCollinear(Vertex a, Vertex b, Vertex c) {
        double area = (b.x - a.x) * (c.y - a.y) -
                (b.y - a.y) * (c.x - a.x);

        return Math.abs(area) < 1e-9; // tolerance for floating point
    }

}

