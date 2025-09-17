/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.Assets;
import renderer.scene.util.DrawSceneGraph;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;

/**
                  y
                  |
                  |
                  | v[4]
                1 +---------------------+ v[5]=(1,1,0)
                 /|                    /|
                / |                   / |
               /  |                  /  |
              /   |                 /   |
             /    |                /    |
       v[7] +---------------------+ v[6]|
            |     |               |     |
            |     |               |     |
            |     | v[0]          |     | v[1]
            |     +---------------|-----+------> x
            |    /                |    /1
            |   /                 |   /
            |  /                  |  /
            | /                   | /
            |/                    |/
          1 +---------------------+
           /v[3]=(0,0,1)          v[2]=(1,0,1)
          /
         /
        z
*/
public class TexturedCube
{
   private static final String assets = Assets.getPath();

   public static void main(String[] args)
   {
      final double fov    = 35.0;
      final double aspect = 1.0;
      final Camera camera = Camera.projPerspective(fov, aspect);
      final Scene scene = new Scene("TexturedCube", camera);
      scene.camera.viewTranslate(0, 0, 4);

      final Model model = new Model("TexturedCube");
      model.doBackFaceCulling = false;
      model.frontFacingIsCCW = true;
      model.facesHaveTwoSides = true;

      // Create five textures for the Model.
      final Texture texture0 = new Texture(assets + "textures/Yoda.ppm");
      // Make all (nearly) white pixels transparent,
      // and all non-white pixles opaque.
      for (int y = 0; y < texture0.height; ++y)
      {
         for (int x = 0; x < texture0.width; ++x)
         {
            final int color = texture0.pixel_buffer[ (y*texture0.width) + x ];
            final Color c = new Color(color);
            if ( 252 > c.getRed() || 252 > c.getGreen() || 252 > c.getBlue() )
            {
               texture0.alpha_buffer[ (y*texture0.width) + x ] = 255;  // opaque
            }
            else // non-white pixel
            {
               texture0.alpha_buffer[ (y*texture0.width) + x ] = 0;  // transparent
            }
         }
      }

      final Texture texture1 = new Texture(assets + "textures/wood.ppm");
      final Texture texture2 = new Texture(assets + "textures/brick3.ppm");
      final Texture texture3 = new Texture(assets + "textures/uv_grid_opengl.ppm");

      // Create a "procedural" texture, pixel by pixel.
      final int SIZE = 256;
      final int[] pascalImage = new int[SIZE * SIZE * 4];
      for (int y = 0; y < SIZE; ++y)
      {
         for (int x = 0; x < SIZE; ++x)
         {
            final int c = ((x|y)%255); // Pascal's Triangle like image.
            final int index = (y*SIZE + x)*4;
            pascalImage[index + 0] = c;   // r
            pascalImage[index + 1] = c;   // g
            pascalImage[index + 2] = c;   // b
            pascalImage[index + 3] = 170; // alpha (about 70%)
         }
      }
      final Texture texture4 = new Texture("pascal", SIZE, SIZE, pascalImage);

      // Add the textures to the model.
      model.addTexture(texture0,  // yoda
                       texture1,  // wood
                       texture2,  // brick
                       texture3,  // grid
                       texture4); // pascal

      // Add texture coordinates to the model.
      model.addTextureCoord(new TexCoord(0.0, 0.0),
                            new TexCoord(0.0, 1.0),
                            new TexCoord(1.0, 1.0),
                            new TexCoord(1.0, 0.0));

      // Create the geometry for the Model.
      // Vertices.
      model.addVertex(new Vertex(0.0, 0.0, 0.0), // four vertices around the bottom face
                      new Vertex(1.0, 0.0, 0.0),
                      new Vertex(1.0, 0.0, 1.0),
                      new Vertex(0.0, 0.0, 1.0),
                      new Vertex(0.0, 1.0, 0.0), // four vertices around the top face
                      new Vertex(1.0, 1.0, 0.0),
                      new Vertex(1.0, 1.0, 1.0),
                      new Vertex(0.0, 1.0, 1.0));

      // Create three colors for the right face and
      // the faces withh transparent textures.
      model.addColor(new Color(255,  0,   255),  // magenta
                     new Color(255, 255,   0 ),  // yellow
                     new Color( 0,  255,  255)); // cyan
      // Add back face colors.
      final Color backFaceColor = new Color(150, 200, 150); // light green
      model.addColor(backFaceColor,
                     backFaceColor.brighter().brighter(),
                     backFaceColor.darker().darker());

      // Add geometry, with color and textures, to the Model.
      model.addPrimitive(
               // bottom
               new Triangle(0, 1, 2,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            2),       // brick texture
               new Triangle(0, 2, 3,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            2),       // brick texture
               // top
               new Triangle(7, 6, 5,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            1),       // wood texture
               new Triangle(7, 5, 4,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            1),       // wood texture
               // back
               new Triangle(0, 4, 5,  // vertices
                            1, 0, 1,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            4),       // pascal texture
               new Triangle(0, 5, 1,  // vertices
                            1, 1, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            4),       // pascal texture
               // front
               new Triangle(3, 2, 6,  // vertices
                            1, 2, 1,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 3, 2,  // texture coordinates
                            0),       // yoda texture
               new Triangle(3, 6, 7,  // vertices
                            1, 1, 2,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 1,  // texture coordinates
                            0),       // yoda texture
               // left
               new Triangle(0, 3, 7,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            3),       // grid texture
               new Triangle(0, 7, 4,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            3),       // grid texture
               // right
               new Triangle(1, 5, 6,  // vertices
                            2, 0, 2,  // front face color, yellow
                            3, 4, 5), // back face colors
               new Triangle(1, 6, 2,  // vertices
                            2, 2, 0,  // front face color, yellow
                            3, 4, 5));// back face colors

      scene.addPosition(new Position(model));

      System.out.println(model);

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "TexturedCube_SG");


      final int width  = 800;
      final int height = 800;
      FrameBuffer fb = new FrameBuffer(width, height, Color.darkGray);

//      scene.debug = true;
//      Clip.debug = true;
//      Rasterize.debug = true;

      // Spin the model.
      for (int i = 0; i < 360; i++)
      {
         scene.setPosition(0,
            scene.getPosition(0).transform( Matrix.rotateX(2*i)
                                     .times(Matrix.rotateZ(3*i)) ));

         // Render again.
         fb.clearFB();
         Pipeline.render(scene, fb);
         fb.dumpFB2File(String.format("TexturedCube_Frame%03d.ppm", i));
      }
   }
}
