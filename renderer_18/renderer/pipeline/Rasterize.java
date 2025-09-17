/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.framebuffer.*;
import static renderer.pipeline.PipelineLogger.*;

/**
   Rasterize a projected geometric {@link Primitive}
   into shaded pixels in a {{@link FrameBuffer.Viewport}.
*/
public class Rasterize
{
   public static boolean debug = false;
   public static boolean doClipping = true;      // no longer being used
   public static boolean doAntiAliasing = false; // no longer being used
   public static boolean doGamma = true;
   public static final double GAMMA = 2.2;

   /**
      Rasterize every clipped {@link Primitive} into shaded pixels
      in a {@link FrameBuffer.Viewport}.

      @param model  {@link Model} that contains clipped {@link Primitive}s
      @param vp     {@link FrameBuffer.Viewport} to hold rasterized, shaded pixels
   */
   public static void rasterize(final Model model,
                                final FrameBuffer.Viewport vp)
   {
      // Rasterize each visible primitive into shaded pixels.
      for (final Primitive p : model.primitiveList)
      {
         logPrimitive("10. Rasterize", model, p);

         if (p instanceof LineSegment)
         {
            Rasterize_Line.rasterize(model, (LineSegment)p, vp);
         }
         else if (p instanceof Points)
         {
            Rasterize_Clip_Points.rasterize(model, (Points)p, vp);
         }
         else if (p instanceof Triangle)
         {
            Rasterize_Triangle.rasterize(model, (Triangle)p, vp);
         }
         else // should never get here
         {
            System.err.println("Incorrect primitive: " + p);
         }
      }
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Rasterize() {
      throw new AssertionError();
   }
}
