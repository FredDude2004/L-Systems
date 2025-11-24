
            Textures


This renderer adds textures.


This renderer adds two new files to the scene package
   Texture.java,
   TexCoord.java.
and it adds a new pipeline stage
   RasterizeTriangleTextured.java.
to the pipeline package. We also modify the following files,
   Model.java         (add a list of Texture and a list of TexCoord),
   Primitive.java     (add a texture index, and a list of texture coordinate indices)
   ClipTriangle.java  (interpolate texture coordinates),
   Rasterize.java     (call RasterizeTriangleWithTexture).




Our pipeline is essentially the same as the previous one.
It has the same ten stages but the rasterization stage can
now rasterize a triangle with a texture.


       v_0 ... v_n     A Model's list of (homogeneous) Vertex objects
          \   /
           \ /
            |
            | model coordinates (of v_0 ... v_n)
            |
        +-------+
        |       |
        |   P1  |    Model2World (matrix) transformation (of the vertices)
        |       |
        +-------+
            |
            | world coordinates (of v_0 ... v_n)
            |
        +-------+
        |       |
        |   P2  |    World2View (matrix) transformation (of the vertices)
        |       |
        +-------+
            |
            | view coordinates (of v_0 ... v_n) relative to an arbitrary view volume
            |
        +-------+
        |       |
        |   P3  |    View2Camera (normalization matrix) transformation (of the vertices)
        |       |
        +-------+
            |
            | camera coordinates (of v_0 ... v_n) relative to the standard view volume
            |
           / \
          /   \
         /     \
        |   P4  |   Backface culling (of Face primitives)
         \     /
          \   /
           \ /
            |
            | list of culled primitives (in image plane coordinates)
            |
           / \
          /   \
         /     \
        |   P5  |   Primitive assembly (of each primitive into triangles, line segments or points)
         \     /
          \   /
           \ /
            |
            | list of triangles, line segments, and points (in image plane coordinates)
            |
           / \
          /   \
         /     \
        |   P6  |   Backface culling (of Triangle primitives)
         \     /
          \   /
           \ /
            |
            | list of culled primitives (in image plane coordinates)
            |
           / \
          /   \
         /     \
        |   P7  |   Near Clipping (of each primitive)
         \     /
          \   /
           \ /
            |
            | camera coordinates (of the near-clipped v_0 ... v_n)
            |
        +-------+
        |       |
        |   P8  |    Projection transformation (of the vertices)
        |       |
        +-------+
            |
            | image plane coordinates (of v_0 ... v_n)
            |
           / \
          /   \
         /     \
        |   P9  |   Clipping (of each primitive)
         \     /
          \   /
           \ /
            |
            | image plane coordinates (of the clipped vertices)
            |
        +-------+
        |       |
        |  P10a |    Viewport transformation (of the clipped vertices)
        |       |
        +-------+
            |
            | pixel-plane coordinates (of the clipped vertices)
            |
           / \
          /   \
         /     \
        |  P10  |   Rasterization with z-buffer and textures (of each clipped primitive)
         \     /
          \   /
           \ /
            |
            |  shaded pixels (for each clipped primitive)
            |
           \|/
    FrameBuffer.ViewPort
