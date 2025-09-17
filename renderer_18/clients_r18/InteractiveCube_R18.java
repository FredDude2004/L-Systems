/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.Assets;
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
   Render a solid cube.
*/
public class InteractiveCube_R18 extends InteractiveAbstractClient_R18
{
   private static final String assets = Assets.getPath();

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public InteractiveCube_R18()
   {
      scene = new Scene("InteractiveCube_R18");

      // Create a Model object to hold the geometry.
      final Model model = new Model("cube");
      doBackFaceCulling = false;
      frontFacingIsCCW = true;
      facesHaveTwoSides = true;
      model.doBackFaceCulling = doBackFaceCulling;
      model.frontFacingIsCCW = frontFacingIsCCW;
      model.facesHaveTwoSides = facesHaveTwoSides;
      scene.addPosition(new Position(model));

      // Create four textures for the Model.
      final Texture texture0 = new Texture(assets + "textures/Yoda.ppm");
      // Make all (nearly) white pixels transparent,
      // and all non-white pixles opaque.
      for (int y = 0; y < texture0.height; ++y)
      {
         for (int x = 0; x < texture0.width; ++x)
         {
            final int color = texture0.pixel_buffer[ (y*texture0.width) + x ];
            final Color c = new Color(color);
            if ( 252 > c.getRed() || 252 > c.getGreen() || 252 > c.getBlue() )
            {
               texture0.alpha_buffer[ (y*texture0.width) + x ] = 255;  // opaque
            }
            else // non-white pixel
            {
               texture0.alpha_buffer[ (y*texture0.width) + x ] = 0;  // transparent
            }
         }
      }

      final Texture texture1 = new Texture(assets + "textures/wood.ppm");
      final Texture texture2 = new Texture(assets + "textures/brick3.ppm");

      // Create a "procedural" texture, pixel by pixel.
      final int SIZE = 256;
      final int[] pascalImage = new int[SIZE * SIZE * 4];
      for (int y = 0; y < SIZE; ++y)
      {
         for (int x = 0; x < SIZE; ++x)
         {
            final int c = ((x|y)%255); // Pascal's Triangle like image.
            final int index = (y*SIZE + x)*4;
            pascalImage[index + 0] = c;   // r
            pascalImage[index + 1] = c;   // g
            pascalImage[index + 2] = c;   // b
            pascalImage[index + 3] = 170; // alpha (about 70%)
         }
      }
      final Texture texture3 = new Texture("pascal", SIZE, SIZE, pascalImage);

      // Add the textures to the model.
      model.addTexture(texture0, texture1, texture2, texture3);

      // Add texture coordinates to the model.
      model.addTextureCoord(new TexCoord(0.0, 0.0),
                            new TexCoord(0.0, 1.0),
                            new TexCoord(1.0, 1.0),
                            new TexCoord(1.0, 0.0));

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

      // Create three colors, one color for the top face,
      // one color for the bottom face, and
      // one color for the vertical faces.
      model.addColor(new Color(255,  0,   0 ),  // red, bottom
                     new Color( 0,  255,  0 ),  // green, top
                     new Color( 0,   0,  255)); // blue, vertical

      // Add back face colors.
      model.addColor(backFaceColor,  // light green
                     backFaceColor.brighter().brighter(),
                     backFaceColor.darker().darker());

      // Add geometry, with color and textures, to the Model.
      model.addPrimitive(
               // bottom
               new Triangle(0, 1, 2,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            2),       // brick texture
               new Triangle(0, 2, 3,  // vertices
                            0, 0, 0,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            2),       // brick texture
               // top
               new Triangle(7, 6, 5,  // vertices
                            1, 1, 1,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            1),       // wood texture
               new Triangle(7, 5, 4,  // vertices
                            1, 1, 1,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            1),       // wood texture
               // back
               new Triangle(0, 4, 5,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            3),       // pascal texture
               new Triangle(0, 5, 1,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            3),       // pascal texture
               // front
               new Triangle(3, 2, 6,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 1, 2,  // texture coordinates
                            0),       // yoda texture
               new Triangle(3, 6, 7,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5,  // back face colors
                            0, 2, 3,  // texture coordinates
                            0),       // yoda texture
               // left
               new Triangle(0, 3, 7,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5), // back face colors
               new Triangle(0, 7, 4,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5), // back face colors
               // right
               new Triangle(1, 5, 6,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5), // back face colors
               new Triangle(1, 6, 2,  // vertices
                            2, 2, 2,  // front face colors
                            3, 4, 5));// back face colors

      showCamera = true;
      showMatrix = true;

      // Draw a picture of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawVertexList = true;
      DrawSceneGraph.draw(scene, "InteractiveCube_R18_SG");


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 18 - Interactive Cube");
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
      System.out.println("Use the 'i/I' keys to get information about the cube model.");
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
         () -> new InteractiveCube_R18()
      );
   }
}
