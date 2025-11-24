/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.*;
import renderer.models_Tex.*; // textured models
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import java.io.File;

/**
   This version moves the camera around the array of models.
<p>
   Compare with
      http://threejs.org/examples/#webgl_geometries
*/
public class Geometries_threejs_R18
{
   private static final String assets = Assets.getPath();

   public static void main(String[] args)
   {
    //final Texture texture = new Texture(assets + "textures/uv_grid_opengl.ppm");
      final Texture texture = new Texture(assets + "textures/uv_grid_256.ppm");
    //final Texture texture = new Texture(assets + "textures/uv_grid_128.ppm");

      // Create a two-dimensional array of Models.
      final Model[][] model = new Model[3][4];

      // Row 0.
      model[0][0] = new Sphere(texture, 0.75, 30, 30);
      ModelShading.setRandomPrimitiveColors(model[0][0]);

      model[0][1] = new Icosahedron(texture);
      model[0][1] = model[0][1].transform(Matrix.scale(0.75, 0.75, 0.75));
      ModelShading.setRandomPrimitiveColors(model[0][1]);

      model[0][2] = new Octahedron(texture, texture);
      model[0][2] = model[0][2].transform(Matrix.scale(0.75, 0.75, 0.75));
      ModelShading.setRandomPrimitiveColors(model[0][2]);

      model[0][3] = new Tetrahedron(texture, texture);
      model[0][3] = model[0][3].transform(Matrix.scale(0.5, 0.5, 0.5));
      ModelShading.setRandomPrimitiveColors(model[0][3]);


      // Row 1.
      model[1][0] = new Square(texture);
      model[1][0] = model[1][0].transform(Matrix.scale(0.5, 0.5, 0.5));
      ModelShading.setRandomPrimitiveColors(model[1][0]);
      model[1][0].doBackFaceCulling = false;
      model[1][0].facesHaveTwoSides = false;

      model[1][1] = new Cube(texture);
      model[1][1] = model[1][1].transform(Matrix.scale(0.4, 0.4, 0.4));
      ModelShading.setRandomPrimitiveColors(model[1][1]);

      model[1][2] = new Disk(texture);
      model[1][2] = model[1][2].transform(Matrix.scale(0.5, 0.5, 0.5));
      ModelShading.setRandomPrimitiveColors(model[1][2]);
      model[1][2].doBackFaceCulling = false;
      model[1][2].facesHaveTwoSides = false;

      model[1][3] = new Ring(texture);
      model[1][3] = model[1][3].transform(Matrix.scale(0.5, 0.5, 0.5));
      ModelShading.setRandomPrimitiveColors(model[1][3]);
      model[1][3].doBackFaceCulling = false;
      model[1][3].facesHaveTwoSides = false;

      // Row 2.
      model[2][0] = new ConeFrustum(texture, texture, texture, 0.25, 1.0, 0.75, 20, 20);
      ModelShading.setRandomPrimitiveColors(model[2][0]);

      model[2][1] = new SurfaceOfRevolution(texture,
                t -> 0.5 + 0.15 * Math.sin(10*t+1.0) * Math.sin(5*t+0.5),
                -0.1, 0.9,
                20, 20);
      //model[2][1] = model[2][1].transform(Matrix.scale(0.75, 0.75, 0.75));
      ModelShading.setRandomPrimitiveColors(model[2][1]);
      model[2][1].doBackFaceCulling = false;
      model[2][1].facesHaveTwoSides = false;

      model[2][2] = new Torus(texture, 0.5, 0.2, 30, 30);
      ModelShading.setRandomPrimitiveColors(model[2][2]);

      model[2][3] = new renderer.models_L.ParametricCurve(
                t -> 0.3*(Math.sin(t) + 2*Math.sin(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(Math.cos(t) - 2*Math.cos(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(-Math.sin(3*t)),
                0, 6*Math.PI, 120);
      ModelShading.setRandomVertexColors(model[2][3]);


      // Create x, y and z axes
      final Model xyzAxes = new renderer.models_L.Axes3D(3, -3, 2, 0, 2, -2, Color.red);

      // Create a horizontal coordinate plane model.
      final Model xzPlane = new renderer.models_F.PanelXZ(-3, 3, -2, 2);
      ModelShading.setColor(xzPlane, Color.darkGray);


      // Create a framebuffer to render our scene into.
      final int width  = 1200;
      final int height = 1000;
      final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

      Rasterize.doGamma = true;

      // Set up the camera's view frustum.
      final double fov    = 45.0;
      final double aspect = (double)width / (double) height;
      final Camera camera = Camera.projPerspective(fov, aspect);

      final long startTime, stopTime;
      startTime = System.currentTimeMillis();
      for (int k = 0; k < 360; ++k)
      {
         final Scene scene = new Scene("Geometries_threejs_R18_frame_"+k, camera);

         // Position the camera (move it around the periphery of the scene).
         scene.camera.viewLookAt(-8*Math.sin(k*Math.PI/180),
                                  4,
                                  8*Math.cos(k*Math.PI/180),
                                 0, 0, 0,
                                 0, 1, 0);

         // Add the xz-plane and xyz-axes models to the Scene.
         scene.addPosition( new Position(xzPlane) ); // draw the grid first
       //scene.addPosition( new Position(xyzAxes) ); // draw the axes on top of the grid

         // Place each model where it belongs in the xz-plane
         // and also rotate each model on its own axis.
         for (int i = 0; i < model.length; ++i)
         {
            for (int j = 0; j < model[i].length; ++j)
            {
               final Matrix mat = Matrix.translate(-3+2*j, 0, 2-2*i)
                           .times(Matrix.rotateX(5*k))
                           .times(Matrix.rotateY(2.5*k));
               scene.addPosition(new Position(model[i][j],
                                              "p["+i+"]["+j+"]",
                                              mat));
            }
         }

         // Draw one Scene graph image.
         if (0 == k) DrawSceneGraph.draw(scene, "Geometries_threejs_R18_SG");

//      scene.debug = true;
//      Clip.debug = true;
//      Rasterize.debug = true;

         // Render
         fb.clearFB();
         Pipeline.render(scene, fb);
         fb.dumpFB2File(String.format("PPM_Geometries_threejs_R18_Frame%03d.ppm", k));
       //fb.dumpFB2File(String.format("PNG_Geometries_threejs_R18_Frame%03d.png", k), "png");
      }
      stopTime = System.currentTimeMillis();
      System.out.println("Wall-clock time: " + (stopTime - startTime));
   }
}
