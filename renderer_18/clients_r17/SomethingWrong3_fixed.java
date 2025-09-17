/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.Assets;
import renderer.scene.util.DrawSceneGraph;
import renderer.scene.util.ModelShading;
import renderer.models_T.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import java.io.File;

/**
   This program shows that our renderer now
   keeps track of depth information.

   This program draws two models, a sphere and
   a cow. The cow is inside the sphere. The image
   drawn by the renderer no longer depends on what
   order the models are placed in the Scene's model
   list. The final image depends on the order of
   the models in space.
*/
public class SomethingWrong3_fixed
{
   private static final String assets = Assets.getPath();

   public static void main(String[] args)
   {
      final Scene scene = new Scene("SomethingWrong3");
      scene.camera.viewTranslate(0, 0, 2);

      final Position p1 = new Position(new Sphere(1.0, 30, 30),
                                       "p1");
      ModelShading.setColor(p1.getModel(), Color.red);

      // Place the cow in the middle of the sphere, but sticking out a bit.
      final Position p2 = new Position(new ObjSimpleModel(new File(assets + "cow.obj")),
                                       "p2",
                                       Matrix.translate(0.3, 0, 0));
      ModelShading.setColor(p2.getModel(), Color.blue);

      // Add the Models to the Scene.
      scene.addPosition(p1, p2);

      DrawSceneGraph.draw(scene, "SG_SomethingWrong3_fixed");


      // Create a FrameBuffer to render our scene into.
      final int width  = 1024;
      final int height = 1024;
      final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

      // Render our scene into the framebuffer.
      Pipeline.render(scene, fb);
      fb.dumpFB2File( "SomethingWrong3a_fixed.ppm" );

      // Reverse the order of the two models in the scene's position list.
      final Position temp = scene.positionList.remove(0);
      scene.addPosition(temp);

      // Render our scene into the framebuffer.
      fb.clearFB();
      Pipeline.render(scene, fb);
      fb.dumpFB2File( "SomethingWrong3b_fixed.ppm" );
   }
}
