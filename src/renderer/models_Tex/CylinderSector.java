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
   Create a textured model of a partial right circular cylinder
   with its axis along the y-axis.
<p>
   By a partial cylinder we mean a cylinder over a circular sector
   of the cylinder's base.
<p>
   See <a href="https://en.wikipedia.org/wiki/Circular_sector" target="_top">
                https://en.wikipedia.org/wiki/Circular_sector</a>

   @see Cylinder
   @see CircleSector
   @see DiskSector
   @see RingSector
   @see ConeSector
   @see SphereSector
   @see TorusSector
*/
public class CylinderSector extends Model implements MeshMaker
{
   public final Texture texture0;
   public final Texture texture1;
   public final Texture texture2;
   public final double r;
   public final double h;
   public final double theta1;
   public final double theta2;
   public final int n;
   public final int k;

   /**
      Create half of a textured cylinder with radius 1
      and its axis along the y-axis from
      {@code y = 1} to {@code y = -1}.

      @param texture0  {@link Texture} for the cylinder's wall
      @param texture1  {@link Texture} for the cylinder's top
      @param texture2  {@link Texture} for the cylinder's bottom
   */
   public CylinderSector(final Texture texture0,
                         final Texture texture1,
                         final Texture texture2)
   {
      this(texture0, texture1, texture2, 1, 1, Math.PI/2, 3*Math.PI/2, 15, 8);
   }


   /**
      Create a part of the textured cylinder with radius {@code r} and its
      axis along the y-axis from {@code y = h} to {@code y = -h}.
   <p>
      The partial cylinder is a cylinder over the circular sector
      from angle {@code theta1} to angle {@code theta2} (in the
      counterclockwise direction).
   <p>
      The last two parameters determine the number of lines of longitude
      and the number of (partial) circles of latitude in the model.
   <p>
      If there are {@code n} circles of latitude in the model, then
      each line of longitude will have {@code n-1} line segments.
      If there are {@code k} lines of longitude, then each (partial)
      circle of latitude will have {@code k-1} line segments.
   <p>
      There must be at least four lines of longitude and at least
      two circles of latitude.

      @param texture0  {@link Texture} for the cylinder's wall
      @param texture1  {@link Texture} for the cylinder's top
      @param texture2  {@link Texture} for the cylinder's bottom
      @param r       radius of the cylinder
      @param h       height of the cylinder (from h to -h along the y-axis)
      @param theta1  beginning longitude angle of the sector (in radians)
      @param theta2  ending longitude angle of the sector (in radians)
      @param n       number of circles of latitude around the cylinder
      @param k       number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 4
   */
   public CylinderSector(final Texture texture0,
                         final Texture texture1,
                         final Texture texture2,
                         final double r, final double h,
                         double theta1, double theta2,
                         final int n, final int k)
   {
      super(String.format("Cylinder_Sector(%.2f,%.2f,%.2f,%.2f,%d,%d)",
                                           r, h, theta1, theta2, n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      theta1 = theta1 % (2*Math.PI);
      theta2 = theta2 % (2*Math.PI);
      if (theta1 < 0) theta1 = 2*Math.PI + theta1;
      if (theta2 < 0) theta2 = 2*Math.PI + theta2;
      if (theta2 <= theta1) theta2 = theta2 + 2*Math.PI;

      this.texture0 = texture0;
      this.texture1 = texture1;
      this.texture2 = texture2;
      this.r = r;
      this.h = h;
      this.theta1 = theta1;
      this.theta2 = theta2;
      this.n = n;
      this.k = k;

      // Add the given textures to this model.
      addTexture(texture0, texture1, texture2);

      // Create the cylinder's geometry.

      final double deltaH = (2 * h) / (n - 1),
                   deltaTheta = (theta2 - theta1)/ (k - 1);

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k];

      // An array of texture coordinates.
      final TexCoord[][] tc = new TexCoord[n][k];

      // Create all the vertices.
      for (int j = 0; j < k; ++j) // choose an angle of longitude
      {
         final double c = Math.cos(theta1 + j * deltaTheta),
                      s = Math.sin(theta1 + j * deltaTheta);
         for (int i = 0; i < n; ++i) // choose a circle of latitude
         {
            v[i][j] = new Vertex( r * c,
                                  h - i * deltaH,
                                 -r * s );
         }
      }
      final Vertex topCenter    = new Vertex(0,  h, 0),
                   bottomCenter = new Vertex(0, -h, 0);

      // Create all the texture coordinates.
      for (int j = 0; j < k; ++j) // choose an angle of longitude
      {
         for (int i = 0; i < n; ++i) // choose an circle of latitude
         {
            tc[i][j] = new TexCoord(j/(k - 1.0), 1.0 - i/(n - 1.0));
         }
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
      addVertex(topCenter,
                bottomCenter);
      final int topCenterIndex = n * k,
                bottomCenterIndex = topCenterIndex + 1;

      // Create all the triangle strips around the cylinder wall.
      for (int i = 0; i < n - 1; ++i) // choose a circle of latitude
      {
         for (int j = 0; j < k - 1; ++j) // choose a line of longitude
         {  //                        v[i][j]   v[i+1][j+1]   v[i][j+1]
            addPrimitive(new Triangle((i*k)+j, ((i+1)*k)+j+1, (i*k)+j+1,
                                      (i*k)+j, ((i+1)*k)+j+1, (i*k)+j+1,
            //                        tc[i][j]  tc[i+1][j+1]  tc[i][j+1]
                                      0));  // texture index

            //                          v[i+1][j+1]  v[i][j]  v[i+1][j]
            addPrimitive(new Triangle(((i+1)*k)+j+1, (i*k)+j, ((i+1)*k)+j,
                                      ((i+1)*k)+j+1, (i*k)+j, ((i+1)*k)+j,
            //                         tc[i+1][j+1]  tc[i][j] tc[i+1][j]
                                      0));  // texture index
         }
      }

      // Create the triangle fan at the top.
      addTextureCoord( new TexCoord(0.5, 0.5) );  // top center
      addTextureCoord( new TexCoord(1.0, 0.5) );
      final int topCenterTCindex = n * k;
      for (int j = 0; j < k - 1; ++j)
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
      final int bottomCenterTCindex = topCenterTCindex + k + 1;
      for (int j = 0; j < k - 1; ++j)
      {
         final double xTC = 0.5 - 0.5*Math.cos((j+1) * deltaTheta);
         final double yTC = 0.5 + 0.5*Math.sin((j+1) * deltaTheta);
         addTextureCoord( new TexCoord(xTC, yTC) );
         //                                           v[n-1][j+1]  v[n-1][j]
         addPrimitive(new Triangle(bottomCenterIndex, (n-1)*k+j+1, (n-1)*k+j,
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
   public CylinderSector remake(final int n, final int k)
   {
      return new CylinderSector(this.texture0,
                                this.texture1,
                                this.texture2,
                                this.r, this.h,
                                this.theta1, this.theta2,
                                n, k);
   }
}//CylinderSector
