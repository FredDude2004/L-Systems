/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.ModelShading;
import renderer.scene.util.DrawSceneGraph;
import renderer.models_TP.*; // models defined using higher order triangle primitives
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;

/**

*/
public class TwoInteractiveModels_R17 extends InteractiveAbstractClient_R17
{
   protected final int[] visibility = {0, 0};

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public TwoInteractiveModels_R17()
   {
      scene = new Scene("TwoInteractiveModels_R17");

      // Add two Positions to the Scene.
      scene.addPosition(new Position( new Octahedron() ));
      scene.addPosition(new Position( new TriangularPyramid() ));

      // Give each model a random color.
      for (final Position p : scene.positionList)
      {
         ModelShading.setRandomPrimitiveColors(p.getModel());
       //ModelShading.setRainbowPrimitiveColors(p.getModel());
      }
      // Give all Face and Triangle objects three back face colors.
      for (final Position p : scene.positionList)
      {
         ModelShading.setBackFaceColor(p.getModel(),
                                       backFaceColor,  // light green
                                       backFaceColor.brighter().brighter(),
                                       backFaceColor.darker().darker());
      }

      // Make both interactive models visible.
      numberOfInteractiveModels = scene.positionList.size();
      currentModel = 0;
      scene.getPosition(0).visible = true;
      scene.getPosition(1).visible = true;
      interactiveModelsAllVisible = true;
      debugWholeScene = true;

      xTranslation = new double[numberOfInteractiveModels];
      yTranslation = new double[numberOfInteractiveModels];
      zTranslation = new double[numberOfInteractiveModels];
      xRotation = new double[numberOfInteractiveModels];
      yRotation = new double[numberOfInteractiveModels];
      zRotation = new double[numberOfInteractiveModels];
      scale = new double[numberOfInteractiveModels];
      java.util.Arrays.fill(scale, 1.0);

      DrawSceneGraph.draw(scene, "SG_TwoInteractiveModels_R17");


      // Create a FrameBufferPanel that will hold a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 17 - Two Interactive Models");
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);

      // Create event handler objects for events from the JFrame.
      jf.addKeyListener(this);
      jf.addComponentListener(this);

      print_help_message();
   }


   // Re-implement part of the KeyListener interface.
   @Override public void keyTyped(KeyEvent e)
   {
      final char c = e.getKeyChar();
      if (';' == c)
      {
         ++visibility[currentModel];
         if (3 == visibility[currentModel]) visibility[currentModel] = 0;
      }

      // Set the visibility of the current model.
      if (0 == visibility[currentModel])
      {
         scene.getPosition(currentModel).visible = true;
         scene.getPosition(currentModel).getModel().visible = true;
      }
      else if (1 == visibility[currentModel])
      {
         scene.getPosition(currentModel).visible = true;
         scene.getPosition(currentModel).getModel().visible = false;
      }
      else if (2 == visibility[currentModel])
      {
         scene.getPosition(currentModel).visible = false;
      }

      super.keyTyped(e);
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

      scene.getPosition(currentModel).transform(matrix);
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
      System.out.println("Use the '/' and '?' keys to cycle forwards and backwards through the two models.");
      System.out.println("Use the ';' key to cycle through the current model's visibility.");
      System.out.println("Use the '\\' key to cycle through the mesh patterns.");
      System.out.println("Use the '>/<' and shift keys to increase and decrease the mesh divisions in each direction.");
      System.out.println("Use the 'i/I' keys to get information about the current model.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to translate the current model along the x, y, z axes.");
      System.out.println("Use the u/U, v/V, w/W, keys to rotate the current model around the x, y, z axes.");
      System.out.println("Use the s/S keys to scale the size of the current model.");
      System.out.println("Use the 'm' key to toggle the display of the current model's matrix.");
      System.out.println("Use the '=' key to reset the current model's matrix.");
      System.out.println("Use the 'c' key to change the random solid model color.");
      System.out.println("Use the 'C' key to randomly change model's colors.");
      System.out.println("Use the 'e' key to change the random solid edge colors.");
      System.out.println("Use the 'E' key to change the random edge colors.");
      System.out.println("Use the 'Alt-e' key combination to change the random vertex colors.");
      System.out.println("Use the 'b' key to toggle gamma correction on and off.");
      System.out.println("Use the 'B' key to toggle near plane clipping on and off.");
      System.out.println("Use the n/N keys to move the camera's near plane.");
      System.out.println("Use the f/F keys to change the camera's field-of-view (keep AR constant.");
      System.out.println("Use the r/R keys to change the camera's aspect ratio (keep fov constant).");
      System.out.println("Use the 'l' key to toggle letterboxing viewport on and off.");
      System.out.println("Use the arrow keys to move the camera location left/right and up/down.");
      System.out.println("Use CTRL arrow keys to rotate the camera left/right and up/down.");
      System.out.println("Use the 'g' key to toggle back face culling.");
      System.out.println("Use the 'G' key to toggle CW vs CCW.");
      System.out.println("Use the 't' key to toggle two sided faces.");
      System.out.println("Use the 'M' key to toggle showing the Camera data.");
      System.out.println("Use the '*' key to show window data.");
      System.out.println("Use the 'P' key to convert the current model to a point cloud.");
      System.out.println("Use the 'K' key to convert the current model to a wireframe.");
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
         () -> new TwoInteractiveModels_R17()
      );
   }
}
