/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_T;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a solid model of a Sierpinski Triangle centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Sierpi%C5%84ski_triangle" target="_top">
                https://en.wikipedia.org/wiki/Sierpi%C5%84ski_triangle</a>
*/
public class SierpinskiTriangle extends Model implements MeshMaker
{
   final int n;

   /**
      Create a Sierpinski Triangle centered at the origin
      using seven recursive iterations.
   */
   public SierpinskiTriangle()
   {
      this(7);
   }


   /**
      Create a Sierpinski Triangle centered at the origin
      using {@code n} recursive iterations.

      @param n  number of recursive iterations
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public SierpinskiTriangle(final int n)
   {
      super(String.format("Sierpinski_Triangle(%d)", n));

      if (n < 0)
         throw new IllegalArgumentException("n must be greater than or equal to 0");

      this.n = n;

      if (0 == n)
      {
         // Create an elquilateral triangle.
         addVertex(new Vertex( 1.0,  0,     0),
                   new Vertex(-0.5,  0.866, 0),
                   new Vertex(-0.5, -0.866, 0));

         // Create a triangle.
         addPrimitive(new Triangle(0, 1, 2));
      }
      else
      {
         addNestedModel(subTriangles(n - 1,  0.5,   0),
                        subTriangles(n - 1, -0.25,  0.433),
                        subTriangles(n - 1, -0.25, -0.433));
      }
   }


   /**
      Recursive helper function.
      <p>
      This function builds the three sub models needed
      for one recusive step.

      @param n   number of recursive iterations
      @param tX  translation in the x-direction
      @param tY  translation in the y-direction
      @return    {@link Model} holding sub tree of triangles
   */
   private Model subTriangles(final int n, final double tX, final double tY)
   {
      assert (n >= 0);

      final Model model =
         new Model("Sierpinski Triangle: level "+n+" ("+tX+", "+tY+")",
                   Matrix.translate(tX, tY, 0)
            .times(Matrix.scale(0.5, 0.5, 0.5)));

      if (0 == n) // stop the recursion
      {
         // Create an elquilateral triangle.
         model.addVertex(new Vertex( 1.0,  0,     0),
                         new Vertex(-0.5,  0.866, 0),
                         new Vertex(-0.5, -0.866, 0));

         // Create a triangle.
         model.addPrimitive(new Triangle(0, 1, 2));
      }
      else
      {
         model.addNestedModel(subTriangles(n - 1,  0.5,   0),
                              subTriangles(n - 1, -0.25,  0.433),
                              subTriangles(n - 1, -0.25, -0.433));
      }
      return model;
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return n;}

   @Override
   public SierpinskiTriangle remake(final int n, final int k)
   {
      final int newN;
      if (n != this.n)
         newN = n;
      else
         newN = k;
      return new SierpinskiTriangle(newN);
   }
}//SierpinskiTriangle
