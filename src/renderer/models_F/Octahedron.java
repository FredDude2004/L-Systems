/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_F;

import renderer.scene.*;
import renderer.scene.primitives.Face;
import renderer.scene.util.MeshMaker;

/**
   Create a wireframe model of a regular octahedron
   with its center at the origin, having side length
   {@code  sqrt(2) = 1.4142},with its center plane given
   by the four vertices {@code  (±1, 0, ±1)}. and with
   the top and bottom vertices being {@code  (0, ±1, 0)}.
<p>
   See <a href="https://en.wikipedia.org/wiki/Octahedron" target="_top">
                https://en.wikipedia.org/wiki/Octahedron</a>

   @see Tetrahedron
   @see Cube
   @see Icosahedron
   @see Dodecahedron
*/
public class Octahedron extends Model implements MeshMaker
{
   public final int n1;
   public final int n2;
   public final int n3;

   /**
      Create a regular octahedron with its center at the
      origin, having side length {@code  sqrt(2) = 1.4142},
      with its center plane given by the four vertices
      {@code  (±1, 0, ±1)}. and with the top and bottom
      vertices being {@code  (0, ±1, 0)}.
   */
   public Octahedron()
   {
      super("Octahedron");

      this.n1 = 0;
      this.n2 = 0;
      this.n3 = 0;

      // Create the octahedron's geometry.
      // It has 6 vertices, 12 edges, and 8 faces.
      addVertex(new Vertex( 1,  0,  0),  // 4 vertices around the center plane
                new Vertex( 0,  0, -1),
                new Vertex(-1,  0,  0),
                new Vertex( 0,  0,  1),
                new Vertex( 0,  1,  0),  // vertex at the top
                new Vertex( 0, -1,  0)); // vertex at the bottom
/*
      // These vertices create an Octahedron with side length 1.
      final double sqrt3 = Math.sqrt(3.0);
      final double sqrt2 = Math.sqrt(2.0);
      addVertex(new Vertex( 0.5, 0,  0.5), // 4 vertices around the center plane
                new Vertex(-0.5, 0,  0.5),
                new Vertex(-0.5, 0, -0.5),
                new Vertex( 0.5, 0, -0.5),
                new Vertex( 0,  1/sqrt2, 0),  // vertex at the top
                new Vertex( 0, -1/sqrt2, 0)); // vertex at the bottom
*/
      // Create eight triangular faces.
      // Make sure the faces are all
      // oriented in the same way!
      // Remember that front facing
      // should be counter-clockwise
      // (and back facing is clock-wise).
      addPrimitive(new Face(4, 1, 0),  // top half
                   new Face(4, 0, 3),
                   new Face(4, 2, 1),
                   new Face(4, 3, 2),
                   new Face(5, 0, 1),  // bottom half
                   new Face(5, 3, 0),
                   new Face(5, 1, 2),
                   new Face(5, 2, 3));
   }


   /**
      Create a regular octahedron with its center at the
      origin, having side length {@code  sqrt(2) = 1.4142},
      with its center plane given by the four vertices
      {@code  (±1, 0, ±1)}. and with the top and bottom
      vertices being {@code  (0, ±1, 0)}.
      <p>
      Add line segments fanning out from the top and bottom
      vertices to the sides around the center plane.

      @param n number of lines fanning out from the top and bottom on each side of the octahedron
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public Octahedron(final int n)
   {
      this(n, 0, 0);
   }


   /**
      Create a regular octahedron with its center at the
      origin, having side length {@code  sqrt(2) = 1.4142},
      with its center plane given by the four vertices
      {@code  (±1, 0, ±1)}. and with the top and bottom
      vertices being {@code  (0, ±1, 0)}.
      <p>
      Add line segments fanning out from each vertex to
      its opposite sides.

      @param n1 number of lines fanning out from the top and bottom on each side of the octahedron
      @param n2 number of lines fanning out from v0 and v2 on each side of the octahedron
      @param n3 number of lines fanning out from v1 and v3 on each side of the octahedron
      @throws IllegalArgumentException if {@code n1} is less than 0
      @throws IllegalArgumentException if {@code n2} is less than 0
      @throws IllegalArgumentException if {@code n3} is less than 0
   */
   public Octahedron(final int n1, final int n2, final int n3)
   {
      this(n1, n1, n2, n2, n3, n3);
   }


