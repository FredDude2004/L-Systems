/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_F;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a wireframe model of a right equilateral triangular prism
   with the y-axis as its central axis.
<p>
   See <a href="https://en.wikipedia.org/wiki/Triangular_prism" target="_top">
                https://en.wikipedia.org/wiki/Triangular_prism</a>
<p>
   See <a href="https://en.wikipedia.org/wiki/Prism_(geometry)" target="_top">
                https://en.wikipedia.org/wiki/Prism_(geometry)</a>
<p>
   Attach to each triangular end of the prism a tetrahedron.
*/
public class TriangularPrism extends Model
{
   /**
      Create a right equilateral triangular prism with a
      regular tetrahedrons attached to each end so that
      the total length runs from -1 to 1 along the y-axis.
   */
   public TriangularPrism( )
   {
      this(0.5, 0.6);
   }


   /**
      Create an equilateral triangular prism that runs
      from {@code -h} to {@code h} along the y-axis, has
      triangle side length {@code s}, and has a regular
      tetrahedron attached to each end.

      @param s  the length of the triangle's sides
      @param h  the body of the prism runs from -h to h along the y-axis
   */
   public TriangularPrism(final double s, final double h)
   {
      this(s, h, 0);
   }


   /**
      Create an equilateral triangular prism that runs
      from {@code -h} to {@code h} along the y-axis, has
      triangle side length {@code s}, has a regular
      tetrahedron attached to each end, and has {@code n}
      lines of latitude around the body of the prism.

      @param s  the length of the triangle's sides
      @param h  the body of the prism runs from -h to h along the y-axis
      @param n  number of lines of latitude around the body of the prism
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public TriangularPrism(final double s, final double h,
                          final int n)
   {
      this(s/Math.sqrt(3), h, Math.atan(Math.sqrt(2)), n);
   }


   /**
      Create an equilateral triangular prism that runs
      from {@code -h} to {@code h} along the y-axis, with
      the triangle inscribed in a circle of radius {@code r},
      has a tetrahedron attached to each end where the
      face-edge-face angle of each tetrahedron is {@code theta}
      (with theta in radians!), and has {@code n} lines of
      latitude around the body of the prism.
   <p>
      If {@code theta = 0}, then there are no tetrahedrons at the ends of the prism.
   <p>
      If {@code theta = arctan(sqrt(2)) = 54.736°}, then the tetrahedrons are regular.

      @param r      radius of circle in xz-plane that the equilateral triangle is inscribed in
      @param h      the body of the prism runs from -h to h along the y-axis
      @param theta  slant angle of each tetrahedron at the ends of the prism
      @param n      number of lines of latitude around the body of the prism
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public TriangularPrism(final double r, final double h, final double theta,
                          final int n)
   {
      this(r, h, r*Math.tan(theta), n, true);
   }


   /**
      Create an equilateral triangular prism that runs
      from {@code -h} to {@code h} along the y-axis, with
      the triangle inscribed in a circle of radius {@code r},
      has a tetrahedron attached to each end where the height
      of each tetrahedron is {@code h2}, and has {@code n} lines
      of latitude around the body of the prism.
   <p>
      So the total height is {@code 2*(h + h2)}.

      @param r   radius of circle in xz-plane that the equilateral triangle is inscribed in
      @param h   the body of the prism runs from h to -h in the y-direction
      @param h2  height of each tetrahedron at the ends of the prism
      @param n   number of lines of latitude around the body of the prism
      @param bothHalves  determines if both halves or only the top half gets created
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public TriangularPrism(final double r,
                          final double h, final double h2,
                          final int n,
                          final boolean bothHalves)
   {
      super("Triangular Prism");

      if (n < 0)
         throw new IllegalArgumentException("n must be greater than or equal to 0");

      // Create the prism's geometry.
      final double sqrt3 = Math.sqrt(3.0);
      double delta_y = 2.0*h/(n+1);
      if (! bothHalves)  // cut off the bottom half
      {
         delta_y = h/(n+1);
      }

      for (int i = 0; i < n+2; ++i)
      {
         double y = -h + i * delta_y;
         if (! bothHalves)  // cut off the bottom half
         {
            y = i * (delta_y / 2);
         }

         addVertex(new Vertex( r,    y,    0),
                   new Vertex(-r/2,  y,  r*0.5*sqrt3),
                   new Vertex(-r/2,  y, -r*0.5*sqrt3));
      }
      final int top = vertexList.size();
      addVertex(new Vertex(0,  h+h2, 0));  // vertex at the top
      final int bot = vertexList.size();
      if (bothHalves)
      {
         addVertex(new Vertex(0, -h-h2, 0)); // vertex at the bottom
      }
      else  // cut off the bottom half
      {
         addVertex(new Vertex(0, 0, 0));     // vertex at the bottom
      }

      // 3 top faces
      addPrimitive(new Face(top, 3*(n+1)+1, 3*(n+1)+0),
                   new Face(top, 3*(n+1)+2, 3*(n+1)+1),
                   new Face(top, 3*(n+1)+0, 3*(n+1)+2));
      // 3 bottom faces
      addPrimitive(new Face(bot, 0, 1),
                   new Face(bot, 1, 2),
                   new Face(bot, 2, 0));
      // Create all the square strips along the prism wall.
      for (int i = 0; i < n+1; ++i)
      {
         addPrimitive(new Face(3*i + 0, 3*(i+1) + 0, 3*(i+1) + 1, 3*i + 1),
                      new Face(3*i + 1, 3*(i+1) + 1, 3*(i+1) + 2, 3*i + 2),
                      new Face(3*i + 2, 3*(i+1) + 2, 3*(i+1) + 0, 3*i + 0));
      }
   }
}//TriangularPrism
