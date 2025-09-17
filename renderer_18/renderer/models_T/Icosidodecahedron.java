/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_T;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a icosidodecahedron
   with its center at the origin, having edge length
   <pre>{@code
     4/(1+sqrt(5)) = 1.2361,
   }</pre>
   and with its vertices on a sphere of radius
   <pre>{@code
     4/(1+sqrt(5)) * sin(2Pi/5) = 1.1756.
   }</pre>
<p>
   See <a href="https://en.wikipedia.org/wiki/Icosidodecahedron" target="_top">
                https://en.wikipedia.org/wiki/Icosidodecahedron</a>

   @see Tetrahedron
   @see Cube
   @see Octahedron
   @see Dodecahedron
   @see Icosahedron
*/
public class Icosidodecahedron extends Model
{
   /**
      Create a icosidodecahedron with its center at
      the origin, having edge length
      <pre>{@code
        4/(1+sqrt(5)) = 1.2361,
      }</pre>
      and with its vertices on a sphere of radius
      <pre>{@code
        4/(1+sqrt(5)) * sin(2Pi/5) = 1.1756.
      }</pre>
   */
   public Icosidodecahedron()
   {
      super("Icosidodecahedron");

      // Create the icosidodecahedron's geometry.
      // It has 30 vertices and 60 edges.
      //https://en.wikipedia.org/wiki/Icosidodecahedron#Cartesian_coordinates
      //http://www.georgehart.com/virtual-polyhedra/vrml/icosidodecahedron.wrl
      final double t = (1 + Math.sqrt(5))/2;  // golden ratio
      final double r = t - 1; // (-1 + Math.sqrt(5))/2;
      addVertex(new Vertex( 0,          0,          1.051462),
                new Vertex( r,          0,          0.8506508),
                new Vertex( 0.2763932,  0.5527864,  0.8506508),
                new Vertex(-r,          0,          0.8506508),
                new Vertex(-0.2763932, -0.5527864,  0.8506508),
                new Vertex( 1,          0,          0.3249197),
                new Vertex( 0.7236068, -0.5527864,  0.5257311),
                new Vertex(-0.1708204,  0.8944272,  0.5257311),
                new Vertex( 0.4472136,  0.8944272,  0.3249197),
                new Vertex(-1,          0,          0.3249197),
                new Vertex(-0.7236068,  0.5527864,  0.5257311),
                new Vertex( 0.1708204, -0.8944272,  0.5257311),
                new Vertex(-0.4472136, -0.8944272,  0.3249197),
                new Vertex( 1,          0,         -0.3249197),
                new Vertex( 0.8944272,  0.5527864,  0),
                new Vertex( 0.5527864, -0.8944272,  0),
                new Vertex(-0.5527864,  0.8944272,  0),
                new Vertex( 0.4472136,  0.8944272, -0.3249197),
                new Vertex(-1,          0,         -0.3249197),
                new Vertex(-0.8944272, -0.5527864,  0),
                new Vertex(-0.4472136, -0.8944272, -0.3249197),
                new Vertex( r,          0,         -0.8506508),
                new Vertex( 0.7236068, -0.5527864, -0.5257311),
                new Vertex( 0.1708204, -0.8944272, -0.5257311),
                new Vertex(-0.7236068,  0.5527864, -0.5257311),
                new Vertex(-0.1708204,  0.8944272, -0.5257311),
                new Vertex( 0.2763932,  0.5527864, -0.8506508),
                new Vertex(-r,          0,         -0.8506508),
                new Vertex(-0.2763932, -0.5527864, -0.8506508),
                new Vertex( 0,          0,         -1.051462));

      // Create 12 pentagon faces (with 36 triangles).
      addPrimitive(
                   //new Face( 0,  2,  7, 10,  3),
                   new Triangle( 0,  2,  7),
                   new Triangle( 0,  7,  3),
                   new Triangle( 7, 10,  3),

                   //new Face( 0,  4, 11,  6,  1),
                   new Triangle( 0,  4, 11),
                   new Triangle( 0, 11,  1),
                   new Triangle(11,  6,  1),

                   //new Face( 1,  5, 14,  8,  2),
                   new Triangle( 1,  5, 14),
                   new Triangle( 1, 14,  2),
                   new Triangle(14,  8,  2),

                   //new Face( 3,  9, 19, 12,  4),
                   new Triangle( 3,  9, 19),
                   new Triangle( 3, 19,  4),
                   new Triangle(19, 12,  4),

                   //new Face( 5,  6, 15, 22, 13),
                   new Triangle( 5,  6, 15),
                   new Triangle( 5, 15, 13),
                   new Triangle(15, 22, 13),

                   //new Face( 7,  8, 17, 25, 16),
                   new Triangle( 7,  8, 17),
                   new Triangle( 7, 17, 16),
                   new Triangle(17, 25, 16),

                   //new Face( 9, 10, 16, 24, 18),
                   new Triangle( 9, 10, 16),
                   new Triangle( 9, 16, 18),
                   new Triangle(16, 24, 18),

                   //new Face(11, 12, 20, 23, 15),
                   new Triangle(11, 12, 20),
                   new Triangle(11, 20, 15),
                   new Triangle(20, 23, 15),

                   //new Face(13, 21, 26, 17, 14),
                   new Triangle(13, 21, 26),
                   new Triangle(13, 26, 14),
                   new Triangle(26, 17, 14),

                   //new Face(18, 27, 28, 20, 19),
                   new Triangle(18, 27, 28),
                   new Triangle(18, 28, 19),
                   new Triangle(28, 20, 19),

                   //new Face(21, 22, 23, 28, 29),
                   new Triangle(21, 22, 23),
                   new Triangle(21, 23, 29),
                   new Triangle(23, 28, 29),

                   //new Face(24, 25, 26, 29, 27),
                   new Triangle(24, 25, 26),
                   new Triangle(24, 26, 27),
                   new Triangle(26, 29, 27));
   }
}//Icosidodecahedron
