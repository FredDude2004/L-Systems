/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a flat solid checkerboard panel in the yz-plane.
*/
public class PanelYZ extends Model
{
   /**
      Create a flat checkerboard panel in the yz-plane that runs
      from -1 to 1 in the y-direction and -1 to 1 in the z-direction.

      @param texture  {@link Texture} to use with this {@link Model}
   */
   public PanelYZ(final Texture texture)
   {
      this(texture, -1, 1, -1, 1);
   }


   /**
      Create a flat checkerboard panel in the yz-plane with the given dimensions.

      @param texture  {@link Texture} to use with this {@link Model}
      @param yMin  location of bottom edge
      @param yMax  location of top edge
      @param zMin  location of back edge
      @param zMax  location of front edge
   */
   public PanelYZ(final Texture texture,
                  final int yMin, final int yMax,
                  final int zMin, final int zMax)
   {
      this(texture, yMin, yMax, zMin, zMax, 0.0);
   }


   /**
      Create a flat checkerboard panel parallel to the yz-plane with the given dimensions.

      @param texture  {@link Texture} to use with this {@link Model}
      @param yMin  location of bottom edge
      @param yMax  location of top edge
      @param zMin  location of back edge
      @param zMax  location of front edge
      @param x     x-plane that holds the panel
   */
   public PanelYZ(final Texture texture,
                  final int yMin, final int yMax,
                  final int zMin, final int zMax,
                  final double x)
   {
      super("PanelYZ");

      // Add the given texture to this model.
      addTexture(texture);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.0, 1.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(1.0, 0.0));

      // Create the checkerboard panel's geometry.

      // An array of indexes to be used to create triangles.
      final int[][] index = new int[(yMax-yMin)+1][(zMax-zMin)+1];

      // Create the checkerboard of vertices.
      int i = 0;
      for (int y = yMin; y <= yMax; ++y)
      {
         for (int z = zMin; z <= zMax; ++z)
         {
            addVertex(new Vertex(x, y, z));
            index[y-yMin][z-zMin] = i;
            ++i;
         }
      }

      // Create the checkerboard of triangles.
      for (int y = 0; y < yMax-yMin; ++y)
      {
         for (int z = 0; z < zMax-zMin; ++z)
         {
            addPrimitive(new Triangle(index[y  ][z  ],
                                      index[y+1][z  ],
                                      index[y+1][z+1],
                                      0, 3, 2, // texture coordinates
                                      0));     // texture index

            addPrimitive(new Triangle(index[y+1][z+1],
                                      index[y  ][z+1],
                                      index[y  ][z  ],
                                      2, 1, 0, // texture coordinates
                                      0));     // texture index
         }
      }
   }
}//PanelYZ
