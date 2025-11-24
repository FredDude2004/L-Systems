/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.*;
import renderer.scene.primitives.*;
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**

*/
public class SimpleOrientationDemo_R14 extends InteractiveAbstractClient_R14
{
   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public SimpleOrientationDemo_R14()
   {
      scene = new Scene("SimpleOrientationDemo_R14");

      final Model model = new Model("orientation demo");
      doBackFaceCulling = true;
      frontFacingIsCCW = true;
      facesHaveTwoSides = true;
      model.doBackFaceCulling = doBackFaceCulling;
      model.frontFacingIsCCW = frontFacingIsCCW;
      model.facesHaveTwoSides = facesHaveTwoSides;

      // The layout of the following two line of code is meant to
      // suggest the relative location of the five vertices.
      final Vertex

             v0=new Vertex(-1, 1, 0),          v1=new Vertex(1, 1, 0),

   v2=new Vertex(-2, 0, 0),   v3=new Vertex(0, 0, 0),   v4=new Vertex(2, 0, 0);

      // Use the layout of the above code to convince yourself of the
      // orientation of the following three triangles with respect to
      // the camera's initial  point-of-view.

      model.addVertex(v0, v1, v2, v3, v4);
      model.addPrimitive(new Face(2, 3, 0),  // CCW (wrt the initial pov)
                         new Face(0, 1, 3),  // CW  (wrt the initial pov)
                         new Face(3, 4, 1)); // CCW (wrt the initial pov)

      // Give the Model random colors.
      ModelShading.setRandomPrimitiveColors(model);
      ModelShading.setBackFaceColor(model, backFaceColor);

      // Add the Model to the Scene.
      scene.addPosition(new Position(model, "top"));

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "SimpleOrientationDemo_R14_SG");


      // Create a FrameBufferPanel that will hold a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Simple Orientation Demo");
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);

      // Create event handler objects for events from the JFrame.
      jf.addKeyListener(this);
      jf.addComponentListener(this);

      print_help_message();
      System.out.println();
      System.out.println(model);
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
         () -> new SimpleOrientationDemo_R14()
      );
   }
}
