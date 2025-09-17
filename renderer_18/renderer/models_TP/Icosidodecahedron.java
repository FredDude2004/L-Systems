/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

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
                   new Triangles( 0,  2,  7,
                                  0,  7,  3,
                                  7, 10,  3),

                   //new Face( 0,  4, 11,  6,  1),
                   new Triangles( 0,  4, 11,
                                  0, 11,  1,
                                 11,  6,  1),

                   //new Face( 1,  5, 14,  8,  2),
                   new Triangles( 1,  5, 14,
                                  1, 14,  2,
                                 14,  8,  2),

                   //new Face( 3,  9, 19, 12,  4),
                   new Triangles( 3,  9, 19,
                                  3, 19,  4,
                                 19, 12,  4),

                   //new Face( 5,  6, 15, 22, 13),
                   new Triangles( 5,  6, 15,
                                  5, 15, 13,
                                 15, 22, 13),

                   //new Face( 7,  8, 17, 25, 16),
                   new Triangles( 7,  8, 17,
                                  7, 17, 16,
                                 17, 25, 16),

                   //new Face( 9, 10, 16, 24, 18),
                   new Triangles( 9, 10, 16,
                                  9, 16, 18,
                                 16, 24, 18),

                   //new Face(11, 12, 20, 23, 15),
                   new Triangles(11, 12, 20,
                                 11, 20, 15,
                                 20, 23, 15),

                   //new Face(13, 21, 26, 17, 14),
                   new Triangles(13, 21, 26,
                                 13, 26, 14,
                                 26, 17, 14),

                   //new Face(18, 27, 28, 20, 19),
                   new Triangles(18, 27, 28,
                                 18, 28, 19,
                                 28, 20, 19),

                   //new Face(21, 22, 23, 28, 29),
                   new Triangles(21, 22, 23,
                                 21, 23, 29,
                                 23, 28, 29),

                   //new Face(24, 25, 26, 29, 27),
                   new Triangles(24, 25, 26,
                                 24, 26, 27,
                                26, 29, 27));
   }
}//Icosidodecahedron