   /**
      Create a regular octahedron with its center at the
      origin, having side length {@code  sqrt(2) = 1.4142},
      with its center plane given by the four vertices
      {@code  (±1, 0, ±1)}. and with the top and bottom
      vertices being {@code  (0, ±1, 0)}.
      <p>
      Add line segments fanning out from each vertex to
      its opposite sides.

      @param n1a number of lines fanning out from the top on each side of the octahedron
      @param n1b number of lines fanning out from the bottom on each side of the octahedron
      @param n2a number of lines fanning out from v0 on each side of the octahedron
      @param n2b number of lines fanning out from v1 on each side of the octahedron
      @param n3a number of lines fanning out from v2 on each side of the octahedron
      @param n3b number of lines fanning out from v3 on each side of the octahedron
      @throws IllegalArgumentException if {@code n1a} is less than 0
      @throws IllegalArgumentException if {@code n1b} is less than 0
      @throws IllegalArgumentException if {@code n2a} is less than 0
      @throws IllegalArgumentException if {@code n2b} is less than 0
      @throws IllegalArgumentException if {@code n3a} is less than 0
      @throws IllegalArgumentException if {@code n3b} is less than 0
   */
   public Octahedron(final int n1a, final int n1b,
                     final int n2a, final int n2b,
                     final int n3a, final int n3b)
   {
      super(String.format("Octahedron(%d,%d,%d,%d,%d,%d)",n1a,n1b,n2a,n2b,n3a,n3b));

      if (n1a < 0)
         throw new IllegalArgumentException("n1a must be greater than or equal to 0");
      if (n1b < 0)
         throw new IllegalArgumentException("n1b must be greater than or equal to 0");
      if (n2a < 0)
         throw new IllegalArgumentException("n2a must be greater than or equal to 0");
      if (n2b < 0)
         throw new IllegalArgumentException("n2b must be greater than or equal to 0");
      if (n3a < 0)
         throw new IllegalArgumentException("n3a must be greater than or equal to 0");
      if (n3b < 0)
         throw new IllegalArgumentException("n3b must be greater than or equal to 0");

      this.n1 = n1a;
      this.n2 = n2a;
      this.n3 = n3a;

      // Create the octahedron's geometry.
      // It has 6 vertices and 12 edges.
      final Vertex v0 = new Vertex( 1,  0,  0); // 4 vertices around the center plane
      final Vertex v1 = new Vertex( 0,  0, -1);
      final Vertex v2 = new Vertex(-1,  0,  0);
      final Vertex v3 = new Vertex( 0,  0,  1);
      final Vertex v4 = new Vertex( 0,  1,  0); // vertex at the top
      final Vertex v5 = new Vertex( 0, -1,  0); // vertex at the bottom
      addVertex(v0, v1, v2, v3, v4, v5);
/*
      // These vertices create an Octahedron with side length 1.
      final double sqrt3 = Math.sqrt(3.0);
      final double sqrt2 = Math.sqrt(2.0);
      final Vertex v0 = new Vertex( 0.5, 0,  0.5); // 4 vertices around the center plane
      final Vertex v1 = new Vertex(-0.5, 0,  0.5);
      final Vertex v2 = new Vertex(-0.5, 0, -0.5);
      final Vertex v3 = new Vertex( 0.5, 0, -0.5);
      final Vertex v4 = new Vertex( 0,  1/sqrt2, 0); // vertex at the top
      final Vertex v5 = new Vertex( 0, -1/sqrt2, 0); // vertex at the bottom
      addVertex(v0, v1, v2, v3, v4, v5);
*/
      // Create eight triangular faces.
      // Make sure the faces are all
      // oriented in the same way!
      // Remember that front facing
      // should be counter-clockwise
      // (and back facing is clock-wise).
      addPrimitive(new Face(4, 1, 0),  // top half
                   new Face(4, 0, 3),
                   new Face(4, 2, 1),
                   new Face(4, 3, 2),
                   new Face(5, 0, 1),  // bottom half
                   new Face(5, 3, 0),
                   new Face(5, 1, 2),
                   new Face(5, 2, 3));

      // The faces fanning out from the top an bottom seem
      // to be oriented correctly. Each set of faces fanning
      // out from a vertex are oriented the same. But at least
      // one set of faces seems to be oriented opposite of what
      // it should be.
      fan(n1a, 4,  0, 1, 2, 3); // fan out from v4 (top)
      fan(n1b, 5,  3, 2, 1, 0); // fan out from v5 (bottom)
      fan(n2a, 0,  3, 4, 1, 5); // fan out from v0
      fan(n3a, 1,  0, 4, 2, 5); // fan out from v1
      fan(n2b, 2,  1, 4, 3, 5); // fan out from v2
      fan(n3b, 3,  2, 4, 0, 5); // fan out from v3
   }


