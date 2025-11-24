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
   Create a textured model of a partial right circular cone with its
   base parallel to the xz-plane and its apex on the positive y-axis.
<p>
   By a partial cone we mean a cone over a circular sector of the
   cone's base and also cutting off the top part of the cone (the
   part between the apex and a circle of latitude) leaving a frustum
   of the (partial) cone.

   @see Cone
   @see DiskSector
   @see RingSector
   @see CylinderSector
   @see SphereSector
   @see TorusSector
*/
public class ConeSector extends Model implements MeshMaker
{
   public final Texture texture0;
   public final Texture texture1;
   public final double r;
   public final double h;
   public final double theta1;
   public final double theta2;
   public final int n;
   public final int k;

   /**
      Create half of a textured right circular cone with its base in the xz-plane,
      a base radius of 1, height 1, and apex on the positive y-axis.

      @param texture0  {@link Texture} for the cone's wall
      @param texture1  {@link Texture} for the cone's base
   */
   public ConeSector(final Texture texture0,
                     final Texture texture1)
   {
      this(texture0, texture1, 1, 1, Math.PI/2, 3*Math.PI/2, 15, 8);
   }


   /**
      Create a part of the textured cone with its base in the xz-plane,
      a base radius of {@code r}, height {@code  h}, and apex
      on the y-axis.
   <p>
      The partial cone is a cone over the circular sector
      from angle {@code theta1} to angle {@code theta2} (in the
      counterclockwise direction). In other words, the (partial)
      circles of latitude in the model extend from angle
      {@code theta1} to angle {@code theta2} (in the
      counterclockwise direction).
   <p>
      The last two parameters determine the number of lines of longitude
      (not counting one edge of any removed sector) and the number of
      (partial) circles of latitude (not counting the top edge of the
      frustum) in the model.
   <p>
      If there are {@code n} circles of latitude in the model (including
      the bottom edge), then each line of longitude will have {@code n+1}
      line segments (including the segment in the base). If there are
      {@code k} lines of longitude (including both the beginning and
      ending edges of the sector), then each circle of latitude will
      have {@code k-1} line segments.
   <p>
      There must be at least four lines of longitude and at least
      two circles of latitude.

      @param texture0  {@link Texture} for the cone's wall
      @param texture1  {@link Texture} for the cone's base
      @param r       radius of the base in the xz-plane
      @param h       height of the apex on the y-axis
      @param theta1  beginning longitude angle of the sector (in radians)
      @param theta2  ending longitude angle of the sector (in radians)
      @param n       number of circles of latitude around the cone
      @param k       number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 4
   */
   public ConeSector(final Texture texture0,
                     final Texture texture1,
                     final double r,
                     final double h,
                     double theta1, double theta2,
                     final int n, final int k)
   {
      super(String.format("Cone Sector(%.2f,%.2f,%.2f,%.2f,%d,%d)",
                                       r, h, theta1, theta2, n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 4)
         throw new IllegalArgumentException("k must be greater than 3");

      theta1 = theta1 % (2*Math.PI);
      theta2 = theta2 % (2*Math.PI);
      if (theta1 < 0) theta1 = 2*Math.PI + theta1;
      if (theta2 < 0) theta2 = 2*Math.PI + theta2;
      if (theta2 <= theta1) theta2 = theta2 + 2*Math.PI;

      this.texture0 = texture0;
      this.texture1 = texture1;
      this.r = r;
      this.h = h;
      this.theta1 = theta1;
      this.theta2 = theta2;
      this.n = n;
      this.k = k;

      // Add the given textures to this model.
      addTexture(texture0, texture1);

      // Create the cone's geometry.

      final double deltaH = h / n,
                   deltaTheta = (theta2 - theta1) / (k - 1);

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k];

      // An array of texture coordinates.
      final TexCoord[][] tc = new TexCoord[n+1][k];

      // Create all the vertices (working from the bottom to the top).
      for (int j = 0; j < k; ++j) // choose an angle of longitude
      {
         final double c = Math.cos(theta1 + j * deltaTheta),
                      s = Math.sin(theta1 + j * deltaTheta);
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
      for (int j = 0; j < k; ++j) // choose an angle of longitude
      {
         for (int i = 0; i < n; ++i) // choose an circle of latitude
         {
            tc[i][j] = new TexCoord(j/(k - 1.0), i/(double)n);
         }
      }
      for (int j = 0; j < k; ++j) // used by the triangle fan at the apex
      {
         //tc[n][j] = new TexCoord(j/(k - 1.0), 1.0);
         // or
         tc[n][j] = new TexCoord(1.0/(2*(k-1)) + j/(k - 1.0), 1.0);
      }

      // Add all of the vertices and texture coordinates to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < k; ++j)
         {
            addVertex( v[i][j] );
            addTextureCoord( tc[i][j] );
         }
      }
      addVertex(apex,
                bottomCenter);
      final int apexIndex = n * k,
                bottomCenterIndex = apexIndex + 1;
      for (int j = 0; j < k; ++j) // used by the triangle fan at the apex
      {
         addTextureCoord ( tc[n][j] );
      }

      // Create all the triangle strips along the cone wall.
      for (int i = 0; i < n - 1; ++i)
      {
         for (int j = 0; j < k - 1; ++j)
         {  //                        v[i][j]   v[i][j+1]   v[i+1][j+1]
            addPrimitive(new Triangle((i*k)+j, (i*k)+j+1,  ((i+1)*k)+j+1,
                                      (i*k)+j, (i*k)+j+1,  ((i+1)*k)+j+1,
            //                        tc[i][j]  tc[i][j+1]  tc[i+1][j+1]
                                      0));  // texture index

            //                        v[i][j]  v[i+1][j+1]     v[i+1][j]
            addPrimitive(new Triangle((i*k)+j, ((i+1)*k)+j+1, ((i+1)*k)+j,
                                      (i*k)+j, ((i+1)*k)+j+1, ((i+1)*k)+j,
            //                        tc[i][j]  tc[i+1][j+1]   tc[i+1][j]
                                      0));  // texture index
         }
      }

      // Create the triangle fan at the top.
      for (int j = 0; j < k - 1; ++j)
      {  //                                   v[0][j]   v[0][j+1]
         addPrimitive(new Triangle(apexIndex,   j,        j+1,
                                    n*k+j,      j,        j+1,
         //                        tc[n][j]   tc[0][j]  tc[0][j+1]
                                   0));  // texture index
      }



      // Create the triangle fan at the bottom.
      addTextureCoord( new TexCoord(0.5, 0.5) );  // bottom center
      addTextureCoord( new TexCoord(0.0, 0.5) );
      final int bottomCenterTCindex = (n+1) * k;
      for (int j = 0; j < k - 1; ++j)
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
      public ConeSector remake(final int n, final int k)
      {
         return new ConeSector(this.texture0,
                               this.texture1,
                               this.r,
                               this.h,
                               this.theta1, this.theta2,
                               n, k);
   }
}//ConeSector
