/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.Assets;
import renderer.scene.util.ModelShading;
import renderer.scene.util.DrawSceneGraph;
import renderer.models_F.*; // models defined using oriented primitives
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
public class Geometries_R14
{
   private static final String assets = Assets.getPath();

   public static void main(String[] args)
   {
      // Create a two-dimensional array of Models.
      final Model[][] model = new Model[5][3];

      // row 0 (first row in the first image)
      model[0][0] = new TriangularPrism(1.0, 1.0, 10);
      ModelShading.setColor(model[0][0],
                            Color.green.darker().darker());

      model[0][1] = new Cylinder(0.5, 1.0, 30, 30);
      ModelShading.setColor(model[0][1],
                            Color.blue.brighter().brighter());

      model[0][2] = new ObjSimpleModel(new File(
                          assets + "great_rhombicosidodecahedron.obj"));
      ModelShading.setColor(model[0][2], Color.red);

      // row 1
      model[1][0] = new GRSModel(new File(
                          assets + "grs/bronto.grs"));
      ModelShading.setColor(model[1][0], Color.red);

      model[1][1] = new ObjSimpleModel(new File(
                          assets + "horse.obj"));
      ModelShading.setColor(model[1][1], Color.pink.darker());

      model[1][2] = new ConeFrustum(0.5, 1.0, 1.0, 10, 10);
      ModelShading.setColor(model[1][2], Color.orange.darker());

      // row 2
      model[2][0] = new Torus(0.75, 0.25, 30, 30);
      ModelShading.setColor(model[2][0], Color.gray);

      model[2][1] = new Octahedron(6);
      ModelShading.setColor(model[2][1], Color.green);

      model[2][2] = new Box(1.0, 1.0, 1.0);
      ModelShading.setRandomPrimitiveColors(model[2][2]);

      // row 3 (back row in the first image)
      model[3][0] = new ParametricCurve(
                t -> 0.3*(Math.sin(t) + 2*Math.sin(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(Math.cos(t) - 2*Math.cos(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(-Math.sin(3*t)),
                0, 6*Math.PI, 120);
      ModelShading.setRandomPrimitiveColors(model[3][0]);

      model[3][1] = new ObjSimpleModel(new File(
                          assets + "small_rhombicosidodecahedron.obj"));
      ModelShading.setColor(model[3][1], Color.magenta);

      model[3][2] = new SurfaceOfRevolution(
                t -> 1.5*(0.5 + 0.15 * Math.sin(10*t+1.0)*Math.sin(5*t+0.5)),
                -0.1, 0.9,
                30, 30);
      ModelShading.setColor(model[3][2], Color.blue);

      // row 4 (last row in first image)
      model[4][0] = new Cone(0.5, 1.0, 30, 30);
      ModelShading.setColor(model[4][0], Color.yellow);

      model[4][1] = new Tetrahedron(12, 12);
      ModelShading.setColor(model[4][1],
                            Color.green.brighter().brighter());

      model[4][2] = new Sphere(1.0, 30, 30);
      ModelShading.setColor(model[4][2],
                            Color.cyan.brighter().brighter());

      // Create x, y and z axes.
      final Model xyzAxes = new Axes3D(6, -6, 6, 0, 7, -7, Color.red);

      // Create a horizontal coordinate plane model.
      final Model xzPlane = new PanelXZ(-6, 6, -7, 7);
      ModelShading.setColor(xzPlane, Color.darkGray);

      // Create a framebuffer to render our scene into.
      final int width  = 1800;
      final int height =  900;
      final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

      Rasterize.doAntiAliasing = true;
      Rasterize.doGamma = true;

      // Set up the camera's view frustum.
      final double right  = 2.0;
      final double left   = -right;
      final double top    = 1.0;
      final double bottom = -top;
      final Camera camera = Camera.projPerspective(left, right, bottom, top);
/*
      final double fov    = 90.0;
      final double aspect = 2.0;
      final Camera camera = Camera.projPerspective(fov, aspect));
*/
      final long startTime, stopTime;
      startTime = System.currentTimeMillis();
      for (int k = 0; k < 360; ++k)
      {
         final Scene scene = new Scene("Geometries_R14_frame_"+k, camera);

         // Position the camera (move it around the periphery of the scene).
         scene.camera.view2Identity();
         scene.camera.viewRotateY(-k);
         scene.camera.viewTranslate(0, 3, 10);

         // Add the xz-plane and xyz-axes models to the Scene.
         scene.addPosition( new Position(xzPlane),   // draw the grid first
                            new Position(xyzAxes) ); // draw the axes on top of the grid

         // Place each model where it belongs in the xz-plane
         // and also rotate each model on its own axis.
         for (int i = model.length - 1; i >= 0; --i) // from back to front
         {
            for (int j = 0; j < model[i].length; ++j)
            {
               // Place this model where it belongs in the plane.
               // Then rotate this model on its own axis.
               final Matrix mat = Matrix.translate(-4+4*j, 0, 6-3*i)
                           .times(Matrix.rotateX(3*k))
                           .times(Matrix.rotateY(3*k));
               scene.addPosition(new Position(model[i][j],
                                              "p["+i+"]["+j+"]",
                                              mat));
            }
         }

         // Draw one Scene graph image.
         if (0 == k) DrawSceneGraph.draw(scene, "Geometries_R14_SG");

         // Render
         fb.clearFB();
         Pipeline.render(scene, fb);
         fb.dumpFB2File(String.format("PPM_Geometries_R14_Frame%03d.ppm", k));
       //fb.dumpFB2File(String.format("PNG_Geometries_R14_Frame%03d.png", k), "png");
      }
      stopTime = System.currentTimeMillis();
      System.out.println("Wall-clock time: " + (stopTime - startTime));
   }
}