   /**
      Create {@code n} line segments fanning out from {@link Vertex}
      {@code v0} towards the four edges spanned by the other four
      vertices.

      @param n    number of lines fanning out from {@link Vertex} {@code v0}
      @param v0   index in the {@link Vertex} list of the vertex to fan out from
      @param v1i  index to a {@link Vertex} opposite to {@code v0}
      @param v2i  index to a {@link Vertex} opposite to {@code v0}
      @param v3i  index to a {@link Vertex} opposite to {@code v0}
      @param v4i  index to a {@link Vertex} opposite to {@code v0}
   */
   private void fan(final int n, final int v0, final int v1i,
                                               final int v2i,
                                               final int v3i,
                                               final int v4i)
   {
      final Vertex v1 = vertexList.get(v1i);
      final Vertex v2 = vertexList.get(v2i);
      final Vertex v3 = vertexList.get(v3i);
      final Vertex v4 = vertexList.get(v4i);

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
      // Create n faces.
      if (n > 0) addPrimitive(new Face(v0, v1i, index));
      for (int i = 0; i < n - 1; ++i)
      {
         addPrimitive(new Face(v0, index+i, index+i+1));
      }


      // Create vertices along the edge from v2 to v3.
      index = vertexList.size();
      for (int i = 0; i < n; ++i)
      {
         // Use linear interpolation (lerp).
         final double t = (double)(i+1) / (double)(n+1);
         final double x = (1-t) * v2.x + t * v3.x;
         final double y = (1-t) * v2.y + t * v3.y;
         final double z = (1-t) * v2.z + t * v3.z;
         addVertex(new Vertex(x, y, z));
      }
      // Create n faces.
      if (n > 0) addPrimitive(new Face(v0, v2i, index));
      for (int i = 0; i < n - 1; ++i)
      {
         addPrimitive(new Face(v0, index+i, index+i+1));
      }


      // Create vertices along the edge from v3 to v4.
      index = vertexList.size();
      for (int i = 0; i < n; ++i)
      {
         // Use linear interpolation (lerp).
         final double t = (double)(i+1) / (double)(n+1);
         final double x = (1-t) * v3.x + t * v4.x;
         final double y = (1-t) * v3.y + t * v4.y;
         final double z = (1-t) * v3.z + t * v4.z;
         addVertex(new Vertex(x, y, z));
      }
      // Create n faces.
      if (n > 0) addPrimitive(new Face(v0, v3i, index));
      for (int i = 0; i < n - 1; ++i)
      {
         addPrimitive(new Face(v0, index+i, index+i+1));
      }


      // Create vertices along the edge from v4 to v1.
      index = vertexList.size();
      for (int i = 0; i < n; ++i)
      {
         // Use linear interpolation (lerp).
         final double t = (double)(i+1) / (double)(n+1);
         final double x = (1-t) * v4.x + t * v1.x;
         final double y = (1-t) * v4.y + t * v1.y;
         final double z = (1-t) * v4.z + t * v1.z;
         addVertex(new Vertex(x, y, z));
      }
      // Create n faces.
      if (n > 0) addPrimitive(new Face(v0, v4i, index));
      for (int i = 0; i < n - 1; ++i)
      {
         addPrimitive(new Face(v0, index+i, index+i+1));
      }
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return this.n1;}

   @Override public int getVertCount() {return this.n2;}

   @Override
   public Octahedron remake(final int n, final int k)
   {
      return new Octahedron(n, k, this.n3);
   }
}//Octahedron
