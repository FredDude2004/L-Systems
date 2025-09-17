/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.Assets;
import renderer.scene.util.ModelShading;
import renderer.models_Tex.*; // textured models
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.io.File;

/**

*/
public class InteractiveModelsAll_R18 extends InteractiveAbstractClient_R18
{
   private static final String assets = Assets.getPath();

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveModelsAll_R18()
   {
      // Load all the textures.
      final long startTime, stopTime;
      startTime = System.currentTimeMillis();
      final Texture texture1 = new Texture(assets + "textures/uv_grid_opengl.ppm");
      final Texture texture2 = new Texture(assets + "textures/Uvrefmap_util-mark1.ppm");
      final Texture texture3 = new Texture(assets + "textures/earth.ppm");
      final Texture texture4 = new Texture(assets + "textures/Dumbledore.ppm");
      final Texture texture5 = new Texture(assets + "textures/wall.ppm");
      final Texture texture = texture1;
      stopTime = System.currentTimeMillis();
      System.out.println("Wall-clock time: " + (stopTime - startTime));

      scene = new Scene("InteractiveModelsAll_R18");

      // Instantiate at least one instance of every textured Model class.
      // 2D models
      scene.addPosition(new Position(
                           new Square(texture, 1.0)));
      scene.addPosition(new Position(
                           new Disk(texture, 1.0, 4, 16)));
      scene.addPosition(new Position(
                           new DiskSector(texture, 1.0, Math.PI/2, 3*Math.PI/2, 4, 8)));
      scene.addPosition(new Position(
                           new Ring(texture, 1.0, 0.25, 3, 16)));
      scene.addPosition(new Position(
                           new RingSector(texture, 1.0, 0.25, Math.PI/2, 3*Math.PI/2, 3, 8)));
      // cubes
      scene.addPosition(new Position(new Cube(texture)));
      scene.addPosition(new Position(new Cube(texture1,
                                              texture2,
                                              texture3,
                                              texture4,
                                              texture5,
                                              texture1, "cube")));
      // polyhedra
      scene.addPosition(new Position(new Tetrahedron(texture, texture5)));
      scene.addPosition(new Position(new Tetrahedron(texture, texture5, true)));
      scene.addPosition(new Position(new Octahedron(texture, texture5)));
      scene.addPosition(new Position(new Dodecahedron(texture)));
      scene.addPosition(new Position(new Icosahedron(texture)));
      scene.addPosition(new Position(new Icosidodecahedron(texture)));
      // pyramids
      scene.addPosition(new Position(new Pyramid(texture, texture5, 2.0, 1.0)));
      // cones
      scene.addPosition(new Position(new Cone(texture, texture2,
                1.0, 1.0, 10, 10)));
      scene.addPosition(new Position(new ConeSector(texture, texture2,
                1.0, 1.0, 0, Math.PI, 10, 11)));
      scene.addPosition(new Position(new ConeSector(texture, texture2,
                1.0, 1.0, Math.PI/2, 3*Math.PI/2, 5, 8)));
      scene.addPosition(new Position(new ConeSector(texture, texture2,
                1.0, 1.0, 0, 3*Math.PI/2, 5, 8)));

      scene.addPosition(new Position(new ConeFrustum(texture, texture5, texture2,
                0.375, 1.5, 0.375, 11, 10)));

      scene.addPosition(new Position(new ConeFrustum(texture, texture5, texture2,
                1.0, 0.5, 0.5, 6, 16)));
      scene.addPosition(new Position(new ConeFrustum(texture, texture5, texture2,
                0.5, 0.5, 1.0, 6, 16)));
      // cylinders
      scene.addPosition(new Position(new Cylinder(texture, texture5, texture2,
                0.5, 1.0, 11, 10)));
      scene.addPosition(new Position(new CylinderSector(texture, texture5, texture2,
                0.5, 1.0, Math.PI/2, 3*Math.PI/2, 11, 11)));
      // spheres
      scene.addPosition(new Position(new Sphere(texture, 1.0, 9, 10)));
      scene.addPosition(new Position(new Sphere(texture, 1.0, 15, 12)));
      scene.addPosition(new Position(new SphereSector(texture,
                1.0, Math.PI/2, 3*Math.PI/2, Math.PI/4, 3*Math.PI/4, 11, 11)));
      // torus
      scene.addPosition(new Position(new Torus(texture,
                0.75, 0.25, 12, 16)));
      scene.addPosition(new Position(new TorusSector(texture,
                0.75, 0.25, Math.PI/2, 3*Math.PI/2, 12, 8)));
      scene.addPosition(new Position(new TorusSector(texture,
                0.75, 0.25, 0, 2*Math.PI, Math.PI, 2*Math.PI, 6, 16)));
      scene.addPosition(new Position(new TorusSector(texture,
                0.75, 0.25, 0, 2*Math.PI, -Math.PI/2, Math.PI/2, 6, 16)));
      scene.addPosition(new Position(new TorusSector(texture,
                0.75, 0.25, Math.PI/2, 3*Math.PI/2, -Math.PI/2, Math.PI/2, 6, 8)));
      // model files
      scene.addPosition(new Position(new ObjSimpleModel(texture,
                new File(assets + "cow.obj"))));
      // parametric surfaces
      scene.addPosition(new Position(new ParametricSurface(texture)));
      scene.addPosition(new Position(new ParametricSurface(texture,
                (s,t) -> s*Math.cos(t*Math.PI),
                (s,t) -> t,
                (s,t) -> s*Math.sin(t*Math.PI),
                -1, 1, -1, 1, 49, 49)));
      scene.addPosition(new Position(new ParametricSurface(texture,
                (u,v) -> 0.3*(1-u)*(3+Math.cos(v))*Math.sin(4*Math.PI*u),
                (u,v) -> 0.3*(3*u+(1-u)*Math.sin(v)),
                (u,v) -> 0.3*(1-u)*(3+Math.cos(v))*Math.cos(4*Math.PI*u),
                0, 1, 0, 2*Math.PI, 49, 49)));
      scene.addPosition(new Position(new SurfaceOfRevolution(texture)));
      scene.addPosition(new Position(new SurfaceOfRevolution(texture,
                t -> 0.5*(1+t*t), -1, 1, 30, 30)));
      scene.addPosition(new Position(new SurfaceOfRevolution(texture,
                t -> t, t->4*t*(1-t), 0, 1, 30, 30)));

      // Give each model a random color (which will
      // not be used because of the textures).
      for (final Position p : scene.positionList)
      {
         ModelShading.setRandomPrimitiveColors( p.getModel() );
      }
      // Give all Triangle objects three back face colors.
      for (final Position p : scene.positionList)
      {
         ModelShading.setBackFaceColor(p.getModel(),
                                       backFaceColor,
                                       backFaceColor.brighter(),
                                       backFaceColor.darker());
      }

      // Make the interactive models invisible, except for the current model.
      numberOfInteractiveModels = scene.positionList.size();
      for (final Position p : scene.positionList)
      {
         p.visible = false;
      }
      currentModel = 0;
      scene.getPosition(currentModel).visible = true;
      interactiveModelsAllVisible = false;
      debugWholeScene = true;

      doBackFaceCulling = false;
      frontFacingIsCCW = true;
      facesHaveTwoSides = true;
      for (int i = 0; i < numberOfInteractiveModels; ++i)
      {
         setBackFaceCulling(scene.getPosition(i), doBackFaceCulling);
         setFrontFacingIsCCW(scene.getPosition(i), frontFacingIsCCW);
         setFacesHaveTwoSides(scene.getPosition(i), facesHaveTwoSides);
      }


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 18 - All Textured Models");
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);

      // Register this object as the event listener for JFrame events.
      jf.addKeyListener(this);
      jf.addComponentListener(this);

      print_help_message();
   }


   /**
      Create an instance of this class which has
      the affect of creating the GUI application.
   */
   public static void main(String[] args)
   {
      // We need to call the program's constructor in the
      // Java GUI Event Dispatch Thread, otherwise we get a
      // race condition between the constructor (running in
      // the main() thread) and the very first ComponentEvent
      // (running in the EDT).
      javax.swing.SwingUtilities.invokeLater(
         () -> new InteractiveModelsAll_R18()
      );
   }
}
