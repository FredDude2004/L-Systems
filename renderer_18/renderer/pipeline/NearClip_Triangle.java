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
   Clip in camera space any {@link Triangle} that that crosses
   the camera's near clipping plane {@code z = -near}. Interpolate
   {@link Vertex} color from any clipped off {@link Vertex} to
   the new {@link Vertex}.
*/
public class NearClip_Triangle
{
   /**
      If the {@link Triangle} crosses the camera's near plane,
      then return a clipped version that is contained in the far
      side of the near plane. When a triangle gets clipped, the
      result can be either one or two new triangles.  The new
      clipped {@link Triangle} objects are returned in a
      {@link List} object.
      <p>
      Two new clipped {@link Vertex} objects may be added to the
      {@link Model}'s vertex list and two new interpolated
      {@link Color} objects may be added to the model's color list.
      <p>
      If the {@link Triangle} is completely on the camera side
      of the near plane, then return an empty {@link List} object
      to indicate that the {@link Triangle} should be discarded
      from the model's {@link Primitive} list.

      @param model   {@link Model} that the {@link Triangle} {@code tri} comes from
      @param tri     {@link Triangle} to be clipped
      @param camera  {@link Camera} that determines the near clipping plane
      @return a clipped version of {@code tri} in a {@link List} object
   */
   public static List<Primitive> clip(final Model model,
                                      final Triangle tri,
                                      final Camera camera)
   {
      // Make local copies of several values.
      final double n = camera.n;

      final int index0 = tri.vIndexList.get(0);
      final int index1 = tri.vIndexList.get(1);
      final int index2 = tri.vIndexList.get(2);

      final Vertex v0 = model.vertexList.get( index0 );
      final Vertex v1 = model.vertexList.get( index1 );
      final Vertex v2 = model.vertexList.get( index2 );

      final double x0 = v0.x,  y0 = v0.y,  z0 = v0.z;
      final double x1 = v1.x,  y1 = v1.y,  z1 = v1.z;
      final double x2 = v2.x,  y2 = v2.y,  z2 = v2.z;

      // 1. Check for trivial accept.
    //if ( z0 <= n && z1 <= n && z2 <= n )
      if ( n >= z0 && n >= z1 && n >= z2 )
      {
         if (Clip.debug) logMessage("-- Trivial accept.");
         return java.util.Arrays.asList(tri);
      }
      // 2. Check for trivial delete.
      else if ( z0 > n && z1 > n && z2 > n )
      {
         if (Clip.debug) logMessage("-- Trivial delete.");
         return new ArrayList<Primitive>();
      }
      // 3. Need to clip this triangle.
      else
      {
         if (z0 > n && z1 <= n && z2 <= n)
         {// Create two new Triangles, neither containing previous v0.
            if (Clip.debug) System.err.println("-- Clip off v0.");
            return interpolateNewTriangles(model, tri, 0, 1, 2, n);
         }
         else if (z2 <= n && z0 > n && z1 > n)
         {// Create one new Triangle containing previous v2 and new v0, v1.
            if (Clip.debug) System.err.println("-- Clip off v0 & v1.");
            return interpolateNewTriangle(model, tri, 2, 0, 1, n);
         }
         else if (z1 > n && z0 <= n && z2 <= n)
         {// Create two new Triangles, neither containing previous v1.
            if (Clip.debug) System.err.println("-- Clip off v1.");
            return interpolateNewTriangles(model, tri, 1, 2, 0, n);
         }
         else if (z0 <= n && z1 > n && z2 > n)
         {// Create one new Triangle containing previous v0 and new v1, v2.
            if (Clip.debug) System.err.println("-- Clip off v1 & v2.");
            return interpolateNewTriangle(model, tri, 0, 1, 2, n);
         }
         else if (z2 > n && z0 <= n && z1 <= n)
         {// Create two new Triangles, neither containing previous v2.
            if (Clip.debug) System.err.println("-- Clip off v2.");
            return interpolateNewTriangles(model, tri, 2, 0, 1, n);
         }
         else // if (z1 <= n && z0 > n && z2 > n)
         {// Create one new Triangle containing previous v1 and new v0, v2.
            if (Clip.debug) System.err.println("-- Clip off v0 & v2.");
            return interpolateNewTriangle(model, tri, 1, 2, 0, n);
         }
      }
   }


