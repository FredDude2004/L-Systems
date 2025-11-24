/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.primitives.*;
import static renderer.pipeline.PipelineLogger.*;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**
   Clip in camera space any {@link LineSegment} that crosses the
   camera's near clipping plane {@code z = -near}. Interpolate
   {@link Vertex} color from any clipped off {@link Vertex} to
   the new {@link Vertex}.
<p>
   This clipping algorithm is a simplification of the Liang-Barsky
   Parametric Line Clipping algorithm.
*/
public final class NearClip_Line
{
   /**
      If the {@link LineSegment} crosses the camera's near plane,
      then return a clipped version that is contained in the far
      side of the near plane. The new, clipped {@link LineSegment}
      object is returned wrapped in a {@link List} object.
      <p>
      One new clipped {@link Vertex} object may be added to the
      {@link Model}'s vertex list and one new interpolated
      {@link Color} object may be added to the model's color list.
      <p>
      If the {@link LineSegment} is completely on the camera side
      of the near plane, then return an empty {@link List} object
      to indicate that the {@link LineSegment} should be discarded
      from the model's {@link Primitive} list.

      @param model   {@link Model} that the {@link LineSegment} {@code ls} comes from
      @param ls      {@link LineSegment} to be clipped
      @param camera  {@link Camera} that determines the near clipping plane
      @return a clipped version of {@code ls} wrapped in an {@link List} object
   */
   public static List<Primitive> clip(final Model model,
                                      final LineSegment ls,
                                      final Camera camera)
   {
      // Make local copies of several values.
      final double n = camera.n;

      final int vIndex0 = ls.vIndexList.get(0);
      final int vIndex1 = ls.vIndexList.get(1);
      final Vertex v0 = model.vertexList.get(vIndex0);
      final Vertex v1 = model.vertexList.get(vIndex1);

      final double z0 = v0.z;
      final double z1 = v1.z;

      // 1. Check for trivial accept.
      if ( z0 <= n && z1 <= n ) // on the far side of the near plane z = n
      {
         if (Clip.debug) logMessage("-- Near_Clip: Trivial accept.");
         return java.util.Arrays.asList(ls); // better than "return ls;"
      }
      // 2. Check for trivial delete.
      if ( z0 > n && z1 > n ) // on the near side of the near plane z = n
      {
         if (Clip.debug) logMessage("-- Near_Clip: Trivial delete.");
         return new ArrayList<Primitive>(); // better than "return null;"
      }
      // 3. Need to clip this line segment.
      else // crosses the near plane z = n
      {
         return java.util.Arrays.asList(interpolateNewVertex(model, ls, n));
      }
   }


   /**
      This method takes a line segment with one vertex on the near
      side of the near clipping plane (in front of clipping plane)
      and the other vertex on the far side of the near clipping plane
      (behind the clipping plane).
      <p>
      This method returns the line segment from the clipping plane to the
      vertex on the far side of the clipping plane.
      <p>
      This method solves for the value of {@code t} for which the z-component
      of the parametric equation
      <pre>{@code
                  p(t) = (1 - t) * v0 + t * v1
      }</pre>
      intersects the given clipping plane, {@code z = n}. The solved for
      value of {@code t} is then plugged into the x and y components of the
      parametric equation to get the coordinates of the intersection point.

      @param model  {@link Model} that the {@link LineSegment} {@code ls} comes from
      @param ls     the {@link LineSegment} being clipped
      @param n      the z-coordinate of the near clipping plane
      @return the index of the new interpolated {@link Vertex} object
   */
   private static LineSegment interpolateNewVertex(final Model model,
                                                   final LineSegment ls,
                                                   final double n)
   {
      // Make local copies of several values.
      final int vIndex0 = ls.vIndexList.get(0);
      final Vertex v0  = model.vertexList.get(vIndex0);
      final double v0x = v0.x;
      final double v0y = v0.y;
      final double v0z = v0.z;
      final int cIndex0 = ls.cIndexList.get(0);
      float c0[] = model.colorList.get(cIndex0).getRGBColorComponents(null);

      final int vIndex1 = ls.vIndexList.get(1);
      final Vertex v1  = model.vertexList.get(vIndex1);
      final double v1x = v1.x;
      final double v1y = v1.y;
      final double v1z = v1.z;
      final int cIndex1 = ls.cIndexList.get(1);
      float c1[] = model.colorList.get(cIndex1).getRGBColorComponents(null);

      // Interpolate between v1 and v0.
      final double t = (n - v1z) / (v0z - v1z);

      // Use the value of t to interpolate the coordinates of the new vertex.
      final double x = (1 - t) * v1x + t * v0x;
      final double y = (1 - t) * v1y + t * v0y;
      final double z = n;

      // Use the value of t to interpolate the color of the new vertex.
      final float t_ = (float)t;
      final float r = (1 - t_) * c1[0] + t_ * c0[0];
      final float g = (1 - t_) * c1[1] + t_ * c0[1];
      final float b = (1 - t_) * c1[2] + t_ * c0[2];

      // Modify the Model to contain the new Vertex.
      final Vertex newVertex = new Vertex(x, y, z);
      final int vIndexNew = model.vertexList.size();
      model.vertexList.add(newVertex);

      // Modify the Model to contain the new Color.
      final Color newColor = new Color(r, g, b);
      final int cIndexNew = model.colorList.size();
      model.colorList.add(newColor);

      // Which Vertex of ls is on the near side of the clipping plane?
      final int vNearIndex;
      if ( v0z > n ) // clip off v0
      {
         vNearIndex = 0;
      }
      else // clip off v1
      {
         vNearIndex = 1;
      }

      if (Clip.debug)
      {
         final String vClipped = (0==vNearIndex) ? "v0" : "v1";
         logMessage(String.format("-- Clip off %s at z=%.3f",
                                        vClipped, n));
         logMessage(String.format("-- t = %.25f", t));
         logMessage(String.format("-- <x0, y0, z0> = <% .8f, % .8f, % .8f",
                                       v0x, v0y, v0z));
         logMessage(String.format("-- <x1, y1, z1> = <% .8f, % .8f, % .8f",
                                       v1x, v1y, v1z));
         logMessage(String.format("-- <x,  y,  z>  = <% .8f, % .8f, % .8f",
                                       x,  y,  z));
         logMessage(String.format("-- <r0, g0, b0> = <%.8f, %.8f, %.8f>",
                                       c0[0], c0[1], c0[2]));
         logMessage(String.format("-- <r1, g1, b1> = <%.8f, %.8f, %.8f>",
                                       c1[0], c1[1], c1[2]));
         logMessage(String.format("-- <r,  g,  b>  = <%.8f, %.8f, %.8f>",
                                       r,  g,  b));
      }

      final LineSegment result;
      if (0 == vNearIndex)
      {
         result = new LineSegment(vIndexNew, vIndex1, cIndexNew, cIndex1);
      }
      else
      {
         result = new LineSegment(vIndex0, vIndexNew, cIndex0, cIndexNew);
      }
      return result;
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private NearClip_Line() {
      throw new AssertionError();
   }
}
