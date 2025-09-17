/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_LP;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a wireframe model of a frustum of a right circular cone
   with its base in the xz-plane.
<p>
   See <a href="https://en.wikipedia.org/wiki/Frustum" target="_top">
                https://en.wikipedia.org/wiki/Frustum</a>

   @see Cone
   @see ConeSector
*/
public class ConeFrustum extends Model implements MeshMaker
{
   public final double r1;
   public final double r2;
   public final double h;
   public final int n;
   public final int k;

   /**
      Create a frustum of a right circular cone with its base in the
      xz-plane, a base radius of 1, top radius of 1/2, and height 1/2.
   */
   public ConeFrustum( )
   {
      this(1.0, 0.5, 0.5, 7, 16);
   }


   /**
      Create a frustum of a right circular cone with its base in the
      xz-plane, a base radius of {@code r}, top of the frustum at
      height {@code h}, and with the cone's apex on the y-axis at
      height {@code a}.
   <p>
      There must be at least three lines of longitude and at least
      two circles of latitude.

      @param n  number of circles of latitude
      @param k  number of lines of longitude
      @param r  radius of the base in the xz-plane
      @param h  height of the frustum
      @param a  height of the apex of the cone
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public ConeFrustum(final int n, final int k,
                      final double r, final double h, final double a)
   {
      this(r, (1 - h/a)*r, h, n, k);
   }


   /**
      Create a frustum of a right circular cone with its base in the
      xz-plane, a base radius of {@code r1}, top radius of {@code r2},
      and height {@code h}.
   <p>
      This model works with either {@code r1 > r2} or {@code r1 < r2}.
      In other words, the frustum can have its "apex" either above or
      below the xz-plane.
   <p>
      There must be at least three lines of longitude and at least
      two circles of latitude.

      @param r1  radius of the base of the frustum
      @param h   height of the frustum
      @param r2  radius of the top of the frustum
      @param n   number of circles of latitude
      @param k   number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public ConeFrustum(final double r1, final double h, final double r2,
                      final int n, final int k)
   {
      super(String.format("Cone Frustum(%.2f,%.2f,%.2f,%d,%d)",
                                        r1,  h,   r2,  n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      this.r1 = r1;
      this.r2 = r2;
      this.h = h;
      this.n = n;
      this.k = k;

      // Create the frustum's geometry.

      final double deltaTheta = (2 * Math.PI) / k;

      // An array of indexes to be used to create line segments.
      final int[][] indexes = new int[n][k];

      // Create all the vertices.
      int index = 0;
      for (int j = 0; j < k; ++j) // choose an angle of longitude
      {
         final double c = Math.cos(j * deltaTheta);
         final double s = Math.sin(j * deltaTheta);
         for (int i = 0; i < n; ++i) // choose a circle of latitude
         {
            final double slantRadius = (i/(double)(n-1)) * r1 + ((n-1-i)/(double)(n-1)) * r2;
            addVertex( new Vertex(slantRadius * c,
                                  h - (i*h)/(n-1),
                                  slantRadius * s) );
            indexes[i][j] = index;
            ++index;
         }
      }
      addVertex( new Vertex(0, h, 0) );  // top center
      final int topCenterIndex = index;
      ++index;
      addVertex( new Vertex(0, 0, 0) );  // bottom center
      final int bottomCenterIndex = index;
      ++index;

      // Create all the horizontal circles of latitude around the frustum wall.
      for (int i = 0; i < n; ++i)
      {
         addPrimitive(new LineLoop(indexes[i]));
      }

      // Create the slanted lines of longitude from the base
      // to the top circle of latitude.
      for (int j = 0; j < k; ++j)
      {
         final int[] indices = new int[n];
         for (int i = 0; i < n; ++i)
         {
            indices[i] = indexes[i][j];
         }
         addPrimitive(new LineStrip(indices));
      }

      // Create the line fan at the top.
      int[] indices = new int[k + 1];
      indices[0] = topCenterIndex;
      for (int j = 0; j < k; ++j)
      {
         indices[j + 1] = indexes[0][j];
      }
      addPrimitive(new LineFan(indices));

      // Create the line fan at the base.
      indices = new int[k + 1];
      indices[0] = bottomCenterIndex;
      for (int j = 0; j < k; ++j)
      {
         indices[j + 1] = indexes[n - 1][j];
      }
      addPrimitive(new LineFan(indices));
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override
   public ConeFrustum remake(final int n, final int k)
   {
      return new ConeFrustum(this.r1, this.h, this.r2,
                             n, k);
   }
}//ConeFrustum
