/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.*;
import renderer.models_TP.*; // models defined using higher order triangle primitives
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import java.io.File;

/**
   This version moves the camera around the array of models.
<p>
   Compare with
      http://threejs.org/examples/#webgl_geometries
   or
      https://stemkoski.github.io/Three.js/Shapes.html
   or
      http://www.smartjava.org/ltjs/chapter-02/04-geometries.html
*/
public class Geometries_threejs_R17
{
   private static final String assets = Assets.getPath();

   public static void main(String[] args)
   {
      // Create a two-dimensional array of Models.
      final Model[][] model = new Model[4][3];

      // Row 0.
      model[0][0] = new ConeFrustum(0.5, 1.0, 1.0, 10, 10);
      ModelShading.setColor(model[0][0], Color.orange.darker());

      model[0][1] = new Cylinder(0.5, 1.0, 30, 30);
      ModelShading.setPrimitiveColorShaded(model[0][1],
                                           Color.blue.brighter().brighter());

      model[0][2] = new Sphere(1.0, 30, 30);
      ModelShading.setPrimitiveColorShaded(model[0][2],
                                           Color.cyan.brighter().brighter());

      // Row 1.
      model[1][0] = new SurfaceOfRevolution(
                t -> 0.5 + 0.15 * Math.sin(10*t+1.0) * Math.sin(5*t+0.5),
                -0.1, 0.9,
                20, 20);
      ModelShading.setRandomPrimitiveColors(model[1][0]);
      model[1][0].doBackFaceCulling = false;
      model[1][0].facesHaveTwoSides = true;
      final Color backFaceColor = new Color(150, 200, 150); // light green
      ModelShading.setBackFaceColor(model[1][0],
                                    backFaceColor,  // light green
                                    backFaceColor.brighter().brighter(),
                                    backFaceColor.darker().darker());

      model[1][1] = new Cube2(10, 10, 10);
      model[1][1] = model[1][1].transform(Matrix.scale(0.5, 0.5, 0.5));
      ModelShading.setRandomPrimitiveColors(model[1][1]);

      model[1][2] = new renderer.models_F.ObjSimpleModel(
                       new File(
                          assets + "great_rhombicosidodecahedron.obj"));
      ModelShading.setColor(model[1][2], Color.red);

      // Row 2.
      model[2][0] = new Torus(0.5, 0.2, 30, 30);
      ModelShading.setRandomPrimitiveColors(model[2][0]);

      model[2][1] = new Octahedron();
      ModelShading.setRandomPrimitiveColors(model[2][1]);

      model[2][2] = new ObjSimpleModel(
                       new File(
                          assets + "small_rhombicosidodecahedron.obj"));
      ModelShading.setRandomPrimitiveColors(model[2][2]);

      // Row 3.
      model[3][0] = new ParametricCurve(
                t -> 0.3*(Math.sin(t) + 2*Math.sin(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(Math.cos(t) - 2*Math.cos(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(-Math.sin(3*t)),
                0, 6*Math.PI, 120);
      ModelShading.setRandomVertexColors(model[3][0]);

      model[3][1] = new Cone(0.5, 1.0, 30, 30);
      ModelShading.setRandomPrimitiveColors(model[3][1]);

      model[3][2] = new Tetrahedron();
      ModelShading.setRandomPrimitiveColors(model[3][2]);

      // Create x, y and z axes
      final Model xyzAxes = new Axes3D(4, -4, 4, 0, 4, -4, Color.red);

      // Create a horizontal coordinate plane model.
      final Model xzPlane = new renderer.models_F.PanelXZ(-4, 4, -4, 4);
      ModelShading.setColor(xzPlane, Color.darkGray);

      // Create a framebuffer to render our scene into.
      final int width  = 1000;
      final int height = 1000;
      final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

      Rasterize.doAntiAliasing = true;
      Rasterize.doGamma = true;

      // Set up the camera's view frustum.
/*
      final double right  = 2.0;
      final double left   = -right;
      final double top    = 1.0;
      final double bottom = -top;
      final Camera camera = Camera.projPerspective(left, right, bottom, top);
*/
      final double fov    = 45.0;
      final double aspect = 1.0;
      final Camera camera = Camera.projPerspective(fov, aspect);

      final long startTime, stopTime;
      startTime = System.currentTimeMillis();
      for (int k = 0; k < 360; ++k)
      {
         final Scene scene = new Scene("Geometries_threejs_R17_frame_"+k, camera);
/*
         // Position the camera (move it around the periphery of the scene).
         scene.camera.view2Identity();
         scene.camera.viewRotateY(-k);
         scene.camera.viewTranslate(0, 4, 8);
*/
         // Position the camera (move it around the periphery of the scene).
         scene.camera.viewLookAt(9*Math.cos(k*Math.PI/180),
                            4,
                            9*Math.sin(k*Math.PI/180),
                            0, 0, 0,
                            0, 1, 0);

         // Add the xz-plane and xyz-axes models to the Scene.
         scene.addPosition( new Position(xzPlane),   // draw the grid first
                            new Position(xyzAxes) ); // draw the axes on top of the grid

         // Place each model where it belongs in the xz-plane
         // and also rotate each model on its own axis.
         for (int i = 0; i < model.length; ++i)
         {
            for (int j = 0; j < model[i].length; ++j)
            {
               final Matrix mat = Matrix.translate(3-3*j, 0, 3-2*i)
                           .times(Matrix.rotateX(3*k))
                           .times(Matrix.rotateY(1.5*k));
               scene.addPosition(new Position(model[i][j],
                                              "p["+i+"]["+j+"]",
                                              mat));
            }
         }

         // Draw one Scene graph image.
         if (0 == k) DrawSceneGraph.draw(scene, "Geometries_threejs_R17_SG");

         // Render
         fb.clearFB();
         Pipeline.render(scene, fb);
         fb.dumpFB2File(String.format("PPM_Geometries_threejs_R17_Frame%03d.ppm", k));
       //fb.dumpFB2File(String.format("PNG_Geometries_threejs_R17_Frame%03d.png", k), "png");
      }
      stopTime = System.currentTimeMillis();
      System.out.println("Wall-clock time: " + (stopTime - startTime));
   }
}
