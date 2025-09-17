/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a textured model of a cube with its center
   at the origin, having edge length 2, and with its
   vertices at {@code (±1, ±1, ±1)}.
<p>
   The square texture is placed on each of the six
   faces of the cube.
<p>
   Here is a picture showing how the cube's eight
   corners are labeled.
<pre>{@code
                  v4=(-1,1,-1)
                  +---------------------+ v5=(1,1,-1)
                 /|                    /|
                / |                   / |
               /  |                  /  |
              /   |                 /   |
             /    |                /    |
         v7 +---------------------+ v6  |
            |     |               |     |
            |     |               |     |
            |     | v0=(-1,-1,-1) |     |
            |     +---------------|-----+ v1=(1,-1,-1)
            |    /                |    /
            |   /                 |   /
            |  /                  |  /
            | /                   | /
            |/                    |/
            +---------------------+
            v3=(-1,-1,1)          v2=(1,-1,1)
}</pre>
   See <a href="https://en.wikipedia.org/wiki/Cube" target="_top">
                https://en.wikipedia.org/wiki/Cube</a>

   @see Tetrahedron
   @see Octahedron
   @see Icosahedron
   @see Dodecahedron
*/
public class Cube extends Model
{
   /**
      Create a textured cube with its center at the origin, having
      edge length 2, and with its vertices at {@code (±1, ±1, ±1)}.

      @param texture  {@link Texture} to use with this {@link Model}
   */
   public Cube(final Texture texture)
   {
      this(texture, "Cube");
   }


   /**
      Create a textured cube with its center at the origin, having
      edge length 2, and with its vertices at {@code (±1, ±1, ±1)}.

      @param texture  {@link Texture} to use with this {@link Model}
      @param name  a {link String} that is a name for this {@code Cube}
   */
   public Cube(final Texture texture,
               final String name)
   {
      this(texture,
           texture,
           texture,
           texture,
           texture,
           texture,
           name);
   }


   /**
      Create a textured cube with its center at the origin, having
      edge length 2, and with its vertices at {@code (±1, ±1, ±1)}.

      @param texture0  {@link Texture} to use on the bottom face
      @param texture1  {@link Texture} to use on the front face
      @param texture2  {@link Texture} to use on the right face
      @param texture3  {@link Texture} to use on the back face
      @param texture4  {@link Texture} to use on the left face
      @param texture5  {@link Texture} to use on the top face
      @param name  a {link String} that is a name for this {@code Cube}
   */
   public Cube(final Texture texture0,
               final Texture texture1,
               final Texture texture2,
               final Texture texture3,
               final Texture texture4,
               final Texture texture5,
               final String name)
   {
      super(name);

      // Create 8 vertices.
      addVertex(new Vertex(-1, -1, -1), // 4 vertices around the bottom face
                new Vertex( 1, -1, -1),
                new Vertex( 1, -1,  1),
                new Vertex(-1, -1,  1),
                new Vertex(-1,  1, -1), // 4 vertices around the top face
                new Vertex( 1,  1, -1),
                new Vertex( 1,  1,  1),
                new Vertex(-1,  1,  1));

      // Add the given textures to this model.
      addTexture(texture0,
                 texture1,
                 texture2,
                 texture3,
                 texture4,
                 texture5);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.0, 1.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(1.0, 0.0));

      // Create 12 triangles, 2 for each face of the cube.
      addPrimitive(new Triangle(0, 1, 2,   // bottom face
                                0, 3, 2,   // texture coordinates
                                0),        // texture index
                   new Triangle(2, 3, 0,
                                2, 1, 0,   // texture coordinates
                                0),        // texture index
                   new Triangle(3, 2, 6,   // front face
                                0, 3, 2,   // texture coordinates
                                1),        // texture index
                   new Triangle(6, 7, 3,
                                2, 1, 0,   // texture coordinates
                                1),        // texture index
                   new Triangle(2, 1, 5,   // right face
                                0, 3, 2,   // texture coordinates
                                2),        // texture index
                   new Triangle(5, 6, 2,
                                2, 1, 0,   // texture coordinates
                                2),        // texture index
                   new Triangle(1, 0, 4,   // back face
                                0, 3, 2,   // texture coordinates
                                3),        // texture index
                   new Triangle(4, 5, 1,
                                2, 1, 0,   // texture coordinates
                                3),        // texture index
                   new Triangle(0, 3, 7,   // left face
                                0, 3, 2,   // texture coordinates
                                4),        // texture index
                   new Triangle(7, 4, 0,
                                2, 1, 0,   // texture coordinates
                                4),        // texture index
                   new Triangle(7, 6, 5,   // top face
                                0, 3, 2,   // texture coordinates
                                5),        // texture index
                   new Triangle(5, 4, 7,
                                2, 1, 0,   // texture coordinates
                                5));       // texture index
   }
}//Cube
