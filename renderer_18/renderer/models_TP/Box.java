/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a solid model of a cuboid aligned with
   the x, y, and z axes and with one corner at the
   origin.
<p>
   Here is a picture showing how the cuboid's eight
   vertices are labeled.
<pre>{@code
                  y
                  |
                  | v4
                  +---------------------+ v5
                 /|                    /|
                / |                   / |
               /  |                  /  |
              /   |                 /   |
             /    |                /    |
         v7 +---------------------+ v6  |
            |     |               |     |
            |     |               |     |
            |     | v0=(0,0,0)    |     | v1
            |     +---------------|-----+------> x
            |    /                |    /
            |   /                 |   /
            |  /                  |  /
            | /                   | /
            |/                    |/
            +---------------------+
           /v3                    v2
          /
         z
}</pre>
   See <a href="http://en.wikipedia.org/wiki/Cuboid" target="_top">
                http://en.wikipedia.org/wiki/Cuboid</a>

   @see Cube
*/
public class Box extends Model
{
   /**
      Create a {@code Box} with all three sides of length 1.
   */
   public Box( )
   {
      this(1, 1, 1);
   }


   /**
      Create a {@code Box} with the given side lengths.

      @param xs  the size of the {@code Box} along the x-axis
      @param ys  the size of the {@code Box} along the y-axis
      @param zs  the size of the {@code Box} along the z-axis
   */
   public Box(final double xs, final double ys, final double zs)
   {
      super("Box");

      // Create 8 vertices.
      addVertex(new Vertex(0,    0,    0), // 4 vertices around the bottom face
                new Vertex(0+xs, 0,    0),
                new Vertex(0+xs, 0,    0+zs),
                new Vertex(0,    0,    0+zs),
                new Vertex(0,    0+ys, 0), // 4 vertices around the top face
                new Vertex(0+xs, 0+ys, 0),
                new Vertex(0+xs, 0+ys, 0+zs),
                new Vertex(0,    0+ys, 0+zs));

      //  https://stackoverflow.com/questions/28375338/cube-using-single-gl-triangle-strip
      //  http://www.cs.umd.edu/gvil/papers/av_ts.pdf

      // Create 12 triangles, 2 for each face of the box.
      addPrimitive(new TriangleStrip(0, 1, 3, 2),   // bottom face
                   new TriangleStrip(2, 6, 3, 7),   // front face
                   new TriangleStrip(1, 5, 2, 6),   // right face
                   new TriangleStrip(0, 4, 1, 5),   // back face
                   new TriangleStrip(0, 3, 4, 7),   // left face
                   new TriangleStrip(4, 7, 5, 6));  // top face
   }
}//Box
