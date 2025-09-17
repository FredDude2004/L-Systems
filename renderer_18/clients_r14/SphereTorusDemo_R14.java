/*
 * Renderer 14. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.*;
import renderer.models_F.Sphere;
import renderer.models_F.Torus;
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
import java.util.List;
import java.util.ArrayList;

/**

*/
public class SphereTorusDemo_R14 implements KeyListener, ActionListener
{
   private int fps;
   private Timer timer = null;

   private double rotation = 0.0;
   private double delta = 1.0; // try delta=24 or delta=22.5 (freeze each model)
   private final boolean[] doBackFaceCulling = {false, true};
   private final boolean[] frontFacingIsCCW  = {true, true};
   private final boolean[] facesHaveTwoSides = {true, false};
   private final Color backFaceColor = new Color(90, 90, 90); // light grey

   private double xRotation = 0.0;
   private double yRotation = 0.0;
   private double zRotation = 0.0;

   private Scene scene;
   private boolean perspective = true;
   private final List<Model> modelArray = new ArrayList<>();
   private int currentModel = 0;
   private int currentVP = 1;

   protected boolean takeScreenshot = false;
   protected int screenshotNumber = 0;

   private final JFrame jf;
   private final FrameBufferPanel fbp;

   /**
      This constructor instantiates the Scene object
      and initializes it with appropriate geometry.
      Then this constructor instantiates the GUI.
   */
   public SphereTorusDemo_R14()
   {
      // Set up the Camera's view frustum.
      final double fov    = 25.0;
      final double aspect = 1.0;
      scene = new Scene("SphereTorusDemo_R14",
                         Camera.projPerspective(fov, aspect));

      // Set up the camera's location and orientation.
      scene.camera.viewTranslate(0.0, 0.0, 5.0);

      // Create Model objects.
      final int nS = 15;
      final int kS = 15;  // 24 degrees between lines of longitude
      final double rS = 1.0;
      modelArray.add( new Sphere(rS, nS, kS) );
      final int nT = 12;
      final int kT = 16;  // 22.5 degrees between lines of longitude
      final double r1 = 0.75;
      final double r2 = 0.25;
      modelArray.add( new Torus(r1, r2, nT, kT) );

      // Give the Models a color.
      ModelShading.setColor(modelArray.get(0), new Color(255, 0, 255));
      ModelShading.setBackFaceColor(modelArray.get(0), backFaceColor);
      ModelShading.setColor(modelArray.get(1), new Color(255, 0, 255));
      ModelShading.setBackFaceColor(modelArray.get(1), backFaceColor);

      // Add a Model to the Scene.
      scene.addPosition(new Position(new Model("empty"), "p0"));
      scene.getPosition(0).addNestedPosition(
                              new Position(modelArray.get(0),
                                           "sphere-torus"));

      DrawSceneGraph.draw(scene, "SG_SphereTorusDemo_R14");


      // Create a FrameBufferPanel that holds a FrameBuffer.
      final int width  = 1200;
      final int height =  600;
      fbp = new FrameBufferPanel(width, height, Color.darkGray);
      fbp.getFrameBuffer().getViewport().setBackgroundColorVP(Color.black);

      // Create a JFrame that will hold the FrameBufferPanel.
      jf = new JFrame("Renderer 14 - Sphere, Torus Demo");
      jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jf.getContentPane().add(fbp, BorderLayout.CENTER);
      jf.pack();
      jf.setLocationRelativeTo(null);
      jf.setVisible(true);
      jf.setResizable(false);

      // Register event handler objects.
      jf.addKeyListener(this);
      fps = 20;
      timer = new Timer(1000/fps, this); // ActionListener
      timer.start();

      print_help_message();
   }


   // Implement the ActionListener interface.
   @Override public void actionPerformed(ActionEvent e)
   {
      //System.out.println( e );

      rotation += delta;

      // Rotate the model.
      scene.getPosition(0).setNestedPosition(0,
         scene.getPosition(0).getNestedPosition(0)
            .transform( Matrix.rotateY(rotation) ));

      setupViewing();
   }


