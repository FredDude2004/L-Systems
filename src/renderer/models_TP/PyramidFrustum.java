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
   Create a solid model of a frustum of a right square pyramid
   with its base in the xz-plane.
<p>
   See <a href="https://en.wikipedia.org/wiki/Frustum" target="_top">
                https://en.wikipedia.org/wiki/Frustum</a>

   @see Pyramid
*/
public class PyramidFrustum extends Model implements MeshMaker_TP
{
   public final double s1;
   public final double s2;
   public final double h;
   public final int n;
   public final int k;
   public final MeshType type;

   /**
      Create a frustum of a right square pyramid with its base in the
      xz-plane, a base side length of 2, top side length of 1, and height 1/2.
   */
   public PyramidFrustum( )
   {
      this(2.0, 1.0, 0.5, 7, 4);
   }


   /**
      Create a frustum of a right square pyramid with its base in the
      xz-plane, a base side length of {@code s1}, top side length of
      {@code s2}, and height {@code h}.
   <p>
      This model works with either {@code s1 > s2} or {@code s1 < s2}.
      In other words, the frustum can have its "apex" either above or
      below the xz-plane.

      @param s1  side length of the base of the frustum
      @param s2  side length of the top of the frustum
      @param h   height of the frustum
   */
   public PyramidFrustum(final double s1, final double s2, final double h)
   {
      super(String.format("Pyramid Frustum(%.2f,%.2f,%.2f)", s1, s2, h));

      this.s1 = s1;
      this.s2 = s2;
      this.h = h;
      this.n = 1;
      this.k = 1;
      this.type = MeshType.HORIZONTAL;

      // Create the frustum's geometry.
      addVertex(new Vertex(-s1/2, 0, -s1/2),  // base
                new Vertex(-s1/2, 0,  s1/2),
                new Vertex( s1/2, 0,  s1/2),
                new Vertex( s1/2, 0, -s1/2),
                new Vertex(-s2/2, h, -s2/2),  // top
                new Vertex(-s2/2, h,  s2/2),
                new Vertex( s2/2, h,  s2/2),
                new Vertex( s2/2, h, -s2/2));

      // Create 12 triangles.
      addPrimitive(new TriangleStrip(0, 3, 1, 2),  // 2 base triangles
                   new TriangleStrip(4, 0, 5, 1, 6, 2, 7, 3, 4, 0),
                   new TriangleStrip(4, 5, 7, 6)); // 2 top triangles
   }


