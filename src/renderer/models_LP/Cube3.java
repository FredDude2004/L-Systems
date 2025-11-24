/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_LP;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a wireframe model of a cube with its center
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

      // We need six LineFan primitives, one for each face of the cube.
      Primitive bottomFan = new LineFan();
      Primitive    topFan = new LineFan();
      Primitive   backFan = new LineFan();
      Primitive  frontFan = new LineFan();
      Primitive   leftFan = new LineFan();
      Primitive  rightFan = new LineFan();
      // Each line fan begins with a vertex at the center of its face.
      addVertex(new Vertex( 0, -1,  0),   // center bottom
                new Vertex( 0,  1,  0),   // center top
                new Vertex( 0,  0, -1),   // center back
                new Vertex( 0,  0,  1),   // center front
                new Vertex(-1,  0,  0),   // center feft
                new Vertex( 1,  0,  0));  // center right
      bottomFan.addIndex(0);
         topFan.addIndex(1);
        backFan.addIndex(2);
       frontFan.addIndex(3);
        leftFan.addIndex(4);
       rightFan.addIndex(5);

      // A counter of vertices in the vertex list.
      int index = 6;

      // All the vertices around the bottom face.
      double x = -1.0;
      for (int i = 0; i < xCount; ++i)
      {
         addVertex(new Vertex(x, -1, -1));
         x += xStep;
         bottomFan.addIndex(index);
           backFan.addIndex(index);
         index++;
      }
      double z = -1.0;
      for (int i = 0; i < zCount; ++i)
      {
         addVertex(new Vertex(1, -1, z));
         z += zStep;
         bottomFan.addIndex(index);
          rightFan.addIndex(index);
         index++;
      }
      x = 1.0;
      for (int i = 0; i < xCount; ++i)
      {
         addVertex(new Vertex(x, -1, 1));
         x -= xStep;
         bottomFan.addIndex(index);
           frontFan.addIndex(index);
         index++;
      }
      z = 1.0;
      for (int i = 0; i < zCount; ++i)
      {
         addVertex(new Vertex(-1, -1, z));
         z -= zStep;
         bottomFan.addIndex(index);
           leftFan.addIndex(index);
         index++;
      }

      // All the vertices around the top face
      x = -1.0;
      for (int i = 0; i < xCount; ++i)
      {
         addVertex(new Vertex(x, 1, -1));
         x += xStep;
          topFan.addIndex(index);
         backFan.addIndex(index);
         index++;
      }
      z = -1.0;
      for (int i = 0; i < zCount; ++i)
      {
         addVertex(new Vertex(1, 1, z));
         z += zStep;
           topFan.addIndex(index);
         rightFan.addIndex(index);
         index++;
      }
      x = 1.0;
      for (int i = 0; i < xCount; ++i)
      {
         addVertex(new Vertex(x, 1, 1));
         x -= xStep;
           topFan.addIndex(index);
         frontFan.addIndex(index);
         index++;
      }
      z = 1.0;
      for (int i = 0; i < zCount; ++i)
      {
         addVertex(new Vertex(-1, 1, z));
         z -= zStep;
          topFan.addIndex(index);
         leftFan.addIndex(index);
         index++;
      }

      // The vertices for the four vertical edges.
      double y = -1.0;
      for (int i = 1; i < yCount; ++i)
      {
         y += yStep;
         addVertex(new Vertex(-1, y, -1));
         backFan.addIndex(index);
         leftFan.addIndex(index);
         index++;
      }
      y = -1.0;
      for (int i = 1; i < yCount; ++i)
      {
         y += yStep;
         addVertex(new Vertex(1, y, -1));
          backFan.addIndex(index);
         rightFan.addIndex(index);
         index++;
      }
      y = -1.0;
      for (int i = 1; i < yCount; ++i)
      {
         y += yStep;
         addVertex(new Vertex(1, y, 1));
         frontFan.addIndex(index);
         rightFan.addIndex(index);
         index++;
      }
      y = -1.0;
      for (int i = 1; i < yCount; ++i)
      {
         y += yStep;
         addVertex(new Vertex(-1, y, 1));
         frontFan.addIndex(index);
          leftFan.addIndex(index);
         index++;
      }

      // Line loop around the bottom face.
      addPrimitive(new LineLoop(6,
                                6 + xCount,
                                6 + xCount+zCount,
                                6 + 2*xCount+zCount));
      // line loop around the top face.
      addPrimitive(new LineLoop(6 + 2*xCount+2*zCount,
                                6 + 3*xCount+2*zCount,
                                6 + 3*xCount+3*zCount,
                                6 + 4*xCount+3*zCount));
      // Four vertical edges.
      addPrimitive(new Lines(6,                   6 + 2*xCount+2*zCount,
                             6 + xCount,          6 + 3*xCount+2*zCount,
                             6 + xCount+zCount,   6 + 3*xCount+2*zCount,
                             6 + 2*xCount+zCount, 6 + 4*xCount+3*zCount));

      addPrimitive(bottomFan,
                      topFan,
                     backFan,
                    frontFan,
                     leftFan,
                    rightFan);
   }
}//Cube3
