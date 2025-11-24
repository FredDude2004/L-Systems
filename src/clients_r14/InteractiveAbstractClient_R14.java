/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.ModelShading;
import renderer.scene.util.MeshMaker;
import renderer.scene.util.PointCloud;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.util.Optional;

/**
   This class holds all the client state information
   and implementations of the client's event handlers.
*/
public abstract class InteractiveAbstractClient_R14 implements
                                                    KeyListener,
                                                    ComponentListener
{
   protected boolean doBackFaceCulling = false;
   protected boolean frontFacingIsCCW = true;
   protected boolean facesHaveTwoSides = true;
   protected final Color backFaceColor = new Color(150, 200, 150); // light green

   protected double cameraDistance = 2.0;
   protected double eyeX = 0.0;
   protected double eyeY = 0.0;
   protected double eyeZ = cameraDistance;
   protected double eyeRotX = 0.0;
   protected double eyeRotY = 0.0;
   protected double eyeRotZ = 0.0;

   protected boolean letterbox = false;
   protected double aspectRatio = 1.0;
   protected double near = 0.1;
   protected boolean perspective = true;
   protected double fovy = 90.0;
   protected boolean showCamera = false;
   protected boolean showWindow = false;

   protected boolean showMatrix = false;
   protected double pushback = -2.0;
   protected double[] xTranslation = {0.0};
   protected double[] yTranslation = {0.0};
   protected double[] zTranslation = {0.0};
   protected double[] xRotation = {0.0};
   protected double[] yRotation = {0.0};
   protected double[] zRotation = {0.0};
   protected double[] scale = {1.0};

   protected Scene scene = null;
   protected int numberOfInteractiveModels = 1;
   protected boolean interactiveModelsAllVisible = false;
   protected boolean debugWholeScene = true;
   protected int currentModel = 0;
   private Model savedModel = null; // used to hold a PointCloud model
   private int pointSize = 0;       // used by the point clouds

   protected boolean useRenderer1 = true;

   protected boolean takeScreenshot = false;
   protected int screenshotNumber = 0;

   protected JFrame jf = null;
   protected FrameBufferPanel fbp = null;


   // Implement the KeyListener interface (three methods).
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
            if (KeyEvent.VK_UP == keyCode )
            {
               eyeRotX += 1;
            }
            else if (KeyEvent.VK_DOWN == keyCode )
            {
               eyeRotX -= 1;
            }
            else if (KeyEvent.VK_LEFT == keyCode )
            {
               eyeRotY += 1;
            }
            else if (KeyEvent.VK_RIGHT == keyCode )
            {
               eyeRotY -= 1;
            }
         }
         else // no control key
         {
            if (KeyEvent.VK_UP == keyCode)
            {
               eyeY += 0.1;
            }
            else if (KeyEvent.VK_DOWN == keyCode)
            {
               eyeY -= 0.1;
            }
            else if (KeyEvent.VK_LEFT == keyCode)
            {
               eyeX -= 0.1;
            }
            else if (KeyEvent.VK_RIGHT == keyCode)
            {
               eyeX += 0.1;
            }
         }

         displayCamera(Optional.empty(),
                       Optional.of(keyCode));

         // Render again.
         setupViewing();
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
      else if ('d' == c)
      {
         if (debugWholeScene)
         {
            scene.debug = ! scene.debug;
            Clip.debug = scene.debug;
         }
         else // debug just the current model
         {
            final Position p = scene.getPosition(currentModel);
            p.debug = ! p.debug;
            Clip.debug = p.debug;
         }
      }
      else if ('D' == c)
      {
         Rasterize.debug = ! Rasterize.debug;
      }
      else if ('i' == c)
      {
         final int[] modelInfo = modelInfo(scene.getPosition(currentModel).getModel());
         System.out.printf("The current Model has:\n");
         System.out.printf("  %,d vertices,\n", modelInfo[0]);
         System.out.printf("  %,d Point primitives,\n", modelInfo[1]);
         System.out.printf("  %,d Line segments, grouped into\n", modelInfo[2]);
         System.out.printf("    %,d LineSgement primitives,\n", modelInfo[3]);
         System.out.printf("    %,d Lines primitives,\n", modelInfo[4]);
         System.out.printf("    %,d LineStrip primitives,\n", modelInfo[5]);
         System.out.printf("    %,d LineLoop primitives,\n", modelInfo[6]);
         System.out.printf("    %,d LineFan primitives.\n", modelInfo[7]);
         System.out.printf("    %,d Face primitives,\n", modelInfo[8]);
      }
      else if ('I' == c)
      {
         System.out.println();
         System.out.println(scene.getPosition(currentModel).getModel());
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
      else if ('/' == c)
      {
         scene.getPosition(currentModel).visible = interactiveModelsAllVisible;
         currentModel = (currentModel + 1) % numberOfInteractiveModels;
         scene.getPosition(currentModel).visible = true;
         savedModel = null;
         pointSize = 0;
      }
      else if ('?' == c)
      {
         scene.getPosition(currentModel).visible = interactiveModelsAllVisible;
         currentModel = currentModel - 1;
         if (currentModel < 0) currentModel = numberOfInteractiveModels - 1;
         scene.getPosition(currentModel).visible = true;
         savedModel = null;
         pointSize = 0;
      }
      else if ('a' == c)
      {
         Rasterize.doAntiAliasing = ! Rasterize.doAntiAliasing;
         System.out.print("Anti-aliasing is turned ");
         System.out.println(Rasterize.doAntiAliasing ? "On" : "Off");
      }
      else if ('b' == c)
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
      else if ('P' == c)
      {
         if (savedModel != null)
         {
            scene.getPosition(currentModel).setModel(savedModel);
            savedModel = null;
            ++pointSize;
         }
         else
         {
            final Model model = scene.getPosition(currentModel).getModel();
            savedModel = model;
            scene.getPosition(currentModel)
                    .setModel(PointCloud.make(model, pointSize));
         }
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
      else if ('B' == c)
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
      else if ('M' == c)
      {
         showCamera = ! showCamera;
      }
      else if ('c' == c)
      {
         // Change the solid random color of the cube.
         ModelShading.setRandomColor(
                         scene.getPosition(currentModel).getModel());
      }
      else if ('C' == c)
      {
         // Change each color in the cube to a random color.
         ModelShading.setRandomColors(
                         scene.getPosition(currentModel).getModel());
      }
      else if ('e' == c && e.isAltDown())
      {
         // Change the random color of each vertex of the cube.
         ModelShading.setRandomVertexColors(
                         scene.getPosition(currentModel).getModel());
      }
      else if ('e' == c)
      {
         // Change the solid random color of each edge of the cube.
         ModelShading.setRandomPrimitiveColors(
                         scene.getPosition(currentModel).getModel());
      }
      else if ('E' == c)
      {
         // Change the random color of each end of each edge of the cube.
         ModelShading.setRainbowPrimitiveColors(
                         scene.getPosition(currentModel).getModel());
      }
      else if ('>' == c || '<' == c
             ||'.' == c || ',' == c)
      {
         final Position p = scene.getPosition(currentModel);
         if (p.getModel() instanceof MeshMaker)
         {
            final MeshMaker model = (MeshMaker)p.getModel();
            final int n;
            final int k;
            if ('>' == c)
            {
               n = model.getHorzCount() + 1;
               k = model.getVertCount();
            }
            else if ('<' == c)
            {
               n = model.getHorzCount() - 1;
               k = model.getVertCount();
            }
            else if ('.' == c)
            {
               n = model.getHorzCount();
               k = model.getVertCount() + 1;
            }
            else  // ',' == c
            {
               n = model.getHorzCount();
               k = model.getVertCount() - 1;
            }
            try
            {
               final Model newM = model.remake(n, k);
               // Copy a color from the previous model.
               // Look for a color.
               Model m2 = p.getModel();
               while (m2.colorList.isEmpty())
               {  // If the color list is empty, then
                  // there aught to be a nested model.
                  m2 = m2.nestedModels.get(0);
               }
               ModelShading.setColor(newM, m2.colorList.get(0));
             //ModelShading.setRandomPrimitiveColors(newM);
               ModelShading.setBackFaceColor(newM, backFaceColor);
               scene.getPosition(currentModel).setModel(newM);
               final MeshMaker m = (MeshMaker)newM;
               final int newN = m.getHorzCount();
               final int newK = m.getVertCount();
               System.out.printf("Mesh: n = %3d and k = %3d.\n", newN, newK);
            }
            catch (IllegalArgumentException ex){}
         }
      }
      else if ('m' == c) // Display matrix information.
      {
         showMatrix = ! showMatrix;
      }
      else if ('*' == c) // Display window information.
      {
         showWindow = ! showWindow;
      }
      else if ('+' == c)
      {
         takeScreenshot = true;
      }

      setTransformations(c);

      setBackFaceCulling(scene.getPosition(currentModel), doBackFaceCulling);
      setFrontFacingIsCCW(scene.getPosition(currentModel), frontFacingIsCCW);
      setFacesHaveTwoSides(scene.getPosition(currentModel), facesHaveTwoSides);

      setupViewing();

      displayMatrix(e);

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

      setupViewing();
   }//componentResized()


   // Get in one place the code to set up the viewport and view volume.
   private void setupViewing()
   {
      // Set up the camera's view volume.
      final Camera camera1;
      if (perspective)
      {
         camera1 = Camera.projPerspective(fovy, aspectRatio);
      }
      else
      {
         camera1 = Camera.projOrtho(fovy, aspectRatio);
      }
      final Camera camera2 = camera1.changeNear(near);
      // Switch cameras.
      scene = scene.changeCamera( camera2 );

      // Set up the camera's location and orientation.
      scene.camera.view2Identity();
      scene.camera.viewTranslate(eyeX, eyeY, eyeZ);
      scene.camera.viewRotateX(eyeRotX);
      scene.camera.viewRotateY(eyeRotY);

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


   // A client program can override how transformations are performed.
   protected void setTransformations(final char c)
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

      // Set the model-to-view transformation matrix.
      // The order of the transformations is very important!
      final Matrix matrix = Matrix.translate(xTranslation[0],
                                             yTranslation[0],
                                             zTranslation[0])
                 .times( Matrix.rotateZ(zRotation[0]) )
                 .times( Matrix.rotateY(yRotation[0]) )
                 .times( Matrix.rotateX(xRotation[0]) )
                 .times( Matrix.scale(scale[0])       );

      scene.setPosition(currentModel,
         scene.getPosition(currentModel).transform(matrix));
   }


   // A client program can override the printing of transformation information.
   protected void displayMatrix(final KeyEvent e)
   {
      final char c = e.getKeyChar();

      if (showMatrix && ('m'==c||'='==c
        ||'s'==c||'x'==c||'y'==c||'z'==c||'u'==c||'v'==c||'w'==c
        ||'S'==c||'X'==c||'Y'==c||'Z'==c||'U'==c||'V'==c||'W'==c))
      {
         System.out.printf("xRot = % .5f, " +
                           "yRot = % .5f, " +
                           "zRot = % .5f\n",
                            xRotation[0],
                            yRotation[0],
                            zRotation[0]);
         System.out.println( scene.getPosition(currentModel).getMatrix() );
      }
   }


   // A client program can override the printing of camera information.
   protected void displayCamera(Optional<Character> ch,
                                Optional<Integer> k)
   {
      if ( ch.isPresent() )
      {
         final char c = ch.get();
         if (showCamera && ('M'==c||'n'==c||'N'==c
                                  ||'f'==c||'F'==c
                                  ||'r'==c||'R'==c
                                  ||('b'==c && NearClip.doNearClipping)
                                  ||'p'==c))
         {
            System.out.println( scene.camera );
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
            System.out.println("  eyeRotX = " + eyeRotX
                             + ", eyeRotY = " + eyeRotY);
            System.out.println("View Matrix");
            System.out.println( scene.camera.getViewMatrix() );
         }
      }
   }


   // A client program can override the printing of window information.
   protected void displayWindow(final KeyEvent e)
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
         final Camera c = scene.camera;
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


   // A client program can override the printing of help information.
   protected void print_help_message()
   {
      System.out.println("Use the 'd/D' keys to toggle debugging information on and off for the current model.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the '/' and '?' keys to cycle forwards and backwards through the models.");
      System.out.println("Use the '>/<' and shift keys to increase and decrease the mesh divisions in each direction.");
      System.out.println("Use the 'i/I' keys to get information about the current model.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to translate the model along the x, y, z axes.");
      System.out.println("Use the u/U, v/V, w/W, keys to rotate the model around the x, y, z axes.");
      System.out.println("Use the s/S keys to scale the size of the model.");
      System.out.println("Use the 'm' key to toggle the display of matrix information.");
      System.out.println("Use the '=' key to reset the model matrix.");
      System.out.println("Use the 'c' key to change the random solid model color.");
      System.out.println("Use the 'C' key to randomly change model's colors.");
      System.out.println("Use the 'e' key to change the random solid edge colors.");
      System.out.println("Use the 'E' key to change the random edge colors.");
      System.out.println("Use the 'Alt-e' key combination to change the random vertex colors.");
      System.out.println("Use the 'a' key to toggle anti-aliasing on and off.");
      System.out.println("Use the 'b' key to toggle gamma correction on and off.");
      System.out.println("Use the 'B' key to toggle near plane clipping on and off.");
      System.out.println("Use the n/N keys to move the camera's near plane.");
      System.out.println("Use the f/F keys to change the camera's field-of-view (keep AR constant).");
      System.out.println("Use the r/R keys to change the camera's aspect ratio (keep fov constant).");
      System.out.println("Use the 'l' key to toggle letterboxing viewport on and off.");
      System.out.println("Use the arrow keys to move the camera location left/right/up/down.");
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


   protected void setBackFaceCulling(final Position p,
                                     final boolean doBackFaceCulling)
   {
      p.getModel().setBackFaceCulling(doBackFaceCulling);

      for (final Position p2 : p.nestedPositions)
      {
         setBackFaceCulling(p2, doBackFaceCulling);
      }
   }


   protected void setFrontFacingIsCCW(final Position p,
                                      final boolean frontFacingIsCCW)
   {
      p.getModel().setFrontFacingIsCCW(frontFacingIsCCW);

      for (final Position p2 : p.nestedPositions)
      {
         setFrontFacingIsCCW(p2, frontFacingIsCCW);
      }
   }


   protected void setFacesHaveTwoSides(final Position p,
                                       final boolean facesHaveTwoSides)
   {
      p.getModel().setFacesHaveTwoSides(facesHaveTwoSides);

      for (final Position p2 : p.nestedPositions)
      {
         setFacesHaveTwoSides(p2, facesHaveTwoSides);
      }
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
      }
      return new int[]{verts, points, segments, lineSegment, lines, lineStrip, lineLoop, lineFan, face};
   }
}
