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
   Transform each {@link Vertex} of a {@link Model} from the
   {@link Camera}'s (shared) view coordinates to normalized
   camera coordinates.
<p>
   This stage transforms the {@link Camera}'s view volume
   from a user defined shape (in the view coordinate system)
   into the standard normalized view volume (in the camera
   coordinate system) used by the {@link Clip} pipeline stage.
<p>
   There are two standard normalized view volumes, one for
   perspective projection and one for orthographic projection.
<p>
   The standard normalized perspective view volume is the infinitely
   long pyramid with its apex at the origin and intersecting the
   image plane {@code z = -1} at the corners {@code (-1, -1, -1)}
   and {@code (+1, +1, -1)}.
<p>
   The standard normalized orthographic view volume is the infinitely
   long parallelepiped centered on the z-axis and intersecting the
   image plane {@code z = -1} at the corners {@code (-1, -1, -1)}
   and {@code (+1, +1, -1)}.
<p>
   The user defined view volume determined by the {@link Scene}'s
   {@link Camera} object is either the infinitely long pyramid with its
   apex at the origin and intersecting the image plane {@code z = -1} at
   the corners {@code (left, bottom, -1)} and {@code (right, top, -1)},
   or it is the infinitely long parallelepiped parallel to the z-axis
   and intersecting the image plane {@code z = -1} at the corners
   {@code (left, bottom, -1)} and {@code (right, top, -1)}.
<p>
   The view coordinate system is relative to the user defined view volume.
<p>
   The normalized camera coordinate system is relative to the normalized
   view volume.
<p>
   The matrix that transforms the user defined view volume into the
   normalized view volume also transform the view coordinate system
   into the normalized camera coordinate system.
<p>
   The matrix that transforms view coordinates into normalized camera
   coordinates is derived in the comments of
   {@link PerspectiveNormalizeMatrix} and
   {@link OrthographicNormalizeMatrix}.
*/
public final class View2Camera
{
   /**
      Use the {@link Camera}'s normalizing {@link Matrix} to transform each
      {@link Vertex} from the {@link Camera}'s view coordinate system to the
      normalized camera coordinate system.

      @param model  {@link Model} with {@link Vertex} objects in the camera's view coordinate system
      @param camera  the {@link Scene}'s {@link Camera} with the normalizing {@link Matrix}
      @return a new {@link Model} with {@link Vertex} objects in the normalized camera coordinate system
   */
   public static Model view2camera(final Model model, final Camera camera)
   {
      final Matrix normalizeMatrix = camera.getNormalizeMatrix();

      // A new vertex list to hold the transformed vertices.
      final List<Vertex> newVertexList =
                            new ArrayList<>(model.vertexList.size());

      // Replace each Vertex object with one that
      // contains normalized camera coordinates.
      for (final Vertex v : model.vertexList)
      {
         newVertexList.add( normalizeMatrix.times(v) );
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
   private View2Camera() {
      throw new AssertionError();
   }
}
