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
   Clip a (projected) geometric {@link Primitive} that sticks out
   of the camera's view rectangle in the image plane. Interpolate
   {@link Vertex} color from any clipped off {@link Vertex} to the
   new {@link Vertex}.
*/
public class Clip
{
   public static boolean debug = false;

   /**
      Start with a {@link Model} that contains {@link Primitive}s
      that have been projected onto the camera's view plane,
      {@code z = -1}.
   <p>
      If a projected {@link Primitive} sticks out of the camera's
      view rectangle, then replace that {@link Primitive}, in the
      {@link Model}'s list of primitives, with one that has been
      clipped so that it is contained in the view rectangle.
   <p>
      If a projected {@link Primitive} is completely outside of
      the view rectangle, then drop that {@link Primitive} from
      the {@link Model}'s list of primitives.
   <p>
      Return a {@link Model} for which every {@link Primitive} is
      completely contained in the camera's view rectangle.

      @param model  {@link Model} containing projected {@link Primitive}s
      @return a {@link Model} containing {@link Primitive}s clipped to the view rectangle
   */
   public static Model clip(final Model model)
   {
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
         logPrimitive("9. Clipping", model2, p);

         final List<Primitive> p_clipped;

         if (p instanceof LineSegment)
         {
            p_clipped = Clip_Line.clip(model2, (LineSegment)p);
         }
         else if (p instanceof Points)
         {
            p_clipped = Clip_Points.clip(model2, (Points)p);
         }
         else // if (p instanceof Triangle)
         {
            p_clipped = Clip_Triangle.clip(model2, (Triangle)p);
         }

         if ( ! p_clipped.isEmpty() )
         {
            // Keep the primitives that are visible.
            newPrimitiveList.addAll( p_clipped );

            for (final Primitive p2 : p_clipped)
            {
               logPrimitive("9. Clipped (accept)", model2, p2);
            }
         }
         else
         {
            // Discard the primitives that are not visible.
            logPrimitive("9. Clipped (reject)", model2, p);
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
   private Clip() {
      throw new AssertionError();
   }
}
