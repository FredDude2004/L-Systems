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
   Use this program to observe the culling of Face primitives.
*/
public class TestCulling_R14 extends InteractiveAbstractClient_R14
{
   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public TestCulling_R14()
   {
      scene = new Scene("TestCulling_R14");

      // Create a Model object to hold the geometry.
      final Model model = new Model("Test Culling");

      // Create a Position for the Model.
      final Position position = new Position(model, "top");

      // Add the Position (and its Model) to the Scene.
      scene.addPosition(position);

      // Create the geometry for the Model.
      model.addVertex(new Vertex(0, 0, 0));
      int index = 1;
      final int center = 0;
      final int count = 2; // number of triangles along each edge
      final double step = 2.0 / count;
      double y = -1.0;
      double z = -1.0;
      for (int i = 0; i < count; ++i)
      {
         // front and back edges
         model.addVertex(new Vertex(0, y,       1),   // index + 0
                         new Vertex(0, y+step,  1),   // index + 1
                         new Vertex(0, y,      -1),   // index + 2
                         new Vertex(0, y+step, -1));  // index + 3
         model.addPrimitive(new Face(index + 0, center,    index + 1),
                            new Face(index + 2, index + 3, center));

         // top and bottom edges
         model.addVertex(new Vertex(0,  1, z),        // index + 4
                         new Vertex(0,  1, z+step),   // index + 5
                         new Vertex(0, -1, z),        // index + 6
                         new Vertex(0, -1, z+step));  // index + 7
         model.addPrimitive(new Face(index + 4, index + 5, center),
                            new Face(index + 6, center,    index + 7));

         index += 8;
         y += step;
         z += step;
      }
      ModelShading.setRandomVertexColors(model);
      ModelShading.setBackFaceColor(model, backFaceColor); // light green

      numberOfInteractiveModels = 1;
      currentModel = 0;
      scene.getPosition(currentModel).visible = true;
      interactiveModelsAllVisible = true;
      debugWholeScene = true;

      doBackFaceCulling = true;
      frontFacingIsCCW = true;
      facesHaveTwoSides = true;
      model.doBackFaceCulling = doBackFaceCulling;
      model.frontFacingIsCCW = frontFacingIsCCW;
      model.facesHaveTwoSides = facesHaveTwoSides;
      scene.debug = true;

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "TestCulling_R14_SG");


      // Create a FrameBufferPanel that will hold a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Test Culling");
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
         () -> new TestCulling_R14()
      );
   }
}