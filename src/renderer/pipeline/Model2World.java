/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;

import java.util.List;
import java.util.ArrayList;

/**
   Transform each {@link Vertex} of a {@link Model} from the model's
   (private) local coordinate system to the (shared) world coordinate
   system.
<p>
   For each {@code Vertex} object in a {@code Model} object, use the
   current model-to-world transformation {@link Matrix} to transform
   the object's {@code Vertex} coordinates from a model's coordinate
   system to the world coordinate system.
<p>
   Return a new {@code Model} object, which contains all the translated
   vertices from the original model, to the renderer. The original model
   object, which belongs to the client program, remains unchanged. So the
   renderer gets the mutated model and the client sees its model as being
   preserved.
*/
public class Model2World
{
   /**
      Use the current model-to-world transformation {@link Matrix} to
      transform each {@link Vertex} from a {@link Model}'s coordinate
      system to the world coordinate system.

      @param model   {@link Model} with {@link Vertex} objects in model coordinate
      @param ctm     the current model-to-world transformation {@link Matrix}
      @return a new {@link Model} with {@link Vertex} objects in the world coordinate system
   */
   public static Model model2world(final Model model,
                                   final Matrix ctm)
   {
      // A new vertex list to hold the transformed vertices.
      final List<Vertex> newVertexList =
                            new ArrayList<>(model.vertexList.size());

      // Replace each Vertex object with one that
      // contains world coordinates.
      for (final Vertex v : model.vertexList)
      {
         newVertexList.add( ctm.times(v) );
      }

      // Return to the renderer an updated model.
      return new Model(newVertexList,
                       model.primitiveList,
                       model.colorList,
                       model.name,
                       model.getMatrix(),
                       model.nestedModels,
                       model.textureList,
                       model.texCoordList,
                       model.visible,
                       model.doBackFaceCulling,
                       model.frontFacingIsCCW,
                       model.facesHaveTwoSides);
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Model2World() {
      throw new AssertionError();
   }
}
