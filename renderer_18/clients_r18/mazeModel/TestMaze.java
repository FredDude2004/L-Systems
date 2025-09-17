/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.pipeline.*;
import renderer.framebuffer.FrameBuffer;

import java.awt.Color;

/**

*/
public class TestMaze
{
   public static void main(String[] args)
   {
      final Scene scene = new Scene("TestMaze");

      final int width  = 1024;
      final int height = 512;
      final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

      final Model model = new Maze("maze4.txt");
      model.doBackFaceCulling = false;    /***** FIX THE ORIENTATIONS! ***/

      scene.addPosition(new Position(model));

      final double fov    = 90.0;
      final double aspect = 2.0;
      final Camera camera = Camera.projPerspective(fov, aspect);
//    final Camera camera = Camera.projOrtho(fov, aspect);

      // Middle of the maze, looking at the Mona Lisa.
      scene.camera.viewTranslate(80, 5, 80);
      scene.camera.viewRotateY(180);

      for (int t = 0; t < 360; ++t)
      {
         scene.camera.viewRotateY(1);

         fb.clearFB();
         Pipeline.render(scene, fb);
         fb.dumpFB2File(String.format("PPM_TestMaze%03d.ppm", t));
      }
   }
}
