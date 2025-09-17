/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a solid model of a Menger Sponge centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Menger_sponge" target="_top">
                https://en.wikipedia.org/wiki/Menger_sponge</a>
*/
public class MengerSponge extends Model implements MeshMaker
{
   final int n;

   /**
      Create a Menger Sponge centered at the origin
      using three recursive iterations.
   */
   public MengerSponge()
   {
      this(3);
   }


   /**
      Create a Menger Sponge centered at the origin
      using {@code n} recursive iterations.

      @param n  number of recursive iterations
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public MengerSponge(final int n)
   {
      super(String.format("Menger_Sponge(%d)", n));

      if (n < 0)
         throw new IllegalArgumentException("n must be greater than or equal to 0");

      this.n = n;

      if (0 == n)
      {
         // Create a cube geometry.
         addVertex(new Vertex(-1, -1, -1), // four vertices around the bottom face
                   new Vertex( 1, -1, -1),
                   new Vertex( 1, -1,  1),
                   new Vertex(-1, -1,  1),
                   new Vertex(-1,  1, -1), // four vertices around the top face
                   new Vertex( 1,  1, -1),
                   new Vertex( 1,  1,  1),
                   new Vertex(-1,  1,  1));

         // Create the 6 faces of the cube.
         final Primitive bottomAndTop = new Triangles(0,1,2, 2,3,0, 7,6,5, 5,4,7),
                                sides = new TriangleStrip(0, 4, 1, 5, 2, 6, 3, 7, 0, 4);
         addPrimitive(bottomAndTop,
                      sides);
      }
      else
      {
         if (true) { // use "false" to erase the extra line segments
         // create 24 vertices for 12 line segements
         addVertex(new Vertex(-1,       -1, -1.0/3.0), // eight vertices in the bottom face
                   new Vertex(-1,       -1,  1.0/3.0),
                   new Vertex( 1,       -1, -1.0/3.0),
                   new Vertex( 1,       -1,  1.0/3.0),
                   new Vertex(-1.0/3.0, -1, -1),
                   new Vertex( 1.0/3.0, -1, -1),
                   new Vertex(-1.0/3.0, -1,  1),
                   new Vertex( 1.0/3.0, -1,  1),
                   new Vertex(-1,        1, -1.0/3.0), // eight vertices in the top face
                   new Vertex(-1,        1,  1.0/3.0),
                   new Vertex( 1,        1, -1.0/3.0),
                   new Vertex( 1,        1,  1.0/3.0),
                   new Vertex(-1.0/3.0,  1, -1),
                   new Vertex( 1.0/3.0,  1, -1),
                   new Vertex(-1.0/3.0,  1,  1),
                   new Vertex( 1.0/3.0,  1,  1),
                   new Vertex(-1, -1.0/3.0, -1), // four vertices in the back face
                   new Vertex(-1,  1.0/3.0, -1),
                   new Vertex( 1, -1.0/3.0, -1),
                   new Vertex( 1,  1.0/3.0, -1),
                   new Vertex(-1, -1.0/3.0,  1), // four vertices in the front face
                   new Vertex(-1,  1.0/3.0,  1),
                   new Vertex( 1, -1.0/3.0,  1),
                   new Vertex( 1,  1.0/3.0,  1));

         // Create 12 line segments.
         addPrimitive(new LineSegment( 0,  1), // bottom face
                      new LineSegment( 2,  3),
                      new LineSegment( 4,  5),
                      new LineSegment( 6,  7),
                      new LineSegment( 8,  9), // top face
                      new LineSegment(10, 11),
                      new LineSegment(12, 13),
                      new LineSegment(14, 15),
                      new LineSegment(16, 17), // back face
                      new LineSegment(18, 19),
                      new LineSegment(20, 21), // front face
                      new LineSegment(22, 23));
         }

         addNestedModel(subSponges(n - 1, -1, -1, -1),
                        subSponges(n - 1, -1, -1,  1),
                        subSponges(n - 1, -1,  1, -1),
                        subSponges(n - 1, -1,  1,  1),
                        subSponges(n - 1,  1, -1, -1),
                        subSponges(n - 1,  1, -1,  1),
                        subSponges(n - 1,  1,  1, -1),
                        subSponges(n - 1,  1,  1,  1));
      }
   }


   /**
      Recursive helper function.
      <p>
      This function builds the eight sub models needed
      for one recusive step. These sub models will not
      be touching each other. In order to make the wireframe
      Menger Sponge look a bit better, we add line segments
      that connect some of the edges between the sub models.

      @param n    number of recursive iterations
      @param pmX  plus or minus 1 for x-direction
      @param pmY  plus or minus 1 for y-direction
      @param pmZ  plus or minus 1 for z-direction
      @return     {@link Model} holding sub tree of sponges
   */
   private Model subSponges(final int n, final int pmX, final int pmY, final int pmZ)
   {
      assert (n >= 0);

      final Model model = new Model("Menger Sponge: level "+n+" ("+pmX+", "+pmY+", "+pmZ+")",
                                    Matrix.translate(pmX*2.0/3.0, pmY*2.0/3.0, pmZ*2.0/3.0)
                             .times(Matrix.scale(1.0/3.0, 1.0/3.0, 1.0/3.0)));

      if (0 == n) // stop the recursion
      {
         // Create a cube geometry.
         model.addVertex(new Vertex(-1, -1, -1), // four vertices around the bottom face
                         new Vertex( 1, -1, -1),
                         new Vertex( 1, -1,  1),
                         new Vertex(-1, -1,  1),
                         new Vertex(-1,  1, -1), // four vertices around the top face
                         new Vertex( 1,  1, -1),
                         new Vertex( 1,  1,  1),
                         new Vertex(-1,  1,  1));

         // Create the 6 faces of the cube.
         final Primitive bottomAndTop = new Triangles(0,1,2, 2,3,0, 7,6,5, 5,4,7),
                                sides = new TriangleStrip(0, 4, 1, 5, 2, 6, 3, 7, 0, 4);
         model.addPrimitive(bottomAndTop,
                            sides);
      }
      else
      {
         if (true) { // provide a way to erase the extra line segments
         // create 24 vertices for 12 line segements
         model.addVertex(new Vertex(-1,       -1, -1.0/3.0), // eight vertices in the bottom face
                         new Vertex(-1,       -1,  1.0/3.0),
                         new Vertex( 1,       -1, -1.0/3.0),
                         new Vertex( 1,       -1,  1.0/3.0),
                         new Vertex(-1.0/3.0, -1, -1),
                         new Vertex( 1.0/3.0, -1, -1),
                         new Vertex(-1.0/3.0, -1,  1),
                         new Vertex( 1.0/3.0, -1,  1),
                         new Vertex(-1,        1, -1.0/3.0), // eight vertices in the top face
                         new Vertex(-1,        1,  1.0/3.0),
                         new Vertex( 1,        1, -1.0/3.0),
                         new Vertex( 1,        1,  1.0/3.0),
                         new Vertex(-1.0/3.0,  1, -1),
                         new Vertex( 1.0/3.0,  1, -1),
                         new Vertex(-1.0/3.0,  1,  1),
                         new Vertex( 1.0/3.0,  1,  1),
                         new Vertex(-1, -1.0/3.0, -1), // four vertices in the back face
                         new Vertex(-1,  1.0/3.0, -1),
                         new Vertex( 1, -1.0/3.0, -1),
                         new Vertex( 1,  1.0/3.0, -1),
                         new Vertex(-1, -1.0/3.0,  1), // four vertices in the front face
                         new Vertex(-1,  1.0/3.0,  1),
                         new Vertex( 1, -1.0/3.0,  1),
                         new Vertex( 1,  1.0/3.0,  1));

         // Create 12 line segments.
         model.addPrimitive(new LineSegment( 0,  1), // bottom face
                            new LineSegment( 2,  3),
                            new LineSegment( 4,  5),
                            new LineSegment( 6,  7),
                            new LineSegment( 8,  9), // top face
                            new LineSegment(10, 11),
                            new LineSegment(12, 13),
                            new LineSegment(14, 15),
                            new LineSegment(16, 17), // back face
                            new LineSegment(18, 19),
                            new LineSegment(20, 21), // front face
                            new LineSegment(22, 23));
         }

         model.addNestedModel(subSponges(n - 1, -1, -1, -1),
                              subSponges(n - 1, -1, -1,  1),
                              subSponges(n - 1, -1,  1, -1),
                              subSponges(n - 1, -1,  1,  1),
                              subSponges(n - 1,  1, -1, -1),
                              subSponges(n - 1,  1, -1,  1),
                              subSponges(n - 1,  1,  1, -1),
                              subSponges(n - 1,  1,  1,  1));
      }
      return model;
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return n;}

   @Override
   public MengerSponge remake(final int n, final int k)
   {
      final int newN;
      if (n != this.n)
         newN = n;
      else
         newN = k;
      return new MengerSponge(newN);
   }
}//MengerSponge