   /**
      Create a frustum of a right square pyramid with its base in the
      xz-plane, a base side length of {@code s}, top of the frustum at
      height {@code h}, and with the pyramid's apex at on the y-axis at
      height {@code a}.

      @param n  number of lines of latitude
      @param k  number of lines of longitude
      @param s  side length of the base of the frustum
      @param h  height of the frustum
      @param a  height of the apex of the pyramid
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public PyramidFrustum(final int n, final int k,
                         final double s, final double h, final double a)
   {
      this(s, (1 - h/a)*s, h, n, k);
   }


   /**
      Create a frustum of a right square pyramid with its base in the
      xz-plane, a base side length of {@code s}, top of the frustum at
      height {@code h}, and with the pyramid's apex at on the y-axis at
      height {@code a}.

      @param n  number of lines of latitude
      @param k  number of lines of longitude
      @param s  side length of the base of the frustum
      @param h  height of the frustum
      @param a  height of the apex of the pyramid
      @param type  choose between striped and checkerboard triangle strips
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public PyramidFrustum(final int n, final int k,
                         final double s, final double h, final double a,
                         final MeshType type)
   {
      this(s, (1 - h/a)*s, h, n, k, type);
   }


   /**
      Create a frustum of a right square pyramid with its base in the
      xz-plane, a base side length of {@code s1}, top side length of
      {@code s2}, and height {@code h}.
   <p>
      This model works with either {@code s1 > s2} or {@code s1 < s2}.
      In other words, the frustum can have its "apex" either above or
      below the xz-plane.

      @param s1  side length of the base of the frustum
      @param s2  side length of the top of the frustum
      @param h   height of the frustum
      @param n   number of lines of latitude
      @param k   number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public PyramidFrustum(double s1, double s2, double h,
                         final int n, final int k)
   {
      this(s1, s2, h, n, k, MeshType.HORIZONTAL);
   }


   /**
      Create a frustum of a right square pyramid with its base in the
      xz-plane, a base side length of {@code s1}, top side length of
      {@code s2}, and height {@code h}.
   <p>
      This model works with either {@code s1 > s2} or {@code s1 < s2}.
      In other words, the frustum can have its "apex" either above or
      below the xz-plane.

      @param s1  side length of the base of the frustum
      @param s2  side length of the top of the frustum
      @param h   height of the frustum
      @param n   number of lines of latitude
      @param k   number of lines of longitude
      @param type  choose between striped and checkerboard triangle strips
      @throws IllegalArgumentException if {@code n} is less than 0
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public PyramidFrustum(double s1, double s2, double h,
                         final int n, final int k,
                         final MeshType type)
   {
      super(String.format("Pyramid Frustum(%.2f,%.2f,%.2f,%d,%d)",
                                           s1,  s2,  h,   n, k));

      if (n < 0)
         throw new IllegalArgumentException("n must be greater than or equal to 0");
      if (k < 1)
         throw new IllegalArgumentException("k must be greater than 0");

      this.s1 = s1;
      this.s2 = s2;
      this.h = h;
      this.n = n;
      this.k = k;
      this.type = type;

      // Create the pyramid's geometry.
      final double deltaH = h / (n - 1);

      // An array of vertices to be used to create faces.
      final Vertex[][] v = new Vertex[n][4*k + 1];

      // Create all the vertices.
      for (int i = 0; i < n; ++i) // choose a height of latitude
      {
         final double y = i * deltaH;
         final double slantSide = s1 - i*((s1 - s2) / (n - 1));
         final double deltaS = slantSide / k;

         for (int j = 0; j < k; ++j)
         {
            v[i][j] = new Vertex(-slantSide/2 + j*deltaS,
                                  y,
                                 -slantSide/2);
         }
         for (int j = 0; j < k; ++j)
         {
            v[i][k+j] = new Vertex( slantSide/2,
                                    y,
                                   -slantSide/2 + j*deltaS);
         }
         for (int j = 0; j < k; ++j)
         {
            v[i][2*k+j] = new Vertex( slantSide/2 - j*deltaS,
                                      y,
                                      slantSide/2);
         }
         for (int j = 0; j < k; ++j)
         {
            v[i][3*k+j] = new Vertex(-slantSide/2,
                                     y,
                                     slantSide/2 - j*deltaS);
         }
         // create one more vertex to close the latitude
         v[i][4*k] = new Vertex(v[i][0].x, v[i][0].y, v[i][0].z);
      }

      // Add all of the vertices to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < 4*k + 1; ++j)
         {
            addVertex( v[i][j] );
         }
      }
      addVertex(new Vertex(0, h, 0),
                new Vertex(0, 0, 0));
      final int topCenterIndex = n * (4*k + 1),
                bottomCenterIndex = topCenterIndex + 1;

      // Create all the square strips around the pyramid wall.
      if (MeshType.HORIZONTAL == type)
      {
         for (int i = 0; i < n - 1; ++i) // choose a height of latitude
         {
            final Primitive triStrip = new TriangleStrip();
            for (int j = 0; j < 4*k + 1; ++j) // choose a line of longitude
            {
               triStrip.addIndex(    i*(4*k+1)+j); // v[i  ][j]
               triStrip.addIndex((i+1)*(4*k+1)+j); // v[i+1][j]
            }
            addPrimitive(triStrip);
         }
      }
      else if (MeshType.VERTICAL == type)
      {
         for (int j = 0; j < 4*k; ++j) // choose a line of longitude
         {
            final Primitive triStrip = new TriangleStrip();
            triStrip.addIndex(bottomCenterIndex);
            for (int i = 0; i < n; ++i) // choose a height of latitude
            {
               triStrip.addIndex(i * (4*k+1) + j);    // v[i][j  ]
               triStrip.addIndex(i * (4*k+1) + j+1);  // v[i][j+1]
            }
            triStrip.addIndex(topCenterIndex);
            addPrimitive(triStrip);
         }
      }
      else // MeshType.CHECKER triangle strips
      {
         for (int i = 0; i < n - 1; ++i) // choose a height of latitude
         {
            for (int j = 0; j < 4*k; ++j) // choose a line of longitude
            {
               addPrimitive(
                  new TriangleStrip(
                         i*(4*k+1)+j,     // v[i  ][j  ]
                     (i+1)*(4*k+1)+j,     // v[i+1][j  ]
                         i*(4*k+1)+j+1,   // v[i  ][j+1]
                     (i+1)*(4*k+1)+j+1)); // v[i+1][j+1]
            }
         }
      }

      if ( MeshType.HORIZONTAL == type
        || MeshType.CHECKER == type )
      {
         // Create the triangle fan in the top.
         final Primitive topFan = new TriangleFan();
         topFan.addIndex(topCenterIndex);
         for (int j = 4*k; j >= 0; --j) // choose a line of longitude
         {
            topFan.addIndex( (n-1)*(4*k+1)+j ); // v[n-1][j]
         }
         addPrimitive(topFan);

         // Create the triangle fan in the base.
         final Primitive botFan = new TriangleFan();
         botFan.addIndex(bottomCenterIndex);
         for (int j = 0; j < 4*k + 1; ++j) // choose a line of longitude
         {
            botFan.addIndex( j );  // v[0][j]
         }
         addPrimitive(botFan);
      }
   }



   // Implement the MeshMaker_TP interface (four methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override public MeshType getType() {return type;}

   @Override
   public PyramidFrustum remake(final int n, final int k, final MeshType type)
   {
      return new PyramidFrustum(this.s1, this.s2,
                                this.h,
                                n, k,
                                type);
   }
}//PyramidFrustum