   // Implement the KeyListener interface.
   @Override public void keyPressed(KeyEvent e){}
   @Override public void keyReleased(KeyEvent e){}
   @Override public void keyTyped(KeyEvent e)
   {
      //System.out.println( e );

      char c = e.getKeyChar();
      if ('h' == c)
      {
         print_help_message();
         return;
      }
      else if ('/' == c)
      {
         currentVP = (currentVP + 1) % 2;
         if (0 == currentVP)
         {
            System.out.println("Left-hand viewport.");
         }
         else
         {
            System.out.println("Right-hand viewport.");
         }
      }
      else if (' ' == c)
      {
         currentModel = (currentModel + 1) % 2;
         scene.getPosition(0).setNestedPosition(0,
            scene.getPosition(0).getNestedPosition(0)
               .setModel( modelArray.get(currentModel) ));
      }
      else if ('i' == c)
      {
         final int[] modelInfo = modelInfo(scene
                                              .getPosition(0)
                                                 .getNestedPosition(0)
                                                    .getModel());
         System.out.printf("The current Model has:\n");
         System.out.printf("  %,d vertices,\n", modelInfo[0]);
         System.out.printf("  %,d line segments, grouped into\n", modelInfo[1]);
         System.out.printf("    %,d Face primitives,\n", modelInfo[7]);
         System.out.printf("    %,d LineSgement primitives,\n", modelInfo[2]);
         System.out.printf("    %,d Lines primitives,\n", modelInfo[3]);
         System.out.printf("    %,d LineStrip primitives,\n", modelInfo[4]);
         System.out.printf("    %,d LineLoop primitives,\n", modelInfo[5]);
         System.out.printf("    %,d LineFan primitives.\n", modelInfo[6]);
      }
      else if ('r' == c) // try setting delta=24 or delta=22.5 (freeze each model)
      {
         delta += 0.1;
         System.out.println("deltaAngle = " + delta);
      }
      else if ('R' == c)
      {
         delta -= 0.1;
         System.out.println("deltaAngle = " + delta);
      }
      else if ('f' == c)
      {
         fps -= 1;
         if (-1 == fps) fps = 0;
         setFPS();
         System.out.println("fps = " + fps);
      }
      else if ('F' == c)
      {
         fps += 1;
         setFPS();
         System.out.println("fps = " + fps);
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
         doBackFaceCulling[currentVP] = ! doBackFaceCulling[currentVP];
         System.out.print("Back face culling is ");
         System.out.println(doBackFaceCulling[currentVP] ? "on" : "off");
      }
      else if ('G' == c)
      {
         frontFacingIsCCW[currentVP] = ! frontFacingIsCCW[currentVP];
         System.out.print("Front face is ");
         System.out.println(frontFacingIsCCW[currentVP] ? "CCW" : "CW");
      }
      else if ('t' == c)
      {
         facesHaveTwoSides[currentVP] = ! facesHaveTwoSides[currentVP];
         System.out.print("Faces have two sides is ");
         System.out.println(facesHaveTwoSides[currentVP] ? "On" : "Off");
      }
      else if ('p' == c)
      {
         perspective = ! perspective;
         final String p = perspective ? "perspective" : "orthographic";
         System.out.println("Using " + p + " projection");
      }
      else if ('c' == c)
      {
         // Change the solid random color of the current model.
         final Model model = scene.getPosition(0).getNestedPosition(0).getModel();
         ModelShading.setRandomColor(model);
         ModelShading.setBackFaceColor(model, backFaceColor);
      }
      else if ('C' == c)
      {
         // Change each color in the current model to a random color.
         final Model model = scene.getPosition(0).getNestedPosition(0).getModel();
         ModelShading.setRandomColors(model);
         ModelShading.setBackFaceColor(model, backFaceColor);
      }
      else if ( 'e' == c && e.isAltDown() )
      {
         // Change the random color of each vertex of the current model.
         final Model model = scene.getPosition(0).getNestedPosition(0).getModel();
         ModelShading.setRandomVertexColors(model);
         ModelShading.setBackFaceColor(model, backFaceColor);
      }
      else if ('e' == c)
      {
         // Change the solid random color of each face of the current model.
         final Model model = scene.getPosition(0).getNestedPosition(0).getModel();
         ModelShading.setRandomPrimitiveColors(model);
         ModelShading.setBackFaceColor(model, backFaceColor);
      }
      else if ('E' == c)
      {
         // Change the random color of each vertex of each face of the current model.
         final Model model = scene.getPosition(0).getNestedPosition(0).getModel();
         ModelShading.setRainbowPrimitiveColors(model);
         ModelShading.setBackFaceColor(model, backFaceColor);
      }
      else if ('>' == c || '<' == c
             ||'.' == c || ',' == c)
      {
         final Position p = scene.getPosition(0).getNestedPosition(0);
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
            ModelShading.setColor(newM, p.getModel().colorList.get(0));
            ModelShading.setBackFaceColor(newM, backFaceColor);
            scene.getPosition(0).getNestedPosition(0).setModel(newM);
            final MeshMaker m = (MeshMaker)newM;
            final int newN = m.getHorzCount();
            final int newK = m.getVertCount();
            System.out.printf("Mesh: n = %3d and k = %3d.\n", newN, newK);
         }
         catch (IllegalArgumentException ex){}
      }
      else if ('=' == c)
      {
         xRotation = 0.0;
         yRotation = 0.0;
         zRotation = 0.0;
      }
      else if ('x' == c)
      {
         xRotation -= 2.0;
      }
      else if ('X' == c)
      {
         xRotation += 2.0;
      }
      else if ('y' == c)
      {
         yRotation -= 2.0;
      }
      else if ('Y' == c)
      {
         yRotation += 2.0;
      }
      else if ('z' == c)
      {
         zRotation -= 2.0;
      }
      else if ('Z' == c)
      {
         zRotation += 2.0;
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

      // Set the model-to-view transformation matrix.
      // The order of the transformations is important!
      scene.getPosition(0).transform( Matrix.rotateX(xRotation)
                              .times( Matrix.rotateY(yRotation) )
                              .times( Matrix.rotateZ(zRotation) ));

      setupViewing();
   }


