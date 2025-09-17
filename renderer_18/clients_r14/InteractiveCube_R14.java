/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.DrawSceneGraph;
import renderer.framebuffer.FrameBufferPanel;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
<pre>{@code
                  y
                  |
                  |
                  | v[4]
                1 +---------------------+ v[5]=(1,1,0)
                 /|                    /|
                / |                   / |
               /  |                  /  |
              /   |                 /   |
             /    |                /    |
       v[7] +---------------------+ v[6]|
            |     |               |     |
            |     |               |     |
            |     | v[0]          |     | v[1]
            |     +---------------|-----+------> x
            |    /                |    /1
            |   /                 |   /
            |  /                  |  /
            | /                   | /
            |/                    |/
          1 +---------------------+
           /v[3]=(0,0,1)          v[2]=(1,0,1)
          /
         /
        z
}</pre>
   Render a wireframe cube.
<p>
   Each corner of the cube is represented by a single Vertex
   object. The three LineSegment objects that meet at each
   corner share the corner's single Vertex object. But we
   may not want the three line segments to have the same color
   at that corner. This cube model initially has the top
   four edges sharing one color object, the bottom four edges
   sharing another color object, and the four vertical edges
   sharing a third color object.
*/
public class InteractiveCube_R14 extends InteractiveAbstractClient_R14
{
   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveCube_R14()
   {
      scene = new Scene("InteractiveCube_R14");

      // Create a Model object to hold the geometry.
      final Model model = new Model("cube");
      doBackFaceCulling = false;
      frontFacingIsCCW = true;
      facesHaveTwoSides = true;
      model.doBackFaceCulling = doBackFaceCulling;
      model.frontFacingIsCCW = frontFacingIsCCW;
      model.facesHaveTwoSides = facesHaveTwoSides;
      scene.addPosition(new Position(model));

      // Create the geometry for the Model.
      // Vertices.
      model.addVertex(new Vertex(0.0, 0.0, 0.0), // four vertices around the bottom face
                      new Vertex(1.0, 0.0, 0.0),
                      new Vertex(1.0, 0.0, 1.0),
                      new Vertex(0.0, 0.0, 1.0),
                      new Vertex(0.0, 1.0, 0.0), // four vertices around the top face
                      new Vertex(1.0, 1.0, 0.0),
                      new Vertex(1.0, 1.0, 1.0),
                      new Vertex(0.0, 1.0, 1.0));

      // Create three colors, one color for the top edges,
      // one color for the bottom edges, and a back face color.
      model.addColor(new Color(255,  0,   0 ),  // red, bottom
                     new Color( 0,  255,  0 ),  // green, top
                     backFaceColor);            // light green

      // Add geometry with color to the Model.
      final Face bottom = new Face();
      final Face top    = new Face();
      final Face back   = new Face();
      final Face front  = new Face();
      final Face left   = new Face();
      final Face right  = new Face();
      model.addPrimitive(bottom, top, back, front, left, right);

      bottom.addIndices(0, 0, 2); // vertex, color, backface color
      bottom.addIndices(1, 0, 2);
      bottom.addIndices(2, 0, 2);
      bottom.addIndices(3, 0, 2);

      top.addIndices(7, 1, 2);
      top.addIndices(6, 1, 2);
      top.addIndices(5, 1, 2);
      top.addIndices(4, 1, 2);

      back.addIndices(0, 0, 2);
      back.addIndices(4, 1, 2);
      back.addIndices(5, 1, 2);
      back.addIndices(1, 0, 2);

      front.addIndices(3, 0, 2);
      front.addIndices(2, 0, 2);
      front.addIndices(6, 1, 2);
      front.addIndices(7, 1, 2);

      left.addIndices(0, 0, 2);
      left.addIndices(3, 0, 2);
      left.addIndices(7, 1, 2);
      left.addIndices(4, 1, 2);

      right.addIndices(1, 0, 2);
      right.addIndices(5, 1, 2);
      right.addIndices(6, 1, 2);
      right.addIndices(2, 0, 2);
/*
      model.addPrimitive(new Face(0, 1, 2, 3),  // bottom face
                         new Face(7, 6, 5, 4),  // top face
                         new Face(0, 4, 5, 1),  // back face
                         new Face(3, 2, 6, 7),  // front face
                         new Face(0, 3, 7, 4),  // left face
                         new Face(1, 5, 6, 2)); // right face
      model.setBackFaceColor(backFaceColor);    // light green
*/
      showCamera = true;
      showMatrix = true;

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "InteractiveCube_R14_SG");


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Interactive Cube");
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
      System.out.println("Use the 'i' key to get information about the cube model.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to translate the cube along the x, y, z axes.");
      System.out.println("Use the u/U, v/V, w/W, keys to rotate the cube around the x, y, z axes.");
      System.out.println("Use the s/S keys to scale the size of the cube.");
      System.out.println("Use the 'm' key to toggle the display of the cube's matrix.");
      System.out.println("Use the '=' key to reset the cube's matrix.");
      System.out.println("Use the 'c' key to change the random solid cube color.");
      System.out.println("Use the 'C' key to randomly change cube's colors.");
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
      System.out.println("Use the 'P' key to convert the cube to a point cloud.");
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
         () -> new InteractiveCube_R14()
      );
   }
}
