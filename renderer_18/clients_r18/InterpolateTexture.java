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
          | v3              v2=(1,1,0)
        1 +-----------------+
          |               / |
          |             /   |
          |           /     |
          |         /       |
          |       /         |
          |     /           |
          |   /             |
          | /               | v1
       v0 +-----------------+-----> x
          0                 1

*/
public class InterpolateTexture
{
   private static final String assets = Assets.getPath();

   public static void main(String[] args)
   {
      final Camera camera = Camera.projPerspective(-0.5, 0.5, -0.5, 0.5);
      final Scene scene = new Scene("InterpolateTexture", camera);
      scene.camera.viewTranslate(0, 0, 1.5);

      final Model model = new Model("InterpolateTexture");
      model.doBackFaceCulling = false;
      model.facesHaveTwoSides = true;

      final Texture texture1 = new Texture(assets + "textures/Yoda.ppm");
      final Texture texture2 = new Texture(assets + "textures/Dumbledore.ppm");
      final Texture texture3 = new Texture(assets + "textures/uv_grid_opengl.ppm");
      final Texture texture = texture1;

      model.addTexture(texture);

      // Make all (nearly) white pixels transparent,
      // and all non-white pixles opaque.
      for (int y = 0; y < texture.height; ++y)
      {
         for (int x = 0; x < texture.width; ++x)
         {
            final int color = texture.pixel_buffer[ (y*texture.width) + x ];
            final Color c = new Color(color);
            if ( 252 > c.getRed() || 252 > c.getGreen() || 252 > c.getBlue() )
            {
               texture.alpha_buffer[ (y*texture.width) + x ] = 255;  // opaque
            }
            else // non-white pixel
            {
               texture.alpha_buffer[ (y*texture.width) + x ] = 0;  // transparent
            }
         }
      }

      model.addTextureCoord(new TexCoord(0.0, 0.0),
                            new TexCoord(0.0, 1.0),
                            new TexCoord(1.0, 1.0),
                            new TexCoord(1.0, 0.0));

      model.addVertex(new Vertex(0.0, -0.5, 0.0),
                      new Vertex(0.0,  0.5, 0.0),
                      new Vertex(1.0,  0.5, 0.0),
                      new Vertex(1.0, -0.5, 0.0));

      model.addColor(Color.red,
                     Color.yellow,
                     Color.green,
                     Color.blue);

      model.addPrimitive(new Triangle(0, 2, 1, // vertices
                                      0, 1, 0, // front face colors
                                      2, 3, 2, // back face colors
                                      0, 2, 1, // texture coordinates
                                      0),      // texture
                         new Triangle(0, 3, 2, // vertices
                                      0, 1, 0, // front face colors
                                      2, 3, 2, // back face colors
                                      0, 3, 2, // texture coordinates
                                      0));     // texture

      scene.addPosition(new Position(model));

      System.out.println(model);

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "InterpolateTexture_SG");


      final int width  = 800;
      final int height = 800;
      FrameBuffer fb = new FrameBuffer(width, height, Color.darkGray);

//      scene.debug = true;
//      Clip.debug = true;
//      Rasterize.debug = true;

      // Spin the model 360 degrees.
      for (int i = 0; i < 360; i++)
      {
         // Rotate the model by i degrees.
         scene.setPosition(0,
            scene.getPosition(0).transform(Matrix.rotateY(i)));

         // Render again.
         fb.clearFB();
         Pipeline.render(scene, fb);
         fb.dumpFB2File(String.format("InterpolateTexture_Frame%03d.ppm", i));
      }
   }
}