   /**
      This method takes three indices for three vertices, one that is on
      the "right" side of the near plane and the other two that are on the
      "wrong" side of the near plane.
      <p>
      The three vertices should be given in a counterclockwise ordering around the triangle.
      <p>
      This method returns a clipped {@link Triangle} with the index index0_keep
      and the indices for two new interpolated vertices, both of which have been
      put into the {@link Model}'s vertex list. Two new {@link Color} objects will
      also have been added to the {@link Model}'s color list.

      @param model        {@link Model} object that holds the {@link Vertex} {@link List}
      @param tri          the {@link Triangle} being clipped
      @param index0_keep  the index in {@code tri} of the {@link Vertex} that is being kept
      @param index1_clip  the index in {@code tri} of a {@link Vertex} that needs to be clipped off
      @param index2_clip  the index in {@code tri} of a {@link Vertex} that needs to be clipped off
      @param n            the location of the near clipping plane, z = n
      @return {@link List} containing a new clipped {@link Triangle} object
   */
   private static List<Primitive> interpolateNewTriangle(
                                       final Model model,
                                       final Triangle tri,
                                       final int index0_keep,
                                       final int index1_clip,
                                       final int index2_clip,
                                       final double n)
   {
      if (Clip.debug) logMessage("-- Create one new triangle.\n");

      // Interpolate a new vertex between index0_keep and index1_clip.
      // The new vertex, color, and texture objects are at the end of
      // the model's vertex, color, and texture lists.
      interpolateNewVertex(model,
                           tri,
                           index0_keep,
                           index1_clip,
                           n);

      // Interpolate a new vertex between index0_keep and index2_clip.
      // The new vertex, color, and texture objects are at the end of
      // the model's vertex, color, and texture lists.
      interpolateNewVertex(model,
                           tri,
                           index0_keep,
                           index2_clip,
                           n);

      // Create a new Triangle primitive using the new vertex.
      // Make sure it has the same orientation as the original triangle.
      // (We cannot reuse the original Triangle object since we don't
      //  know which vertex is in which location of the triangle.)
      final int vIndex1_new = model.vertexList.size() - 2;
      final int cIndex1_new = model.colorList.size() - 2;
      final int vIndex2_new = model.vertexList.size() - 1;
      final int cIndex2_new = model.colorList.size() - 1;

      final Triangle tri_new;
      if (! tri.textured)
      {
         tri_new = new Triangle(tri.vIndexList.get(index0_keep),
                                vIndex1_new,
                                vIndex2_new,
                                tri.cIndexList.get(index0_keep),
                                cIndex1_new,
                                cIndex2_new);
      }
      else // a textured Triangle
      {
         final int tcIndex1_new = model.texCoordList.size() - 2;
         final int tcIndex2_new = model.texCoordList.size() - 1;
         tri_new = new Triangle(tri.vIndexList.get(index0_keep),
                                vIndex1_new,
                                vIndex2_new,
                                tri.cIndexList.get(index0_keep),
                                cIndex1_new,
                                cIndex2_new,
                                tri.cIndexList2.get(0),
                                tri.cIndexList2.get(1),
                                tri.cIndexList2.get(2),
                                tri.tcIndexList.get(index0_keep),
                                tcIndex1_new,
                                tcIndex2_new,
                                tri.textureIndex);
      }

      //if (Clip.debug) logMessage( tri_new.toString() );
      return java.util.Arrays.asList(tri_new);
   }//interpolateNewTriangle()


