/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_T;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a cube with its center
   at the origin, having edge length 2, and with its
   vertices at {@code (±1, ±1, ±1)}.
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
      Create a cube with its center at the origin, having edge
      length 2, and with its vertices at {@code (±1, ±1, ±1)}.
   */
   public Cube( )
   {
      this("Cube");
   }


   /**
      Create a cube with its center at the origin, having edge
      length 2, and with its vertices at {@code (±1, ±1, ±1)}.

      @param name  a {link String} that is a name for this {@code Cube}
   */
   public Cube(final String name)
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

      // Create 12 triangles, 2 for each face of the cube.
      addPrimitive(new Triangle(0, 1, 2),  // bottom face
                   new Triangle(2, 3, 0),
                   new Triangle(3, 2, 6),  // front face
                   new Triangle(6, 7, 3),
                   new Triangle(2, 1, 5),  // right face
                   new Triangle(5, 6, 2),
                   new Triangle(1, 0, 4),  // back face
                   new Triangle(4, 5, 1),
                   new Triangle(0, 3, 7),  // left face
                   new Triangle(7, 4, 0),
                   new Triangle(7, 6, 5),  // top face
                   new Triangle(5, 4, 7));
   }
}//Cube
