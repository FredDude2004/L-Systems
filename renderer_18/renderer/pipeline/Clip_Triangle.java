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
   Clip a (projected) {@link Triangle} that sticks out of
   the view rectangle in the image plane.  Interpolate
   {@link Vertex} color from any clipped off {@code Vertex}
   to a new {@code Vertex}.
*/
public class Clip_Triangle
{
   /**
      If the {@link Triangle} sticks out of the view rectangle,
      then clip it so that it is contained in the view rectangle.
      <p>
      When we clip a triangle, we may end up with as many as ten
      new sub-triangles. The new, clipped, sub-triangles are
      gathered into the return {@link List}.

      @param model  {@link Model} that the {@link Triangle} {@code t} comes from
      @param t      {@link Triangle} to be clipped
      @return a {@link List} that is either empty or contains clipped {@code Triangle}s
   */
   public static List<Primitive> clip(final Model model,
                                      final Triangle t)
   {
      // Create an empty Primitive list to hold
      // the clipped Triangle objects.
      final List<Primitive> doneList = new ArrayList<>();

      // Create an empty Triangle list to hold Triangle
      // objects that still need clipping.
      final List<Triangle> toDoList = new ArrayList<>();

      toDoList.add(t);

      while ( ! toDoList.isEmpty() )
      {
         final Triangle t2 = toDoList.remove(0);

         // Make local copies of several values.
         final int index0 = t2.vIndexList.get(0);
         final int index1 = t2.vIndexList.get(1);
         final int index2 = t2.vIndexList.get(2);

         final Vertex v0 = model.vertexList.get( index0 );
         final Vertex v1 = model.vertexList.get( index1 );
         final Vertex v2 = model.vertexList.get( index2 );

         final double x0 = v0.x,  y0 = v0.y;
         final double x1 = v1.x,  y1 = v1.y;
         final double x2 = v2.x,  y2 = v2.y;

         // 1. Check for trivial accept.
         if ( ! ( Math.abs( x0 ) > 1
               || Math.abs( y0 ) > 1
               || Math.abs( x1 ) > 1
               || Math.abs( y1 ) > 1
               || Math.abs( x2 ) > 1
               || Math.abs( y2 ) > 1 ) )
         {
            // This triangle doesn't need clipping, so put it in the done list.
            if (Clip.debug) logMessage("-- Trivial accept.");
            doneList.add(t2);
         }
         // 2. Check for trivial delete.
         else if ( (x0 >  1 && x1 >  1 && x2 >  1)  // right of the line x=1
                || (x0 < -1 && x1 < -1 && x2 < -1)  // left of the line x=-1
                || (y0 >  1 && y1 >  1 && y2 >  1)  // above the line y=1
                || (y0 < -1 && y1 < -1 && y2 < -1) )// below the line y=-1
         {
            if (Clip.debug) logMessage("-- Trivial delete.");
         }
         // 3. Need to clip this triangle.
         else if (x0 > 1 || x1 > 1 || x2 > 1) // crosses the line x = 1
         {
            // Clip to the  line x = +1.
            //if (Clip.debug) System.err.println("-- Clip to x = +1");
            if (x0 > 1 && x1 > 1)
            {// Create one new Triangle containing previous v2 and new v0, v1.
               if (Clip.debug) logMessage("-- Clip off v0 & v1 at x = 1.");
               interpolateNewTriangle(model, t2, 2, 0, 1, toDoList, 1);
            }
            else if (x0 > 1 && x2 > 1)
            {// Create one new Triangle containing previous v1 and new v0, v2.
               if (Clip.debug) logMessage("-- Clip off v0 & v2 at x = 1.");
               interpolateNewTriangle(model, t2, 1, 2, 0, toDoList, 1);
            }
            else if (x1 > 1 && x2 > 1)
            {// Create one new Triangle containing previous v0 and new v1, v2.
               if (Clip.debug) logMessage("-- Clip off v1 & v2 at x = 1.");
               interpolateNewTriangle(model, t2, 0, 1, 2, toDoList, 1);
            }
            else if (x0 > 1)
            {// Create two new Triangles, neither containing previous v0.
               if (Clip.debug) logMessage("-- Clip off v0 at x = 1.");
               interpolateNewTriangles(model, t2, 0, 1, 2, toDoList, 1);
            }
            else if (x1 > 1)
            {// Create two new Triangles, neither containing previous v1.
               if (Clip.debug) logMessage("-- Clip off v1 at x = 1.");
               interpolateNewTriangles(model, t2, 1, 2, 0, toDoList, 1);
            }
            else if (x2 > 1)
            {// Create two new Triangles, neither containing previous v2.
               if (Clip.debug) logMessage("-- Clip off v2 at x = 1.");
               interpolateNewTriangles(model, t2, 2, 0, 1, toDoList, 1);
            }
            else  // We should never get here.
            {
               System.err.println("Clipping to x = +1 dropped a triangle!");
               Thread.dumpStack();
               System.exit(-1);
            }
         }
         else if (x0 < -1 || x1 < -1 || x2 < -1) // crosses the line x = -1
         {
            // Clip to the line x = -1.
            //if (Clip.debug) System.err.println("-- Clip to x = -1");
            if (x0 < -1 && x1 < -1)
            {// Create one new Triangle containing previous v2 and new v0, v1.
               if (Clip.debug) logMessage("-- Clip off v0 & v1 at x = -1.");
               interpolateNewTriangle(model, t2, 2, 0, 1, toDoList, 2);
            }
            else if (x0 < -1 && x2 < -1)
            {// Create one new Triangle containing previous v1 and new v0, v2.
               if (Clip.debug) logMessage("-- Clip off v0 & v2 at x = -1.");
               interpolateNewTriangle(model, t2, 1, 2, 0, toDoList, 2);
            }
            else if (x1 < -1 && x2 < -1)
            {// Create one new Triangle containing previous v0 and new v1, v2.
               if (Clip.debug) logMessage("-- Clip off v1 & v2 at x = -1.");
               interpolateNewTriangle(model, t2, 0, 1, 2, toDoList, 2);
            }
            else if (x0 < -1)
            {// Create two new Triangles, neither containing previous v0.
               if (Clip.debug) logMessage("-- Clip off v0 at x = -1.");
               interpolateNewTriangles(model, t2, 0, 1, 2, toDoList, 2);
            }
            else if (x1 < -1)
            {// Create two new Triangles, neither containing previous v1.
               if (Clip.debug) logMessage("-- Clip off v1 at x = -1.");
               interpolateNewTriangles(model, t2, 1, 2, 0, toDoList, 2);
            }
            else if (x2 < -1)
            {// Create two new Triangles, neither containing previous v2.
               if (Clip.debug) logMessage("-- Clip off v2 at x = -1.");
               interpolateNewTriangles(model, t2, 2, 0, 1, toDoList, 2);
            }
            else  // We should never get here.
            {
               System.err.println("Clipping to x = -1 dropped a triangle!");
               Thread.dumpStack();
               System.exit(-1);
            }
         }
         else if (y0 > 1 || y1 > 1 || y2 > 1) // crosses the line y = 1
         {
            // Clip to the line y = +1.
            //if (debug) System.err.println("-- Clip to y = +1");
            if (y0 > 1 && y1 > 1)
            {// create one new Triangle containing previous v2 and new v0, v1.
               if (Clip.debug) logMessage("-- Clip off v0 & v1 at y = +1.");
               interpolateNewTriangle(model, t2, 2, 0, 1, toDoList, 3);
            }
            else if (y0 > 1 && y2 > 1)
            {// Create one new Triangle containing previous v1 and new v0, v2.
               if (Clip.debug) logMessage("-- Clip off v0 & v2 at y = +1.");
               interpolateNewTriangle(model, t2, 1, 2, 0, toDoList, 3);
            }
            else if (y1 > 1 && y2 > 1)
            {// Create one new Triangle containing previous v0 and new v1, v2.
               if (Clip.debug) logMessage("-- Clip off v1 & v2 at y = +1.");
               interpolateNewTriangle(model, t2, 0, 1, 2, toDoList, 3);
            }
            else if (y0 > 1)
            {// Create two new Triangles, neither containing previous v0.
               if (Clip.debug) logMessage("-- Clip off v0 at y = +1.");
               interpolateNewTriangles(model, t2, 0, 1, 2, toDoList, 3);
            }
            else if (y1 > 1)
            {// Create two new Triangles, neither containing previous v1.
               if (Clip.debug) logMessage("-- Clip off v1 at y = +1.");
               interpolateNewTriangles(model, t2, 1, 2, 0, toDoList, 3);
            }
            else if (y2 > 1)
            {// Create two new Triangles, neither containing previous v2.
               if (Clip.debug) logMessage("-- Clip off v2 at y = +1.");
               interpolateNewTriangles(model, t2, 2, 0, 1, toDoList, 3);
            }
            else  // We should never get here.
            {
               System.err.println("Clipping to y = +1 dropped a triangle!");
               Thread.dumpStack();
               System.exit(-1);
            }
         }
         else if (y0 < -1 || y1 < -1 || y2 < -1) // crosses the line y = -1
         {
            // Clip to the line y = -1.
            //if (Clip.debug) System.err.println("-- Clip to y = -1");
            if (y0 < -1 && y1 < -1)
            {// Create one new Triangle containing previous v2 and new v0, v1.
               if (Clip.debug) logMessage("-- Clip off v0 & v1 at y = -1.");
               interpolateNewTriangle(model, t2, 2, 0, 1, toDoList, 4);
            }
            else if (y0 < -1 && y2 < -1)
            {// Create one new Triangle containing previous v1 and new v0, v2.
               if (Clip.debug) logMessage("-- Clip off v0 & v2 at y = -1.");
               interpolateNewTriangle(model, t2, 1, 2, 0, toDoList, 4);
            }
            else if (y1 < -1 && y2 < -1)
            {// Create one new Triangle containing previous v0 and new v1, v2.
               if (Clip.debug) logMessage("-- Clip off v1 & v2 at y = -1.");
               interpolateNewTriangle(model, t2, 0, 1, 2, toDoList, 4);
            }
            else if (y0 < -1)
            {// Create two new Triangles, neither containing previous v0.
               if (Clip.debug) logMessage("-- Clip off v0 at y = -1.");
               interpolateNewTriangles(model, t2, 0, 1, 2, toDoList, 4);
            }
            else if (y1 < -1)
            {// Create two new Triangles, neither containing previous v1.
               if (Clip.debug) logMessage("-- Clip off v1 at y = -1.");
               interpolateNewTriangles(model, t2, 1, 2, 0, toDoList, 4);
            }
            else if (y2 < -1)
            {// Create two new Triangles, neither containing previous v2.
               if (Clip.debug) logMessage("-- Clip off v2 at y = -1.");
               interpolateNewTriangles(model, t2, 2, 0, 1, toDoList, 4);
            }
            else  // We should never get here.
            {
               System.err.println("Clipping to y = -1 dropped a triangle!");
               Thread.dumpStack();
               System.exit(-1);
            }
         }
         else // We should never get here.
         {
            System.err.println("Triangle Clipping Error!");
            Thread.dumpStack();
          //System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()));
            System.exit(-1);
         }
      }
      return doneList;
   }


