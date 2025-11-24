/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.util;

import renderer.scene.*;
import renderer.scene.primitives.*;

import java.util.ArrayList;
import java.awt.Color;

/**
   Convert a {@link Model} object into a point cloud {@link Model}.
<p>
   See <a href="https://en.wikipedia.org/wiki/Point_cloud" target="_top">
                https://en.wikipedia.org/wiki/Point_cloud</a>
*/
public class PointCloud
{
   /**
      A static factory method that converts a given {@link Model}
      into a {@link Model} made up of only points. This method
      converts each {@link Primitive} in the {@link Model} into
      a {@link Points} primitive.

      @param model  {@link Model} to convert into a point cloud
      @return a {@link Model} that is a point cloud version of the input {@link Model}
      @throws NullPointerException if {@code model} is {@code null}
   */
   public static Model make(final Model model)
   {
      return make(model, 0); // set the point size to 0
   }


   /**
      A static factory method that converts a given {@link Model}
      into a {@link Model} made up of only points. This method
      converts each {@link Primitive} in the {@link Model} into
      a {@link Points} primitive.

      @param model      {@link Model} to convert into a point cloud
      @param pointSize  size, in pixels, of the {@link Point}s in the point cloud
      @return a {@link Model} that is a point cloud version of the input {@link Model}
      @throws NullPointerException if {@code model} is {@code null}
      @throws IllegalArgumentException if {@code pointSize} is less than 0
   */
   public static Model make(final Model model, final int pointSize)
   {
      if (null == model)
         throw new NullPointerException("model must not be null");
      if (pointSize < 0)
         throw new IllegalArgumentException("pointSize must be greater than or equal to 0");

      final Model pointCloud = new Model(new ArrayList<Vertex>(model.vertexList),
                                         new ArrayList<>(), // empty primitiveList
                                         new ArrayList<Color>(model.colorList),
                                         "PointCloud: " + model.name,
                                         model.getMatrix(),
                                         new ArrayList<>(), // empty nestedModels
                                         model.visible,
                                         model.doBackFaceCulling,
                                         model.frontFacingIsCCW,
                                         model.facesHaveTwoSides);

      // Convert every Primitive into a Points primitive.
      for (final Primitive p : model.primitiveList)
      {
         pointCloud.addPrimitive( new Points(p.vIndexList,
                                             p.cIndexList) );
      }

      // Set the radius for each new Points primitive.
      for (final Primitive p : pointCloud.primitiveList)
      {
         ((Points)p).radius = pointSize;;
      }

      // Recursively convert all the nested models.
      for (final Model m : model.nestedModels)
      {
         pointCloud.addNestedModel( make(m, pointSize) );  // recursion
      }

      return pointCloud;
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private PointCloud() {
      throw new AssertionError();
   }
}
