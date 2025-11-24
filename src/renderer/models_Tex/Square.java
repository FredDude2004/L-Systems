/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a textured model of a square in the xy-plane centered at the origin.
<p>
   Here is a picture showing how the square's four vertices are labeled.
<pre>{@code
                   y
                   |
                   |
      v1           |            v2
        +----------------------+
        |          |           |
        |          |           |
        |          |           |
        |          |           |
  ------|----------+-----------|-------> x
        |          |           |
        |          |           |
        |          |           |
        +----------------------+
      v0           |            v3
                   |
                   |
}</pre>
*/
public class Square extends Model
{
   /**
      Create a textured square in the xy-plane with corners {@code (±1, ±1, 0)}.

      @param texture  {@link Texture} to use with this {@link Model}
   */
   public Square(final Texture texture)
   {
      this(texture, 1);
   }


   /**
      Create a textured square in the xy-plane with corners {@code (±r, ±r, 0)}.

      @param texture  {@link Texture} to use with this {@link Model}
      @param r  determines the corners of the square
      @throws IllegalArgumentException if {@code r} is less than or equal to 0
   */
   public Square(final Texture texture, final double r)
   {
      super("Square");

      if (r <= 0)
         throw new IllegalArgumentException("r must be greater than 0");

      // Create the square's geometry.
      addVertex(new Vertex(-r, -r, 0),
                new Vertex(-r,  r, 0),
                new Vertex( r,  r, 0),
                new Vertex( r, -r, 0));

      // Add the given texture to this model.
      addTexture(texture);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.0, 1.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(1.0, 0.0));

      // Create the triangles.
      addPrimitive(new Triangle(0, 3, 2,  // CCW
                                0, 3, 2,  // texture coordinates
                                0),       // texture index
                   new Triangle(0, 2, 1,  // CCW
                                0, 2, 1,  // texture coordinates
                                0));      // texture index
   }
}//Square
