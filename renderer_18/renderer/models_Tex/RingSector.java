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
   Create a textured model of a sector of a ring (an annulus)
   in the xy-plane centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Annulus_(mathematics)" target="_top">
                https://en.wikipedia.org/wiki/Annulus_(mathematics)</a>
<p>
   See <a href="https://en.wikipedia.org/wiki/Circular_sector" target="_top">
                https://en.wikipedia.org/wiki/Circular_sector</a>

   @see Ring
   @see CircleSector
   @see DiskSector
   @see ConeSector
   @see CylinderSector
   @see SphereSector
   @see TorusSector
*/
public class RingSector extends Model implements MeshMaker
{
   public final Texture texture;
   public final double r1;
   public final double r2;
   public final double theta1;
   public final double theta2;
   public final int n;
   public final int k;

   /**
      Create half a textured ring (annulus) in the xy-plane
      with outer radius 1, inner radius 0.33, with 7
      spokes coming out of the center, and with 5
      concentric circles.

      @param texture  {@link Texture} to use with this {@link Model}
   */
   public RingSector(final Texture texture)
   {
      this(texture, 1.0, 0.33, 0, Math.PI, 5, 7);
   }


   /**
      Create a sector of a textured ring (annulus) in the xy-plane
      with outer radius {@code r1}, inner radius {@code r2},
      starting angle {@code theta1}, ending angle {@code theta2},
      with {@code k} spokes coming out of the center, and
      with {@code n} concentric circles.
   <p>
      If there are {@code k} spokes, then each (partial) circle
      around the center will have {@code k-1} line segments.
      If there are {@code n} concentric circles around the center,
      then each spoke will have {@code n-1} line segments.
   <p>
      There must be at least four spokes and at least two concentric circle.

      @param texture  {@link Texture} to use with this {@link Model}
      @param r1      outer radius of the ring
      @param r2      inner radius of the ring
      @param theta1  beginning angle of the sector (in radians)
      @param theta2  ending angle of the sector (in radians)
      @param n       number of concentric circles
      @param k       number of spokes in the ring
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 4
   */
   public RingSector(final Texture texture,
                     final double r1, final double r2,
                     double theta1, double theta2,
                     final int n, final int k)
   {
      super(String.format("Ring Sector(%.2f,%.2f,%.2f,%.2f,%d,%d)",
                                       r1, r2, theta1, theta2, n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 4)
         throw new IllegalArgumentException("k must be greater than 3");

      theta1 = theta1 % (2*Math.PI);
      theta2 = theta2 % (2*Math.PI);
      if (theta1 < 0) theta1 = 2*Math.PI + theta1;
      if (theta2 < 0) theta2 = 2*Math.PI + theta2;
      if (theta2 <= theta1) theta2 = theta2 + 2*Math.PI;

      this.texture = texture;
      this.r1 = r1;
      this.r2 = r2;
      this.theta1 = theta1;
      this.theta2 = theta2;
      this.n = n;
      this.k = k;

      // Add the given texture to this model.
      addTexture(texture);

      // Create the rings's geometry.

      final double deltaR = (r1 - r2) / (n - 1),
                   deltaTheta = (theta2 - theta1) / (k - 1);

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k];

      // An array of texture coordinates.
      final TexCoord[][] tc = new TexCoord[n+1][k];

      // Create all the vertices.
      for (int j = 0; j < k; ++j) // choose a spoke (an angle)
      {
         final double c = Math.cos(theta1 + j * deltaTheta),
                      s = Math.sin(theta1 + j * deltaTheta);
         for (int i = 0; i < n; ++i) // move along the spoke
         {
            final double ri = r2 + i * deltaR;
            v[i][j] = new Vertex(ri * c,
                                 ri * s,
                                 0);
         }
      }

      // Create all the texture coordinates.
      for (int j = 0; j < k; ++j) // choose a spoke (an angle)
      {
         final double c = Math.cos(theta1 + j * deltaTheta),
                      s = Math.sin(theta1 + j * deltaTheta);
         for (int i = 0; i < n; ++i) // move along the spoke
         {
            final double ri = (r2 + i * deltaR) / (2.0 * r1);
            tc[i][j] = new TexCoord( 0.5 + ri * c,
                                     0.5 + ri * s);
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

      // Create all the triangle strips.
      for (int i = 0; i < n - 1; ++i)
      {
         for (int j = 0; j < k - 1; ++j)
         {
            if ((i == 0) && (r2 == 0)) // prevent degenerate squares
            {  //                        v[i][j]   v[i+1][j]    v[i+1][j+1]
               addPrimitive(new Triangle((i*k)+j, ((i+1)*k)+j, ((i+1)*k)+j+1,
                                         (i*k)+j, ((i+1)*k)+j, ((i+1)*k)+j+1,
               //                        tc[i][j]  tc[i+1][j]   tc[i+1][j+1]
                                         0)); // texture index
            }
            else
            {  //                         v[i][j]   v[i+1][j+1]   v[i][j+1]
               addPrimitive(new Triangle((i*k)+j, ((i+1)*k)+j+1, (i*k)+j+1,
                                         (i*k)+j, ((i+1)*k)+j+1, (i*k)+j+1,
               //                        tc[i][j]  tc[i+1][j+1]  tc[i][j+1]
                                         0)); // texture index

               //                         v[i+1][j+1]    v[i][j]   v[i+1][j]
               addPrimitive(new Triangle(((i+1)*k)+j+1, (i*k)+j, ((i+1)*k)+j,
                                         ((i+1)*k)+j+1, (i*k)+j, ((i+1)*k)+j,
               //                         tc[i+1][j+1]   tc[i][j]  tc[i+1][j]
                                         0)); // texture index
            }
         }
      }
   }



   // Implement the MeshMaker interface (thre methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override
   public RingSector remake(final int n, final int k)
   {
      return new RingSector(this.texture,
                            this.r1, this.r2,
                            this.theta1, this.theta2,
                            n, k);
   }
}//RingSector
