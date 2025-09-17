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

import java.awt.Color;

/**
   Rasterize a clipped, textured {@link Triangle} into
   shaded pixels in the {@link FrameBuffer}'s viewport.
<p>
   This rasterization algorithm is based on
<pre>
     "Fundamentals of Computer Graphics", 3rd Edition,
      by Peter Shirley, pages 163-165.
</pre>
*/
public class Rasterize_Triangle
{
   /**
      Rasterize a clipped, textured {@link Triangle} into
      shaded pixels in the {@link FrameBuffer}'s viewport.

      @param model  {@link Model} that the {@link Triangle} {@code tri} comes from
      @param tri    {@code Triangle} to rasterize into the {@code FrameBuffer.Viewport}
      @param vp     {@link FrameBuffer.Viewport} to hold rasterized pixels
   */
   public static void rasterize(final Model model,
                                final Triangle tri,
                                final FrameBuffer.Viewport vp)
   {
      // Make local copies of several values.
      final int w = vp.getWidthVP();
      final int h = vp.getHeightVP();

      final int vIndex0 = tri.vIndexList.get(0);
      final int vIndex1 = tri.vIndexList.get(1);
      final int vIndex2 = tri.vIndexList.get(2);

      final int cIndex0 = tri.cIndexList.get(0);
      final int cIndex1 = tri.cIndexList.get(1);
      final int cIndex2 = tri.cIndexList.get(2);

      final Vertex v0 = model.vertexList.get(vIndex0);
      final Vertex v1 = model.vertexList.get(vIndex1);
      final Vertex v2 = model.vertexList.get(vIndex2);

      final float[] c0 = model.colorList.get(cIndex0).getRGBComponents(null);
      final float[] c1 = model.colorList.get(cIndex1).getRGBComponents(null);
      final float[] c2 = model.colorList.get(cIndex2).getRGBComponents(null);

      // Get the coordinates for the three vertices.
      double x0 = v0.x,  y0 = v0.y,  z0 = v0.z;
      double x1 = v1.x,  y1 = v1.y,  z1 = v1.z;
      double x2 = v2.x,  y2 = v2.y,  z2 = v2.z;

      // Get the colors for the three vertices.
      final float r0 = c0[0],  g0 = c0[1],  b0 = c0[2];
      final float r1 = c1[0],  g1 = c1[1],  b1 = c1[2];
      final float r2 = c2[0],  g2 = c2[1],  b2 = c2[2];

      // Rasterize a degenerate triangle (a triangle that
      // projected onto a line) as three line segments.
      final double area = (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);
      if ( 0.00001 > Math.abs(area) ) // 0.0001 causes noticeable artifacts
      {
         Rasterize_Line.rasterize(model, new LineSegment(vIndex0, vIndex1, cIndex0, cIndex1), vp);
         Rasterize_Line.rasterize(model, new LineSegment(vIndex1, vIndex2, cIndex1, cIndex2), vp);
         Rasterize_Line.rasterize(model, new LineSegment(vIndex2, vIndex0, cIndex2, cIndex0), vp);
         return;
      }

      // Transform each vertex to the "pixel plane" coordinate system.
      x0 = 0.5 + w/2.001 * (x0 + 1); // x_pp = 0.5 + w/2 * (x_p+1)
      y0 = 0.5 + h/2.001 * (y0 + 1); // y_pp = 0.5 + h/2 * (y_p+1)
      x1 = 0.5 + w/2.001 * (x1 + 1);
      y1 = 0.5 + h/2.001 * (y1 + 1);
      x2 = 0.5 + w/2.001 * (x2 + 1);
      y2 = 0.5 + h/2.001 * (y2 + 1);
      // NOTE: Notice the 2.001 fudge factor in the last two equations.
      // This is explained on page 142 of
      //    "Jim Blinn's Corner: A Trip Down The Graphics Pipeline"
      //     by Jim Blinn, 1996, Morgan Kaufmann Publishers.

      // Recall that this triangle's viewport coordinates are in the intervals
      //     x in (-0.5, vpWidth  - 0.5)
      //     y in (-0.5, vpHeight - 0.5)
      // We want to "sample" the triangle (in viewport coordinates)
      // at the pixel locations with integer coordinates between
      //     0 and vpWidth - 1
      //     0 and vpHeight - 1
      // Our first step is to find a "pixel bounding box" for the triangle.
      // Each vertex of the triangle is rounded to integer coordinates, and
      // then we find the left-most, right-most, highest, and lowest integer
      // coordinates that bound the triangle's vertices.

      // Find the greatest integer less than or equal to x0, x1, and x2.
      int xMin = (int)x0;
      if (x1 < xMin) xMin = (int)x1;
      if (x2 < xMin) xMin = (int)x2;
      if (xMin == -1) xMin = 0;

      // Find the greatest integer less than or equal to y0, y1, and y2.
      int yMin = (int)y0;
      if (y1 < yMin) yMin = (int)y1;
      if (y2 < yMin) yMin = (int)y2;
      if (yMin == -1) yMin = 0;

      // Find the least integer greater than or equal to x0, x1, and x2.
      int xMax = (int)Math.ceil(x0);
      if (x1 > xMax) xMax = (int)Math.ceil(x1);
      if (x2 > xMax) xMax = (int)Math.ceil(x2);
      if (xMax == vp.getWidthVP()) xMax--;

      // Find the least integer greater than or equal to y0, y1, and y2.
      int yMax = (int)Math.ceil(y0);
      if (y1 > yMax) yMax = (int)Math.ceil(y1);
      if (y2 > yMax) yMax = (int)Math.ceil(y2);
      if (yMax == vp.getHeightVP()) yMax--;

      if (Rasterize.debug)
      {
         logMessage(
            String.format("xMin = %d, xMax = %d, yMin = %d, yMax = %d\n",
                           xMin,      xMax,      yMin,      yMax));
      }

    //for (int y = yMin;   y <  yMax; ++y) // bottom to top
      for (int y = yMax-1; y >= yMin; --y) // top to bottom
      {
         for (int x = xMin; x < xMax; ++x) // left to right
         {
            //  f01(x,y) = (y0-y1)*(x) + (x1-x0)*(y) + x0*y1 - x1*y0
            //  f12(x,y) = (y1-y2)*(x) + (x2-x1)*(y) + x1*y2 - x2*y1
            //  f20(x,y) = (y2-y0)*(x) + (x0-x2)*(y) + x2*y0 - x0*y2

            //  alpha = f12(x,y) / f12(x0,y0)
            //  beta  = f20(x,y) / f20(x1,y1)
            //  gamma = f01(x,y) / f01(x2,y2)

            final double alpha = ( (y1-y2)*(x)  + (x2-x1)*(y)  + x1*y2 - x2*y1 )
                               / ( (y1-y2)*(x0) + (x2-x1)*(y0) + x1*y2 - x2*y1 );
            final double beta  = ( (y2-y0)*(x)  + (x0-x2)*(y)  + x2*y0 - x0*y2 )
                               / ( (y2-y0)*(x1) + (x0-x2)*(y1) + x2*y0 - x0*y2 );
            final double gamma = ( (y0-y1)*(x)  + (x1-x0)*(y)  + x0*y1 - x1*y0 )
                               / ( (y0-y1)*(x2) + (x1-x0)*(y2) + x0*y1 - x1*y0 );

          //if (Rasterize.debug) logMessage(String.format("alpha=%f,beta=%f,gamma=%f",alpha,beta,gamma));

            // Create a new fragment for the FrameBuffer's viewport.
            if (alpha >= 0 && beta >= 0 && gamma >= 0)
            {
               final int x_vp = x - 1;  // viewport coordinate
               final int y_vp = h - y;  // viewport coordinate

               // Interpolate z-coordinate from the triangle's vertices to this fragment.
               final double zCoord = (alpha * z0) + (beta * z1) + (gamma * z2);

               // Get the z-buffer value currently at this pixel of the viewport.
               final double zBuff = vp.getDepthVP(x_vp, y_vp);

               // If the current fragment is in front of the pixel in the framebuffer,
               // then overwrite the pixel in the framebuffer with this fragment.
               if (zCoord > zBuff)
               {
                  // Interpolate color data from the triangle's vertices to this fragment.
                  float r = (float)( (alpha * r0) + (beta * r1) + (gamma * r2) );
                  float g = (float)( (alpha * g0) + (beta * g1) + (gamma * g2) );
                  float b = (float)( (alpha * b0) + (beta * b1) + (gamma * b2) );

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

                  if (tri.textured)
                  {
                     final int tcIndex0 = tri.tcIndexList.get(0);
                     final int tcIndex1 = tri.tcIndexList.get(1);
                     final int tcIndex2 = tri.tcIndexList.get(2);

                     // Get the texture coordinates for the three vertices.
                     final double s0 = model.texCoordList.get( tcIndex0 ).s;
                     final double t0 = model.texCoordList.get( tcIndex0 ).t;
                     final double s1 = model.texCoordList.get( tcIndex1 ).s;
                     final double t1 = model.texCoordList.get( tcIndex1 ).t;
                     final double s2 = model.texCoordList.get( tcIndex2 ).s;
                     final double t2 = model.texCoordList.get( tcIndex2 ).t;

                     // Interpolate texture coordinates from the triangle's vertices to this fragment.
                     double s = alpha * s0 + beta * s1 + gamma * s2;
                     double t = alpha * t0 + beta * t1 + gamma * t2;

                     s = s - Math.floor(s);  // wrap the texture in both the
                     t = t - Math.floor(t);  // s and t coordinate directions
                                             // for both pos and neg texture coordinates

//                   s = s - (int)s;  // wrap the texture in both the
//                   t = t - (int)t;  // s and t coordinate directions

                     //System.err.printf("s = %f, t = %f\n", s, t);

                     // Get this triangle's texture.
                     final Texture tex = model.textureList.get(tri.textureIndex);

                     // Convert (interpolated) texture coordinates into pixel indices.
                     final int xTex = (int)(s * (tex.width - 1));   // column index
                     final int yTex = (int)(t * (tex.height - 1));  // row index

                     // Get color data from the texture.
                     final int index = (yTex * tex.width) + xTex;
                     final int color = tex.pixel_buffer[index];
                     final Color c = new Color(color);
                     final float rT = c.getRed() / 255.0f;
                     final float gT = c.getGreen() / 255.0f;
                     final float bT = c.getBlue() / 255.0f;
                     final float alphaT = tex.alpha_buffer[index] / 255.0f;

                     // Blend the texture's color data with the pixel's color data.
                     r = (1.0f - alphaT) * r + (alphaT * rT);
                     g = (1.0f - alphaT) * g + (alphaT * gT);
                     b = (1.0f - alphaT) * b + (alphaT * bT);
                  }

                  if (Rasterize.debug) logPixel((double)x, (double)y, x_vp, y_vp, r, g, b, vp);
                  // Log the pixel before setting it so that an array out-
                  // of-bounds error will be right after the pixel's address.

                  vp.setPixelVP(x_vp, y_vp, new Color(r, g, b));
                  vp.setDepthVP(x_vp, y_vp, zCoord);
               }
            }
         }
      }
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Rasterize_Triangle() {
      throw new AssertionError();
   }
}
