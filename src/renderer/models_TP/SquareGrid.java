/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker_TP;
import renderer.scene.util.MeshType;

/**
   Create a wireframe model of a square in the xy-plane centered at the origin.
*/
public class SquareGrid extends Model implements MeshMaker_TP
{
   public final double r;
   public final int n;
   public final int k;
   public final MeshType type;

   /**
      Create a square in the xy-plane with corners {@code (±1, ±1, 0)}.
   */
   public SquareGrid( )
   {
      this(1, 0, 0);
   }


   /**
      Create a square in the xy-plane with corners {@code (±1, ±1, 0)} and
      with {@code n} grid lines parallel to each of the x and y axes.

      @param n  number of grid lines parallel to the axes
      @throws IllegalArgumentException if {@code n} is less than 0
   */
   public SquareGrid(final int n)
   {
      this(1, n, n);
   }


   /**
      Create a square in the xy-plane with corners {@code (±1, ±1, 0)} and
      with {@code n} grid lines parallel to the x-axis and
      with {@code k} grid lines parallel to the y-axis.
   <p>
      If there are {@code n} grid lines parallel to the x-axis, then each
      grid line parallel to the y-axis will have {@code n+1} line segments.
      If there are {@code k} grid lines parallel to the y-axis, then each
      grid line parallel to the x-axis will have {@code k+1} line segments.

      @param n  number of grid lines parallel to the x-axis
      @param k  number of grid lines parallel to the y-axis
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 0
   */
   public SquareGrid(final int n, final int k)
   {
      this(1, n, k);
   }


   /**
      Create a square in the xy-plane with corners {@code (±r, ±r, 0)}.

      @param r  determines the corners of the square
      @throws IllegalArgumentException if {@code r} is less than or equal to 0
   */
   public SquareGrid(final double r)
   {
      this(r, 0, 0);
   }


   /**
      Create a square in the xy-plane with corners {@code (±r, ±r, 0)} and
      with {@code n} grid lines parallel to each of the x and y axes.

      @param r  determines the corners of the square
      @param n  number of grid lines parallel to the axes
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code r} is less than or equal to 0
   */
   public SquareGrid(final double r, final int n)
   {
      this(r, n, n);
   }


   /**
      Create a square in the xy-plane with corners {@code (±r, ±r, 0)} and
      with {@code n} grid lines parallel to the x-axis and
      with {@code k} grid lines parallel to the y-axis.
   <p>
      If there are {@code n} grid lines parallel to the x-axis, then each
      grid line parallel to the y-axis will have {@code n+1} line segments.
      If there are {@code k} grid lines parallel to the y-axis, then each
      grid line parallel to the x-axis will have {@code k+1} line segments.

      @param r  determines the corners of the square
      @param n  number of grid lines parallel to the x-axis
      @param k  number of grid lines parallel to the y-axis
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 0
      @throws IllegalArgumentException if {@code r} is less than or equal to 0
   */
   public SquareGrid(final double r, final int n, final int k)
   {
      this(r, n, k, MeshType.CHECKER);
   }


   /**
      Create a square in the xy-plane with corners {@code (±r, ±r, 0)} and
      with {@code n} grid lines parallel to the x-axis and
      with {@code k} grid lines parallel to the y-axis.
   <p>
      If there are {@code n} grid lines parallel to the x-axis, then each
      grid line parallel to the y-axis will have {@code n+1} line segments.
      If there are {@code k} grid lines parallel to the y-axis, then each
      grid line parallel to the x-axis will have {@code k+1} line segments.

      @param r  determines the corners of the square
      @param n  number of grid lines parallel to the x-axis
      @param k  number of grid lines parallel to the y-axis
      @param type  choose between horizoantal, vertical, and checkerboard triangle strips
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 0
      @throws IllegalArgumentException if {@code r} is less than or equal to 0
   */
   public SquareGrid(final double r, final int n, final int k,
                     final MeshType type)
   {
      super(String.format("Square Grid(%.2f,%d,%d)", r, n, k));

      if (n < 0)
         throw new IllegalArgumentException("n must be greater than or equal to 0");
      if (k < 0)
         throw new IllegalArgumentException("k must be greater than or equal to 0");
      if (r <= 0)
         throw new IllegalArgumentException("r must be greater than 0");

      this.r = r;
      this.n = n;
      this.k = k;
      this.type = type;

      // Create the square's geometry.

      final double xStep = (2 * r) / (1 + k),
                   yStep = (2 * r) / (1 + n);

      // An array of vertices to be used to create the faces.
      final Vertex[][] v = new Vertex[n+2][k+2];

      // Create all the vertices.
      for (int i = 0; i <= n + 1; ++i)
      {
         for (int j = 0; j <= k + 1; ++j)
         {
            // from top-to-bottom and left-to-right
            v[i][j] = new Vertex(r - j * xStep, -r + i * yStep, 0);
         }
      }

      // Add all of the vertices to this model.
      for (int i = 0; i < n + 2; ++i)
      {
         for (int j = 0; j < k + 2; ++j)
         {
            addVertex( v[i][j] );
         }
      }

      if (MeshType.HORIZONTAL == type)
      {
         for (int i = 0; i < n + 1; ++i) // choose a line of latitude
         {
            final Primitive triStrip = new TriangleStrip();
            for (int j = 0; j < k + 2; ++j) // choose a line of longitude
            {
               triStrip.addIndex((i+0)*(k+2) + j); // v[i+0][j]
               triStrip.addIndex((1+i)*(k+2) + j); // v[i+1][j]
            }
            addPrimitive(triStrip);
         }
      }
      else if (MeshType.VERTICAL == type)
      {
         for (int j = 0; j < k + 1; ++j) // choose a line of longitude
         {
            final Primitive triStrip = new TriangleStrip();
            for (int i = 0; i < n + 2; ++i) // choose a line of latitude
            {
               triStrip.addIndex((i+0)*(k+2) + j+1);  // v[i][j+1]
               triStrip.addIndex((i+0)*(k+2) + j);    // v[i][j  ]
            }
            addPrimitive(triStrip);
         }
      }
      else // MeshType.CHECKER triangle strips
      {
         for (int i = 0; i < n + 1; ++i)
         {
            for (int j = 0; j < k + 1; ++j)
            {
               addPrimitive(new TriangleStrip(
                                (i+0)*(k+2) + (j+0),   // v[i+0][j+0]
                                (i+1)*(k+2) + (j+0),   // v[i+1][j+0]
                                (i+0)*(k+2) + (j+1),   // v[i+0][j+1]
                                (i+1)*(k+2) + (j+1))); // v[i+1][j+1]
            }
         }
      }
   }



   // Implement the MeshMaker_TP interface (four methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override public MeshType getType() {return type;}

   @Override
   public SquareGrid remake(final int n, final int k, final MeshType type)
   {
      return new SquareGrid(this.r, n, k, type);
   }
}//SquareGrid
