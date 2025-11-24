/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.primitives.LineSegment;
import renderer.framebuffer.*;
import static renderer.pipeline.PipelineLogger.*;

import java.awt.Color;

/**
   Rasterize a clipped {@link LineSegment} into shaded pixels
   in a {@link FrameBuffer}'s viewport and (optionally)
   gamma-encode the line at the same time.
<p>
   This pipeline stage takes a clipped {@link LineSegment}
   with vertices in the {@link Camera}'s view rectangle and
   rasterizezs the line segment into shaded pixels in a
   {@link FrameBuffer}'s viewport. This rasterizer will
   linearly interpolate color from the line segment's two
   endpoints to each rasterized pixel in the line segment.
<p>
   This rasterization algorithm is based on
<pre>
     "Fundamentals of Computer Graphics", 3rd Edition,
      by Peter Shirley, pages 163-165.
</pre>
*/
public class Rasterize_Line
{
   /**
      Rasterize a clipped {@link LineSegment} into shaded pixels
      in the {@link FrameBuffer.Viewport}.

      @param model  {@link Model} that the {@link LineSegment} {@code ls} comes from
      @param ls     {@link LineSegment} to rasterize into the {@link FrameBuffer.Viewport}
      @param vp     {@link FrameBuffer.Viewport} to hold rasterized pixels
   */
   public static void rasterize(final Model model,
                                final LineSegment ls,
                                final FrameBuffer.Viewport vp)
   {
      // Make local copies of several values.
      final int w = vp.getWidthVP();
      final int h = vp.getHeightVP();

      final int vIndex0 = ls.vIndexList.get(0);
      final int vIndex1 = ls.vIndexList.get(1);
      final Vertex v0 = model.vertexList.get(vIndex0);
      final Vertex v1 = model.vertexList.get(vIndex1);

      final int cIndex0 = ls.cIndexList.get(0);
      final int cIndex1 = ls.cIndexList.get(1);
      final float[] c0 = model.colorList.get(cIndex0).getRGBComponents(null);
      final float[] c1 = model.colorList.get(cIndex1).getRGBComponents(null);
      float r0 = c0[0], g0 = c0[1], b0 = c0[2];
      float r1 = c1[0], g1 = c1[1], b1 = c1[2];

      // Transform each vertex to the "pixel plane" coordinate system.
      double x0 = 0.5 + w/2.001 * (v0.x + 1); // x_pp = 0.5 + w/2 * (x_p+1)
      double y0 = 0.5 + h/2.001 * (v0.y + 1); // y_pp = 0.5 + h/2 * (y_p+1)
      double x1 = 0.5 + w/2.001 * (v1.x + 1);
      double y1 = 0.5 + h/2.001 * (v1.y + 1);
      // NOTE: Notice the 2.001 fudge factor in the last two equations.
      // This is explained on page 142 of
      //    "Jim Blinn's Corner: A Trip Down The Graphics Pipeline"
      //     by Jim Blinn, 1996, Morgan Kaufmann Publishers.

      if (Rasterize.debug)
      {
         logMessage(String.format("(x0_pp, y0_pp) = (%9.4f, %9.4f)", x0,y0));
         logMessage(String.format("(x1_pp, y1_pp) = (%9.4f, %9.4f)", x1,y1));
      }

      // Round each vertex to the nearest logical pixel.
      // This makes the algorithm a lot simpler, but it can
      // cause a slight, but noticeable, shift of the line segment.
      x0 = Math.round(x0);
      y0 = Math.round(y0);
      x1 = Math.round(x1);
      y1 = Math.round(y1);

      // Get the z coordinates.
      double z0 = v0.z;
      double z1 = v1.z;

      // Rasterize a degenerate line segment (a line segment
      // that projected onto a point) as a single pixel.
      if ( (x0 == x1) && (y0 == y1) )
      {
         final int x0_vp = (int)x0 - 1;  // viewport coordinate
         final int y0_vp = h - (int)y0;  // viewport coordinate

         // Get the z-buffer value currently at this pixel of the viewport.
         double zBuff = vp.getDepthVP(x0_vp, y0_vp);

         // Determine which endpoint of the line segment is in front
         // and use the z value and color values from that endpoint.
         final double z;
         final float r;
         final float g;
         final float b;
         if ( z0 >= z1 )
         {
            z = z0;
            r = r0;
            g = g0;
            b = b0;
         }
         else
         {
            z = z1;
            r = r1;
            g = g1;
            b = b1;
         }

         // Determine if the front endpoint is in front of what is in
         // the framebuffer. If so, overwrite the data in the framebuffer.
         if ( z > zBuff )
         {
            if (Rasterize.debug) logPixel(x0, y0, x0_vp, y0_vp, r, g, b, vp);
            // Log the pixel before setting it so that an array out-
            // of-bounds error will be right after the pixel's address.

            vp.setPixelVP(x0_vp, y0_vp, new Color(r, g, b));
            vp.setDepthVP(x0_vp, y0_vp, z);
         }
         return;
      }

      // If abs(slope) > 1, then transpose this line so that
      // the transposed line has slope < 1. Remember that the
      // line has been transposed.
      final boolean transposedLine;
      if (Math.abs(y1 - y0) > Math.abs(x1 - x0)) // if abs(slope) > 1
      {
         // Swap x0 with y0.
         final double temp0 = x0;
         x0 = y0;
         y0 = temp0;
         // Swap x1 with y1.
         final double temp1 = x1;
         x1 = y1;
         y1 = temp1;
         transposedLine = true; // Remember that this line is transposed.
      }
      else
      {
         transposedLine = false; // Remember that this line is not transposed.
      }

      if (x1 < x0) // We want to rasterize in the direction of increasing x,
      {            // so, if necessary, swap (x0, y0, z0) with (x1, y1, z1).
         // Swap x0 with x1.
         final double tempX = x0;
         x0 = x1;
         x1 = tempX;
         // Swap y0 with y1.
         final double tempY = y0;
         y0 = y1;
         y1 = tempY;
         // Swap z0 with z1.
         final double tempZ = z0;
         z0 = z1;
         z1 = tempZ;
         // Swap the colors too.
         final float tempR = r0;
         final float tempG = g0;
         final float tempB = b0;
         r0 = r1;
         g0 = g1;
         b0 = b1;
         r1 = tempR;
         g1 = tempG;
         b1 = tempB;
      }

      // Compute this line segment's slopes.
      final double      m = (y1 - y0) / (x1 - x0);
      final double slopeZ = (z1 - z0) / (x1 - x0);
      final double slopeR = (r1 - r0) / (x1 - x0);
      final double slopeG = (g1 - g0) / (x1 - x0);
      final double slopeB = (b1 - b0) / (x1 - x0);

      if (Rasterize.debug)
      {
         final String inverseSlope = (transposedLine)
                                        ? " (transposed, so 1/m = " + 1/m + ")"
                                        : "";
         logMessage("Slope m    = " + m + inverseSlope);
         logMessage("Slope z    = " + slopeZ);
         logMessage("Slope mRed = " + slopeR);
         logMessage("Slope mGrn = " + slopeG);
         logMessage("Slope mBlu = " + slopeB);
         logMessage(String.format("(x0_vp, y0_vp, z0) = (%9.4f, %9.4f, %9.4f)", x0-1,h-y0,z0));
         logMessage(String.format("(x1_vp, y1_vp, z1) = (%9.4f, %9.4f, %9.4f)", x1-1,h-y1,z1));
      }

      // Rasterize this line segment in the direction of increasing x.
      // In the following loop, as x moves across the logical horizontal
      // (or vertical) pixels, we will compute a y value for each x.
      double y = y0;
      for (int x = (int)x0; x < (int)x1; x += 1, y += m)
      {
         // Compute the viewport coordinates of this pixel.
         // The value of y will almost always be between
         // two vertical (or horizontal) pixel coordinates.
         // By rounding off the value of y, we are choosing the
         // nearest logical vertical (or horizontal) pixel coordinate.
         final int x_vp;  // viewport coordinate
         final int y_vp;  // viewport coordinate
         if ( ! transposedLine )
         {
            x_vp = x - 1;
            y_vp = h - (int)Math.round(y);
         }
         else // a transposed line
         {
            x_vp = (int)Math.round(y) - 1;
            y_vp = h - x;
         }

         // Interpolate this pixel's depth between the two endpoint's depths.
         final double z = z0 + slopeZ * (x - x0);

         // Get the z-buffer value currently at this pixel of the viewport.
         final double zBuff = vp.getDepthVP(x_vp, y_vp);

         // If the current fragment is in front of what is in the
         //  framebuffer, then overwrite the data in the framebuffer.
         if (z > zBuff)
         {
            // Interpolate this pixel's color between the two endpoint's colors.
            float r = (float)Math.abs(r0 + slopeR*(x - x0));
            float g = (float)Math.abs(g0 + slopeG*(x - x0));
            float b = (float)Math.abs(b0 + slopeB*(x - x0));
            // We need the Math.abs() because otherwise, we sometimes get -0.0.

            if (Rasterize.doGamma)
            {
               // Apply gamma-encoding (gamma-compression) to the colors.
               // https://www.scratchapixel.com/lessons/digital-imaging/digital-images
               // http://blog.johnnovak.net/2016/09/21/what-every-coder-should-know-about-gamma/
               final double gammaInv = 1.0 / Rasterize.GAMMA;
               r = (float)Math.pow(r, gammaInv);
               g = (float)Math.pow(g, gammaInv);
               b = (float)Math.pow(b, gammaInv);
            }

            if (Rasterize.debug)
            {
               if ( ! transposedLine )
               {
                  logPixel(x, y, x_vp, y_vp, r, g, b, vp);
               }
               else // a transposed line
               {
                  logPixel(y, x, x_vp, y_vp, r, g, b, vp);
               }
            }
            // Log the pixel before setting it so that an array out-
            // of-bounds error will be right after the pixel's address.

            vp.setPixelVP(x_vp, y_vp, new Color(r, g, b));
            vp.setDepthVP(x_vp, y_vp, z);
         }
         // Advance (x,y) to the next pixel (delta_x is 1, so delta_y is m).
      }
      // Set the pixel for the (x1,y1,z1) endpoint.
      // We do this separately to avoid rounding errors.
      final int x_vp;  // viewport coordinate
      final int y_vp;  // viewport coordinate
      if ( ! transposedLine )
      {
         x_vp = (int)x1 - 1;
         y_vp = h - (int)y1;
      }
      else // a transposed line
      {
         x_vp = (int)y1 - 1;
         y_vp = h - (int)x1;
      }
      final double zBuff = vp.getDepthVP(x_vp, y_vp);
      if (z1 > zBuff)
      {
         if (Rasterize.debug)
         {
            if ( ! transposedLine )
            {
               logPixel(x1, y1, x_vp, y_vp, r1, g1, b1, vp);
            }
            else // a transposed line
            {
               logPixel(y1, x1, x_vp, y_vp, r1, g1, b1, vp);
            }
         }
         vp.setPixelVP(x_vp, y_vp, new Color(r1, g1, b1));
         vp.setDepthVP(x_vp, y_vp, z1);
      }
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Rasterize_Line() {
      throw new AssertionError();
   }
}