   private void setupViewing()
   {
      if (perspective)
      {
         final double fov    = 25.0;
         final double aspect = 1.0;
         // Switch cameras.
         scene = scene.changeCamera(Camera.projPerspective(fov, aspect));
         // Set up the new camera's location.
         scene.camera.viewTranslate(0.0, 0.0, 5.0);
      }
      else
      {
         final double fov    = 90.0;
         final double aspect = 1.0;
         final Camera camera1;
         scene = scene.changeCamera(Camera.projOrtho(fov, aspect));
         // Set up the new camera's location.
         scene.camera.viewTranslate(0.0, 0.0, 5.0);
      }

      FrameBuffer fb = this.fbp.getFrameBuffer();
      //fb.clearFB();

      // Set a viewport within the framebuffer.
      fb.setViewport(0, 0, 600, 600); //upper-left-hand-corner, width, height
      fb.vp.clearVP();

      Model model = scene.getPosition(0).getNestedPosition(0).getModel();
      model.doBackFaceCulling = doBackFaceCulling[0];
      model.frontFacingIsCCW  = frontFacingIsCCW[0];
      model.facesHaveTwoSides = facesHaveTwoSides[0];
      // Render into this viewport.
      Pipeline.render(scene, fb.vp);

      // Set a viewport within the framebuffer.
      fb.setViewport(600, 0, 600, 600); //upper-left-hand-corner, width, height
      fb.vp.clearVP();
      model.doBackFaceCulling = doBackFaceCulling[1];
      model.frontFacingIsCCW  = frontFacingIsCCW[1];
      model.facesHaveTwoSides = facesHaveTwoSides[1];
      // Render into this viewport.
      Pipeline.render(scene, fb.vp);
      if (takeScreenshot)
      {
         fb.dumpFB2File(String.format("Screenshot%03d.png", screenshotNumber), "png");
         ++screenshotNumber;
         takeScreenshot = false;
      }
      fbp.repaint();
   }


   private void setFPS()
   {
      timer.stop();
      if (fps > 0)
      {
         timer = new Timer(1000/fps, this);
         timer.start();
      }
   }


   protected void print_help_message()
   {
      System.out.println("Use the 'r/R' keys to change the speed of rotation.");
      System.out.println("Use the 'f/F' keys to slow down or speed up the frame rate.");
      System.out.println("Use the 's/S' key to stop/Start the animation.");
      System.out.println("Use the 'p' key to toggle between parallel and orthographic projection.");
      System.out.println("Use the x/X, y/Y, z/Z, keys to rotate the sphere's axis around the x, y, z axes.");
      System.out.println("Use the 'c' key to change the random solid model color.");
      System.out.println("Use the 'C' key to randomly change the model's colors.");
      System.out.println("Use the 'e' key to change the random solid face colors.");
      System.out.println("Use the 'E' key to change the random face colors.");
      System.out.println("Use the 'Alt-e' key combination to change the random vertex colors.");
      System.out.println();
      System.out.println("Use the ' ' key to switch between the models.");
      System.out.println("Use the '/' key to switch between the viewports.");
      System.out.println("Use the '>/<' and shift keys to increase and decrease the mesh divisions in each direction.");
      System.out.println("Use the 'i' key to get information about the current model.");
      System.out.println("Use the 'g' key to toggle back face culling in the current viewport.");
      System.out.println("Use the 'G' key to toggle CW vs CCW in the current viewport.");
      System.out.println("Use the 't' key to toggle two sided faces in the curent viewport.");
      System.out.println("Use the '=' key to reset the transformation matrix to the identity.");
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
         () -> new SphereTorusDemo_R14()
      );
   }


   public static int[] modelInfo(final Model model)
   {
      int verts = model.vertexList.size();
      int lineSegments = 0;
      int line = 0;
      int lines = 0;
      int lineStrip = 0;
      int lineLoop = 0;
      int lineFan = 0;
      int face = 0;
      for (final Primitive p : model.primitiveList)
      {
         if (p instanceof LineSegment)
         {
            ++line;
            ++lineSegments;
         }
         else if (p instanceof Lines)
         {
            ++lines;
            lineSegments += p.vIndexList.size() / 2;
         }
         else if (p instanceof LineStrip)
         {
            ++lineStrip;
            lineSegments += p.vIndexList.size() - 1;
         }
         else if (p instanceof LineLoop)
         {
            ++lineLoop;
            lineSegments += p.vIndexList.size();
         }
         else if (p instanceof LineFan)
         {
            ++lineFan;
            lineSegments += p.vIndexList.size() - 1;
         }
         else if (p instanceof Face)
         {
            ++face;
            lineSegments += p.vIndexList.size();
         }
      }
      return new int[]{verts, lineSegments, line, lines, lineStrip, lineLoop, lineFan, face};
   }
}