   /**
      This method takes three indices for three vertices, one that is on
      the "right" side of a clipping line and the other two that are on the
      "wrong" side of the clipping line, a "todo" {@link List} into which
      a new clipped {@link Triangle} should be put (since it may need to
      be clipped again), and an integer which specifies which clipping line
      to use when calling {@link #interpolateNewVertex}.
      <p>
      The three vertices should be given in a counterclockwise ordering around the triangle.
      <p>
      When this method returns, it will have placed in {@code triangleList}
      a clipped {@link Triangle} with the index index0_inside and the indices
      for two new interpolated vertices, both of which have been put into
      the {@link Model}'s vertex list.

      @param model           {@link Model} object that holds the vertex list
      @param tri             the {@link Triangle} being clipped
      @param index0_inside   the index in {@code tri} of the {@link Vertex} that is inside the view rectangle
      @param index1_outside  the index in {@code tri} of a {@link Vertex} that needs to be clipped off
      @param index2_outside  the index in {@code tri} of a {@link Vertex} that needs to be clipped off
      @param triangleList    {@link List} to hold new {@link Triangle} object that replaces the clipped {@link Triangle}
      @param eqn_number      the identifier of the line crossed by the {@link Triangle}
   */
   private static void interpolateNewTriangle(final Model model,
                                              final Triangle tri,
                                              final int index0_inside,
                                              final int index1_outside,
                                              final int index2_outside,
                                              final List<Triangle> triangleList,
                                              final int eqn_number)
   {
      if (Clip.debug) logMessage("-- Create one new triangle.");

      // Interpolate a new vertex between index0_inside and index1_outside.
      // The new vertex, color, and texture coordinate objects are at the
      // end of the model's vertex, color, and texture coordinate lists.
      interpolateNewVertex(model,
                           tri,
                           index0_inside,
                           index1_outside,
                           eqn_number);

      // Interpolate a new vertex between index0_inside and index2_outside.
      // The new vertex, color, and texture coordinate objects are at the
      // end of the model's vertex, color, and texture coordinate lists.
      interpolateNewVertex(model,
                           tri,
                           index0_inside,
                           index2_outside,
                           eqn_number);

      // Create a new Triangle primitive using the new vertex.
      // Make sure it has the same orientation as the original triangle.
      // (We cannot reuse the original Triangle object since we don't
      //  know which vertex is in which location of the triangle.)
      final int vIndex1_new  = model.vertexList.size() - 2;
      final int cIndex1_new  = model.colorList.size() - 2;
      final int vIndex2_new  = model.vertexList.size() - 1;
      final int cIndex2_new  = model.colorList.size() - 1;

      final Triangle tri_new;
      if (! tri.textured)
      {
         tri_new = new Triangle(tri.vIndexList.get(index0_inside),
                                vIndex1_new,
                                vIndex2_new,
                                tri.cIndexList.get(index0_inside),
                                cIndex1_new,
                                cIndex2_new,
                                tri.cIndexList2.get(0),
                                tri.cIndexList2.get(1),
                                tri.cIndexList2.get(2));
      }
      else // a textured Triangle
      {
         final int tcIndex1_new = model.texCoordList.size() - 2;
         final int tcIndex2_new = model.texCoordList.size() - 1;
         tri_new = new Triangle(tri.vIndexList.get(index0_inside),
                                vIndex1_new,
                                vIndex2_new,
                                tri.cIndexList.get(index0_inside),
                                cIndex1_new,
                                cIndex2_new,
                                tri.cIndexList2.get(0),
                                tri.cIndexList2.get(1),
                                tri.cIndexList2.get(2),
                                tri.tcIndexList.get(index0_inside),
                                tcIndex1_new,
                                tcIndex2_new,
                                tri.textureIndex);
      }

      // Add the Triangle to the end of the todo list
      // since the Triangle may need to be clipped again.
      triangleList.add(tri_new);

      //if (Clip.debug) logMessage( tri_new.toString() );
   }//interpolateNewTriangle()


