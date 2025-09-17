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
   Create a textured model of a right circular cone with its base
   parallel to the xz-plane and its apex on the positive y-axis.
<p>
   See <a href="https://en.wikipedia.org/wiki/Cone" target="_top">
                https://en.wikipedia.org/wiki/Cone</a>
<p>
   This model can also be used to create right k-sided polygonal pyramids.
<p>
   See <a href="https://en.wikipedia.org/wiki/Pyramid_(geometry)" target="_top">
                https://en.wikipedia.org/wiki/Pyramid_(geometry)</a>

   @see ConeFrustum
*/
public class Cone extends Model implements MeshMaker
{
   public final Texture texture0;
   public final Texture texture1;
   public final double r;
   public final double h;
   public final int n;
   public final int k;

   /**
      Create a textured, right circular cone with its base in the xz-plane,
      a base radius of 1, height 1, and apex on the positive y-axis.

      @param texture0  {@link Texture} for the cone's wall
      @param texture1  {@link Texture} for the cone's base
   */
   public Cone(final Texture texture0, final Texture texture1)
   {
      this(texture0, texture1, 1, 1, 15, 16);
   }


   /**
      Create a textured, right circular cone with its base in the xz-plane,
      a base radius of {@code r}, height {@code h}, and apex on
      the y-axis.

      @param texture0  {@link Texture} for the cone's wall
      @param texture1  {@link Texture} for the cone's base
      @param r  radius of the base in the xz-plane
      @param h  height of the apex on the y-axis
   */
   public Cone(final Texture texture0,
               final Texture texture1,
               final double r, final double h)
   {
      this(texture0, texture1, r, h, 15, 16);
   }


   /**
      Create a textured right circular cone with its base in the xz-plane,
      a base radius of {@code r}, height {@code h}, and apex on
      the y-axis.
   <p>
      The last two parameters determine the number of lines of longitude
      and the number of circles of latitude in the model.
   <p>
      If there are {@code n} circles of latitude in the model (including
      the bottom edge), then each line of longitude will have {@code n+1}
      line segments (including the segment in the base). If there are
      {@code k} lines of longitude, then each circle of latitude will
      have {@code k} line segments.
   <p>
      There must be at least three lines of longitude and at least
      two circles of latitude.
   <p>
      By setting {@code k} to be a small integer, this model can also
      be used to create k-sided polygonal pyramids.

      @param texture0  {@link Texture} for the cone's wall
      @param texture1  {@link Texture} for the cone's base
      @param r  radius of the base in the xz-plane
      @param h  height of the apex on the y-axis
      @param n  number of circles of latitude around the cone
      @param k  number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public Cone(final Texture texture0,
               final Texture texture1,
               final double r, final double h, final int n, final int k)
   {
      super(String.format("Cone(%.2f,%.2f,%d,%d)",
                                r,   h,   n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      this.texture0 = texture0;
      this.texture1 = texture1;
      this.r = r;
      this.h = h;
      this.n = n;
      this.k = k;

      // Add the given textures to this model.
      addTexture(texture0, texture1);

      // Create the cone's geometry.

      final double deltaH = h / n,
                   deltaTheta = (2.0*Math.PI) / k;

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k+1];

      // An array of texture coordinates.
      final TexCoord[][] tc = new TexCoord[n+1][k+1];

      // Create all the vertices (working from the bottom to the top).
      for (int j = 0; j < k+1; ++j) // choose an angle of longitude
      {
         final double c = Math.cos(j * deltaTheta),
                      s = Math.sin(j * deltaTheta);
         for (int i = 0; i < n; ++i) // choose a circle of latitude
         {
            final double slantRadius = r * (1 - i * deltaH / h);
            v[i][j] = new Vertex(slantRadius * s,
                                 i * deltaH,
                                 slantRadius * c);
         }
      }
      final Vertex apex = new Vertex(0, h, 0),
           bottomCenter = new Vertex(0, 0, 0);

      // Create all the texture coordinates.
      for (int j = 0; j < k+1; ++j) // choose an angle of longitude
      {
         for (int i = 0; i < n; ++i) // choose an circle of latitude
         {
            tc[i][j] = new TexCoord(j/(double)k, i/(double)n);
         }
      }
      for (int j = 0; j < k+1; ++j) // used by the triangle fan at the apex
      {
         //tc[n][j] = new TexCoord(j/(double)k, 1.0);
         // or
         tc[n][j] = new TexCoord(1.0/(2*k) + j/(double)k, 1.0);
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
      addVertex(apex,
                bottomCenter);
      final int apexIndex = n * (k+1),
                bottomCenterIndex = apexIndex + 1;
      for (int j = 0; j < k+1; ++j) // used by the triangle fan at the apex
      {
         addTextureCoord ( tc[n][j] );
      }

      // Create all the triangle strips along the cone wall.
      for (int i = 0; i < n - 1; ++i)
      {
         for (int j = 0; j < k; ++j)
         {  //                        v[i][j]    v[i][j+1]     v[i+1][j+1]
            addPrimitive(new Triangle(i*(k+1)+j, i*(k+1)+j+1, (i+1)*(k+1)+j+1,
                                      i*(k+1)+j, i*(k+1)+j+1, (i+1)*(k+1)+j+1,
            //                        tc[i][j]   tc[i][j+1]   tc[i+1][j+1]
                                      0));  // texture index

            //                        v[i][j]     v[i+1][j+1]     v[i+1][j]
            addPrimitive(new Triangle(i*(k+1)+j, (i+1)*(k+1)+j+1, (i+1)*(k+1)+j,
                                      i*(k+1)+j, (i+1)*(k+1)+j+1, (i+1)*(k+1)+j,
            //                        tc[i][j]   tc[i+1][j+1]      tc[i+1][j]
                                      0));  // texture index
         }
      }

      // Create the triangle fan at the top.
      for (int j = 0; j < k; ++j)
      {  //                                   v[0][j]   v[0][j+1]
         addPrimitive(new Triangle(apexIndex,   j,        j+1,
                                   n*(k+1)+j,   j,        j+1,
         //                        tc[n][j]   tc[0][j]  tc[0][j+1]
                                   0));  // texture index
      }

      // Create the triangle fan at the bottom.
      addTextureCoord( new TexCoord(0.5, 0.5) );  // bottom center
      addTextureCoord( new TexCoord(0.0, 0.5) );
      final int bottomCenterTCindex = (n+1) * (k+1);
      for (int j = 0; j < k; ++j)
      {
         final double xTC = 0.5 - 0.5*Math.cos((j+1) * deltaTheta);
         final double yTC = 0.5 + 0.5*Math.sin((j+1) * deltaTheta);
         addTextureCoord( new TexCoord(xTC, yTC) );
         //                                            v[0][j+1]  v[0][j]
         addPrimitive(new Triangle(bottomCenterIndex,     j+1,      j,
                                   bottomCenterTCindex,
                                   bottomCenterTCindex + 2 + j,
                                   bottomCenterTCindex + 1 + j,
                                   1));  // texture index
      }
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override
   public Cone remake(final int n, final int k)
   {
      return new Cone(this.texture0,
                      this.texture1,
                      this.r,
                      this.h,
                      n, k);
   }
}//Cone
