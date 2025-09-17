/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**

*/
public class InteractiveMaze_R18 extends InteractiveAbstractClient_R18
{
   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveMaze_R18()
   {
      scene = new Scene("InteractiveMaze_R18");

      Model model = new Maze("maze2.txt");
      model.doBackFaceCulling = false;

      scene.addPosition(new Position(model));

      aspectRatio = 2.0;
      //near   = 1.0;

      // Set up the camera's initial location and orientation.
      // Middle of the maze, looking at the Mona Lisa.
      scene.camera.view2Identity();
      eyeX = 80;
      eyeY =  5;
      eyeZ = 80;
    //scene.camera.viewTranslate(eyeX, eyeY, eyeZ);
      eyeRotY = 180;
    //scene.camera.viewRotateX(eyeRotX);


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1024;
      final int height =  512;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 18 - Interactive Maze");
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
         () -> new InteractiveMaze_R18()
      );
   }
}
