/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.Assets;
import renderer.scene.util.ModelShading;
import renderer.scene.util.DrawSceneGraph;
import renderer.models_TP.*; // models defined using higher order triangle primitives
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
import javax.swing.Timer;
import java.io.File;
import java.util.Optional;

/**
   Compare with
      http://threejs.org/examples/#webgl_geometries
   or
      https://stemkoski.github.io/Three.js/Shapes.html
   or
      http://www.smartjava.org/ltjs/chapter-02/04-geometries.html
*/
public class Geometries_R17_navigate implements
                                     KeyListener,
                                     ComponentListener,
                                     ActionListener
{
   private static final String assets = Assets.getPath();

   private int fps;
   private Timer timer = null;


   protected boolean doBackFaceCulling = true;
   protected boolean frontFacingIsCCW = true;
   protected boolean facesHaveTwoSides = true;
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

   private boolean debug = false;
   private Camera camera;

   private final Model[][] model;
   private int angleNumber = 0;
   private final Position xyzAxes;
   private final Position xzPlane;

   private boolean useRenderer1 = true;

   private boolean takeScreenshot = false;
   private int screenshotNumber = 0;

   private final JFrame jf;
   private final FrameBufferPanel fbp;

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public Geometries_R17_navigate()
   {
      // Create a two-dimensional array of Models.
      model = new Model[5][3];

      // row 0 (first row in the first image)
      model[0][0] = new TriangularPrism(1.0, 1.0, 10);
      ModelShading.setRandomPrimitiveColors(model[0][0]);

      model[0][1] = new Cylinder(0.5, 1.0, 30, 30);
      ModelShading.setPrimitiveColorShaded(model[0][1],
                                           ModelShading.randomColor());

      model[0][2] = new renderer.models_F.ObjSimpleModel(new File(
                             assets + "great_rhombicosidodecahedron.obj"));
      ModelShading.setColor(model[0][2], Color.red);

      // row 1
      model[1][0] = new GRSModel(new File(
                          assets + "grs/bronto.grs"));
      ModelShading.setColor(model[1][0], Color.red);

      model[1][1] = new ObjSimpleModel(new File(
                          assets + "horse.obj"));
      ModelShading.setColor(model[1][1], Color.pink.darker());

      model[1][2] = new ConeFrustum(0.5, 1.0, 1.0, 10, 10);
      ModelShading.setRandomPrimitiveColors(model[1][2]);

      // row 2
      model[2][0] = new Torus(0.75, 0.25, 30, 30);
      ModelShading.setColor(model[2][0], Color.gray);

      model[2][1] = new Octahedron(6);
      ModelShading.setPrimitiveColorShaded(model[2][1],
                                           Color.magenta.brighter());

      model[2][2] = new Box(1.0, 1.0, 1.0);
      ModelShading.setRandomPrimitiveColors(model[2][2]);

      // row 3 (back row in the first image)
      model[3][0] = new ParametricCurve(
                t -> 0.3*(Math.sin(t) + 2*Math.sin(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(Math.cos(t) - 2*Math.cos(2*t)) + 0.1*Math.sin(t/6),
                t -> 0.3*(-Math.sin(3*t)),
                0, 6*Math.PI, 120);
      ModelShading.setRandomVertexColors(model[3][0]);

      model[3][1] = new ObjSimpleModel(new File(
                          assets + "small_rhombicosidodecahedron.obj"));
      ModelShading.setRandomPrimitiveColors(model[3][1]);

      model[3][2] = new SurfaceOfRevolution(
                t -> 1.5*(0.5 + 0.15 * Math.sin(10*t+1.0)*Math.sin(5*t+0.5)),
                -0.1, 0.9,
                30, 30);
      ModelShading.setPrimitiveColorShaded(model[3][2], Color.red);
      model[3][2].doBackFaceCulling = false;
      model[3][2].facesHaveTwoSides = true;
      final Color backFaceColor = new Color(150, 200, 150); // light green
      ModelShading.setBackFaceColor(model[3][2],
                                    backFaceColor,  // light green
                                    backFaceColor.brighter().brighter(),
                                    backFaceColor.darker().darker());

      // row 4 (last row in first image)
      model[4][0] = new Cone(0.5, 1.0, 30, 30);
      ModelShading.setColor(model[4][0], Color.yellow);

      model[4][1] = new Tetrahedron(12, 12);
      ModelShading.setRandomPrimitiveColors(model[4][1]);

      model[4][2] = new Sphere(1.0, 30, 30);
      ModelShading.setPrimitiveColorShaded(model[4][2],
                                           Color.cyan.brighter().brighter());

      // Create x, y and z axes
      xyzAxes = new Position(new Axes3D(6, -6, 6, 0, 7, -7, Color.red));

      // Create a horizontal coordinate plane model.
      xzPlane = new Position(new renderer.models_F.PanelXZ(-6, 6, -7, 7));
      ModelShading.setColor(xzPlane.getModel(), Color.darkGray);


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1800;
      final int height =  900;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 17 - Geometries navigate");
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);

      // Create event handler objects for events from the JFrame.
      jf.addKeyListener(this);
      jf.addComponentListener(this);

      fps = 30;
      timer = new Timer(1000/fps, this); // ActionListener
      timer.start();

      print_help_message();
   }


   // Implement the ActionListener interface.
   @Override public void actionPerformed(ActionEvent e)
   {
      //System.out.println( e );

      ++angleNumber;
      if (360 == angleNumber) angleNumber = 0;

      // Render again.
      setupViewing();
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
      else if ('d' == c)
      {
         debug = ! debug;
         Clip.debug = debug;
      }
      else if ('D' == c)
      {
         Rasterize.debug = ! Rasterize.debug;
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
      else if ('*' == c) // Display window information.
      {
         showWindow = ! showWindow;
      }
      else if ('+' == c)
      {
         takeScreenshot = true;
      }
      else if ('s' == c)
      {
         // Stop the animation.
         timer.stop();
      }
      else if ('S' == c)
      {
         // Start the animation.
         timer.start();
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
         if (showCamera && ('M'==c||'n'==c||'N'==c
                                  ||'f'==c||'F'==c
                                  ||'r'==c||'R'==c
                                  ||('b'==c && NearClip.doNearClipping)
                                  ||'p'==c))
         {
            System.out.println( camera );
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
            System.out.println("  cameraRotX = " + cameraRotX
                             + ", cameraRotY = " + cameraRotY);
            System.out.println("View Matrix");
            System.out.println( camera.getViewMatrix() );
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

      // Set up the camera's location and orientation.
      camera.view2Identity();
      camera.viewTranslate(cameraX, cameraY, cameraZ);
      camera.viewRotateX(cameraRotX);
      camera.viewRotateY(cameraRotY);
      camera.viewRotateZ(cameraRotZ);

      // Set each model's orientation properties.
      for (int i = 0; i < model.length; ++i)
      {
         for (int j = 0; j < model[i].length; ++j)
         {
            model[i][j].setBackFaceCulling(doBackFaceCulling);
            model[i][j].setFrontFacingIsCCW(frontFacingIsCCW);
            model[i][j].setFacesHaveTwoSides(facesHaveTwoSides);
         }
      }

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

      // Build the Scene for this frame of the animation.
      final Scene scene = new Scene("Geometries_R17_navigate_angle_"+angleNumber, camera);
      scene.debug = debug;

      // Add the positions (and their models) to the Scene.
      scene.addPosition(xzPlane,  // draw the grid first
                        xyzAxes); // draw the axes on top of the grid

      // Place each model where it belongs in the xz-plane
      // and also rotate each model on its own axis.
      for (int i = model.length - 1; i >= 0; --i) // from back to front
      {
         for (int j = 0; j < model[i].length; ++j)
         {
            // Place this model where it belongs in the plane.
            // Then rotate this model on its own axis.
            final Matrix mat = Matrix.translate(-4+4*j, 0, 6-3*i)
                        .times(Matrix.rotateX(3*angleNumber))
                        .times(Matrix.rotateY(3*angleNumber));
            scene.addPosition(new Position(model[i][j],
                                           "p["+i+"]["+j+"]",
                                           mat) );
         }
      }

      // Draw one Scene graph image.
      if (0 == angleNumber) DrawSceneGraph.draw(scene, "Geometries_R17_navigate_SG");

      // Render again.
      Pipeline.render(scene, fb.vp);
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
      System.out.println("Use the '*' key to show window data.");
      System.out.println("Use the '+' key to save a \"screenshot\" of the framebuffer.");
      System.out.println("Use the 's/S' key to stop/Start the animation.");
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
         () -> new Geometries_R17_navigate()
      );
   }
}
