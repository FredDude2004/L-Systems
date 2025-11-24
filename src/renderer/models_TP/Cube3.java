/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a cube with its center
   at the origin, having edge length 2, and with its
   corners at {@code (±1, ±1, ±1)}.
<p>
   This version of the cube model has each face of
   the cube cut up by a triangle fan.
<p>
   Here is a picture showing how the cube's eight
   corners are labeled.
<pre>{@code
                  v4=(-1,1,-1)
                  +---------------------+ v5=(1,1,-1)
                 /|                    /|
                / |                   / |
               /  |                  /  |
              /   |                 /   |
             /    |                /    |
         v7 +---------------------+ v6  |
            |     |               |     |
            |     |               |     |
            |     | v0=(-1,-1,-1) |     |
            |     +---------------|-----+ v1=(1,-1,-1)
            |    /                |    /
            |   /                 |   /
            |  /                  |  /
            | /                   | /
            |/                    |/
            +---------------------+
            v3=(-1,-1,1)          v2=(1,-1,1)
}</pre>

   @see Cube
   @see Cube2
   @see Cube4
*/
public class Cube3 extends Model
{
   /**
      Create a cube with its center at the origin, having edge
      length 2, with its corners at {@code (±1, ±1, ±1)}. and
      with a triangle fan of four triangles in each face.
   */
   public Cube3( )
   {
      this(1, 1, 1);
   }


   /**
      Create a cube with its center at the origin, having edge
      length 2, with its corners at {@code (±1, ±1, ±1)}, and
      with each of the cube's faces containing a triangle fan
      with the given number of triangles along each of the x,
      y, and z directions.
      <p>
      There must be at least one triangle along each direction.

      @param xCount  number of triangles along the x-direction
      @param yCount  number of triangles along the y-direction
      @param zCount  number of triangles along the z-direction
      @throws IllegalArgumentException if {@code xCount} is less than 1
      @throws IllegalArgumentException if {@code yCount} is less than 1
      @throws IllegalArgumentException if {@code zCount} is less than 1
   */
   public Cube3(int xCount, int yCount, int zCount)
   {
      super( String.format("Cube3(%d,%d,%d)", xCount, yCount, zCount) );

      if (xCount < 1)
         throw new IllegalArgumentException("xCount must be greater than or equal to 1");
      if (yCount < 1)
         throw new IllegalArgumentException("yCount must be greater than or equal to 1");
      if (zCount < 1)
         throw new IllegalArgumentException("zCount must be greater than or equal to 1");

      final double xStep = 2.0 / xCount,
                   yStep = 2.0 / yCount,
                   zStep = 2.0 / zCount;

      addVertex(new Vertex(0,  0,  1),  // center front
                new Vertex(0,  0, -1),  // center back
                new Vertex(0,  1,  0),  // top center
                new Vertex(0, -1,  0),  // center bottom
                new Vertex( 1, 0,  0),  // center right
                new Vertex(-1, 0,  0)); // center left

      int index = 0;
      int cFront  = index;
      int cBack   = index + 1;
      int cTop    = index + 2;
      int cBottom = index + 3;
      int rCenter = index + 4;
      int lCenter = index + 5;
      index += 6;

      // Triangles along all four edges parallel to the x-axis.
      double x = -1.0;
      addVertex(new Vertex(x,  1,  1),   // index - 4
                new Vertex(x, -1,  1),   // index - 3
                new Vertex(x,  1, -1),   // index - 2
                new Vertex(x, -1, -1));  // index - 1
      index += 4;
      for (int i = 0; i < xCount; ++i)
      {
         x += xStep;
         addVertex(new Vertex(x,  1,  1),   // index + 0
                   new Vertex(x, -1,  1),   // index + 1
                   new Vertex(x,  1, -1),   // index + 2
                   new Vertex(x, -1, -1));  // index + 3

         final Primitive triangles = new Triangles();
         // front face, top and bottom edges
         triangles.addIndex(index-4, cFront, index+0);
         triangles.addIndex(index-3, index+1, cFront);
         // back face, top and bottom edges
         triangles.addIndex(index-2, index+2, cBack);
         triangles.addIndex(index-1, cBack, index+3);
         // top face, front and back edges
         triangles.addIndex(index-4, index+0, cTop);
         triangles.addIndex(index-2, cTop, index+2);
         // bottom face, front and back edges
         triangles.addIndex(index-3, cBottom, index+1);
         triangles.addIndex(index-1, index+3, cBottom);

         addPrimitive(triangles);
         index += 4;
      }

      // Triangles along all four edges parallel to the y-axis.
      double y = -1.0;
      addVertex(new Vertex( 1,  y,  1),   // index - 4
                new Vertex(-1,  y,  1),   // index - 3
                new Vertex( 1,  y, -1),   // index - 2
                new Vertex(-1,  y, -1));  // index - 1
      index += 4;
      for (int i = 0; i < yCount; ++i)
      {
         y += yStep;
         addVertex(new Vertex( 1,  y,  1),   // index + 0
                   new Vertex(-1,  y,  1),   // index + 1
                   new Vertex( 1,  y, -1),   // index + 2
                   new Vertex(-1,  y, -1));  // index + 3

         final Primitive triangles = new Triangles();
         // front face, right and left edges
         triangles.addIndex(index-4, index+0, cFront);
         triangles.addIndex(index-3, cFront,  index+1);
         // back face, right and left edges
         triangles.addIndex(index-2, cBack,   index+2);
         triangles.addIndex(index-1, index+3, cBack);
         // right face, front and back edges
         triangles.addIndex(index-4, rCenter, index+0);
         triangles.addIndex(index-2, index+2, rCenter);
         // left face, front and back edges
         triangles.addIndex(index-3, index+1, lCenter);
         triangles.addIndex(index-1, lCenter, index+3);

         addPrimitive(triangles);
         index += 4;
      }

      // Triangles along all four edges parallel to the z-axis.
      double z = -1.0;
      addVertex(new Vertex( 1,  1,  z),   // index - 4
                new Vertex(-1,  1,  z),   // index - 3
                new Vertex( 1, -1,  z),   // index - 2
                new Vertex(-1, -1,  z));  // index - 1
      index += 4;
      for (int i = 0; i < zCount; ++i)
      {
         z += zStep;
         addVertex(new Vertex( 1,  1,  z),   // index + 0
                   new Vertex(-1,  1,  z),   // index + 1
                   new Vertex( 1, -1,  z),   // index + 2
                   new Vertex(-1, -1,  z));  // index + 3

         final Primitive triangles = new Triangles();
         // top face, right and left edges
         triangles.addIndex(index-4, cTop, index+0);
         triangles.addIndex(index-3, index+1, cTop);
         // bottom face, right and left edges
         triangles.addIndex(index-2, index+2, cBottom);
         triangles.addIndex(index-1, cBottom, index+3);
         // right face, top and bottom edges
         triangles.addIndex(index-4, index+0, rCenter);
         triangles.addIndex(index-2, rCenter, index+2);
         // left face, top and bottom edges
         triangles.addIndex(index-3, lCenter, index+1);
         triangles.addIndex(index-1, index+3, lCenter);

         addPrimitive(triangles);
         index += 4;
      }
   }
}//Cube3
