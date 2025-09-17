/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.primitives.*;
import static renderer.pipeline.PipelineLogger.*;

import java.util.List;
import java.util.ArrayList;

/**
   Clip in camera space any {@link Vertex} from {@link Points} that
   crosses the camera's near clipping plane {@code z = -near}.
*/
public class NearClip_Points
{
   /**
      If any {@link Vertex} used by this {@link Points} is on the camera
      side of the near plane, then delete that vertex's index from the
      {@link Points}'s vetex and color lists.
      <p>
      If this {@link Points}'s vertex list ends up empty, then return an
      empty {@link List} object to indicate that this {@link Points}
      object should be discarded from the model's {@link Primitive} list.
      <p>
      If any {@link Vertex} used by this {@link Points} is on the far side
      of the near plane, then return a clipped {@link Points} wrapped in
      a {@link List} object.

      @param model   {@link Model} that the {@link Points} {@code pts} comes from
      @param pts     {@link Points} to be clipped
      @param camera  {@link Camera} that determines the near clipping plane
      @return the clipped version of {@code pts} wrapped in an {@link List} object
   */
   public static List<Primitive> clip(final Model model,
                                      final Points pts,
                                      final Camera camera)
   {
      final List<Integer> clippedvIndexList = new ArrayList<>();
      final List<Integer> clippedcIndexList = new ArrayList<>();

      for (int i = 0; i < pts.vIndexList.size(); ++i)
      {
         final int vIndex = pts.vIndexList.get(i);
         final int cIndex = pts.cIndexList.get(i);

         final Vertex v = model.vertexList.get(vIndex);
         final double z = v.z;

         // 1. Check for trivial accept.
         if ( z <= camera.n )
         {
            clippedvIndexList.add(vIndex);
            clippedcIndexList.add(cIndex);

            if (Clip.debug) logMessage("-- Trivial accept: " + vIndex);
         }
         // 2. Trivial delete.
         else
         {
            if (Clip.debug) logMessage("-- Trivial delete: " + vIndex);
         }
      }

      if ( clippedvIndexList.isEmpty() )
      {
         return new ArrayList<Primitive>();  // better than "return null;"
      }
      else
      {
         final Points pts2 = new Points(clippedvIndexList,
                                        clippedcIndexList);
         pts2.radius = pts.radius;
         return java.util.Arrays.asList(pts2); // better than "return pts2;"
      }
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private NearClip_Points() {
      throw new AssertionError();
   }
}
