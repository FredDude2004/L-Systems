/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_F;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a wireframe model of a Sierpinski Sponge centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Sierpi%C5%84ski_triangle#Analogues_in_higher_dimensions" target="_top">
                https://en.wikipedia.org/wiki/Sierpi%C5%84ski_triangle#Analogues_in_higher_dimensions</a>
*/
public class SierpinskiSponge extends Model implements MeshMaker
{
   final int n;

   /**
      Create a Sierpinski Sponge centered at the origin
      using five recursive iterations.
   */
   public SierpinskiSponge()
   {
      this(5);
   }


   /**
      Create a Sierpinski Sponge centered at the origin
      using {@code n} recursive iterations.

      @param n  number of recursive iterations
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public SierpinskiSponge(final int n)
   {
      super(String.format("Sierpinski_Sponge(%d)", n));

      if (n < 0)
         throw new IllegalArgumentException("n must be greater than or equal to 0");

      this.n = n;

      if (0 == n)
      {
         // Create a tetrahedron geometry.
         // It has 4 vertices and 6 edges.
         addVertex(new Vertex( 1,  1,  1),
                   new Vertex(-1,  1, -1),
                   new Vertex( 1, -1, -1),
                   new Vertex(-1, -1,  1));

         // Create four triangular faces.
         // Make sure the faces are all
         // oriented in the same way!
         // Remember that front facing
         // should be counterclockwise
         // (and back facing is clockwise).
         addPrimitive(new Face(3, 1, 0),
                      new Face(3, 2, 1),
                      new Face(3, 0, 2),
                      new Face(0, 1, 2));
      }
      else
      {
         addNestedModel(subSponges(n - 1,  1,  1,  1),
                        subSponges(n - 1, -1,  1, -1),
                        subSponges(n - 1,  1, -1, -1),
                        subSponges(n - 1, -1, -1,  1));
      }
   }


   /**
      Recursive helper function.
      <p>
      This function builds the four sub models needed
      for one recusive step.

      @param n    number of recursive iterations
      @param pmX  plus or minus 1 for x-direction
      @param pmY  plus or minus 1 for y-direction
      @param pmZ  plus or minus 1 for z-direction
      @return     {@link Model} holding sub tree of sponges
   */
   private Model subSponges(final int n,
                            final int pmX, final int pmY, final int pmZ)
   {
      assert (n >= 0);

      final Model model =
         new Model("Sierpinski Sponge: level "+n+" ("+pmX+", "+pmY+", "+pmZ+")",
                   Matrix.translate(pmX*0.5, pmY*0.5, pmZ*0.5)
            .times(Matrix.scale(0.5, 0.5, 0.5)));

      if (0 == n) // stop the recursion
      {
         // Create a tetrahedron geometry.
         // It has 4 vertices and 6 edges.
         model.addVertex(new Vertex( 1,  1,  1),
                         new Vertex(-1,  1, -1),
                         new Vertex( 1, -1, -1),
                         new Vertex(-1, -1,  1));

         // Create four triangular faces.
         // Make sure the faces are all
         // oriented in the same way!
         // Remember that front facing
         // should be counterclockwise
         // (and back facing is clockwise).
         model.addPrimitive(new Face(3, 1, 0),
                            new Face(3, 2, 1),
                            new Face(3, 0, 2),
                            new Face(0, 1, 2));
      }
      else
      {
         model.addNestedModel(subSponges(n - 1,  1,  1,  1),
                              subSponges(n - 1, -1,  1, -1),
                              subSponges(n - 1,  1, -1, -1),
                              subSponges(n - 1, -1, -1,  1));
      }
      return model;
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return n;}

   @Override
   public SierpinskiSponge remake(final int n, final int k)
   {
      final int newN;
      if (n != this.n)
         newN = n;
      else
         newN = k;
      return new SierpinskiSponge(newN);
   }
}//SierpinskiSponge
