/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a regular dodecahedron
   with its center at the origin, having edge length
   <pre>{@code
     2*(sqrt(5)-1)/(1+sqrt(5)) = (1/2)*(sqrt(5)-1)^2 = 0.7639,
   }</pre>
   and with its vertices on a sphere of radius
   <pre>{@code
     2*sqrt(3)/(1+sqrt(5)) = 1.0705.
   }</pre>
<p>
   See <a href="https://en.wikipedia.org/wiki/Regular_dodecahedron" target="_top">
                https://en.wikipedia.org/wiki/Regular_dodecahedron</a>

   @see Tetrahedron
   @see Cube
   @see Octahedron
   @see Icosahedron
*/
public class Dodecahedron extends Model
{
   /**
      Create a regular dodecahedron with its center at
      the origin, having edge length
      <pre>{@code
        2*(sqrt(5)-1)/(1+sqrt(5)) = (1/2)*(sqrt(5)-1)^2 = 0.7639,
      }</pre>
      and with its vertices on a sphere of radius
      <pre>{@code
        2*sqrt(3)/(1+sqrt(5)) = 1.0705.
     }</pre>
   */
   public Dodecahedron()
   {
      super("Dodecahedron");

      // Create the dodecahedron's geometry.
      // It has 20 vertices, 30 edgee, and 12 faces.
      final double t = (1 + Math.sqrt(5))/2,   // golden ratio
                   r = 1/t,
                  r2 = r * r;
      //https://en.wikipedia.org/wiki/Regular_dodecahedron#Cartesian_coordinates
      // (±r, ±r, ±r)
      addVertex(new Vertex(-r, -r, -r),
                new Vertex(-r, -r,  r),
                new Vertex(-r,  r, -r),
                new Vertex(-r,  r,  r),
                new Vertex( r, -r, -r),
                new Vertex( r, -r,  r),
                new Vertex( r,  r, -r),
                new Vertex( r,  r,  r));

      // (0, ±r2, ±1)
      addVertex(new Vertex( 0, -r2, -1),
                new Vertex( 0, -r2,  1),
                new Vertex( 0,  r2, -1),
                new Vertex( 0,  r2,  1));

      // (±r2, ±1, 0)
      addVertex(new Vertex(-r2, -1,  0),
                new Vertex(-r2,  1,  0),
                new Vertex( r2, -1,  0),
                new Vertex( r2,  1,  0));

      // (±1, 0, ±r2)
      addVertex(new Vertex(-1,  0, -r2),
                new Vertex( 1,  0, -r2),
                new Vertex(-1,  0,  r2),
                new Vertex( 1,  0,  r2));
/*
      // These vertices create a dodecahedron with vertices
      // on a sphere of radius sqrt(3), and with edge length
      //    2/t = 4/(1 + sqrt(5)) = sqrt(5) - 1 = 1.2361.
      //https://en.wikipedia.org/wiki/Regular_dodecahedron#Cartesian_coordinates
      // (±1, ±1, ±1)
      addVertex(new Vertex(-1, -1, -1),
                new Vertex(-1, -1,  1),
                new Vertex(-1,  1, -1),
                new Vertex(-1,  1,  1),
                new Vertex( 1, -1, -1),
                new Vertex( 1, -1,  1),
                new Vertex( 1,  1, -1),
                new Vertex( 1,  1,  1));

      // (0, ±r, ±t)
      addVertex(new Vertex( 0, -r, -t),
                new Vertex( 0, -r,  t),
                new Vertex( 0,  r, -t),
                new Vertex( 0,  r,  t));

      // (±r, ±t, 0)
      addVertex(new Vertex(-r, -t,  0),
                new Vertex(-r,  t,  0),
                new Vertex( r, -t,  0),
                new Vertex( r,  t,  0));

      // (±t, 0, ±r)
      addVertex(new Vertex(-t,  0, -r),
                new Vertex( t,  0, -r),
                new Vertex(-t,  0,  r),
                new Vertex( t,  0,  r));
*/
      // Create 12 faces, each with three triangles.
//https://github.com/mrdoob/three.js/blob/master/src/geometries/DodecahedronGeometry.js
      addPrimitive(new TriangleFan( 3, 11,  7, 15, 13),
                   new TriangleFan( 7, 19, 17,  6, 15),
                   new TriangleFan(17,  4,  8, 10,  6),
                   new TriangleFan( 8,  0, 16,  2, 10),
                   new TriangleFan( 0, 12,  1, 18, 16),
                   new TriangleFan( 6, 10,  2, 13, 15),
                   new TriangleFan( 2, 16, 18,  3, 13),
                   new TriangleFan(18,  1,  9, 11,  3),
                   new TriangleFan( 4, 14, 12,  0,  8),
                   new TriangleFan(11,  9,  5, 19,  7),
                   new TriangleFan(19,  5, 14,  4, 17),
                   new TriangleFan( 1, 12, 14,  5,  9));
/*
      addPrimitive(new Triangles( 3, 11,  7,
                                  3,  7, 15,
                                  3, 15, 13));
      addPrimitive(new Triangles( 7, 19, 17,
                                  7, 17,  6,
                                  7,  6, 15));
      addPrimitive(new Triangles(17,  4,  8,
                                 17,  8, 10,
                                 17, 10,  6));
      addPrimitive(new Triangles( 8,  0, 16,
                                  8, 16,  2,
                                  8,  2, 10));
      addPrimitive(new Triangles( 0, 12,  1,
                                  0,  1, 18,
                                  0, 18, 16));
      addPrimitive(new Triangles( 6, 10,  2,
                                  6,  2, 13,
                                  6, 13, 15));
      addPrimitive(new Triangles( 2, 16, 18,
                                  2, 18,  3,
                                  2,  3, 13));
      addPrimitive(new Triangles(18,  1,  9,
                                 18,  9, 11,
                                 18, 11,  3));
      addPrimitive(new Triangles( 4, 14, 12,
                                  4, 12,  0,
                                  4,  0,  8));
      addPrimitive(new Triangles(11,  9,  5,
                                 11,  5, 19,
                                 11, 19,  7));
      addPrimitive(new Triangles(19,  5, 14,
                                 19, 14,  4,
                                 19,  4, 17));
      addPrimitive(new Triangles( 1, 12, 14,
                                  1, 14,  5,
                                  1,  5,  9));
*/
   }
}//Dodecahedron
