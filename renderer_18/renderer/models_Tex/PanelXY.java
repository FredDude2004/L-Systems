/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a flat solid checkerboard panel in the xy-plane.
*/
public class PanelXY extends Model
{
   /**
      Create a flat checkerboard panel in the xy-plane that runs
      from -1 to 1 in the x-direction and -1 to 1 in the y-direction.

      @param texture  {@link Texture} to use with this {@link Model}
   */
   public PanelXY(final Texture texture)
   {
      this(texture, -1, 1, -1, 1);
   }


   /**
      Create a flat checkerboard panel in the xy-plane with the given dimensions.

      @param texture  {@link Texture} to use with this {@link Model}
      @param xMin  location of left edge
      @param xMax  location of right edge
      @param yMin  location of bottom edge
      @param yMax  location of top edge
   */
   public PanelXY(final Texture texture,
                  final int xMin, final int xMax,
                  final int yMin, final int yMax)
   {
      this(texture, xMin, xMax, yMin, yMax, 0.0);
   }


   /**
      Create a flat checkerboard panel parallel to the xy-plane with the given dimensions.

      @param texture  {@link Texture} to use with this {@link Model}
      @param xMin  location of left edge
      @param xMax  location of right edge
      @param yMin  location of bottom edge
      @param yMax  location of top edge
      @param z     z-plane that holds the panel
   */
   public PanelXY(final Texture texture,
                  final int xMin, final int xMax,
                  final int yMin, final int yMax,
                  final double z)
   {
      super("PanelXY");

      // Add the given texture to this model.
      addTexture(texture);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.0, 1.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(1.0, 0.0));

      // Create the checkerboard panel's geometry.

      // An array of indexes to be used to create triangles.
      final int[][] index = new int[(xMax-xMin)+1][(yMax-yMin)+1];

      // Create the checkerboard of vertices.
      int i = 0;
      for (int x = xMin; x <= xMax; ++x)
      {
         for (int y = yMin; y <= yMax; ++y)
         {
            addVertex(new Vertex(x, y, z));
            index[x-xMin][y-yMin] = i;
            ++i;
         }
      }

      // Create the checkerboard of triangles.
      for (int x = 0; x < xMax-xMin; ++x)
      {
         for (int y = 0; y < yMax-yMin; ++y)
         {
            addPrimitive(new Triangle(index[x  ][y  ],
                                      index[x+1][y  ],
                                      index[x+1][y+1],
                                      0, 3, 2, // texture coordinates
                                      0));     // texture index

            addPrimitive(new Triangle(index[x+1][y+1],
                                      index[x  ][y+1],
                                      index[x  ][y  ],
                                      2, 1, 0, // texture coordinates
                                      0));     // texture index
         }
      }
   }
}//PanelXY
