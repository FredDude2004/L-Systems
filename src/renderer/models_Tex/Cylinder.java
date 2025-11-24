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
   Create a textured model of a right circular cylinder
   with its axis along the y-axis.
<p>
   See <a href="https://en.wikipedia.org/wiki/Cylinder" target="_top">
                https://en.wikipedia.org/wiki/Cylinder</a>
<p>
   This model can also be used to create right k-sided polygonal prisms.
<p>
   See <a href="https://en.wikipedia.org/wiki/Prism_(geometry)" target="_top">
                https://en.wikipedia.org/wiki/Prism_(geometry)</a>

   @see CylinderSector
*/
public class Cylinder extends Model implements MeshMaker
{
   public final Texture texture0;
   public final Texture texture1;
   public final Texture texture2;
   public final double r;
   public final double h;
   public final int n;
   public final int k;

   /**
      Create a textured right circular cylinder with radius 1 and its
      axis along the y-axis from {@code y = 1} to {@code y = -1}.

      @param texture0  {@link Texture} for the cylinder's wall
      @param texture1  {@link Texture} for the cylinder's top
      @param texture2  {@link Texture} for the cylinder's bottom
   */
   public Cylinder(final Texture texture0,
                   final Texture texture1,
                   final Texture texture2)
   {
      this(texture0, texture1, texture2, 1, 1, 15, 16);
   }


   /**
      Create a textured right circular cylinder with radius {@code r} and
      its axis along the y-axis from {@code y = h} to {@code y = -h}.

      @param texture0  {@link Texture} for the cylinder's wall
      @param texture1  {@link Texture} for the cylinder's top
      @param texture2  {@link Texture} for the cylinder's bottom
      @param r  radius of the cylinder
      @param h  height of the cylinder (from h to -h along the y-axis)
   */
   public Cylinder(final Texture texture0,
                   final Texture texture1,
                   final Texture texture2,
                   final double r, final double h)
   {
      this(texture0, texture1, texture2, r, h, 15, 16);
   }


   /**
      Create a textured right circular cylinder with radius {@code r} and
      its axis along the y-axis from {@code y = h} to {@code y = -h}.
   <p>
      The last two parameters determine the number of lines of longitude
      and the number of circles of latitude in the model.
   <p>
      If there are {@code n} circles of latitude in the model (including
      the top and bottom edges), then each line of longitude will have
      {@code n+1} line segments. If there are {@code k} lines of longitude,
      then each circle of latitude will have {@code k} line segments.
   <p>
      There must be at least three lines of longitude and at least
      two circles of latitude.
   <p>
      By setting {@code k} to be a small integer, this model can also be
      used to create k-sided polygonal prisms.

      @param texture0  {@link Texture} for the cylinder's wall
      @param texture1  {@link Texture} for the cylinder's top
      @param texture2  {@link Texture} for the cylinder's bottom
      @param r  radius of the cylinder
      @param h  height of the cylinder (from h to -h along the y-axis)
      @param n  number of circles of latitude around the cylinder
      @param k  number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 4
   */
   public Cylinder(final Texture texture0,
                   final Texture texture1,
                   final Texture texture2,
                   final double r, final double h, final int n, final int k)
   {
      super(String.format("Cylinder(%.2f,%.2f,%d,%d)", r, h, n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      this.texture0 = texture0;
      this.texture1 = texture1;
      this.texture2 = texture2;
      this.r = r;
      this.h = h;
      this.n = n;
      this.k = k;

      // Add the given textures to this model.
      addTexture(texture0, texture1, texture2);

      // Create the cylinder's geometry.

      final double deltaH = (2.0 * h) / (n - 1),
                   deltaTheta = (2.0*Math.PI) / k;

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k+1];

      // An array of texture coordinates.
      final TexCoord[][] tc = new TexCoord[n][k+1];

      // Create all the vertices.
      for (int j = 0; j < k+1; ++j) // choose an angle of longitude
      {
         final double c = Math.cos(j * deltaTheta),
                      s = Math.sin(j * deltaTheta);
         for (int i = 0; i < n; ++i) // choose a circle of latitude
         {
            v[i][j] = new Vertex(r * s,
                                 h - i * deltaH,
                                 r * c);
         }
      }
      final Vertex topCenter    = new Vertex(0,  h, 0),
                   bottomCenter = new Vertex(0, -h, 0);

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


      // Create all the triangle strips along the cylinder wall.
      for (int i = 0; i < n - 1; ++i)
      {
         for (int j = 0; j < k; ++j)
         {  //                        v[i][j]    v[i+1][j+1]       v[i][j+1]
            addPrimitive(new Triangle(i*(k+1)+j, (i+1)*(k+1)+j+1, i*(k+1)+j+1,
                                      i*(k+1)+j, (i+1)*(k+1)+j+1, i*(k+1)+j+1,
            //                        tc[i][j]   tc[i+1][j+1]      tc[i][j+1]
                                      0));  // texture index

            //                         v[i][j]   v[i+1][j]       v[i+1][j+1]
            addPrimitive(new Triangle(i*(k+1)+j, (i+1)*(k+1)+j, (i+1)*(k+1)+j+1,
                                      i*(k+1)+j, (i+1)*(k+1)+j, (i+1)*(k+1)+j+1,
            //                        tc[i][j]   tc[i+1][j]      tc[i+1][j+1]
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
   public Cylinder remake(final int n, final int k)
   {
      return new Cylinder(this.texture0,
                          this.texture1,
                          this.texture2,
                          this.r, this.h, n, k);
   }
}//Cylinder
