/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a textured model of a right square pyramid with its
   base in the xz-plane and its apex on the positive y-axis.
<p>
   The square texture covers the bottom of the pyramid and
   it is "folded" over the top four triangles of the pyramid.
   The center of the texture is at the top of the pyramid.
<p>
   See <a href="https://en.wikipedia.org/wiki/Pyramid_(geometry)" target="_top">
                https://en.wikipedia.org/wiki/Pyramid_(geometry)</a>
*/
public class Pyramid extends Model
{
   /**
      Create a textured right square pyramid with its base in the xz-plane,
      a base side length of 2, height 1, and apex on the positive y-axis.

      @param texture0  {@link Texture} for the pyramid's walls
      @param texture1  {@link Texture} for the pyramid's base
   */
   public Pyramid(final Texture texture0, final Texture texture1)
   {
      this(texture0, texture1, 2.0, 1.0);
   }


   /**
      Create a textured right square pyramid with its base in the xz-plane,
      a base length of {@code s}, height {@code h}, and apex on the
      positive y-axis.

      @param texture0  {@link Texture} for the pyramid's walls
      @param texture1  {@link Texture} for the pyramid's base
      @param s  side length of the base in the xz-plane
      @param h  height of the apex on the y-axis
   */
   public Pyramid(final Texture texture0, final Texture texture1,
                  final double s, final double h)
   {
      super(String.format("Pyramid(%.2f,%.2f)", s, h));

      // Create the pyramid's geometry.
      addVertex(new Vertex(-s/2, 0, -s/2),  // base
                new Vertex(-s/2, 0,  s/2),
                new Vertex( s/2, 0,  s/2),
                new Vertex( s/2, 0, -s/2),
                new Vertex(  0,  h,   0));  // apex

      // Add the given textures to this model.
      addTexture(texture0, texture1);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 1.0),
                      new TexCoord(0.0, 0.0),
                      new TexCoord(1.0, 0.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(0.5, 0.5));

      // Create 6 triangles.
      // Make sure the triangles are all
      // oriented in the same way!
      addPrimitive(new Triangle(0, 3, 1,   // 2 base triangles
                                0, 3, 1,   // texture coordinates
                                1),        // texture index
                   new Triangle(2, 1, 3,
                                2, 1, 3,   // texture coordinates
                                1),        // texture index
                   new Triangle(4, 0, 1,   // 4 sides
                                4, 0, 1,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 1, 2,
                                4, 1, 2,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 2, 3,
                                4, 2, 3,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 3, 0,
                                4, 3, 0,   // texture coordinates
                                0));       // texture index
/*
      addPrimitive(new Triangle(0, 3, 1,   // 2 base triangles
                                0, 3, 1,   // texture coordinates
                                0),        // texture index
                   new Triangle(2, 1, 3,
                                2, 1, 3,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 0, 1,   // 4 sides
                                2, 1, 0,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 1, 2,
                                2, 0, 3,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 2, 3,
                                1, 0, 3,   // texture coordinates
                                0),        // texture index
                   new Triangle(4, 3, 0,
                                1, 3, 2,   // texture coordinates
                                0));       // texture index
*/
   }
}//Pyramid
