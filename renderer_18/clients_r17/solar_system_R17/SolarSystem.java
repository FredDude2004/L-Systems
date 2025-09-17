/*
 * Renderer 17. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.util.ModelShading;
import renderer.scene.util.DrawSceneGraph;
import renderer.models_T.Sphere;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

/**
   Draw an animation of a solar system with a sun, planet, and moon.
<p>
   In this version, the planet orbits around the sun at the same rate as the sun
   rotates on its axis. Similarly, the moon orbits around the planet at the same
   rate as the planet rotates on its axis.
<p>
   But the orbit of the planet should be independent of the rotation of the sun,
   and the orbit of the moon should be independent of the rotation of the planet.
<pre>{@code
         Scene
        /     \
       /       \
  Camera     List<Position>
               |
               |
            Position
           /   |    \
          /    |     \
    Matrix   Model    List<Position>
      R      (sun)          |
                            |
                         Position
                        /   |    \
                       /    |     \
                 Matrix   Model    List<Position>
                   TR    (planet)       |
                                        |
                                     Position
                                    /   |    \
                                   /    |     \
                               Matrix  Model   List<Position>
                                 TR   (moon)      (empty)
}</pre>
*/
public class SolarSystem implements ActionListener, KeyListener, ComponentListener
{
   private int fps;
   private Timer timer = null;

   private double planetOrbitRadius = 5.0;
   private double   moonOrbitRadius = 1.0;

   private double planetOrbitRot = 0.0;
   private double   moonOrbitRot = 0.0;

   private double  moonAxisRot = 0.0;

   private double ecliptic = 7.0; // angle of the ecliptic plane

   private boolean letterbox = true;
   private double aspectRatio = 1.0;
   private double fovy = 90.0;

   private Scene scene;
   private final Position sun_p;
   private final Position planetMoon_p;
   private final Position moon_p;

   private boolean perspective = true;
   private boolean takeScreenshot = false;
   private int screenshotNumber = 0;

   private boolean useRenderer1 = true;

   private final JFrame jf;
   private final FrameBufferPanel fbp;

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public SolarSystem()
   {
      /*
         See the above picture of the tree that this code creates.
      */
      scene = new Scene("Solar System",
                        Camera.projPerspective());
      scene.camera.viewTranslate(0, 0, 8);

      // Create the sun.
      final Model sun = new Sphere(1.0, 10, 10);
      ModelShading.setPrimitiveColorShaded(sun, Color.yellow);
      scene.addPosition( new Position(sun, "Sun") );
      sun_p = scene.getPosition(0);

      // Create the planet.
      final Model planet = new Sphere(0.5, 10, 10);
      ModelShading.setPrimitiveColorShaded(planet, Color.blue);
      sun_p.addNestedPosition( new Position(planet, "Planet") );
      planetMoon_p = sun_p.getNestedPosition(0);

      // Create the planet's moon.
      final Model moon = new Sphere(0.2, 10, 10);
      ModelShading.setPrimitiveColorShaded(moon, Color.green);
      planetMoon_p.addNestedPosition( new Position(moon, "Moon") );
      moon_p = planetMoon_p.getNestedPosition(0);

      // Draw pictures of the scene's tree (DAG) data structure.
      DrawSceneGraph.drawCameraDetails = false;
      DrawSceneGraph.drawMatrixDetails = false;
      DrawSceneGraph.draw(scene, "SolarSystem_v1_SG");
      DrawSceneGraph.drawMatrixDetails = true;
      DrawSceneGraph.draw(scene, "SolarSystem_v1_SG_with_Matrices");


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1024;
      final int height = 1024;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 17 - Solar System Scene");
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);

      // Create event handler objects for events from the JFrame.
      jf.addKeyListener(this);
      jf.addComponentListener(this);

//      scene.debug = true;
//      Clip.debug = true;
//      System.out.println(scene);

      fps = 20;
      timer = new Timer(1000/fps, this); // ActionListener
      timer.start();

