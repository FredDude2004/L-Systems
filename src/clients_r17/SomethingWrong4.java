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
   This program demonstrates a problem with the
   way depth is interpolated across a line segment
   when using a perspective projection.
*/
public class SomethingWrong4
{
   public static void main(String[] args)
   {
      final Scene scene = new Scene();

      final Model model = new Model();

      final double z = -0.0001;

      model.addVertex(new Vertex( z, 0.0,  z),
                      new Vertex(-z, 0.0,  z),
                      new Vertex(-1, 0.0, -1),
                      new Vertex(-z/(2+z), 0.0, z/(2-z)));

      model.addColor(Color.red, Color.blue);

      model.addPrimitive(new LineSegment(0, 1, 0),  // red
                         new LineSegment(2, 3, 1)); // blue

      scene.addPosition(new Position(model));

      final int width  = 512;
      final int height = 512;
      final FrameBuffer fb = new FrameBuffer(width, height, Color.lightGray);

      scene.debug = true;
      Rasterize.debug = true;
      NearClip.doNearClipping = false;
      Pipeline.render(scene, fb);

      fb.dumpFB2File( "SomethingWrong4.ppm" );
   }
}