   /**
      This method takes three indices for three vertices, two that are on
      the "right" side of the near plane and one that is on the "wrong"
      side of the near plane.
      <p>
      The three vertices should be given in a counterclockwise ordering around the triangle.
      <p>
      This method returns two new {@link Triangle} objects, one from the
      two inside vertices to a new interpolated vertex, and the other new
      triangle from the two new interpolated vertices to one of the original
      inside vertices. Two new {@link Vertex} objects and two new {@link Color}
      objects will have also been added to the {@link Model}'s vertex and color
      lists.

      @param model        {@link Model} object that holds the {@link Vertex} {@link List}
      @param tri          the {@link Triangle} being clipped
      @param index0_clip  the index in {@code tri} of the {@link Vertex} that needs to be clipped off
      @param index1_keep  the index in {@code tri} of a {@link Vertex} that is inside the view rectangle
      @param index2_keep  the index in {@code tri} of a {@link Vertex} that is inside the view rectangle
      @param n      the location of the near clipping plane, z = n
      @return {@link List} containing two new clipped {@link Triangle} objects
   */
   private static List<Primitive> interpolateNewTriangles(
                                       final Model model,
                                       final Triangle tri,
                                       final int index0_clip,
                                       final int index1_keep,
                                       final int index2_keep,
                                       final double n)
   {
      if (Clip.debug) logMessage("-- Create two new triangles.\n");

      // Interpolate a new vertex between v1_keep and v0_clip.
      // The new vertex, color, and texture coordinate objects are at the
      // end of the model's vertex, color, and texture coordinate lists.
      interpolateNewVertex(model,
                           tri,
                           index1_keep,
                           index0_clip,
                           n);

      // Interpolate a new vertex between v2_keep and v0_clip.
      // The new vertex, color, and texture coordinate objects are at the
      // end of the model's vertex, color, and texture coordinate lists.
      interpolateNewVertex(model,
                           tri,
                           index2_keep,
                           index0_clip,
                           n);

      final int vIndex0_new1 = model.vertexList.size() - 2;
      final int cIndex0_new1 = model.colorList.size() - 2;
      final int vIndex0_new2 = model.vertexList.size() - 1;
      final int cIndex0_new2 = model.colorList.size() - 1;

      // Create a new Triangle primitive using the first new vertex.
      // Make sure it has the same orientation as the original triangle.
      // (We cannot reuse the original Triangle object since we don't
      //  know which vertex is in which location of the triangle.)
      final Triangle tri_1;
      if (! tri.textured)
      {
         tri_1 = new Triangle(vIndex0_new1,
                             tri.vIndexList.get(index1_keep),
                             tri.vIndexList.get(index2_keep),
                             cIndex0_new1,
                             tri.cIndexList.get(index1_keep),
                             tri.cIndexList.get(index2_keep));
      }
      else // a textured Triangle
      {
         final int tcIndex0_new1 = model.texCoordList.size() - 2;
         tri_1 = new Triangle(vIndex0_new1,
                              tri.vIndexList.get(index1_keep),
                              tri.vIndexList.get(index2_keep),
                              cIndex0_new1,
                              tri.cIndexList.get(index1_keep),
                              tri.cIndexList.get(index2_keep),
                              tri.cIndexList2.get(0),
                              tri.cIndexList2.get(1),
                              tri.cIndexList2.get(2),
                              tcIndex0_new1,
                              tri.tcIndexList.get(index1_keep),
                              tri.tcIndexList.get(index2_keep),
                              tri.textureIndex);
      }

      // Create a new Triangle primitive using the two new vertices.
      // Make sure it has the same orientation as the original triangle.
      final Triangle tri_2;
      if (! tri.textured)
      {
         tri_2 = new Triangle(vIndex0_new2,
                              vIndex0_new1,
                              tri.vIndexList.get(index2_keep),
                              cIndex0_new2,
                              cIndex0_new1,
                              tri.cIndexList.get(index2_keep));
      }
      else // a textured Triangle
      {
         final int tcIndex0_new1 = model.texCoordList.size() - 2;
         final int tcIndex0_new2 = model.texCoordList.size() - 1;
         tri_2 = new Triangle(vIndex0_new2,
                              vIndex0_new1,
                              tri.vIndexList.get(index2_keep),
                              cIndex0_new2,
                              cIndex0_new1,
                              tri.cIndexList.get(index2_keep),
                              tri.cIndexList2.get(0),
                              tri.cIndexList2.get(1),
                              tri.cIndexList2.get(2),
                              tcIndex0_new2,
                              tcIndex0_new1,
                              tri.tcIndexList.get(index2_keep),
                              tri.textureIndex);
      }

      //if (Clip.debug) logMessage( tri_1.toString() );
      //if (Clip.debug) logMessage( tri_2.toString() );
      return java.util.Arrays.asList(tri_1, tri_2);
   }//interpolateNewTriangles()


