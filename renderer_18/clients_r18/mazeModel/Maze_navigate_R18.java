/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;

/**

*/
public class Maze_navigate_R18 implements
                               KeyListener,
                               ComponentListener
{
   protected boolean doBackFaceCulling = false;
   protected boolean frontFacingIsCCW = true;
   protected boolean facesHaveTwoSides = false;
   protected final Color backFaceColor = new Color(150, 200, 150); // light green

   private double cameraX =  0.0;
   private double cameraY =  3.0;
   private double cameraZ = 10.0;
   private double cameraRotX = 0.0;
   private double cameraRotY = 0.0;
   private double cameraRotZ = 0.0;

   private boolean letterbox = false;
   private double aspectRatio = 2.0;
   private double near = 1.0;
   private boolean perspective = true;
   private double fovy = 90.0;
   private boolean showCamera = false;
   private boolean showWindow = false;

   private Camera camera;

   private boolean useRenderer1 = true;

   private boolean takeScreenshot = false;
   private int screenshotNumber = 0;

   private Scene scene;
   private final Model model;

   private final JFrame jf;
   private final FrameBufferPanel fbp;

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public Maze_navigate_R18()
   {
      // Set up the camera's view volume.
      camera = Camera.projPerspective(fovy, aspectRatio);
      camera = camera.changeNear(near);

      // Set up the camera's initial location and orientation.
      // Middle of the maze, looking at the Mona Lisa.
      cameraX = 80;
      cameraY =  5;
      cameraZ = 80;
      camera.viewTranslate(cameraX, cameraY, cameraZ);
      cameraRotY = 180;
      camera.viewRotateX(cameraRotX);
      camera.viewRotateY(cameraRotY);
      camera.viewRotateZ(cameraRotZ);

      scene = new Scene("Maze_navigate_R18", camera);

      model = new Maze("maze2.txt");

      scene.addPosition(new Position(model));

      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1800;
      final int height =  900;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 18 - Maze navigate");
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


   // Implement the KeyListener interface.
   @Override public void keyPressed(KeyEvent e)
   {
      //System.out.println( e );

      final int keyCode = e.getKeyCode();
      // Only handle the four arrow keys.
      if (KeyEvent.VK_UP == keyCode   || KeyEvent.VK_DOWN == keyCode
       || KeyEvent.VK_LEFT == keyCode || KeyEvent.VK_RIGHT == keyCode)
      {
         if ( 0 != (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) )
         {
            if (KeyEvent.VK_UP == keyCode)
            {
               cameraRotX += 1;
            }
            else if (KeyEvent.VK_DOWN == keyCode)
            {
               cameraRotX -= 1;
            }
            else if (KeyEvent.VK_LEFT == keyCode)
            {
               cameraRotY += 1;
            }
            else if (KeyEvent.VK_RIGHT == keyCode)
            {
               cameraRotY -= 1;
            }
         }
         else // no control key
         {
            if (KeyEvent.VK_UP == keyCode )
            {
               cameraY += 0.1;
            }
            else if (KeyEvent.VK_DOWN == keyCode )
            {
               cameraY -= 0.1;
            }
            else if (KeyEvent.VK_LEFT == keyCode )
            {
               cameraX -= 0.1;
            }
            else if (KeyEvent.VK_RIGHT == keyCode )
            {
               cameraX += 0.1;
            }
         }

         // Render again.
         setupViewing();

         displayCamera(Optional.empty(),
                       Optional.of(keyCode));
      }
   }

   @Override public void keyReleased(KeyEvent e){}

   @Override public void keyTyped(KeyEvent e)
   {
      //System.out.println( e );

      final char c = e.getKeyChar();
      if ('h' == c)
      {
         print_help_message();
         return;
      }
      else if ('d' == c && e.isAltDown())
      {
         System.out.println();
         System.out.println(scene.getPosition(0).getModel());
      }
      else if ('d' == c)
      {
         scene.debug = ! scene.debug;
         Clip.debug = scene.debug;
      }
      else if ('D' == c)
      {
         Rasterize.debug = ! Rasterize.debug;
      }
      else if ('i' == c)
      {
         final String name = scene.getPosition(0).getModel().name;
         final int[] modelInfo = modelInfo(scene.getPosition(0).getModel());
         System.out.printf("The current Model ("+name+") has:\n");
         System.out.printf("  %,d vertices,\n", modelInfo[0]);
         System.out.printf("  %,d Point primitives,\n", modelInfo[1]);
         System.out.printf("  %,d Line segments, grouped into\n", modelInfo[2]);
         System.out.printf("    %,d LineSgement primitives,\n", modelInfo[3]);
         System.out.printf("    %,d Lines primitives,\n", modelInfo[4]);
         System.out.printf("    %,d LineStrip primitives,\n", modelInfo[5]);
         System.out.printf("    %,d LineLoop primitives,\n", modelInfo[6]);
         System.out.printf("    %,d LineFan primitives.\n", modelInfo[7]);
         System.out.printf("    %,d Face primitives,\n", modelInfo[8]);
         System.out.printf("  %,d Triangles, grouped into\n", modelInfo[9]);
         System.out.printf("    %,d Triangle primitives,\n", modelInfo[10]);
         System.out.printf("    %,d Triangles primitives,\n", modelInfo[11]);
         System.out.printf("    %,d TriangleStrip primitives,\n", modelInfo[12]);
         System.out.printf("    %,d TriangleFan primitives.\n", modelInfo[13]);
      }
      else if ('I' == c)
      {
         System.out.println();
         System.out.println(scene.getPosition(0).getModel());
      }
      else if ('1' == c)
      {
         if (! useRenderer1)
         {
            useRenderer1 = true;
         }
         System.out.println("Using Pipeline 1.");
      }
      else if ('2' == c)
      {
         if (useRenderer1)
         {
            useRenderer1 = false;
         }
         System.out.println("Using Pipeline 2.");
      }
      else if ('3' == c)
      {
         Rasterize.doAntiAliasing = ! Rasterize.doAntiAliasing;
         System.out.print("Anti-aliasing is turned ");
         System.out.println(Rasterize.doAntiAliasing ? "On" : "Off");
      }
      else if ('4' == c)
      {
         Rasterize.doGamma = ! Rasterize.doGamma;
         System.out.print("Gamma correction is turned ");
         System.out.println(Rasterize.doGamma ? "On" : "Off");
      }
      else if ('g' == c)
      {
         doBackFaceCulling = ! doBackFaceCulling;
         System.out.print("Back face culling is ");
         System.out.println(doBackFaceCulling ? "on" : "off");
      }
      else if ('G' == c)
      {
         frontFacingIsCCW = ! frontFacingIsCCW;
         System.out.print("Front face is ");
         System.out.println(frontFacingIsCCW ? "CCW" : "CW");
      }
      else if ('t' == c)
      {
         facesHaveTwoSides = ! facesHaveTwoSides;
         System.out.print("Faces have two sides is ");
         System.out.println(facesHaveTwoSides ? "On" : "Off");
      }
      else if ('p' == c)
      {
         perspective = ! perspective;
         final String p = perspective ? "perspective" : "orthographic";
         System.out.println("Using " + p + " projection");
      }
      else if ('l' == c)
      {
         letterbox = ! letterbox;
         System.out.print("Letter boxing is turned ");
         System.out.println(letterbox ? "On" : "Off");
      }
      else if ('n' == c)
      {
         // Move the near plane closer to the camera.
         near -= 0.01;
      }
      else if ('N' == c)
      {
         // Move the near plane away from the camera.
         near += 0.01;
      }
      else if ('b' == c)
      {
         NearClip.doNearClipping = ! NearClip.doNearClipping;
         System.out.print("Near-plane clipping is turned ");
         System.out.println(NearClip.doNearClipping ? "On" : "Off");
      }
      else if ('r' == c || 'R' == c)
      {
         // Change the aspect ratio of the camera's view rectangle.
         if ('r' == c)
         {
            aspectRatio -= 0.01;
         }
         else
         {
            aspectRatio += 0.01;
         }
      }
      else if ('f' == c)
      {
         fovy -= 0.5;  // change by 1/2 a degree
      }
      else if ('F' == c)
      {
         fovy += 0.5;  // change by 1/2 a degree
      }
      else if ('=' == c)
      {
         cameraRotX = 0.0;
         cameraRotY = 0.0;
         cameraRotZ = 0.0;
      }
      else if ('z' == c && e.isAltDown())
      {
         cameraRotZ -= 1;
      }
      else if ('Z' == c && e.isAltDown())
      {
         cameraRotZ += 1;
      }
      else if ('z' == c)
      {
         cameraZ -= 0.1;
      }
      else if ('Z' == c)
      {
         cameraZ += 0.1;
      }
      else if ('m' == c || 'M' == c)
      {
         showCamera = ! showCamera;
      }
      else if ('k' == c) // Display window information.
      {
         showWindow = ! showWindow;
      }
      else if ('+' == c)
      {
         takeScreenshot = true;
      }

      // Render again.
      setupViewing();
      displayCamera(Optional.of(c),
                    Optional.empty());
      displayWindow(e);
   }//keyTyped()


   // Implement the ComponentListener interface.
   @Override public void componentMoved(ComponentEvent e){}
   @Override public void componentHidden(ComponentEvent e){}
   @Override public void componentShown(ComponentEvent e){}
   @Override public void componentResized(ComponentEvent e)
   {
      // Get the new size of the FrameBufferPanel.
      final int w = fbp.getWidth();
      final int h = fbp.getHeight();

      // Create a new FrameBuffer that fits the FrameBufferPanel.
      final Color bg1 = fbp.getFrameBuffer().getBackgroundColorFB();
      final Color bg2 = fbp.getFrameBuffer().getViewport()
                                            .getBackgroundColorVP();
      final FrameBuffer fb = new FrameBuffer(w, h, bg1);
      fb.vp.setBackgroundColorVP(bg2);
      fbp.setFrameBuffer(fb);

      // Render again.
      setupViewing();
   }//componentResized()


   private void displayCamera(Optional<Character> ch,
                              Optional<Integer> k)
   {
      if ( ch.isPresent() )
      {
         final char c = ch.get();
         if (showCamera && ('m'==c||'M'==c
                          ||'n'==c||'N'==c
                          ||'f'==c||'F'==c
                          ||'r'==c||'R'==c
                          ||('b'==c && NearClip.doNearClipping)
                          ||'p'==c))
         {
            System.out.println( scene.camera );
         }
      }

      if ( ch.isPresent() )
      {
         final char c = ch.get();
         if (showCamera && ('z'==c||'Z'==c))
         {
            System.out.println("Camera Location:");
            System.out.println("  cameraX = " + cameraX
                             + ", cameraY = " + cameraY
                             + ", cameraZ = " + cameraZ);
            System.out.println("  cameraRotX = " + cameraRotX
                             + ", cameraRotY = " + cameraRotY
                             + ", cameraRotZ = " + cameraRotZ);
            System.out.println("View Matrix");
            System.out.println( scene.camera.getViewMatrix() );
         }
      }

      if ( k.isPresent() )
      {
         final int keyCode = k.get();
         if (showCamera
          &&( KeyEvent.VK_UP == keyCode   || KeyEvent.VK_DOWN == keyCode
           || KeyEvent.VK_LEFT == keyCode || KeyEvent.VK_RIGHT == keyCode) )
         {
            System.out.println("Camera Location:");
            System.out.println("  cameraX = " + cameraX
                             + ", cameraY = " + cameraY
                             + ", cameraZ = " + cameraZ);
            System.out.println("  cameraRotX = " + cameraRotX
                             + ", cameraRotY = " + cameraRotY
                             + ", cameraRotZ = " + cameraRotZ);
            System.out.println("View Matrix");
            System.out.println( scene.camera.getViewMatrix() );
         }
      }
   }


   private void displayWindow(final KeyEvent e)
   {
      //final char c = e.getKeyChar();

      if (showWindow)
      {
         // Get the size of the JFrame.
         final int wJF = jf.getWidth();
         final int hJF = jf.getHeight();
         // Get the size of the FrameBufferPanel.
         final int wFBP = fbp.getWidth();
         final int hFBP = fbp.getHeight();
         // Get the size of the FrameBuffer.
         final int wFB = fbp.getFrameBuffer().getWidthFB();
         final int hFB = fbp.getFrameBuffer().getHeightFB();
         // Get the size of the Viewport.
         final int wVP = fbp.getFrameBuffer().getViewport().getWidthVP();
         final int hVP = fbp.getFrameBuffer().getViewport().getHeightVP();
         // Get the location of the Viewport in the FrameBuffer.
         final int vp_ul_x = fbp.getFrameBuffer().getViewport().vp_ul_x;
         final int vp_ul_y = fbp.getFrameBuffer().getViewport().vp_ul_y;
         // Get the size of the camera's view rectangle.
         final Camera c = camera;
         final double wVR = c.right - c.left;
         final double hVR = c.top - c.bottom;

         final double rJF  = (double)wJF/(double)hJF;
         final double rFBP = (double)wFBP/(double)hFBP;
         final double rFB  = (double)wFB/(double)hFB;
         final double rVP  = (double)wVP/(double)hVP;
         final double rC   = wVR / hVR;

         System.out.printf(
            "Window information:\n" +
             "            JFrame [w=%4d, h=%4d], aspect ratio = %.2f\n" +
             "  FrameBufferPanel [w=%4d, h=%4d], aspect ratio = %.2f\n" +
             "       FrameBuffer [w=%4d, h=%4d], aspect ratio = %.2f\n" +
             "          Viewport [w=%4d, h=%4d, x=%d, y=%d], aspect ratio = %.2f\n" +
             "            Camera [w=%.2f, h=%.2f], aspect ratio = %.2f\n",
             wJF, hJF, rJF,
             wFBP, hFBP, rFBP,
             wFB, hFB, rFB,
             wVP, hVP, vp_ul_x, vp_ul_y, rVP,
             wVR, hVR, rC);
      }
      showWindow = false;
   }


   // Get in one place the code to set up the viewport and view volume.
   private void setupViewing()
   {
      // Set up the camera's view volume.
      if (perspective)
      {
         camera = Camera.projPerspective(fovy, aspectRatio);
      }
      else
      {
         camera = Camera.projOrtho(fovy, aspectRatio);
      }
      camera = camera.changeNear(near);
      // Switch cameras.
      scene = scene.changeCamera( camera );

      // Set up the camera's location and orientation.
      camera.viewTranslate(cameraX, cameraY, cameraZ);
      camera.viewRotateX(cameraRotX);
      camera.viewRotateY(cameraRotY);
      camera.viewRotateZ(cameraRotZ);

      // Set the model's orientation properties.
      model.setBackFaceCulling(doBackFaceCulling);
      model.setFrontFacingIsCCW(frontFacingIsCCW);
      model.setFacesHaveTwoSides(facesHaveTwoSides);

      // Get the size of the FrameBuffer.
      final FrameBuffer fb = fbp.getFrameBuffer();
      final int w = fb.width;
      final int h = fb.height;
      // Create a viewport with the correct aspect ratio.
      if ( letterbox )
      {
         if ( aspectRatio <= w/(double)h )
         {
            final int width = (int)(h * aspectRatio);
            final int xOffset = (w - width) / 2;
            fb.setViewport(xOffset, 0, width, h);
         }
         else
         {
            final int height = (int)(w / aspectRatio);
            final int yOffset = (h - height) / 2;
            fb.setViewport(0, yOffset, w, height);
         }
         fb.clearFB();
         fb.vp.clearVP();
      }
      else // The viewport is the whole framebuffer.
      {
         fb.setViewport();
         fb.vp.clearVP();
      }

      // Render again.
      if (useRenderer1)
      {
         Pipeline.render(scene, fb.vp);
      }
      else
      {
         Pipeline2.render(scene, fb.vp);
      }
      if (takeScreenshot)
      {
         fb.dumpFB2File(String.format("Screenshot%03d.png", screenshotNumber),
                        "png");
         ++screenshotNumber;
         takeScreenshot = false;
      }
      fbp.repaint();
   }


   private void print_help_message()
   {
      System.out.println("Use the 'd/D' keys to toggle debugging information on and off for the current model.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the 'i/I' keys to get information about the current model.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the '3' key to toggle anti-aliasing on and off.");
      System.out.println("Use the '4' key to toggle gamma correction on and off.");
      System.out.println("Use the 'b' key to toggle near plane clipping on and off.");
      System.out.println("Use the n/N keys to move the camera's near plane.");
      System.out.println("Use the f/F keys to change the camera's field-of-view (keep AR constant).");
      System.out.println("Use the r/R keys to change the camera's aspect ratio (keep fov constant).");
      System.out.println("Use the 'l' key to toggle letterboxing viewport on and off.");
      System.out.println("Use the arrow keys to translate the camera left/right/up/down.");
      System.out.println("Use CTRL arrow keys to rotate the camera left/right/up/down.");
      System.out.println("Use z/Z keys to translate the camera forward/backward.");
      System.out.println("Use Alt-z/Z keys to rotate the camera sideways (on the z-axis).");
      System.out.println("Use the 'g' key to toggle back face culling.");
      System.out.println("Use the 'G' key to toggle CW vs CCW.");
      System.out.println("Use the 't' key to toggle two sided faces.");
      System.out.println("Use the '=' key to reset the Camera's rotation.");
      System.out.println("Use the 'm' key to toggle showing the Camera data.");
      System.out.println("Use the 'k' key to show window data.");
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
         () -> new Maze_navigate_R18()
      );
   }

   public static int[] modelInfo(final Model model)
   {
      int verts = model.vertexList.size();
      int points = 0;
      int segments = 0;
      int lineSegment = 0;
      int lines = 0;
      int lineStrip = 0;
      int lineLoop = 0;
      int lineFan = 0;
      int face = 0;
      int tris = 0;
      int triangle = 0;
      int triangles = 0;
      int triangleStrip = 0;
      int triangleFan = 0;

      for (final Primitive p : model.primitiveList)
      {
         if (p instanceof Point)
         {
            ++points;
         }
         else if (p instanceof Points)
         {
            points += p.vIndexList.size();
         }
         else if (p instanceof LineSegment)
         {
            ++lineSegment;
            ++segments;
         }
         else if (p instanceof Lines)
         {
            ++lines;
            segments += p.vIndexList.size() / 2;
         }
         else if (p instanceof LineStrip)
         {
            ++lineStrip;
            segments += p.vIndexList.size() - 1;
         }
         else if (p instanceof LineLoop)
         {
            ++lineLoop;
            segments += p.vIndexList.size();
         }
         else if (p instanceof LineFan)
         {
            ++lineFan;
            segments += p.vIndexList.size() - 1;
         }
         else if (p instanceof Face)
         {
            ++face;
            segments += p.vIndexList.size();
         }
         else if (p instanceof Triangle)
         {
            ++triangle;
            ++tris;
         }
         else if (p instanceof Triangles)
         {
            ++triangles;
            tris += p.vIndexList.size() / 3;
         }
         else if (p instanceof TriangleStrip)
         {
            ++triangleStrip;
            tris += p.vIndexList.size() - 2;
         }
         else if (p instanceof TriangleFan)
         {
            ++triangleFan;
            tris += p.vIndexList.size() - 2;
         }
      }

      for (final Model m : model.nestedModels)
      {
         final int[] info = modelInfo(m);
         verts += info[0];
         points += info[1];
         segments += info[2];
         lineSegment += info[3];
         lines += info[4];
         lineStrip += info[5];
         lineLoop += info[6];
         lineFan += info[7];
         face += info[8];
         tris += info[9];
         triangle += info[10];
         triangle += info[11];
         triangleStrip += info[12];
         triangleFan += info[13];
      }
      return new int[]{verts,
                       points,
                       segments, lineSegment, lines, lineStrip, lineLoop, lineFan, face,
                       tris, triangle, triangles, triangleStrip, triangleFan};
   }
}
