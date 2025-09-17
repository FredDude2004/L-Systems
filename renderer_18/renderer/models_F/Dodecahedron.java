/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_F;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a wireframe model of a regular dodecahedron
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
      // It has 20 vertices and 30 edges.
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
      // Create 12 faces.
//https://github.com/mrdoob/three.js/blob/master/src/geometries/DodecahedronGeometry.js
      addPrimitive(new Face( 3, 11,  7, 15, 13),
                   new Face( 7, 19, 17,  6, 15),
                   new Face(17,  4,  8, 10,  6),
                   new Face( 8,  0, 16,  2, 10),
                   new Face( 0, 12,  1, 18, 16),
                   new Face( 6, 10,  2, 13, 15),
                   new Face( 2, 16, 18,  3, 13),
                   new Face(18,  1,  9, 11,  3),
                   new Face( 4, 14, 12,  0,  8),
                   new Face(11,  9,  5, 19,  7),
                   new Face(19,  5, 14,  4, 17),
                   new Face( 1, 12, 14,  5,  9));
   }
}//Dodecahedron
