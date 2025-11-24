/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.Assets;
import renderer.scene.util.ModelShading;
import renderer.models_F.*;  // models defined using face primitives
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.io.File;

/**

*/
public class InteractiveModels_R14 extends InteractiveAbstractClient_R14
{
   private static final String assets = Assets.getPath();

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveModels_R14()
   {
      scene = new Scene("InteractiveModels_R14");

      // Create several Model objects.
      scene.addPosition(new Position(new ObjSimpleModel(
                             new File(assets + "apple.obj"))));
      scene.addPosition(new Position(new ObjSimpleModel(
                             new File(assets + "cow.obj"))));
      scene.addPosition(new Position(new ObjSimpleModel(
                             new File(assets + "galleon.obj"))));
      scene.addPosition(new Position(new ObjSimpleModel(
                             new File(assets + "teapot.obj"))));
      scene.addPosition(new Position(new ObjSimpleModel(
                             new File(assets + "stanford_bunny.obj"))));
      scene.addPosition(new Position(new ObjSimpleModel(
                             new File(assets + "cessna.obj"))));
      scene.addPosition(new Position(new Sphere(1.0, 30, 30)));
      scene.addPosition(new Position(new Cylinder(0.5, 1.0, 20, 20)));
      scene.addPosition(new Position(new Torus(0.75, 0.25, 25, 25)));
      scene.addPosition(new Position(new Cube2(15, 15, 15)));
      scene.addPosition(new Position(new ObjSimpleModel(new File(
                             assets + "small_rhombicosidodecahedron.obj"))));
      scene.addPosition(new Position(new PanelXY(-7, 7, -1, 3)));  // wall
      scene.addPosition(new Position(new PanelXZ(-7, 7, -3, 1)));  // floor
      scene.addPosition(new Position(new ObjSimpleModel(           // airplane
                             new File(assets + "cessna.obj"))));

      // Give each model a random color.
      for (final Position p : scene.positionList)
      {
         ModelShading.setRandomColor( p.getModel() );
      }
      // Give each model a back face color.
      for (final Position p : scene.positionList)
      {
         ModelShading.setBackFaceColor(p.getModel(), backFaceColor);
      }

      // Make the interactive models invisible, except for the current model.
      numberOfInteractiveModels = scene.positionList.size() - 3;
      for (int i = 0; i < numberOfInteractiveModels; ++i)
      {
         scene.getPosition(i).visible = false;
      }
      currentModel = 1; // cow
      scene.getPosition(currentModel).visible = true;
      interactiveModelsAllVisible = false;
      debugWholeScene = false;

      // Position the wall, floor and airplane.
      final int size = scene.positionList.size();
      scene.getPosition(size - 3).transform(Matrix.translate(0,  0, -3)); // wall
      scene.getPosition(size - 2).transform(Matrix.translate(0, -1,  0)); // floor
      scene.getPosition(size - 1).transform(Matrix.translate(3,  0,  0)); // airplane

      doBackFaceCulling = true;
      for (int i = 0; i < numberOfInteractiveModels; ++i)
      {
         setBackFaceCulling(scene.getPosition(i), doBackFaceCulling);
         setFrontFacingIsCCW(scene.getPosition(i), frontFacingIsCCW);
         setFacesHaveTwoSides(scene.getPosition(i), facesHaveTwoSides);
      }
      scene.getPosition(size - 3).getModel().doBackFaceCulling = false; // wall
      scene.getPosition(size - 2).getModel().doBackFaceCulling = false; // floor
      scene.getPosition(size - 1).getModel().doBackFaceCulling = true;  // airplane
      scene.getPosition(size - 1).getModel().frontFacingIsCCW = true;
      scene.getPosition(size - 1).getModel().facesHaveTwoSides = true;

      xTranslation = new double[numberOfInteractiveModels];
      yTranslation = new double[numberOfInteractiveModels];
      zTranslation = new double[numberOfInteractiveModels];
      xRotation = new double[numberOfInteractiveModels];
      yRotation = new double[numberOfInteractiveModels];
      zRotation = new double[numberOfInteractiveModels];
      scale = new double[numberOfInteractiveModels];
      java.util.Arrays.fill(scale, 1.0);


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Interactive Models");
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


   // Change how the program deals with transformations.
   @Override protected void setTransformations(final char c)
   {
      if ('=' == c)
      {
         scale[currentModel] = 1.0;
         xTranslation[currentModel] = 0.0;
         yTranslation[currentModel] = 0.0;
         zTranslation[currentModel] = 0.0;
         xRotation[currentModel] = 0.0;
         yRotation[currentModel] = 0.0;
         zRotation[currentModel] = 0.0;
      }
      else if ('s' == c) // Scale the model 10% smaller.
      {
         scale[currentModel] /= 1.1;
      }
      else if ('S' == c) // Scale the model 10% larger.
      {
         scale[currentModel] *= 1.1;
      }
      else if ('x' == c)
      {
         xTranslation[currentModel] -= 0.1;
      }
      else if ('X' == c)
      {
         xTranslation[currentModel] += 0.1;
      }
      else if ('y' == c)
      {
         yTranslation[currentModel] -= 0.1;
      }
      else if ('Y' == c)
      {
         yTranslation[currentModel] += 0.1;
      }
      else if ('z' == c)
      {
         zTranslation[currentModel] -= 0.1;
      }
      else if ('Z' == c)
      {
         zTranslation[currentModel] += 0.1;
      }
      else if ('u' == c)
      {
         xRotation[currentModel] -= 2.0;
      }
      else if ('U' == c)
      {
         xRotation[currentModel] += 2.0;
      }
      else if ('v' == c)
      {
         yRotation[currentModel] -= 2.0;
      }
      else if ('V' == c)
      {
         yRotation[currentModel] += 2.0;
      }
      else if ('w' == c)
      {
         zRotation[currentModel] -= 2.0;
      }
      else if ('W' == c)
      {
         zRotation[currentModel] += 2.0;
      }

      // Set the model-to-view transformation matrix.
      // The order of the transformations is very important!
      final Matrix matrix = Matrix.translate(xTranslation[currentModel],
                                             yTranslation[currentModel],
                                             zTranslation[currentModel])
                 .times( Matrix.rotateZ(zRotation[currentModel]) )
                 .times( Matrix.rotateY(yRotation[currentModel]) )
                 .times( Matrix.rotateX(xRotation[currentModel]) )
                 .times( Matrix.scale(scale[0])       );

      scene.setPosition(currentModel,
         scene.getPosition(currentModel).transform(matrix));
   }


   // Change how the program prints transformation information.
   @Override protected void displayMatrix(final KeyEvent e)
   {
      final char c = e.getKeyChar();

      if (showMatrix && ('m'==c||'='==c||'/'==c||'?'==c
        ||'s'==c||'x'==c||'y'==c||'z'==c||'u'==c||'v'==c||'w'==c
        ||'S'==c||'X'==c||'Y'==c||'Z'==c||'U'==c||'V'==c||'W'==c))
      {
         System.out.println("Current model is " + currentModel +".");
         System.out.println("xRot = " + xRotation[currentModel]
                        + ", yRot = " + yRotation[currentModel]
                        + ", zRot = " + zRotation[currentModel]);
         System.out.println( scene.getPosition(currentModel).getMatrix() );
      }
   }


   // Change how the program prints help information.
   @Override protected void print_help_message()
   {
      System.out.println("Use the 'd/D' keys to toggle debugging information on and off for the current model.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the '/' and '?' keys to cycle forwards and backwards through the models.");
      System.out.println("Use the '>/<' and shift keys to increase and decrease the mesh divisions in each direction.");
      System.out.println("Use the 'i/I' keys to get information about the current model.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to translate the current model along the x, y, z axes.");
      System.out.println("Use the u/U, v/V, w/W, keys to rotate the current model around the x, y, z axes.");
      System.out.println("Use the s/S keys to scale the size of the current model.");
      System.out.println("Use the 'm' key to toggle the display of the current model's matrix information.");
      System.out.println("Use the '=' key to reset the current model's matrix.");
      System.out.println("Use the 'c' key to change the random solid model color.");
      System.out.println("Use the 'C' key to randomly change model's colors.");
      System.out.println("Use the 'e' key to change the random solid edge colors.");
      System.out.println("Use the 'E' key to change the random edge colors.");
      System.out.println("Use the 'Alt-e' key combination to change the random vertex colors.");
      System.out.println("Use the 'a' key to toggle anti-aliasing on and off.");
      System.out.println("Use the 'b' key to toggle gamma correction on and off.");
      System.out.println("Use the 'B' key to toggle near plane clipping on and off.");
      System.out.println("Use the n/N keys to move the camera's near plane.");
      System.out.println("Use the f/F keys to change the camera's field-of-view (keep AR constant).");
      System.out.println("Use the r/R keys to change the camera's aspect ratio (keep fov constant).");
      System.out.println("Use the 'l' key to toggle letterboxing viewport on and off.");
      System.out.println("Use the arrow keys to translate the camera location left/right/up/down.");
      System.out.println("Use CTRL arrow keys to rotate the camera left/right/up/down.");
      System.out.println("Use the 'g' key to toggle back face culling.");
      System.out.println("Use the 'G' key to toggle CW vs CCW.");
      System.out.println("Use the 't' key to toggle two sided faces.");
      System.out.println("Use the 'M' key to toggle showing the Camera data.");
      System.out.println("Use the '*' key to show window data.");
      System.out.println("Use the 'P' key to convert the current model to a point cloud.");
      System.out.println("Use the '+' key to save a \"screenshot\" of the framebuffer.");
      System.out.println("Use the 'h' key to redisplay this help message.");
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
         () -> new InteractiveModels_R14()
      );
   }
}
