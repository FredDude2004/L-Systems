/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.Triangle;

/**
   Create a textured model of a regular tetrahedron
   with its center at the origin, having edge length
   {@code 2*sqrt(2)}, and with its vertices at corners
   of the cube with vertices {@code (±1, ±1, ±1)}.
<p>
   The square texture is "folded" over each half
   (two triangles) of the tetrahedron.
<p>
   See <a href="https://en.wikipedia.org/wiki/Tetrahedron" target="_top">
                https://en.wikipedia.org/wiki/Tetrahedron</a>

   @see Cube
   @see Octahedron
   @see Icosahedron
   @see Dodecahedron
*/
public class Tetrahedron extends Model
{
   /**
      Create a textured regular tetrahedron with its center at
      the origin, having edge length {@code 2*sqrt(2)},
      and with its vertices at corners of the cube with
      vertices {@code (±1, ±1, ±1)}.

      @param texture0  {@link Texture} for one half of the tetrahedron
      @param texture1  {@link Texture} for other half of the tetrahedron
   */
   public Tetrahedron(final Texture texture0, final Texture texture1)
   {
      this(texture0, texture1, false);
   }


   /**
      Create a textured regular tetrahedron or its dual tetrahedron
      (the dual of a tetrahedron is another tetrahedron).
   <p>
      <a href="https://en.wikipedia.org/wiki/Tetrahedron#Regular_tetrahedron" target="_top">
               https://en.wikipedia.org/wiki/Tetrahedron#Regular_tetrahedron</a>
   <p>
      The combination of these two dual tetrahedrons is a stellated octahedron.
   <p>
      <a href="https://en.wikipedia.org/wiki/Stellated_octahedron" target="_top">
               https://en.wikipedia.org/wiki/Stellated_octahedron</a>

      @param texture0  {@link Texture} for one half of the tetrahedron
      @param texture1  {@link Texture} for other half of the tetrahedron
      @param dual  choose between the two dual tetrahedrons
   */
   public Tetrahedron(final Texture texture0, final Texture texture1,
                      final boolean dual)
   {
      super("Tetrahedron");

      // Create the tetrahedron's geometry.
      // It has 4 vertices, 6 edges, and 4 faces.
      if (! dual)
      {
         addVertex(new Vertex( 1,  1,  1),
                   new Vertex(-1,  1, -1),
                   new Vertex( 1, -1, -1),
                   new Vertex(-1, -1,  1));
      }
      else // Create the dual tetrahedron by
      {    // inverting the coordinates given above.
         addVertex(new Vertex( 1, -1,  1),
                   new Vertex(-1, -1, -1),
                   new Vertex(-1,  1,  1),
                   new Vertex( 1,  1, -1));
      }

      // Add the given textures to this model.
      addTexture(texture0, texture1);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.0, 1.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(1.0, 0.0));

      // Create four triangles.
      addPrimitive(new Triangle(0, 1, 3,  // top edge
                                3, 1, 0,  // texture coordinates
                                0),       // texture index
                   new Triangle(1, 0, 2,
                                1, 3, 2,  // texture coordinates
                                0),       // texture index
                   new Triangle(3, 2, 0,  // bottome edge
                                0, 2, 3,  // texture coordinates
                                1),       // texture index
                   new Triangle(2, 3, 1,
                                2, 0, 1,  // texture coordinates
                                1));      // texture index
   }
}//Tetrahedron
