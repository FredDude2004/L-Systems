/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.Triangle;

/**
   Create a textured model of a regular octahedron
   with its center at the origin, having side length
   {@code  sqrt(2) = 1.4142},with its center plane given
   by the four vertices {@code  (±1, 0, ±1)}. and with
   the top and bottom vertices being {@code  (0, ±1, 0)}.
<p>
   The square texture is "folded" onto each of the four triangles
   that make up the top and bottom halves of the octahedron. The
   center of the texture is at the top and bottom vertex.
<p>
   See <a href="http://en.wikipedia.org/wiki/Octahedron" target="_top">
                http://en.wikipedia.org/wiki/Octahedron</a>

   @see Tetrahedron
   @see Cube
   @see Icosahedron
   @see Dodecahedron
*/
public class Octahedron extends Model
{
   /**
      Create a textured regular octahedron with its center at the
      origin, having side length {@code  sqrt(2) = 1.4142},
      with its center plane given by the four vertices
      {@code  (±1, 0, ±1)}. and with the top and bottom
      vertices being {@code  (0, ±1, 0)}.

      @param texture0  {@link Texture} for one half of the octahedron
      @param texture1  {@link Texture} for other half of the octahedron
   */
   public Octahedron(final Texture texture0, final Texture texture1)
   {
      super("Octahedron");

      // Create the octahedron's geometry.
      // It has 6 vertices, 12 edges, and 8 faces.
      addVertex(new Vertex(-1,  0,  0),  // four vertices around the center plane
                new Vertex( 0,  0, -1),
                new Vertex( 1,  0,  0),
                new Vertex( 0,  0,  1),
                new Vertex( 0,  1,  0),  // vertex at the top
                new Vertex( 0, -1,  0)); // vertex at the bottom
/*
      addVertex(new Vertex( 1,  0,  0),  // four vertices around the center plane
                new Vertex( 0,  0, -1),
                new Vertex(-1,  0,  0),
                new Vertex( 0,  0,  1),
                new Vertex( 0,  1,  0),  // vertex at the top
                new Vertex( 0, -1,  0)); // vertex at the bottom
*/
/*
      // These vertices create an Octahedron with side length 1.
      final double sqrt3 = Math.sqrt(3.0),
                   sqrt2 = Math.sqrt(2.0);
      addVertex(new Vertex( 0.5, 0,  0.5), // 4 vertices around the center plane
                new Vertex(-0.5, 0,  0.5),
                new Vertex(-0.5, 0, -0.5),
                new Vertex( 0.5, 0, -0.5),
                new Vertex( 0,  1/sqrt2, 0),  // vertex at the top
                new Vertex( 0, -1/sqrt2, 0)); // vertex at the bottom
*/
      // Add the given textures to this model.
      addTexture(texture0, texture1);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.0, 1.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(1.0, 0.0),
                      new TexCoord(0.5, 0.5));

      // Create 8 triangles.
      addPrimitive(new Triangle(1, 0, 4,  // top half
                                1, 0, 4,  // texture coordinates
                                0),       // texture index
                   new Triangle(0, 3, 4,
                                0, 3, 4,  // texture coordinates
                                0),       // texture index
                   new Triangle(2, 1, 4,
                                2, 1, 4,  // texture coordinates
                                0),       // texture index
                   new Triangle(3, 2, 4,
                                3, 2, 4,  // texture coordinates
                                0),       // texture index
                   new Triangle(0, 1, 5,  /// bottom half
                                0, 1, 4,  // texture coordinates
                                1),       // texture index
                   new Triangle(3, 0, 5,
                                3, 0, 4,  // texture coordinates
                                1),       // texture index
                   new Triangle(1, 2, 5,
                                1, 2, 4,  // texture coordinates
                                1),       // texture index
                   new Triangle(2, 3, 5,
                                2, 3, 4,  // texture coordinates
                                1));      // texture index
   }
}//Octahedron
