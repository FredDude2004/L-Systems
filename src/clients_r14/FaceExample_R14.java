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
   Taken from the book "Fast Algorithms for 3D-Graphics"
   by Georg Glaeser, pages 58 and 69.
   <pre>{@code
   Pyramid
      vertices
         2 0 0, 1 2 0, -1 2 0, -2 0 0, -1 -2 0, 1 -2 0,
         2 0 4, 1 2 1, -1 2 1, -2 0 4, -1 -2 1, 1 -2 1
       edges
         1 2, 2 3, 3 4, 4 5, 5 6, 6 1,
         7 8, 8 9, 9 10, 10 11, 11 12, 12 7,
         1 7, 2 8, 3 9, 4 10, 5 11, 6 12, 1 10
       faces
         1 2 3 4 5 6,
         1 2 8 7,   2 3 9 8,    3 4 10 9,  4 5 11 10,
         5 6 12 11, 6 1 7 12,  10 11 12 7
         10 7 8 9
   }</pre>
   <p>
   Notice that if you move a few of the vertices a bit, then
   not all of the faces remain planar. For example, if you
   move vertex 8 (see page 69), then the face (1 2 8 7) will
   no longer be planar. Similarly for vertices 9, 11, and 12.
   <p>
   This example can be simplified to a cube. If you move one
   corner of the cube "in" a bit, then the three faces it
   touches will no longer be planar.
*/
public class FaceExample_R14 extends InteractiveAbstractClient_R14
{
   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public FaceExample_R14()
   {
      scene = new Scene("FaceExample_R14");

      cameraDistance = 6.0;
      eyeZ = cameraDistance;
      scene.camera.viewTranslate(eyeX, eyeY, eyeZ);

      final Model model = new Model("Fast Algorithms for 3D-Graphics");
      model.doBackFaceCulling = doBackFaceCulling;
      model.frontFacingIsCCW = frontFacingIsCCW;
      model.facesHaveTwoSides = facesHaveTwoSides;
      scene.addPosition(new Position(model, "top"));

      model.addVertex(new Vertex(0,  0,  2),
                      new Vertex(0,  2,  1),
                      new Vertex(0,  2, -1),
                      new Vertex(0,  0, -2),
                      new Vertex(0, -2, -1),
                      new Vertex(0, -2,  1),
                      new Vertex(4,  0,  2),
                      new Vertex(1,  2,  1),
                      new Vertex(1,  2, -1),
                      new Vertex(4,  0, -2),
                      new Vertex(1, -2, -1),
                      new Vertex(1, -2,  1));
      model.addPrimitive(new Face(0,  5,  4,  3, 2, 1),
                         new Face(0,  1,  7,  6),
                         new Face(1,  2,  8,  7),
                         new Face(2,  3,  9,  8),
                         new Face(3,  4, 10,  9),
                         new Face(4,  5, 11, 10),
                         new Face(5,  0,  6, 11),
                         new Face(9, 10, 11,  6),
                         new Face(9,  6,  7,  8));

      ModelShading.setRandomPrimitiveColors(model);
      ModelShading.setBackFaceColor(model, backFaceColor);

      numberOfInteractiveModels = 1;
      currentModel = 0;
      scene.getPosition(currentModel).visible = true;
      interactiveModelsAllVisible = true;
      debugWholeScene = true;

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "FaceExample_R14_SG");


      // Create a FrameBufferPanel that will hold a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Face Example");
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
         () -> new FaceExample_R14()
      );
   }
}
