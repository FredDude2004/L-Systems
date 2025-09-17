/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.primitives.Primitive;
import renderer.scene.primitives.Face;
import renderer.scene.primitives.Triangle;
import static renderer.pipeline.PipelineLogger.*;

import java.util.List;
import java.util.ArrayList;

/**
   This stage is used by the renderer to cull from a {@link Model}'s
   {@link List} of {@link Primitive}s any {@link Triangle} that
   is "back facing".
<p>
   The culling of {@link Triangle} primitives needs to be done after
   the primitive assembly stage. The culling of {@link Face} primitives
   needs to be done before the primitive assembly stage.
<p>
   This stage also implements a simple version of the "painter's algorithm".
   When backface culling is turned off, this stage still sorts primitives
   into separate lists of front and back facing primitives. Then the back
   facing primitives get put into the model's primitive list ahead of the
   front facing primitives. This causes all the back facing primitives to
   be rasterized (painted) first, and then all the front facing primitives
   get rasterized (or, painted) over the back facing ones. The painter's
   algorithm helps give the renderer a sense of 3D depth.
<p>
   For a good explanation of orientation, forward and backward facing
   polygons, and back face culling, see Chapter 9 of<br>
      "Computer Graphics Through OpenGL: From Theory to Experiments,"
      4th Edition, by Sumanta Guha, Chapman and Hall/CRC, 2022.
<p>
   That chapter is available online at<br>
       <a href="https://www.sumantaguha.com/wp-content/uploads/2022/06/chapter9.pdf" target="_top">
                https://www.sumantaguha.com/wp-content/uploads/2022/06/chapter9.pdf</a>
*/
public class BackFaceCulling2
{
   /**
      Replace the given {@link Model}'s list of primitives with a
      new primitive list from which all back facing primitives have
      been filtered out. Notice that non {@link Face} primitives are
      considered to be not back facing, so they are kept in the
      primitive list.

      @param model   {@link Model} whose {@link Primitive} list is to be culled
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return a {@link Model} with culled back faces or the appropriate side of two-sided faces
   */
   public static Model cull(final Model model, final Camera camera)
   {
      if ( (! model.doBackFaceCulling)
        && (! model.facesHaveTwoSides) )
      {
         return model;
      }

      final List<Primitive> newPrimitiveList = new ArrayList<>();

      for (final Primitive p : model.primitiveList)
      {
         if (! isBackFace(p, model, camera))
         {
            // Keep the non-Triangle primitives and the
            // Triangle ones with the desired orientation.
            newPrimitiveList.add(p);
         }
         else // back facing Triangle
         {
            if ( ! model.doBackFaceCulling )
            {
               if ( ! model.facesHaveTwoSides)
               {
                  // Keep a back facing Triangle and
                  // use its front facing colors.
                  newPrimitiveList.add(p);
               }
               else // faces have two sides
               {
                  // Keep a back facing Triangle but
                  // use its back facing colors.
                  final Triangle p2 = new Triangle(p.vIndexList,
                                                   ((Triangle)p).cIndexList2);
                  newPrimitiveList.add(p2);
               }
            }
         }
      }

      return new Model(model.vertexList,
                       newPrimitiveList,
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


   /**
      Determine if a {@link Primitive} is a {@link Triangle} and if that
      Triangle is backward facing. Whether a Triangle is back facing or not
      depends on the {@link Model}'s setting for {@code frontFacingIsCCW}.

      @param p      {@link Primitive} with at least three vertices
      @param model  {@link Model} that {@code p} comes from
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return true if {@code p} is a back facing {@link Triangle}
   */
   public static boolean isBackFace(final Primitive p,
                                    final Model model,
                                    final Camera camera)
   {
      return  (p instanceof Triangle)
           && ( model.frontFacingIsCCW ^ isCCW(p, model, camera) );
   //https://docs.oracle.com/javase/specs/jls/se6/html/expressions.html#15.22.2
   }


   /**
      Determine if a {@link Triangle} is counterclockwise.
      <p>
      This method assumes that the {@link Primitive} {@code p}
      has at least three vertices so that it can compute two
      edge vectors and then compute the z-component of their
      cross product.

      @param p      {@link Primitive} with at least three vertices
      @param model  {@link Model} that {@code p} comes from
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return true if the {@link Primitive} is counterclockwise
   */
   private static boolean isCCW(final Primitive p,
                                final Model model,
                                final Camera camera)
   {
      // We only need to know the z-component of the cross product
      // of the two edge vectors, v1-v0 and v2-v0. This works
      // because the camera is looking down the negative z-axis.
      final double zValueOfCrossProduct;

      double x0 = model.vertexList.get(p.vIndexList.get(0)).x;
      double x1 = model.vertexList.get(p.vIndexList.get(1)).x;
      double x2 = model.vertexList.get(p.vIndexList.get(2)).x;
      double y0 = model.vertexList.get(p.vIndexList.get(0)).y;
      double y1 = model.vertexList.get(p.vIndexList.get(1)).y;
      double y2 = model.vertexList.get(p.vIndexList.get(2)).y;
      final double z0 = model.vertexList.get(p.vIndexList.get(0)).z;
      final double z1 = model.vertexList.get(p.vIndexList.get(1)).z;
      final double z2 = model.vertexList.get(p.vIndexList.get(2)).z;

      // The calculation of orientation needs
      // to be done with projected coordinates.
      if ( camera.perspective )
      {
         // Calculate the perspective projection.
         x0 = x0 / -z0;  // xp = xc / -zc
         x1 = x1 / -z1;
         x2 = x2 / -z2;
         y0 = y0 / -z0;  // yp = yc / -zc
         y1 = y1 / -z1;
         y2 = y2 / -z2;
      }

      zValueOfCrossProduct = (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);

      if (PipelineLogger.debugScene || PipelineLogger.debugPosition)
      {
         System.err.println( "6. Culling_2: " + p.toString() );
         System.err.printf("-- zValueOfCrossProduct = % .26f\n",
                               zValueOfCrossProduct);
         System.err.print( (0 <= zValueOfCrossProduct) ? "-- CCW, "
                                                       : "-- CW, " );
         System.err.println(
            (model.frontFacingIsCCW ^ (0 <= zValueOfCrossProduct))
            ? "Back facing"
            : "Front facing");
      }

      return 0 <= zValueOfCrossProduct;
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private BackFaceCulling2() {
      throw new AssertionError();
   }
}
