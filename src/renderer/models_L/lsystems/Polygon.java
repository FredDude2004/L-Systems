/*
 * Renderer L-Systems. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_L.lsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;

import renderer.scene.primitives.Triangle;
import renderer.models_L.turtlegraphics.Turtle2D;
import renderer.models_L.turtlegraphics.TurtleState2D;
import renderer.models_L.lsystems.PolygonHelpers;
import renderer.scene.Vector;
import renderer.scene.Vertex;
import renderer.scene.Model;

public class Polygon extends Model {

    /**
     * Create a closed filled Color.black Polygon
     *
     * @param axiom the {@link Turtle2D} instructions to draw the polygon
     * @throws NullPointerException
     */
    public Polygon(final String axiom,
                   final double delta)
    {
        this("Polygon", axiom, delta, Color.black);
    }

    /**
     * Create a closed filled Color.black Polygon
     *
     * @param axiom the {@link Turtle2D} instructions to draw the polygon
     * @throws NullPointerException
     */
    public Polygon(final String name,
                   final String axiom,
                   final double delta)
    {
        this(name, axiom, delta, Color.black);
    }

    /**
     * Create a closed filled Polygon with colors
     *
     * @param axiom
     * @param c
     */
    public Polygon(final String name,
                   final String axiom,
                   final double delta,
                   final Color c)
    {
        this(name, axiom, 1.0, delta, c, c, c);
    }

    /**
     * Create a closed filled Polygon with colors
     *
     * @param axiom
     * @param stepSize
     * @param delta
     * @param c
     */
    public Polygon(final String name,
                   final String axiom,
                   final double stepSize,
                   final double delta,
                   final Color c)
    {
        this(name, axiom, stepSize, delta, c, c, c);
    }

    /**
     * Create a closed filled Polygon with colors
     *
     * @param axiom
     * @param delta
     * @param c1
     * @param c2
     * @param c3
     */
    public Polygon(final String name,
                   final String axiom,
                   final double delta,
                   final Color c1,
                   final Color c2,
                   final Color c3)
    {
        this(name, axiom, 1.0, delta, c1, c2, c3);
    }


    /**
     * Create a closed filled Polygon with colors
     *
     * @param axiom
     * @param stepSize
     * @param delta
     * @param c1
     * @param c2
     * @param c3
     */
    public Polygon(final String name,
                   final String axiom,
                   final double stepSize,
                   final double delta,
                   final Color c1,
                   final Color c2,
                   final Color c3)
    {
        PolygonResult res = buildPolygonWithTurtle2D(axiom, stepSize, delta, c1, c2, c3);
        super(res.polygon.vertexList,
              res.polygon.primitiveList,
              res.polygon.colorList,
              name,
              true);
    }

    /**
     * Check if a {@link Model} is a {@code Polygon} triangulate it and return a
     * filled
     * closed {@code Polygon}
     *
     * @param m {@link Model} the Model to check if is a {@code Polygon}
     */
    public Polygon(Model m) {

    }

    public record PolygonResult(
            boolean success,
            Model polygon,
            String errorMessage) {
    }

    public static PolygonResult buildPolygonWithTurtle2D(String s,
                                                         double stepSize,
                                                         double delta,
                                                         Color c1,
                                                         Color c2,
                                                         Color c3)
    {
        boolean success = true;
        Model polygon = null;
        String err = "";

        while (success) {
            polygon = build(s, stepSize, delta);
            polygon.addColor(c1, c2, c3);
            Vertex[] vertices = new Vertex[polygon.vertexList.size()];
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = polygon.vertexList.get(i);
            }

            TriangulateResult res = triangulateWithEarClipping(vertices);
            if (!res.success) {
                err = "Failed to triangulate: " + res.errorMessage + " Exited with error code 1.";
                System.out.println(err);
                System.exit(1);
                success = false;
            }



            for (int i = 0; i < res.triangles.length; i += 3) {
                int a = res.triangles[i];
                int b = res.triangles[i + 1];
                int c = res.triangles[i + 2];

                polygon.addPrimitive(
                        new Triangle(a, b, c, 0, 1, 2));
            }

            break;
        }

        return new PolygonResult(success, polygon, err);
    }

    /**
     * Draws the {@code LSystem} according to these rules:
     *
     * @param axiom    The string wiht instructions
     * @param stepSize the Step Size
     * @param delta    the angle the turtle will turn
     *
     *                 <br>
     *                 'F' Move forward and draw a line. <br>
     *                 'f' Move forward without drawing a line <br>
     *                 '+' Turn left. <br>
     *                 '-' Turn right. <br>
     *                 '|' Turn around. <br>
     *                 '$' Rotate the turtle to vertical. <br>
     *                 '[' Start a branch. <br>
     *                 ']' Complete a branch. <br>
     *                 <br>
     *
     *                 Creates a bounding box that can be retrieved with
     *                 getBoundingBox()
     *
     *                 Returns a {@link Model}
     */
    public static Model build(String axiom, double stepSize, double delta) {
        Model lSystem = new Model("lSystem");
        Turtle2D turtle = new Turtle2D(lSystem);
        Stack<TurtleState2D> branchStack = new Stack<>();

        for (int i = 0; i < axiom.length(); ++i) {
            switch (axiom.charAt(i)) {
                case 'F' -> {
                    turtle.forward(stepSize);
                }
                case 'f' -> {
                    turtle.penUp();
                    turtle.forward(stepSize);
                    turtle.penDown();
                }
                case '+' -> turtle.turn(-delta);
                case '-' -> turtle.turn(delta);
                case '|' -> turtle.turn(180.0);
                case '$' -> turtle.setHeading(0.0);
                case '[' -> {
                    double startBranchX = turtle.getXPos();
                    double startBranchY = turtle.getYPos();
                    branchStack.push(new TurtleState2D(startBranchX, startBranchY, 0.0, turtle.getHeading()));
                }
                case ']' -> {
                    turtle.penUp();
                    TurtleState2D startOfBranch = branchStack.pop();
                    turtle.moveTo(startOfBranch.getX(), startOfBranch.getY());
                    turtle.setHeading(startOfBranch.getHeading());
                    turtle.penDown();
                }
                // TODO: Implement this
                case '{' -> {
                    continue;
                }
                case 'G' -> {
                    continue;
                }
                case '.' -> {
                    turtle.recordVertex();
                }
                case '}' -> {
                    continue;
                }
                default -> {
                }
            }
        }

        return lSystem;
    }

    // TODO: Implement this
    public boolean isPolygon(Model m) {
        return true;
    }

    /**
     *
     * TriangulateResult
     *
     * @param success
     * @param triangles    an array of integer indices representing the
     *                     {@code Vertex}'s that make up each triangle three at a
     *                     time.
     * @param errorMessage
     */
    public record TriangulateResult(
            boolean success,
            int[] triangles,
            String errorMessage) {
    }

    // TODO: This might be better if it took in a list rather than an array
    /**
     * See https://youtu.be/QAdfkylpYwc?si=1CqI70Y0dsAXR102 <br>
     * and https://youtu.be/hTJFcHutls8?si=APEiC99_BxG3JDyj <br>
     * for more info
     *
     * @param vertices an array of {@link Vertex}
     *
     *                 Triangulates the {@link Model} using the Ear-Clipping
     *                 algorithm
     */
    public static TriangulateResult triangulateWithEarClipping(Vertex[] vertices) {
        for (Vertex v : vertices) {
            System.out.println(v.toString(3, 4));
        }
        boolean success = true;
        int[] triangles = null;
        String err = "";

        while (success) {
            if (vertices == null) {
                err = "The vertex list is null.";
                success = false;
            }

            if (vertices.length < 3) {
                err = "The vertex list must have at least 3 vertices.";
                success = false;
            }

            if (vertices.length > 1024) {
                err = "The Max vertex list length is 1024.";
                success = false;
            }


            if (!PolygonHelpers.isPlanar(vertices)) {
                err = "The vertices are not planar.";
                success = false;
            }

            if (!PolygonHelpers.isSimplePolygon(vertices)) {
                err = "The vertex list does not define a simple polygon.";
                success = false;
            }

            // if (PolygonHelpers.containsColinearEdges(vertices)) {
            //     err = "The vertex list contains colinear edges.";
            //     success = false;
            // }
            //
            // PolygonHelpers.ComputePolygonArea(vertices, out float area, out WindingOrder
            // windingOrder);
            //
            // if(windingOrder is WindingOrder.Invalid) {
            // err = "The vertices list does not contain a valid polygon.";
            // return false;
            // }
            //
            // if(windingOrder is WindingOrder.CounterClockwise) {
            // Array.Reverse(vertices);
            // }

            break;
        }

        if (!success)
            return new TriangulateResult(success, triangles, err);

        ArrayList<Integer> idxList = new ArrayList<>();
        for (int i = 0; i < vertices.length; i++) {
            idxList.add(i);
        }

        int totalTriangleCount = vertices.length - 2;
        int totalTriangleIdxCount = totalTriangleCount * 3;

        triangles = new int[totalTriangleIdxCount];
        int triangleIdxCount = 0;

        System.out.println(idxList);

        while (idxList.size() > 3) {
            // System.out.println("Vertices: " + vertices);
            // System.out.println("HERE 1");
            for (int i = 0; i < idxList.size(); i++) {
                // System.out.println("HERE 2");
                int a = idxList.get(i);
                int b = PolygonHelpers.getItem(idxList, i - 1);
                int c = PolygonHelpers.getItem(idxList, i + 1);
                // System.out.println(String.format("a: %d b: %d c: %d", a, b, c));

                Vertex va = vertices[a];
                Vertex vb = vertices[b];
                Vertex vc = vertices[c];
                // System.out.println("va: " + va);
                // System.out.println("vb: " + vb);
                // System.out.println("vc: " + vc);

                Vector va_to_vb = new Vector(va, vb);
                Vector vb_to_vc = new Vector(vb, vc);
                // System.out.println(va_to_vb);
                // System.out.println(vb_to_vc);

                // test to see if the ear is convex
                // System.out.println("Cross Product: " + PolygonHelpers.crossProduct2D(va_to_vb, vb_to_vc));
                if (PolygonHelpers.crossProduct2D(va_to_vb, vb_to_vc) < 0.0) {
                    continue;
                }

                boolean isEar = true;

                // Test if the 'ear' contains any other verticies in the polygon
                for (int j = 0; j < vertices.length; j++) {
                    // System.out.println("HERE 3");
                    if (j == a || j == b || j == c) {
                        continue;
                    }

                    Vertex p = vertices[j];

                    if (PolygonHelpers.isPointInTriangle(p, vb, va, vc)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    triangles[triangleIdxCount++] = b;
                    triangles[triangleIdxCount++] = a;
                    triangles[triangleIdxCount++] = c;
                    System.out.println("HERE");

                    idxList.remove(i);
                    break;
                }
            }
        }

        // Add the last three remaining points in the list to the traingles array
        triangles[triangleIdxCount++] = idxList.get(0);
        triangles[triangleIdxCount++] = idxList.get(1);
        triangles[triangleIdxCount++] = idxList.get(2);

        return new TriangulateResult(success, triangles, err);
    }
}
