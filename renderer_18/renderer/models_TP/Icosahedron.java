/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a regular icosahedron
   with its center at the origin, having edge length
   <pre>{@code
     4/(1+sqrt(5)) = 1.2361,
   }</pre>
   and with its vertices on a sphere of radius
   <pre>{@code
     4/(1+sqrt(5)) * sin(2Pi/5) = 1.1756.
   }</pre>
<p>
   See <a href="https://en.wikipedia.org/wiki/Regular_icosahedron" target="_top">
                https://en.wikipedia.org/wiki/Regular_icosahedron</a>

   @see Tetrahedron
   @see Cube
   @see Octahedron
   @see Dodecahedron
*/
public class Icosahedron extends Model
{
   /**
      Create a regular icosahedron with its center at
      the origin, having edge length
      <pre>{@code
        4/(1+sqrt(5)) = 1.2361,
      }</pre>
      and with its vertices on a sphere of radius
      <pre>{@code
        4/(1+sqrt(5)) * sin(2Pi/5) = 1.1756.
      }</pre>
   */
   public Icosahedron()
   {
      super("Icosahedron");

      // Create the icosahedron's geometry.
      // It has 12 vertices, 30 edges, and 20 facess.
      final double t = (1 + Math.sqrt(5))/2,  // golden ratio
                   r = 1/t;
      //https://en.wikipedia.org/wiki/Regular_icosahedron#Cartesian_coordinates
      // All cyclic permutations of (0, ±r, ±1).
      addVertex(new Vertex(-r,  1,  0),
                new Vertex( r,  1,  0),
                new Vertex(-r, -1,  0),
                new Vertex( r, -1,  0),
                new Vertex( 0, -r,  1),
                new Vertex( 0,  r,  1),
                new Vertex( 0, -r, -1),
                new Vertex( 0,  r, -1),
                new Vertex( 1,  0, -r),
                new Vertex( 1,  0,  r),
                new Vertex(-1,  0, -r),
                new Vertex(-1,  0,  r));
/*
      // These vertices create a icosahedron with edge length 2,
      // and vertices on a sphere of radius
      //    sqrt(10+2sqrt(5))/2 = 2sin(2Pi/5) = 1.90211.
      //https://en.wikipedia.org/wiki/Regular_icosahedron#Cartesian_coordinates
      // and also
      //https://github.com/mrdoob/three.js/blob/master/src/geometries/IcosahedronGeometry.js
      // All cyclic permutations of (0, ±1, ±t).
      addVertex(new Vertex(-1,  t,  0),
                new Vertex( 1,  t,  0),
                new Vertex(-1, -t,  0),
                new Vertex( 1, -t,  0),
                new Vertex( 0, -1,  t),
                new Vertex( 0,  1,  t),
                new Vertex( 0, -1, -t),
                new Vertex( 0,  1, -t),
                new Vertex( t,  0, -1),
                new Vertex( t,  0,  1),
                new Vertex(-t,  0, -1),
                new Vertex(-t,  0,  1));
*/
      // To figure out the edges, look at the orthogonal projection in the z-direction.
      // https://en.wikipedia.org/wiki/Regular_icosahedron#Orthogonal_projections

      // The edge from v00 to v01 is the top horizontal edge.
      // The edge from v02 to v03 is the bottom horizontal edge.
      // The edge from v04 to v05 is the front vertical edge.
      // The edge from v06 to v07 is the back vertical edge.
      // The edge from v08 to v09 is the right horizontal edge.
      // The edge from v10 to v11 is the left horizontal edge.
/*
      // Version 1
      addPrimitive(new TriangleFan(0, 1, 7, 10, 11, 5, 1),
                   new Triangles(1,  5,  9,
                                 1,  8,  7,
                                 1,  9,  8,
                                 4,  5, 11,
                                 6, 10,  7,
                                 4,  9,  5,
                                 6,  7,  8,
                                 2, 11, 10,
                                 2,  4, 11,
                                 2, 10,  6),
                   new TriangleFan(3, 8, 9, 4, 2, 6, 8));
*/
/*
      // Version 2
      addPrimitive(new TriangleStrip(0,  1,  7, 8),   // top half
                   new TriangleStrip(0,  7, 10, 6),
                   new TriangleStrip(0, 10, 11, 2),
                   new TriangleStrip(0, 11,  5, 4),
                   new TriangleStrip(0,  5,  1, 9));
      addPrimitive(new TriangleStrip(3, 8, 9, 1),     // bottom half
                   new TriangleStrip(3, 9, 4, 5),
                   new TriangleStrip(3, 4, 2, 11),
                   new TriangleStrip(3, 2, 6, 10),
                   new TriangleStrip(3, 6, 8, 7));
*/
      // Version 3
      addPrimitive(new TriangleFan(0, 1, 7, 10, 11, 5, 1),
                   new TriangleStrip(1,8, 7,6, 10,2, 11,4, 5,9, 1,8),
                   new TriangleFan(3, 8, 9, 4, 2, 6, 8));


      // Also add a wireframe on top of the solid triangles.
      addPrimitive(new LineFan(0, 1, 7, 10, 11, 5),
                   new LineLoop(  1, 7, 10, 11, 5),
                   new Lines(1,8, 8,7, 7,6, 6,10, 10,2, 2,11, 11,4, 4,5, 5,9, 9,1),
                   new LineLoop(  8, 6, 2, 4, 9),
                   new LineFan(3, 8, 6, 2, 4, 9));

      // Highlight each vertex of the icosahedron.
      final Points pts1 = new Points(0, 3),
                   pts2 = new Points(1, 7, 10, 11, 5),
                   pts3 = new Points(8, 6,  2,  4, 9);
      pts1.radius = 10;
      pts2.radius = 5;
      pts3.radius = 5;
      addPrimitive(pts1, pts2, pts3);
   }
}//Icosahedron
