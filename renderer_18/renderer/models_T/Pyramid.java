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
   Create a solid model of a right square pyramid with its
   base in the xz-plane and its apex on the positive y-axis.
<p>
   See <a href="https://en.wikipedia.org/wiki/Pyramid_(geometry)" target="_top">
                https://en.wikipedia.org/wiki/Pyramid_(geometry)</a>

   @see PyramidFrustum
*/
public class Pyramid extends Model implements MeshMaker
{
   public final double s;
   public final double h;
   public final int n;
   public final int k;

   /**
      Create a right square pyramid with its base in the xz-plane,
      a base side length of 2, height 1, and apex on the positive y-axis.
   */
   public Pyramid( )
   {
      this(2.0, 1.0, 15, 4);
   }


   /**
      Create a right square pyramid with its base in the xz-plane,
      a base length of {@code s}, height {@code h}, and apex on the
      positive y-axis.

      @param s  side length of the base in the xz-plane
      @param h  height of the apex on the y-axis
   */
   public Pyramid(final double s, final double h)
   {
      super(String.format("Pyramid(%.2f,%.2f)", s, h));

      this.s = s;
      this.h = h;
      this.n = 1;
      this.k = 1;

      // Create the pyramid's geometry.
      addVertex(new Vertex(-s/2, 0, -s/2),  // base
                new Vertex(-s/2, 0,  s/2),
                new Vertex( s/2, 0,  s/2),
                new Vertex( s/2, 0, -s/2),
                new Vertex(  0,  h,   0));  // apex

      // Create 6 triangles.
      // Make sure the triangles are all
      // oriented in the same way!
      addPrimitive(new Triangle(0, 1, 3),  // 2 base triangles
                   new Triangle(2, 3, 1),
                   new Triangle(4, 0, 1),  // 4 sides
                   new Triangle(4, 1, 2),
                   new Triangle(4, 2, 3),
                   new Triangle(4, 3, 0));
   }


   /**
      Create a right square pyramid with its base in the xz-plane,
      a base length of {@code s}, height {@code h}, and apex on the
      positive y-axis.

      @param s  side length of the base in the xz-plane
      @param h  height of the apex on the y-axis
      @param n  number of lines of latitude around the body of the pyramid
      @param k  number of triangles in the triangle fan at the top of each side
      @throws IllegalArgumentException if {@code n} is less than 1
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public Pyramid(final double s, final double h,
                  final int n, final int k)
   {
      this(s, h, n, k, false);
   }


   /**
      Create a right square pyramid with its base in the xz-plane,
      a base length of {@code s}, height {@code h}, and apex on the
      positive y-axis.
   <p>
      The last parameter provides a choice between having a square
      grid of lines or a line fan in the base of the pyramid.

      @param s  side length of the base in the xz-plane
      @param h  height of the apex on the y-axis
      @param n  number of lines of latitude around the body of the pyramid
      @param k  number of triangles in the triangle fan at the top of each side
      @param grid  choose either a square grid or a line fan in the base
      @throws IllegalArgumentException if {@code n} is less than 1
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public Pyramid(double s, double h,
                  final int n, final int k,
                  final boolean grid)
   {
      super(String.format("Pyramid(%.2f,%.2f,%d,%d)", s, h, n, k));

      if (n < 1)
         throw new IllegalArgumentException("n must be greater than 0");
      if (k < 1)
         throw new IllegalArgumentException("k must be greater than 0");

      this.s = s;
      this.h = h;
      this.n = n;
      this.k = k;

      // Create the pyramid's geometry.
      final double deltaH = h / n;

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][4*k + 1];

      // Create the vertices.
      for (int i = 0; i < n; ++i) // choose a height of latitude
      {
         final double y = i * deltaH;
       //final double slantSide = s * (1.0 - i*(1.0 / n));
         final double slantSide = s - i*(s / n);
         final double deltaS = slantSide / k;

         for (int j = 0; j < k; ++j)
         {
            v[i][j] = new Vertex(-slantSide/2 + j*deltaS,
                                  y,
                                 -slantSide/2);

            v[i][k+j] = new Vertex( slantSide/2,
                                    y,
                                   -slantSide/2 + j*deltaS);

            v[i][2*k+j] = new Vertex( slantSide/2 - j*deltaS,
                                      y,
                                      slantSide/2);

            v[i][3*k+j] = new Vertex(-slantSide/2,
                                      y,
                                      slantSide/2 - j*deltaS);
         }
         // create one more vertex to close the latitude
         v[i][4*k] = new Vertex(v[i][0].x, v[i][0].y, v[i][0].z);
      }

      // Add the vertices to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < 4*k + 1; ++j)
         {
            addVertex( v[i][j] );
         }
      }
      addVertex(new Vertex(0, h, 0),
                new Vertex(0, 0, 0));
      final int apexIndex = n * (4*k + 1),
                bottomCenterIndex = apexIndex + 1;


      // Create the triangle fan at the apex.
      for (int j = 0; j < 4*k; ++j)
      {  //                                     v[n-1][j+1]         v[n-1][j]
         addPrimitive(new Triangle(apexIndex, (n-1)*(4*k+1)+j+1, (n-1)*(4*k+1)+j));
      }

      // Create all the triangle strips around the pyramid wall.
      for (int i = 0; i < n - 1; ++i) // choose a height of latitude
      {
         for (int j = 0; j < 4*k; ++j)
         {  //                          v[i][j]      v[i+1][j]         v[i+1][j+1]
            addPrimitive(new Triangle(i*(4*k+1)+j, (i+1)*(4*k+1)+j, (i+1)*(4*k+1)+j+1));
            //                         v[i+1][j+1]      v[i][j+1]      v[i][j]
            addPrimitive(new Triangle((i+1)*(4*k+1)+j+1, i*(4*k+1)+j+1, i*(4*k+1)+j));
         }
      }

      if (grid)
      {
         // Create the grid in the base of the pyramid.
         // Arrays of indices to be used to create the grid.
         final int[][] vGrid = new int[k+1][k+1];
         int vIndex = vertexList.size();
         // Create vertices for the bottom grid.
         final double deltaS = s / k;
         for (int i = 0; i < k + 1; ++i)
         {
            for (int j = 0; j < k + 1; ++j)
            {
               addVertex(new Vertex(-s/2 + i * deltaS,
                                     0,
                                    -s/2 + j * deltaS));
               vGrid[i][j] = vIndex;
               vIndex += 1;
            }
         }
         // Create the triangles in the base grid.
         for (int i = 0; i < k; ++i)
         {
            for (int j = 0; j < k; ++j)
            {
               addPrimitive(new Triangle(vGrid[i  ][j  ],
                                         vGrid[i+1][j  ],
                                         vGrid[i+1][j+1]));
               addPrimitive(new Triangle(vGrid[i+1][j+1],
                                         vGrid[i  ][j+1],
                                         vGrid[i  ][j  ]));
            }
         }
      }
      else
      {
         // Create the triangle fan in the base.
         for (int j = 0; j < 4*k; ++j)
         {  //                                           v[0][j]  v[0][j+1]
            addPrimitive(new Triangle(bottomCenterIndex,    j,       j+1 ));
         }
      }
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override
   public Pyramid remake(final int n, final int k)
   {
      return new Pyramid(this.s, this.h,
                         n, k);
   }
}//Pyramid