      print_help_message();
   }


   // Implement the ActionListener interface.
   @Override public void actionPerformed(ActionEvent e)
   {
      //System.out.println( e );

      // Update the parameters for the next frame.
      planetOrbitRot += 1.0;
      moonOrbitRot += 5.0;
      moonAxisRot -= 10.0;

      setupViewing();
   }


   // Implement the KeyListener interface.
   @Override public void keyPressed(KeyEvent e){}
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
         System.out.println( scene );
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
      else if ('g' == c)
      {
         Rasterize.doGamma = ! Rasterize.doGamma;
         System.out.print("Gamma correction is turned ");
         System.out.println(Rasterize.doGamma ? "On" : "Off");
      }
      else if ('f' == c)
      {
         fps -= 1;
         if (0 > fps) fps = 0;
         System.out.println("fps = " + fps);
         setFPS();
      }
      else if ('F' == c)
      {
         fps += 1;
         System.out.println("fps = " + fps);
         setFPS();
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
      else if ('v' == c)
      {
         fovy -= 0.5;  // change by 1/2 a degree
      }
      else if ('V' == c)
      {
         fovy += 0.5;  // change by 1/2 a degree
      }
      else if ('e' == c)
      {
         ecliptic -= 1;
         System.out.println("ecliptic = " + ecliptic);
      }
      else if ('E' == c)
      {
         ecliptic += 1;
         System.out.println("ecliptic = " + ecliptic);
      }
      else if ('+' == c)
      {
         takeScreenshot = true;
      }

      setupViewing();
   }


   public void setFPS()
   {
      timer.stop();
      if (fps > 0)
      {
         timer = new Timer(1000/fps, this);
         timer.start();
      }
   }


   // Implement the ComponentListener interface.
   @Override public void componentMoved(ComponentEvent e){}
   @Override public void componentHidden(ComponentEvent e){}
   @Override public void componentShown(ComponentEvent e){}
   @Override public void componentResized(ComponentEvent e)
   {
      //System.out.println( e );
      /*
      System.out.printf("JFrame [w = %d, h = %d]: " +
                        "FrameBufferPanel [w = %d, h = %d].\n",
                        jf.getWidth(), jf.getHeight(),
                        fbp.getWidth(), fbp.getHeight());
      */
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

      // allow the timer event to update the new framebuffer and window
   }


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
      // Switch cameras.
      scene = scene.changeCamera( camera1 );
      // Set up the camera's location.
      scene.camera.viewTranslate(0, 0, 8);

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

      // Rotate the plane of the ecliptic (rotate the sun's xz-plane
      // about the x-axis), and rotate the sun on its y-axis.
      sun_p.transform( Matrix.rotateX(ecliptic)
               .times( Matrix.rotateY(planetOrbitRot) ));

      // Place the planet-moon away from the sun and rotate the planet-moon on its axis.
      planetMoon_p.transform( Matrix.translate(planetOrbitRadius, 0, 0)
                      .times( Matrix.rotateY(moonOrbitRot) ));

      // Place the moon away from the planet and rotate the moon on its axis.
      moon_p.transform( Matrix.translate(moonOrbitRadius, 0, 0)
                .times( Matrix.rotateY(moonAxisRot) ));

      // Render again.
      if (useRenderer1)
      {
         Pipeline.render(scene, fb);
      }
      else
      {
         Pipeline2.render(scene, fb);
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
      System.out.println("Use the 'd' key to toggle debugging information on and off.");
      System.out.println("Use the 'Alt-d' key combination to print the Scene data structure.");
      System.out.println("Use the '1' and '2' keys to switch between the two renderers.");
      System.out.println("Use the 'g' key to toggle gamma correction on and off.");
      System.out.println("Use the f/F keys to slow down or speed up the frame rate.");
      System.out.println("Use the 's/S' key to stop/Start the animation.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the v/V keys to change the camera's field-of-view (keep AR constant).");
      System.out.println("Use the r/R keys to change the camera's aspect ratio (keep fov constant).");
      System.out.println("Use the 'l' key to toggle letterboxing viewport on and off.");
      System.out.println("Use the e/E keys to change the angle of the ecliptic plane.");
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
         () -> new SolarSystem()
      );
   }
}
