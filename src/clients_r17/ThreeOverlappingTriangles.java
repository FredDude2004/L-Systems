/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.*;
import renderer.scene.primitives.Triangle;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;

/**
   See
   https://en.wikipedia.org/wiki/Painter%27s_algorithm
*/
public class ThreeOverlappingTriangles
{
   public static void main(String[] args)
   {
//      scene.camera.projPerspective(-2, 2, -2, 2, 1);
//      scene.camera.viewTranslate(0, 0, 1);

      final Model model0 = new Model();
      final Model model1 = new Model();
      final Model model2 = new Model();

      final Vertex v0 = new Vertex(-1,  1,  -3);  // red
      final Vertex v1 = new Vertex(-2,  0,  -3);
      final Vertex v2 = new Vertex( 2, -1,  -2);

      final Vertex v3 = new Vertex( 2,  1,  -3);  // green
      final Vertex v4 = new Vertex( 2,  2,  -3);
      final Vertex v5 = new Vertex(-1, -1,  -2);

      final Vertex v6 = new Vertex( 1, -2,  -3);  // blue
      final Vertex v7 = new Vertex( 2, -2,  -3);
      final Vertex v8 = new Vertex( 1,  2,  -2);

      model0.addVertex(v0, v1, v2);
      model1.addVertex(v3, v4, v5);
      model2.addVertex(v6, v7, v8);

      model0.addPrimitive(new Triangle(0, 1, 2));
      model1.addPrimitive(new Triangle(0, 1, 2));
      model2.addPrimitive(new Triangle(0, 1, 2));

      ModelShading.setColor(model0, Color.red);
      ModelShading.setColor(model1, Color.green);
      ModelShading.setColor(model2, Color.blue);

      final Scene scene0 = new Scene();
      final Scene scene1 = new Scene();
      final Scene scene2 = new Scene();

      scene0.addPosition(new Position(model0),  // red
                         new Position(model1),  // green
                         new Position(model2)); // blue

      scene1.addPosition(new Position(model1),  // green
                         new Position(model2),  // blue
                         new Position(model0)); // red

      scene2.addPosition(new Position(model2),  // blue
                         new Position(model0),  // red
                         new Position(model1)); // green

      final int width  = 1024;
      final int height = 1024;
      final FrameBuffer fb = new FrameBuffer(width, height);

      fb.clearFB(Color.darkGray);
      Pipeline.render(scene0, fb.vp);
      fb.dumpFB2File( "ThreeOverlappingTriangles_scene_0.ppm" );

      fb.clearFB(Color.darkGray);
      Pipeline.render(scene1, fb.vp);
      fb.dumpFB2File( "ThreeOverlappingTriangles_scene_1.ppm" );

      fb.clearFB(Color.darkGray);
      Pipeline.render(scene2, fb.vp);
      fb.dumpFB2File( "ThreeOverlappingTriangles_scene_2.ppm" );
   }
}