   /**
      This method takes three indices for three vertices, two that are on
      the "right" side of a clipping line and one that is on the "wrong"
      side of the clipping line, a "todo" {@link List} into which two new
      clipped {@link Triangle} objects should be put (since they may need
      to be clipped again), and an integer which specifies which clipping
      line to use when calling {@link #interpolateNewVertex}.
      <p>
      The three vertices should be given in a counterclockwise ordering around the triangle.
      <p>
      When this method returns, it will have placed in {@code triangleList}
      two new {@link Triangle} objects, one from the two inside vertices to
      a new interpolated vertex, and the other triangle from the two new
      interpolated vertices to one of the original inside vertices. Two new
      {@link Vertex} objects will have also been added to the {@link Model}'s
      vertex list.

      @param model           {@link Model} object that holds the vertex list
      @param tri             the {@link Triangle} being clipped
      @param index0_outside  the index in {@code tri} of the {@link Vertex} that needs to be clipped off
      @param index1_inside   the index in {@code tri} of a {@link Vertex} that is inside the view rectangle
      @param index2_inside   the index in {@code tri} of a {@link Vertex} that is inside the view rectangle
      @param triangleList    {@link List} to hold two new {@link Triangle} objects that replace the clipped {@link Triangle}
      @param eqn_number      the identifier of the line crossed by the triangle
   */
   private static void interpolateNewTriangles(final Model model,
                                               final Triangle tri,
                                               final int index0_outside,
                                               final int index1_inside,
                                               final int index2_inside,
                                               final List<Triangle> triangleList,
                                               final int eqn_number)
   {
      if (Clip.debug) logMessage("-- Create two new triangles.");

      // Interpolate a new vertex between v1_inside and v0_outside.
      // The new vertex, color, and texture coordinate objects are at the
      // end of the model's vertex, color, and texture coordinate lists.
      interpolateNewVertex(model,
                           tri,
                           index1_inside,
                           index0_outside,
                           eqn_number);

      // Interpolate a new vertex between v2_inside and v0_outside.
      // The new vertex, color, and texture coordinate objects are at the
      // end of the model's vertex, color, and texture coordinate lists.
      interpolateNewVertex(model,
                           tri,
                           index2_inside,
                           index0_outside,
                           eqn_number);

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
                              tri.vIndexList.get(index1_inside),
                              tri.vIndexList.get(index2_inside),
                              cIndex0_new1,
                              tri.cIndexList.get(index1_inside),
                              tri.cIndexList.get(index2_inside),
                              tri.cIndexList2.get(0),
                              tri.cIndexList2.get(1),
                              tri.cIndexList2.get(2));
      }
      else // a textured Triangle
      {
         final int tcIndex0_new1 = model.texCoordList.size() - 2;
         tri_1 = new Triangle(vIndex0_new1,
                              tri.vIndexList.get(index1_inside),
                              tri.vIndexList.get(index2_inside),
                              cIndex0_new1,
                              tri.cIndexList.get(index1_inside),
                              tri.cIndexList.get(index2_inside),
                              tri.cIndexList2.get(0),
                              tri.cIndexList2.get(1),
                              tri.cIndexList2.get(2),
                              tcIndex0_new1,
                              tri.tcIndexList.get(index1_inside),
                              tri.tcIndexList.get(index2_inside),
                              tri.textureIndex);
      }

      // Create a new Triangle primitive using the two new vertices.
      final Triangle tri_2;
      if (! tri.textured)
      {
         tri_2 = new Triangle(vIndex0_new2,
                              vIndex0_new1,
                              tri.vIndexList.get(index2_inside),
                              cIndex0_new2,
                              cIndex0_new1,
                              tri.cIndexList.get(index2_inside),
                              tri.cIndexList2.get(0),
                              tri.cIndexList2.get(1),
                              tri.cIndexList2.get(2));
      }
      else // a textured Triangle
      {
         final int tcIndex0_new1 = model.texCoordList.size() - 2;
         final int tcIndex0_new2 = model.texCoordList.size() - 1;
         tri_2 = new Triangle(vIndex0_new2,
                              vIndex0_new1,
                              tri.vIndexList.get(index2_inside),
                              cIndex0_new2,
                              cIndex0_new1,
                              tri.cIndexList.get(index2_inside),
                              tri.cIndexList2.get(0),
                              tri.cIndexList2.get(1),
                              tri.cIndexList2.get(2),
                              tcIndex0_new2,
                              tcIndex0_new1,
                              tri.tcIndexList.get(index2_inside),
                              tri.textureIndex);
      }

      // Add the two Triangles to the end of the todo list
      // since the Triangles may need to be clipped again.
      triangleList.add(tri_1);
      triangleList.add(tri_2);

      //if (Clip.debug) logMessage( tri_1.toString() );
      //if (Clip.debug) logMessage( tri_2.toString() );
   }//interpolateNewTriangles()


   /**
      This method takes in two vertices, one that is on the "right" side
      of a clipping line and the other that is on the "wrong" side of the
      clipping line, and an integer which specifies which clipping line
      to use, where
      <pre>{@code
         eqn_number == 1 means clipping line x =  1
         eqn_number == 2 means clipping line x = -1
         eqn_number == 3 means clipping line y =  1
         eqn_number == 4 means clipping line y = -1
      }</pre>
      This method adds to the {@link Model}'s vertex list the vertex
      that is the intersection point between the given line segment
      and the given clipping line.
      <p>
      This method solves for the value of {@code t} for which the
      parametric equation
      <pre>{@code
                  p(t) = (1-t) * v_outside + t * v_inside
      }</pre>
      intersects the given clipping line. (Notice that the equation
      is parameterized so that we move from the outside vertex towards
      the inside vertex as {@code t} increases from 0 to 1.) The solved
      for value of {@code t} is then plugged into the parametric formula
      to get the coordinates of the intersection point. The solved for
      value of {@code t} is also used to interpolate a new {@link Color}
      object for the new vertex, and the new {@link Color} object is
      added to the end of the {@link Model}'s color list.

      @param model     {@link Model} object that holds the vertex list
      @param tri       the {@link Triangle} being clipped
      @param inside    the index in {@code tri} of the {@link Vertex} that is inside the view rectangle
      @param outside   the index in {@code tri} of the {@link Vertex} that is outside the view rectangle
      @param eqn_number  the identifier of the view rectangle edge crossed by the line segment
   */
   private static void interpolateNewVertex(final Model model,
                                            final Triangle tri,
                                            final int inside,
                                            final int outside,
                                            final int eqn_number)
   {
      if (Clip.debug) logMessage("-- Create new vertex.");

      // Make local copies of several values.
      final Vertex v_inside  = model.vertexList.get( tri.vIndexList.get(inside) );
      final Vertex v_outside = model.vertexList.get( tri.vIndexList.get(outside) );
      final Color  c_inside  = model.colorList.get( tri.cIndexList.get(inside) );
      final Color  c_outside = model.colorList.get( tri.cIndexList.get(outside) );

      final double vix = v_inside.x; // "i" for "inside"
      final double viy = v_inside.y;
      final double viz = v_inside.z;
      final float ci[] = c_inside.getRGBColorComponents(null);
      final double vox = v_outside.x; // "o" for "outside"
      final double voy = v_outside.y;
      final double voz = v_outside.z;
      final float co[] = c_outside.getRGBColorComponents(null);

      // Interpolate between v_outside and v_inside.
      double t = 0.0;
      if (1 == eqn_number)            // clip to x = 1
         t = (1 - vox) / (vix - vox);
      else if (2 == eqn_number)       // clip to x = -1
         t = (-1 - vox) / (vix - vox);
      else if (3 == eqn_number)       // clip to y = 1
         t = (1 - voy) / (viy - voy);
      else if (4 == eqn_number)       // clip to y = -1
         t = (-1 - voy) / (viy - voy);

      //if (t < 0.9999999)
      //t = t + 0.0000001;  /*** keep the new vertex off the edge!! ***/

      // Use the value of t to interpolate the coordinates of the new vertex.
    //final double x = (1-t) * vox + t * vix;
    //final double y = (1-t) * voy + t * viy;
    //final double z = (1-t) * voz + t * viz;
      final double x = vox + t * (vix - vox);
      final double y = voy + t * (viy - voy);
      final double z = voz + t * (viz - voz);

      final Vertex v_new = new Vertex(x, y, z);

      // Use the value of t to interpolate the color of the new vertex.
      final float t_ = (float)t;
    //final float r = (1-t_) * co[0] + t_ * ci[0];
    //final float g = (1-t_) * co[1] + t_ * ci[1];
    //final float b = (1-t_) * co[2] + t_ * ci[2];
      final float r = co[0] + t_ * (ci[0] - co[0]);
      final float g = co[1] + t_ * (ci[1] - co[1]);
      final float b = co[2] + t_ * (ci[2] - co[2]);

      final Color c_new = new Color(r, g, b);

      // Modify the Model to contain the new Vertex and Color.
      model.vertexList.add(v_new);
      model.colorList.add(c_new);

      // Use the value of t to interpolate the texture coordinates of the new vertex.
      if (tri.textured)
      {
         final TexCoord tc_inside  = model.texCoordList.get( tri.tcIndexList.get(inside) );
         final TexCoord tc_outside = model.texCoordList.get( tri.tcIndexList.get(outside) );

       //final double sTex = (1-t) * tc_outside.s + t * tc_inside.s;
       //final double tTex = (1-t) * tc_outside.t + t * tc_inside.t;
         final double sTex = tc_outside.s + t * (tc_inside.s - tc_outside.s);
         final double tTex = tc_outside.t + t * (tc_inside.t - tc_outside.t);
         // Modify the Model to contain the new TexCoord.
         model.texCoordList.add(new TexCoord(sTex, tTex));
      }

      if (Clip.debug)
      {
         logMessage(String.format("-- t = % .25f", t));
         logMessage(String.format("-- <x_o,y_o,z_o> = <% .24f % .24f % .24f",
                                       vox,voy,voz));
         logMessage(String.format("-- <x_i,y_i,z_i> = <% .24f % .24f % .24f",
                                       vix,viy,viz));
         logMessage(String.format("-- <x,  y,  z>   = <% .24f % .24f % .24f",
                                       x,  y,  z));
         logMessage(String.format("-- <r_o,g_o,b_o> = <% .15f  % .15f  % .15f>",
                                       co[0], co[1], co[2]));
         logMessage(String.format("-- <r_i,g_i,b_i> = <% .15f  % .15f  % .15f>",
                                       ci[0], ci[1], ci[2]));
         logMessage(String.format("-- <r,  g,  b>   = <% .15f  % .15f  % .15f>",
                                       r,  g,  b));
      }
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Clip_Triangle() {
      throw new AssertionError();
   }
}
