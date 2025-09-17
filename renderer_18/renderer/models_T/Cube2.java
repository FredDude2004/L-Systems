/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_T;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a cube with its center
   at the origin, having edge length 2, and with its
   corners at {@code (±1, ±1, ±1)}.
<p>
   This version of the cube model has each face of
   the cube cut up by an n by m grid of lines.
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
   @see Cube3
   @see Cube4
*/
public class Cube2 extends Model
{
   /**
      Create a cube with its center at the origin, having edge
      length 2, with its corners at {@code (±1, ±1, ±1)}. and
      with two perpendicular grid lines going across the middle
      of each of the cube's faces.
   */
   public Cube2( )
   {
      this(1, 1, 1);
   }


   /**
      Create a cube with its center at the origin, having edge
      length 2, with its corners at {@code (±1, ±1, ±1)}, and
      with each of the cube's faces containing the given number
      of grid lines parallel to the x, y, and z directions.

      @param xGrid  number of grid lines perpendicular to the x-axis
      @param yGrid  number of grid lines perpendicular to the y-axis
      @param zGrid  number of grid lines perpendicular to the z-axis
      @throws IllegalArgumentException if {@code xGrid} is less than 0
      @throws IllegalArgumentException if {@code yGrid} is less than 0
      @throws IllegalArgumentException if {@code zGrid} is less than 0
   */
   public Cube2(final int xGrid, final int yGrid, final int zGrid)
   {
      super("Cube2");

      if (xGrid < 0)
         throw new IllegalArgumentException("xGrid must be greater than or equal to 0");
      if (yGrid < 0)
         throw new IllegalArgumentException("yGrid must be greater than or equal to 0");
      if (zGrid < 0)
         throw new IllegalArgumentException("zGrid must be greater than or equal to 0");

      final double xStep = 2.0 / (1 + xGrid),
                   yStep = 2.0 / (1 + yGrid),
                   zStep = 2.0 / (1 + zGrid);

      // Create the top and bottom sides of the cube.
      // Arrays of indices to be used to create the triangles.
      final int[][] vT = new int[xGrid+2][zGrid+2];
            int[][] vB = new int[xGrid+2][zGrid+2];
      int vIndex = 0;
      // Create vertices for the top and bottom sides.
      for (int i = 0; i < xGrid + 2; ++i)
      {
         for (int j = 0; j < zGrid + 2; ++j)
         {
            addVertex(new Vertex(-1 + i * xStep,  1, -1 + j * zStep),
                      new Vertex(-1 + i * xStep, -1, -1 + j * zStep));
            vT[i][j] = vIndex + 0;
            vB[i][j] = vIndex + 1;
            vIndex += 2;
         }
      }
      // Create triangles for the top and bottom sides.
      for (int i = 0; i < xGrid + 1; ++i)
      {
         for (int j = 0; j < zGrid + 1; ++j)
         {
            addPrimitive(new Triangle(vT[i][j],     vT[i][j+1], vT[i+1][j+1]),
                         new Triangle(vT[i+1][j+1], vT[i+1][j], vT[i][j]));

            addPrimitive(new Triangle(vB[i][j],     vB[i+1][j], vB[i+1][j+1]),
                         new Triangle(vB[i+1][j+1], vB[i][j+1], vB[i][j]));
         }
      }


      // Create the front and back sides of the cube.
      // Arrays of indices to be used to create the triangles.
      final int[][] vF = new int[yGrid+2][xGrid+2];
                    vB = new int[yGrid+2][xGrid+2];
      // Create vertices for the front and back sides.
      for (int i = 0; i < yGrid + 2; ++i)
      {
         for (int j = 0; j < xGrid + 2; ++j)
         {
            addVertex(new Vertex(-1 + j * xStep, -1 + i * yStep,  1),
                      new Vertex(-1 + j * xStep, -1 + i * yStep, -1));
            vF[i][j] = vIndex + 0;
            vB[i][j] = vIndex + 1;
            vIndex += 2;
         }
      }
      // Create triangles for the front and back sides.
      for (int i = 0; i < yGrid + 1; ++i)
      {
         for (int j = 0; j < xGrid + 1; ++j)
         {
            addPrimitive(new Triangle(vF[i][j],     vF[i][j+1], vF[i+1][j+1]),
                         new Triangle(vF[i+1][j+1], vF[i+1][j], vF[i][j]));

            addPrimitive(new Triangle(vB[i][j],     vB[i+1][j], vB[i+1][j+1]),
                         new Triangle(vB[i+1][j+1], vB[i][j+1], vB[i][j]));
         }
      }


      // Create the right and left sides of the cube.
      // Arrays of indices to be used to create the triangles.
      final int[][] vR = new int[yGrid+2][zGrid+2],
                    vL = new int[yGrid+2][zGrid+2];
      // Create vertices for the right and left sides.
      for (int i = 0; i < yGrid + 2; ++i)
      {
         for (int j = 0; j < zGrid + 2; ++j)
         {
            addVertex(new Vertex( 1, -1 + i * yStep, -1 + j * zStep),
                      new Vertex(-1, -1 + i * yStep, -1 + j * zStep));
            vR[i][j] = vIndex + 0;
            vL[i][j] = vIndex + 1;
            vIndex += 2;
         }
      }
      // Create triangles for the right and left sides.
      for (int i = 0; i < yGrid + 1; ++i)
      {
         for (int j = 0; j < zGrid + 1; ++j)
         {
            addPrimitive(new Triangle(vR[i][j],     vR[i+1][j], vR[i+1][j+1]),
                         new Triangle(vR[i+1][j+1], vR[i][j+1], vR[i][j]));

            addPrimitive(new Triangle(vL[i][j],     vL[i][j+1], vL[i+1][j+1]),
                         new Triangle(vL[i+1][j+1], vL[i+1][j], vL[i][j]));
         }
      }
   }
}//Cube2
