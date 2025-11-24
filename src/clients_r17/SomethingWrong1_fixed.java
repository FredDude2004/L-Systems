/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.LineSegment;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;

/**

*/
public class SomethingWrong1_fixed
{
   public static void main(String[] args)
   {
      // Create a Model object to hold the geometry.
      Model model = new Model();

      // Add the geometry (in model coordinates) to the Model.
      Vertex v0 = new Vertex( -2.0, 0.0,  -1.0  );
      Vertex v1 = new Vertex(  0.0, 0.0,  -100.0 );
      Vertex v2 = new Vertex( -2.0, 0.25, -1.0  );
      Vertex v3 = new Vertex(  0.0, 0.25, -1.0 );

      model.addVertex(v0, v1, v2, v3);
      model.addColor(Color.red, Color.blue);

      model.addPrimitive(new LineSegment(0, 1, 0, 1),
                         new LineSegment(2, 3, 0, 1));

      // Create the Scene object that we shall render.
      Scene scene = new Scene();

      // Add the Model to the Scene.
      scene.addPosition(new Position(model));


      // Create a FrameBuffer to render our scene into.
      int width  = 512;
      int height = 512;
      FrameBuffer fb = new FrameBuffer(width, height);

      // Give the framebuffer a background color.
      fb.clearFB(Color.lightGray);
      // Render our scene into the frame buffer using a Perspective Projection.
      Pipeline.render(scene, fb);
      // Save the resulting image in a file.
      fb.dumpFB2File( "SomethingWrong1_fixed.ppm" );
   }
}
