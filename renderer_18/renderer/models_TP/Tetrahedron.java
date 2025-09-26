/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.Triangle;
import renderer.scene.util.MeshMaker;

/**
   Create a solid model of a regular tetrahedron
   with its center at the origin, having edge length
   {@code 2*sqrt(2)}, and with its vertices at corners
   of the cube with vertices {@code (±1, ±1, ±1)}.
<p>
   See <a href="https://en.wikipedia.org/wiki/Tetrahedron" target="_top">
                https://en.wikipedia.org/wiki/Tetrahedron</a>

   @see Cube
   @see Octahedron
   @see Icosahedron
   @see Dodecahedron
*/
public class Tetrahedron extends Model implements MeshMaker
{
   public final int n1;
   public final int n2;

   /**
      Create a regular tetrahedron with its center at
      the origin, having edge length {@code 2*sqrt(2)},
      and with its vertices at corners of the cube with
      vertices {@code (±1, ±1, ±1)}.
   */
   public Tetrahedron()
   {
      this(false);
   }


   /**
      Create a regular tetrahedron or its dual tetrahedron
      (the dual of a tetrahedron is another tetrahedron).
   <p>
      <a href="https://en.wikipedia.org/wiki/Tetrahedron#Regular_tetrahedron" target="_top">
               https://en.wikipedia.org/wiki/Tetrahedron#Regular_tetrahedron</a>
   <p>
      The combination of these two dual tetrahedrons is a stellated octahedron.
   <p>
      <a href="https://en.wikipedia.org/wiki/Stellated_octahedron" target="_top">
               https://en.wikipedia.org/wiki/Stellated_octahedron</a>

      @param dual  choose between the two dual tetrahedrons
   */
   public Tetrahedron(final boolean dual)
   {
      super("Tetrahedron");

      this.n1 = 0;
      this.n2 = 0;

      // Create the tetrahedron's geometry.
      // It has 4 vertices, 6 edges, and 4 faces.
      if (! dual)
      {
         addVertex(new Vertex( 1,  1,  1),
                   new Vertex(-1,  1, -1),
                   new Vertex( 1, -1, -1),
                   new Vertex(-1, -1,  1));
      }
      else // Create the dual tetrahedron by
      {    // inverting the coordinates given above.
         addVertex(new Vertex(-1, -1, -1),
                   new Vertex( 1, -1,  1),
                   new Vertex(-1,  1,  1),
                   new Vertex( 1,  1, -1));
      }

      // Create four triangles.
      addPrimitive(new Triangle(0, 1, 3), // top edge
                   new Triangle(1, 0, 2),
                   new Triangle(3, 2, 0), // bottome edge
                   new Triangle(2, 3, 1));
   }


   /**
      Create a regular tetrahedron with its center at
      the origin, having edge length {@code 2*sqrt(2)},
      and with its vertices at corners of the cube with
      vertices {@code (±1, ±1, ±1)}.
      <p>
      Add line segments fanning out from each vertex to
      its opposite edge.

      @param n1 number of lines fanning out from v0 and v1 towards the edge (v2, v3)
      @param n2 number of lines fanning out from v2 and v3 towards the edge (v0, v1)
      @throws IllegalArgumentException if {@code n1} is less than 0
      @throws IllegalArgumentException if {@code n2} is less than 0
   */
   public Tetrahedron(final int n1, final int n2)
   {
      super(String.format("Tetrahedron(%d,%d)", n1, n2));

      if (n1 < 0)
         throw new IllegalArgumentException("n1 must be greater than or equal to 0");
      if (n2 < 0)
         throw new IllegalArgumentException("n2 must be greater than or equal to 0");

      this.n1 = n1;
      this.n2 = n2;

      // Create the tetrahedron's geometry.
      // It has 4 vertices and 6 edges.
      final Vertex v0 = new Vertex( 1,  1,  1),
                   v1 = new Vertex(-1,  1, -1),
                   v2 = new Vertex( 1, -1, -1),
                   v3 = new Vertex(-1, -1,  1);

      addVertex(v0, v1, v2, v3);

      // Create four triangular faces.
      addPrimitive(new Triangle(0, 1, 3),  // top edge
                   new Triangle(1, 0, 2),
                   new Triangle(3, 2, 0),  // bottome edge
                   new Triangle(2, 3, 1));

      fan(n1, 0,  3, 2); // fan out from v0
      fan(n1, 1,  2, 3); // fan out from v1
      fan(n2, 2,  1, 0); // fan out from v2
      fan(n2, 3,  0, 1); // fan out from v3
   }


   /**
      Create {@code n} line segments fanning out from {@link Vertex}
      {@code v0} towards the edge spanned by the other two vertices.

      @param n    number of lines fanning out from {@link Vertex} {@code v0}
      @param v0   index in the {@link Vertex} list of the vertex to fan out from
      @param v1i  index to a {@link Vertex} opposite to {@code v0}
      @param v2i  index to a {@link Vertex} opposite to {@code v0}
   */
   private void fan(final int n, final int v0, final int v1i,
                                               final int v2i)
   {
      final Vertex v1 = vertexList.get(v1i);
      final Vertex v2 = vertexList.get(v2i);

      // Create vertices along the edge from v1 to v2.
      int index = vertexList.size();
      for (int i = 0; i < n; ++i)
      {
         // Use linear interpolation (lerp).
         final double t = (double)(i+1) / (double)(n+1);
         final double x = (1-t) * v1.x + t * v2.x;
         final double y = (1-t) * v1.y + t * v2.y;
         final double z = (1-t) * v1.z + t * v2.z;
         addVertex(new Vertex(x, y, z));
      }
      // Create n triangles.
      if (n > 0) addPrimitive(new Triangle(v0, v1i, index));
      for (int i = 0; i < n - 1; ++i)
      {
         addPrimitive(new Triangle(v0, index+i, index+i+1));
      }
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return this.n1;}

   @Override public int getVertCount() {return this.n2;}

   @Override
   public Tetrahedron remake(final int n, final int k)
   {
      return new Tetrahedron(n, k);
   }
}//Tetrahedron
