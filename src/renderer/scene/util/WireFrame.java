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
   Convert a {@link Model} object into a wireframe {@link Model}.
<p>
   See <a href="https://en.wikipedia.org/wiki/Point_cloud" target="_top">
                https://en.wikipedia.org/wiki/Point_cloud</a>

   @see renderer.pipeline.PrimitiveAssembly
*/
public class WireFrame
{
   /**
      A static factory method that converts a given {@link Model}
      into a {@link Model} made up of line segments. This method
      converts each {@link Triangle}, {@link TriangleStrip},
      {@link TriangleFan} and {@link Triangles} in the {@link Model}
      into an appropriate number of {@link Face} primitives.

      @param model  {@link Model} to convert into a wireframe
      @return a {@link Model} that is a wireframe version of the input {@link Model}
   */
   public static Model make(final Model model)
   {
      final Model wireframe = new Model(new ArrayList<Vertex>(model.vertexList),
                                        new ArrayList<>(), // empty primitiveList
                                        new ArrayList<Color>(model.colorList),
                                        "Wireframe: " + model.name,
                                        model.getMatrix(),
                                        new ArrayList<>(), // empty nestedModels
                                        model.visible,
                                        model.doBackFaceCulling,
                                        model.frontFacingIsCCW,
                                        model.facesHaveTwoSides);

      for (final Primitive p : model.primitiveList)
      {
         if (p instanceof Triangle)
         {
            final Triangle t = (Triangle)p;
            wireframe.addPrimitive(new Face(t.vIndexList,
                                            t.cIndexList,
                                            t.cIndexList2));
         }
         else if (p instanceof TriangleStrip)
         {
            final TriangleStrip p2 = (TriangleStrip)p;
            int c = 0;
            for (int i = 0; i < p2.vIndexList.size() - 2; ++i)
            {
               final int a = 1 + c; // a is either 1 or 2 (since c is 0 or 1)
               final int b = 2 - c; // b is either 2 or 1 (since c is 0 or 1)
               final Face face = new Face();
               face.addIndices(p2.vIndexList.get(i + 0),
                               p2.cIndexList.get(i + 0),
                               p2.cIndexList2.get(i + 0));
               face.addIndices(p2.vIndexList.get(i + a),
                               p2.cIndexList.get(i + a),
                               p2.cIndexList2.get(i + a));
               face.addIndices(p2.vIndexList.get(i + b),
                               p2.cIndexList.get(i + b),
                               p2.cIndexList2.get(i + b));
               wireframe.addPrimitive(face);
               c = 1 - c;  // c alternates between 0 and 1
            }
         }
         else if (p instanceof TriangleFan)
         {
            final TriangleFan p2 = (TriangleFan)p;
            for (int i = 1; i < p2.vIndexList.size() - 1; ++i)
            {
               final Face face = new Face();
               face.addIndices(p2.vIndexList.get(0),
                               p2.cIndexList.get(0),
                               p2.cIndexList2.get(0));
               face.addIndices(p2.vIndexList.get(i),
                               p2.cIndexList.get(i),
                               p2.cIndexList2.get(i));
               face.addIndices(p2.vIndexList.get(i + 1),
                               p2.cIndexList.get(i + 1),
                               p2.cIndexList2.get(i + 1));
               wireframe.addPrimitive(face);
            }
         }
         else if (p instanceof Triangles)
         {
            final Triangles p2 = (Triangles)p;
            for (int i = 0; i < p2.vIndexList.size() - 2; i += 3)
            {
               final Face face = new Face();
               face.addIndices(p2.vIndexList.get(i + 0),
                               p2.cIndexList.get(i + 0),
                               p2.cIndexList2.get(i + 0));
               face.addIndices(p2.vIndexList.get(i + 1),
                               p2.cIndexList.get(i + 1),
                               p2.cIndexList2.get(i + 1));
               face.addIndices(p2.vIndexList.get(i + 2),
                               p2.cIndexList.get(i + 2),
                               p2.cIndexList2.get(i + 2));
               wireframe.addPrimitive(face);
            }
         }
         else
         {
            wireframe.addPrimitive(p);
         }
      }

      for (final Model m : model.nestedModels)
      {
         wireframe.addNestedModel( make(m) );  // recursion
      }

      return wireframe;
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private WireFrame() {
      throw new AssertionError();
   }
}