   /**
      This method takes in two vertices, one that is on the "right" side
      of the near plane and the other that is on the "wrong" side of the
      near plane. This method adds to the {@link Model}'s vertex list the
      vertex that is the intersection point between the given line segment
      and the near plane.
      <p>
      This method solves for the value of {@code t} for which the
      parametric equation
      <pre>{@code
                  p(t) = (1-t) * v_clip + t * v_keep
      }</pre>
      intersects the near plane. (Notice that the equation is parameterized
      so that we move from the clipped vertex towards the kept vertex as
      {@code t} increases from 0 to 1.) The solved for value of {@code t}
      is then plugged into the parametric formula to get the coordinates
      of the intersection point. The solved for value of {@code t} is also
      used to interpolate a new {@link Color} object for the new vertex,
      and the new {@link Color} object is added to the end of the
      {@link Model}'s color list.

      @param model  {@link Model} object that holds the vertex list
      @param tri    the {@link Triangle} being clipped
      @param keep   the index in {@code tri} of the {@link Vertex} that is being kept
      @param clip   the index in {@code tri} of the {@link Vertex} that is being clipped
      @param n      the location of the near clipping plane, z = n
   */
   private static void interpolateNewVertex(final Model model,
                                            final Triangle tri,
                                            final int keep,
                                            final int clip,
                                            final double n)
   {
      if (Clip.debug) logMessage("-- Create new vertex.");

      // Make local copies of several values.
      final Vertex v_keep = model.vertexList.get( tri.vIndexList.get(keep) );
      final Vertex v_clip = model.vertexList.get( tri.vIndexList.get(clip) );
      final Color  c_keep = model.colorList.get(  tri.cIndexList.get(keep) );
      final Color  c_clip = model.colorList.get(  tri.cIndexList.get(clip) );

      final double vKx = v_keep.x;  // "K" for "keep"
      final double vKy = v_keep.y;
      final double vKz = v_keep.z;
      final float cK[] = c_keep.getRGBColorComponents(null);
      final double vCx = v_clip.x;  // "C" for "clip"
      final double vCy = v_clip.y;
      final double vCz = v_clip.z;
      final float cC[] = c_clip.getRGBColorComponents(null);

      // Interpolate between v_clip and v_keep.
      final double t = (n - vCz) / (vKz - vCz); // clip to z = n

      // Use the value of t to interpolate the coordinates of the new vertex.
    //final double x = (1-t) * vCx + t * vKx;
    //final double y = (1-t) * vCy + t * vKy;
      final double x = vCx + t * (vKx - vCx);
      final double y = vCy + t * (vKy - vCy);
      final double z = n;

      final Vertex v_new = new Vertex(x, y, z);

      // Use the value of t to interpolate the color of the new vertex.
      final float t_ = (float)t;
    //final float r = (1-t_) * cC[0] + t_ * cK[0];
    //final float g = (1-t_) * cC[1] + t_ * cK[1];
    //final float b = (1-t_) * cC[2] + t_ * cK[2];
      final float r = cC[0] + t_ * (cK[0] - cC[0]);
      final float g = cC[1] + t_ * (cK[1] - cC[1]);
      final float b = cC[2] + t_ * (cK[2] - cC[2]);

      final Color c_new = new Color(r, g, b);

      // Modify the Model to contain the new Vertex and Color.
      model.vertexList.add(v_new);
      model.colorList.add(c_new);

      // Use the value of t to interpolate the texture coordinates of the new vertex.
      if (tri.textured)
      {
         final TexCoord tc_keep = model.texCoordList.get( tri.tcIndexList.get(keep) );
         final TexCoord tc_clip = model.texCoordList.get( tri.tcIndexList.get(clip) );

       //final double sTex = (1-t) * tc_clip.s + t * tc_keep.s;
       //final double tTex = (1-t) * tc_clip.t + t * tc_keep.t;
         final double sTex = tc_clip.s + t * (tc_keep.s - tc_clip.s);
         final double tTex = tc_clip.t + t * (tc_keep.t - tc_clip.t);
         // Modify the Model to contain the new TexCoord.
         model.texCoordList.add(new TexCoord(sTex, tTex));
      }


      if (Clip.debug)
      {
         logMessage(String.format("-- t = % .25f", t));
         logMessage(String.format("-- <x_C,y_C> = <% .24f % .24f", vCx, vCy));
         logMessage(String.format("-- <x_K,y_K> = <% .24f % .24f", vKx, vKy));
         logMessage(String.format("-- <x,  y>   = <% .24f % .24f",   x,   y));
         logMessage(String.format("-- <r_C,g_C,b_C> = <% .15f  % .15f  % .15f>",
                                       cC[0], cC[1], cC[2]));
         logMessage(String.format("-- <r_K,g_K,b_K> = <% .15f  % .15f  % .15f>",
                                       cK[0], cK[1], cK[2]));
         logMessage(String.format("-- <r,  g,  b>   = <% .15f  % .15f  % .15f>",
                                       r,  g,  b));
      }
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private NearClip_Triangle() {
      throw new AssertionError();
   }
}