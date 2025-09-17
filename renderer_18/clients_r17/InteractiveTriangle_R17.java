/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.ModelShading;
import renderer.scene.util.DrawSceneGraph;
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
<pre>{@code
                  y       |
                  |       |
                  |       |
                  | v[1]  |
                1 +       |   /
                  |       |  /
                  |       | /
                  |       |/
                  |    -1 +--------------- image plane
                  |      /
                  |     /
                  |    /
                  |   /
                  |  /
                  | /
                  |/                v[0]
             v[2] +-----------------+------> x
                 /0                 1
                /
               /
              /
           1 +
            /
           z
}</pre>
   Render a wireframe triangle. This is just about the
   simplest possible model. It is useful for debugging.
*/
public class InteractiveTriangle_R17 extends InteractiveAbstractClient_R17
{
   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveTriangle_R17()
   {
      // Create the Scene object that we shall render.
      scene = new Scene("InteractiveTriangle_R17");

      // Set up the camera's location and orientation.
      cameraDistance = 1.0;
      eyeZ = cameraDistance;
      scene.camera.viewTranslate(eyeX, eyeY, eyeZ);

      // Create a Model object to hold the geometry.
      final Model model = new Model("triangle");
      doBackFaceCulling = false;
      frontFacingIsCCW = true;
      facesHaveTwoSides = true;
      model.doBackFaceCulling = doBackFaceCulling;
      model.frontFacingIsCCW = frontFacingIsCCW;
      model.facesHaveTwoSides = facesHaveTwoSides;
      scene.addPosition(new Position(model));

      // Create the geometry for the Model.
      // Vertices.
      model.addVertex(new Vertex(1.0, 0.0, 0.0),
                      new Vertex(0.0, 1.0, 0.0),
                      new Vertex(0.0, 0.0, 0.0));

      // Create a different color for each vertex.
      model.addColor(new Color(255,  0,   0 ),  // red
                     new Color( 0,  255,  0 ),  // green
                     new Color( 0,   0,  255)); // blue

      // Add geometry with color to the Model.
      model.addPrimitive(new Triangle(0, 1, 2));
      ModelShading.setBackFaceColor(model, backFaceColor,  // light green
                                           backFaceColor.brighter().brighter(),
                                           backFaceColor.darker().darker());
      showCamera = true;
      showMatrix = true;

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "InteractiveTriangle_R17_SG");


      // Create a FrameBufferPanel that will hold a FrameBuffer.
      final int width  = 512;
      final int height = 512;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 17 - Interactive Triangle");
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


   // Change how the program prints help information.
   @Override protected void print_help_message()
   {
      System.out.println("Use the 'd/D' keys to toggle debugging information on and off.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to translate the triangle along the x, y, z axes.");
      System.out.println("Use the u/U, v/V, w/W, keys to rotate the triangle around the x, y, z axes.");
      System.out.println("Use the s/S keys to scale the size of the triangle.");
      System.out.println("Use the 'm' key to toggle the display of triangle's matrix.");
      System.out.println("Use the '=' key to reset the triangle's matrix.");
      System.out.println("Use the 'c' key to change the random solid triangle color.");
      System.out.println("Use the 'C' key to randomly change triangle's colors.");
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
      System.out.println("Use the 'k' key to show window data.");
      System.out.println("Use the 'P' key to convert the triangle to a point cloud.");
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
         () -> new InteractiveTriangle_R17()
      );
   }
}
