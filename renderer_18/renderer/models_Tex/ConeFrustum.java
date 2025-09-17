/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a textured model of a frustum of a right circular cone
   with its base in the xz-plane.
<p>
   See <a href="https://en.wikipedia.org/wiki/Frustum" target="_top">
                https://en.wikipedia.org/wiki/Frustum</a>

   @see Cone
   @see ConeSector
*/
public class ConeFrustum extends Model implements MeshMaker
{
   public final Texture texture0;
   public final Texture texture1;
   public final Texture texture2;
   public final double r1;
   public final double r2;
   public final double h;
   public final int n;
   public final int k;

   /**
      Create a textured frustum of a right circular cone with its base in the
      xz-plane, a base radius of 1, top radius of 1/2, and height 1/2.

      @param texture0  {@link Texture} for the frustum's wall
      @param texture1  {@link Texture} for the frustum's top
      @param texture2  {@link Texture} for the frustum's bottom
   */
   public ConeFrustum(final Texture texture0,
                      final Texture texture1,
                      final Texture texture2)
   {
      this(texture0, texture1, texture2, 1.0, 0.5, 0.5, 7, 16);
   }


   /**
      Create a textured frustum of a right circular cone with its base in the
      xz-plane, a base radius of {@code r}, top of the frustum at
      height {@code h}, and with the cone's apex on the y-axis at
      height {@code a}.
   <p>
      There must be at least three lines of longitude and at least
      two circles of latitude.

      @param texture0  {@link Texture} for the frustum's wall
      @param texture1  {@link Texture} for the frustum's top
      @param texture2  {@link Texture} for the frustum's bottom
      @param n  number of circles of latitude
      @param k  number of lines of longitude
      @param r  radius of the base in the xz-plane
      @param h  height of the frustum
      @param a  height of the apex of the cone
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public ConeFrustum(final Texture texture0,
                      final Texture texture1,
                      final Texture texture2,
                      final int n, final int k,
                      final double r, final double h, final double a)
   {
      this(texture0, texture1, texture2, r, (1 - h/a)*r, h, n, k);
   }


   /**
      Create a textured frustum of a right circular cone with its base in the
      xz-plane, a base radius of {@code r1}, top radius of {@code r2},
      and height {@code h}.
   <p>
      This model works with either {@code r1 > r2} or {@code r1 < r2}.
      In other words, the frustum can have its "apex" either above or
      below the xz-plane.
   <p>
      There must be at least three lines of longitude and at least
      two circles of latitude.

      @param texture0  {@link Texture} for the frustum's wall
      @param texture1  {@link Texture} for the frustum's top
      @param texture2  {@link Texture} for the frustum's bottom
      @param r1  radius of the base of the frustum
      @param h   height of the frustum
      @param r2  radius of the top of the frustum
      @param n   number of circles of latitude
      @param k   number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public ConeFrustum(final Texture texture0,
                      final Texture texture1,
                      final Texture texture2,
                      final double r1, final double h, final double r2,
                      final int n, final int k)
   {
      super(String.format("Cone Frustum(%.2f,%.2f,%.2f,%d,%d)",
                                        r1,  h,   r2,  n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      this.texture0 = texture0;
      this.texture1 = texture1;
      this.texture2 = texture2;
      this.r1 = r1;
      this.r2 = r2;
      this.h = h;
      this.n = n;
      this.k = k;

      // Add the given textures to this model.
      addTexture(texture0, texture1, texture2);

      // Create the frustum's geometry.

      final double deltaH = h / (n - 1),
                   deltaTheta = (2 * Math.PI) / k;

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k+1];

      // An array of texture coordinates.
      final TexCoord[][] tc = new TexCoord[n][k+1];

      // Create all the vertices (working from the top to the bottom).
      for (int j = 0; j < k+1; ++j) // choose an angle of longitude
      {
         final double c = Math.cos(j * deltaTheta),
                      s = Math.sin(j * deltaTheta);
         for (int i = 0; i < n; ++i) // choose a circle of latitude
         {
            final double slantRadius = (i/(double)(n-1)) * r1 + ((n-1-i)/(double)(n-1)) * r2;
            v[i][j] = new Vertex(slantRadius * s,
                                 h - i* deltaH,
                                 slantRadius * c);
         }
      }
      final Vertex topCenter = new Vertex(0, h, 0),
                bottomCenter = new Vertex(0, 0, 0);

      // Create all the texture coordinates.
      for (int j = 0; j < k+1; ++j) // choose an angle of longitude
      {
         for (int i = 0; i < n; ++i) // choose an circle of latitude
         {
            tc[i][j] = new TexCoord(j/(double)k, 1.0 - i/(n - 1.0));
         }
      }

      // Add all of the vertices and texture coordinates to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < k+1; ++j)
         {
            addVertex( v[i][j] );
            addTextureCoord( tc[i][j] );
         }
      }
      addVertex(topCenter,
                bottomCenter);
      final int topCenterIndex = n * (k+1),
                bottomCenterIndex = topCenterIndex + 1;

      // Create all the triangle strips along the cone wall.
      for (int i = 0; i < n - 1; ++i)
      {
         for (int j = 0; j < k; ++j)
         {  //                        v[i][j]    v[i+1][j+1]      v[i][j+1]
            addPrimitive(new Triangle(i*(k+1)+j, (i+1)*(k+1)+j+1, i*(k+1)+j+1,
                                      i*(k+1)+j, (i+1)*(k+1)+j+1, i*(k+1)+j+1,
            //                        tc[i][j]    tc[i+1][j+1]    tc[i][j+1]
                                      0));  // texture index

            //                        v[i][j]     v[i+1][j]      v[i+1][j+1]
            addPrimitive(new Triangle(i*(k+1)+j, (i+1)*(k+1)+j, (i+1)*(k+1)+j+1,
                                      i*(k+1)+j, (i+1)*(k+1)+j, (i+1)*(k+1)+j+1,
            //                        tc[i][j]    tc[i+1][j]     tc[i+1][j+1]
                                      0));  // texture index
         }
      }

      // Create the triangle fan at the top.
      addTextureCoord( new TexCoord(0.5, 0.5) );  // top center
      addTextureCoord( new TexCoord(1.0, 0.5) );
      final int topCenterTCindex = n * (k+1);
      for (int j = 0; j < k; ++j)
      {
         final double xTC = 0.5 + 0.5*Math.cos((j+1) * deltaTheta);
         final double yTC = 0.5 + 0.5*Math.sin((j+1) * deltaTheta);
         addTextureCoord( new TexCoord(xTC, yTC) );
         //                                       v[0][j]  v[0][j+1]
         addPrimitive(new Triangle(topCenterIndex,   j,       j+1,
                                   topCenterTCindex,
                                   topCenterTCindex + 1 + j,
                                   topCenterTCindex + 2 + j,
                                   1));  // texture index
      }

      // Create the triangle fan at the bottom.
      addTextureCoord( new TexCoord(0.5, 0.5) );  // bottom center
      addTextureCoord( new TexCoord(0.0, 0.5) );
      final int bottomCenterTCindex = topCenterTCindex + k + 2;
      for (int j = 0; j < k; ++j)
      {
         final double xTC = 0.5 - 0.5*Math.cos((j+1) * deltaTheta);
         final double yTC = 0.5 + 0.5*Math.sin((j+1) * deltaTheta);
         addTextureCoord( new TexCoord(xTC, yTC) );
         //                                            v[n-1][j+1]     v[n-1][j]
         addPrimitive(new Triangle(bottomCenterIndex, (n-1)*(k+1)+j+1, (n-1)*(k+1)+j,
                                   bottomCenterTCindex,
                                   bottomCenterTCindex + 2 + j,
                                   bottomCenterTCindex + 1 + j,
                                   2));  // texture index
      }
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override
   public ConeFrustum remake(final int n, final int k)
   {
      return new ConeFrustum(this.texture0,
                             this.texture1,
                             this.texture2,
                             this.r1, this.h, this.r2,
                             n, k);
   }
}//ConeFrustum
