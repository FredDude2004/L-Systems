/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.ModelShading;
import renderer.models_F.SierpinskiTriangle;
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**


*/
public class InteractiveSierpinskiTriangle_R14 extends InteractiveAbstractClient_R14
{
   private boolean xSubRotation1 = false;
   private boolean xSubRotation2 = false;
   private boolean ySubRotation1 = false;
   private boolean ySubRotation2 = false;
   private boolean zSubRotation1 = false;
   private boolean zSubRotation2 = false;

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveSierpinskiTriangle_R14()
   {
      scene = new Scene("InteractiveSierpinskiTriangle_R14");

      // Set up the camera's location and orientation.
      eyeZ = 1.0;
      scene.camera.viewTranslate(eyeX, eyeY, eyeZ);

      // Create the Model object.
      final Model model = new SierpinskiTriangle(8);
      scene.addPosition(new Position( model ));
      ModelShading.setRandomColor(model);
      // Give all Face objects a back face color.
      ModelShading.setBackFaceColor(model, backFaceColor);

      doBackFaceCulling = false;
      facesHaveTwoSides = true;
      model.setBackFaceCulling(doBackFaceCulling);
      model.setFrontFacingIsCCW(frontFacingIsCCW);
      model.setFacesHaveTwoSides(facesHaveTwoSides);

      numberOfInteractiveModels = 1;
      currentModel = 0;
      scene.getPosition(currentModel).visible = true;
      interactiveModelsAllVisible = true;
      debugWholeScene = true;


      // Create a FrameBufferPanel that will hold a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Interactive Sierpinski Triangle");
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
         scale[0] = 1.0;
         xTranslation[0] = 0.0;
         yTranslation[0] = 0.0;
         zTranslation[0] = 0.0;
         xRotation[0] = 0.0;
         yRotation[0] = 0.0;
         zRotation[0] = 0.0;
      }
      else if ('s' == c) // Scale the model 10% smaller.
      {
         scale[0] /= 1.1;
      }
      else if ('S' == c) // Scale the model 10% larger.
      {
         scale[0] *= 1.1;
      }
      else if ('x' == c)
      {
         xTranslation[0] -= 0.1;
      }
      else if ('X' == c)
      {
         xTranslation[0] += 0.1;
      }
      else if ('y' == c)
      {
         yTranslation[0] -= 0.1;
      }
      else if ('Y' == c)
      {
         yTranslation[0] += 0.1;
      }
      else if ('z' == c)
      {
         zTranslation[0] -= 0.1;
      }
      else if ('Z' == c)
      {
         zTranslation[0] += 0.1;
      }
      else if ('u' == c)
      {
         xRotation[0] -= 2.0;
      }
      else if ('U' == c)
      {
         xRotation[0] += 2.0;
      }
      else if ('v' == c)
      {
         yRotation[0] -= 2.0;
      }
      else if ('V' == c)
      {
         yRotation[0] += 2.0;
      }
      else if ('w' == c)
      {
         zRotation[0] -= 2.0;
      }
      else if ('W' == c)
      {
         zRotation[0] += 2.0;
      }
      else if ('3' == c)
      {
         xSubRotation1 = true;
      }
      else if ('#' == c)
      {
         xSubRotation2 = true;
      }
      else if ('4' == c)
      {
         ySubRotation1 = true;
      }
      else if ('$' == c)
      {
         ySubRotation2 = true;
      }
      else if ('5' == c)
      {
         zSubRotation1 = true;
      }
      else if ('%' == c)
      {
         zSubRotation2 = true;
      }
      else if ('6' == c)
      {
         ModelShading.setRandomNestedModelColors(scene.getPosition(0).getModel());
      }

      // Update the nested matrices within the hierarchical model.
      Matrix mat = Matrix.identity();
      if (xSubRotation1)
         mat = Matrix.rotateX(2.0);
      else if (xSubRotation2)
         mat = Matrix.rotateX(-2.0);
      else if (ySubRotation1)
         mat = Matrix.rotateY(2.0);
      else if (ySubRotation2)
         mat = Matrix.rotateY(-2.0);
      else if (zSubRotation1)
         mat = Matrix.rotateZ(2.0);
      else if (zSubRotation2)
         mat = Matrix.rotateZ(-2.0);
      xSubRotation1 = false;
      xSubRotation2 = false;
      ySubRotation1 = false;
      ySubRotation2 = false;
      zSubRotation1 = false;
      zSubRotation2 = false;
      updateNestedMatrices(scene.getPosition(0).getModel(), mat);

      // Set the model-to-view transformation matrix.
      // The order of the transformations is very important!
      scene.getPosition(currentModel).transform(
                Matrix.translate(xTranslation[0],
                                 yTranslation[0],
                                 zTranslation[0])
        .times( Matrix.rotateX(xRotation[0]) )
        .times( Matrix.rotateY(yRotation[0]) )
        .times( Matrix.rotateZ(zRotation[0]) )
        .times( Matrix.scale(scale[0]) ));
   }


   // Apply the given Matrix to the nested models within the given Model.
   private static void updateNestedMatrices(final Model model, final Matrix matrix)
   {
      for (int i = 0; i < model.nestedModels.size(); ++i)
      {
         final Matrix mat = model.getNestedModel(i).getMatrix().times(matrix);
         model.getNestedModel(i).transform(mat);
      }

      for (Model m : model.nestedModels)
      {
         updateNestedMatrices(m, matrix); // recursion
      }
   }


   // Change how the program prints help information.
   @Override protected void print_help_message()
   {
      System.out.println("Use the 'd/D' keys to toggle debugging information on and off for the current model.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the '>/<' keys to increase and decrease the depth of the sub-triangles.");
      System.out.println("Use the 'i/I' keys to get information about the current model.");
      System.out.println("Use the 3/#, 4/$, 5/%, keys to rotate the sub-triangles around the x, y, z axes.");
      System.out.println("Use the '6' key to change the random solid sub-triangle colors.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to translate the sponge along the x, y, z axes.");
      System.out.println("Use the u/U, v/V, w/W, keys to rotate the sponge around the x, y, z axes.");
      System.out.println("Use the s/S keys to scale the size of the sponge.");
      System.out.println("Use the 'm' key to toggle the display of matrix information.");
      System.out.println("Use the '=' key to reset the model matrix.");
      System.out.println("Use the 'c' key to change the random solid sponge color.");
      System.out.println("Use the 'C' key to randomly change sponge's colors.");
      System.out.println("Use the 'e' key to change the random solid edge colors.");
      System.out.println("Use the 'E' key to change the random edge colors.");
      System.out.println("Use the 'Alt-e' key combination to change the random vertex colors.");
      System.out.println("Use the 'a' key to toggle anti-aliasing on and off.");
      System.out.println("Use the 'b' key to toggle gamma correction on and off.");
      System.out.println("Use the 'B' key to toggle near plane clipping on and off.");
      System.out.println("Use the n/N keys to move the camera's near plane.");
      System.out.println("Use the f/F keys to change the camera's field-of-view (keep AR constant.");
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
         () -> new InteractiveSierpinskiTriangle_R14()
      );
   }
}
