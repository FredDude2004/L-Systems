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
   Transform each {@link Vertex} of a {@link Model} from the world
   coordinate system to the {@link Camera}'s view coordinate system.
<p>
   For each {@code Vertex} object in a {@code Model} object, use the
   {@link Camera}'s world-to-view {@link Matrix} to transform the
   object's {@code Vertex} coordinates from the world coordinate
   system to the camera's view coordinate system.
*/
public class World2View
{
   /**
      Use a {@link Camera}'s world-to-view {@link Matrix} to transform
      each {@link Vertex} from the world coordinate system to the camera's
      view coordinate system.

      @param model   {@link Model} with {@link Vertex} objects in the world coordinate system
      @param camera  a {@link Camera} with a view {@link Matrix}
      @return a new {@link Model} with {@link Vertex} objects in the {@link Camera}'s view coordinate system
   */
   public static Model world2view(final Model model,
                                  final Camera camera)
   {
      final Matrix viewMatrix = camera.getViewMatrix();

      // A new vertex list to hold the transformed vertices.
      final List<Vertex> newVertexList =
                            new ArrayList<>(model.vertexList.size());

      // Replace each Vertex object with one that
      // contains view coordinates.
      for (final Vertex v : model.vertexList)
      {
         newVertexList.add( viewMatrix.times(v) );
      }

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
   private World2View() {
      throw new AssertionError();
   }
}
