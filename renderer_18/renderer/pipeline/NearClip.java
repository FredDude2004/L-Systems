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
import java.util.Optional;

/**
   Clip in camera space any {@link Primitive} that crosses
   the camera's near clipping plane {@code z = -near}.
*/
public class NearClip
{
   public static boolean doNearClipping = true;
   public static boolean debug = false;

   /**
      Start with a {@link Model} that contains {@link Primitive}s
      that have been transformed into camera space.
   <p>
      If a transformed {@link Primitive} crosses the camera's
      near plane, then replace that {@link Primitive}, in the
      {@link Model}'s list of primitives, with one that has been
      clipped so that it lies completely in the far side of the
      camera's near plane (the side of the near plane away from
      the camera).
   <p>
      If a transformed {@link Primitive} is completely in the
      camera side of the near plane, then drop that
      {@link Primitive} from the {@link Model}'s list of primitives.
   <p>
      Return a {@link Model} for which every {@link Primitive} is
      completely on the far side of the camera's near plane.

      @param model   {@link Model} containing {@link Primitive}s transformed into camera space
      @param camera  {@link Camera} that determines the near clipping plane
      @return a {@link Model} containing {@link Primitive}s clipped to the camera's near plane
   */
   public static Model clip(final Model model, final Camera camera)
   {
      if (! doNearClipping)
      {
         return model;
      }

      // Replace the model's list of colors with a shallow copy.
      final Model model2 =  new Model(model.vertexList,
                                      model.primitiveList,
                                      new ArrayList<>(model.colorList),
                                      model.name,
                                      model.getMatrix(),
                                      model.nestedModels,
                                      model.textureList,
                                      model.texCoordList,
                                      model.visible,
                                      model.doBackFaceCulling,
                                      model.frontFacingIsCCW,
                                      model.facesHaveTwoSides);

      final List<Primitive> newPrimitiveList = new ArrayList<>();

      for (final Primitive p : model2.primitiveList)
      {
         logPrimitive("7. Near_Clipping", model2, p);

         final List<Primitive> p_clipped;

         if (p instanceof LineSegment)
         {
            p_clipped = NearClip_Line.clip(model2, (LineSegment)p, camera);
         }
         else if (p instanceof Points)
         {
            p_clipped = NearClip_Points.clip(model2, (Points)p, camera);
         }
         else // if (p instanceof Triangle)
         {
            p_clipped = NearClip_Triangle.clip(model2, (Triangle)p, camera);
         }

         if ( ! p_clipped.isEmpty() )
         {
            // Keep the primitives that are visible.
            for (final Primitive p2 : p_clipped)
            {
               newPrimitiveList.add( p2 );
               logPrimitive("7. Near_Clipped (accept)", model2, p2);
            }
         }
         else
         {
            // Discard the primitives that are not visible.
            logPrimitive("7. Near_Clipped (reject)", model2, p);
         }
      }

      // Replace the model's original list of line segments
      // with the list of clipped line segments.
      return new Model(model2.vertexList,  // has been updated with clipped vertices
                       newPrimitiveList,   // clipped primitives
                       model2.colorList,   // has been updated with interpolated colors
                       model2.name,
                       model2.getMatrix(),
                       model2.nestedModels,
                       model2.textureList,
                       model2.texCoordList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private NearClip() {
      throw new AssertionError();
   }
}
