/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.primitives.*;

import java.util.List;
import java.util.ArrayList;

/**
   Assemble a {@link Model}'s {@link List} of {@link Primitive}s
   into a {@link List} containing only {@link LineSegment},
   {@link Points} and {@link Triangle} {@link Primitive}s.
<p>
   In order to clip a {@link Primitive}, we need to convert the
   {@link Primitive} into individual {@link LineSegment}s or
   {@link Triangle}s, and then clip each one separately.
*/
public class PrimitiveAssembly
{
   /**
      Assemble a {@link Model}'s {@link List} of {@link Primitive}s into a
      {@link List} containing only {@link LineSegment}, {@link Points}
      and {@link Triangle} {@link Primitive}s.

      @param model  {@link Model} whose {@link Primitive}s need to be assembled
      @return a {@link Model} containing just {@link LineSegment}, {@link Points}, and {@link Triangle} primitives
   */
   public static Model assemble(final Model model)
   {
      final List<Primitive> newPrimitiveList = new ArrayList<>();

      for (final Primitive p : model.primitiveList)
      {
         if (p instanceof Triangle)
         {
            newPrimitiveList.add(p);
         }
         else if (p instanceof TriangleStrip)
         {
            final TriangleStrip p2 = (TriangleStrip)p;

            // Convert the TriangleStrip into individual Triangles.
            int c = 0;
            for (int i = 0; i < p2.vIndexList.size() - 2; ++i)
            {
               final int a = 1 + c; // a is either 1 or 2 (since c is 0 or 1)
               final int b = 2 - c; // b is either 2 or 1 (since c is 0 or 1)
               final Triangle newTri = new Triangle(
                                            p2.vIndexList.get(i + 0),
                                            p2.vIndexList.get(i + a),
                                            p2.vIndexList.get(i + b),
                                            p2.cIndexList.get(i + 0),
                                            p2.cIndexList.get(i + a),
                                            p2.cIndexList.get(i + b),
                                            p2.cIndexList2.get(i + 0),
                                            p2.cIndexList2.get(i + a),
                                            p2.cIndexList2.get(i + b));
               newPrimitiveList.add(newTri);
               c = 1 - c;  // c alternates between 0 and 1
            }
         }
         else if (p instanceof TriangleFan)
         {
            final TriangleFan p2 = (TriangleFan)p;

            // Convert the TriangleFan into individual Triangles.
            for (int i = 1; i < p2.vIndexList.size() - 1; ++i)
            {
               final Triangle newTri = new Triangle(
                                            p2.vIndexList.get(0),
                                            p2.vIndexList.get(i),
                                            p2.vIndexList.get(i + 1),
                                            p2.cIndexList.get(0),
                                            p2.cIndexList.get(i),
                                            p2.cIndexList.get(i + 1),
                                            p2.cIndexList2.get(0),
                                            p2.cIndexList2.get(i),
                                            p2.cIndexList2.get(i + 1));
               newPrimitiveList.add(newTri);
            }
         }
         else if (p instanceof Triangles)
         {
            final Triangles p2 = (Triangles)p;

            // Convert the Triangles into individual Triangles.
            for (int i = 0; i < p2.vIndexList.size() - 2; i += 3)
            {
               final Triangle newTri = new Triangle(
                                            p2.vIndexList.get(i + 0),
                                            p2.vIndexList.get(i + 1),
                                            p2.vIndexList.get(i + 2),
                                            p2.cIndexList.get(i + 0),
                                            p2.cIndexList.get(i + 1),
                                            p2.cIndexList.get(i + 2),
                                            p2.cIndexList2.get(i + 0),
                                            p2.cIndexList2.get(i + 1),
                                            p2.cIndexList2.get(i + 2));
               newPrimitiveList.add(newTri);
            }
         }
         else if (p instanceof LineSegment)
         {
            newPrimitiveList.add(p);
         }
         else if (p instanceof Lines)
         {
            // Convert the Lines into individual LineSegments.
            for (int i = 0; i < p.vIndexList.size(); i += 2)
            {
               newPrimitiveList.add(new LineSegment(p.vIndexList.get(i),
                                                    p.vIndexList.get(i+1),
                                                    p.cIndexList.get(i),
                                                    p.cIndexList.get(i+1)));
            }
         }
         else if (p instanceof LineLoop || p instanceof Face)
         {
            // Convert the LineLoop or Face into individual LineSegments.
            for (int i = 0; i < p.vIndexList.size() - 1; ++i)
            {
               newPrimitiveList.add(new LineSegment(p.vIndexList.get(i),
                                                    p.vIndexList.get(i+1),
                                                    p.cIndexList.get(i),
                                                    p.cIndexList.get(i+1)));
            }
            // Close the LineLoop or Face into a loop of line segments.
            newPrimitiveList.add(new LineSegment(p.vIndexList.get(p.vIndexList.size() - 1),
                                                 p.vIndexList.get(0),
                                                 p.cIndexList.get(p.vIndexList.size() - 1),
                                                 p.cIndexList.get(0)));
         }
         else if (p instanceof LineStrip)
         {
            // Convert the LineStrip into individual LineSegments.
            for (int i = 0; i < p.vIndexList.size() - 1; ++i)
            {
               newPrimitiveList.add(new LineSegment(p.vIndexList.get(i),
                                                    p.vIndexList.get(i+1),
                                                    p.cIndexList.get(i),
                                                    p.cIndexList.get(i+1)));
            }
         }
         else if (p instanceof LineFan)
         {
            // Convert the LineFan into individual LineSegments.
            for (int i = 1; i < p.vIndexList.size(); ++i)
            {
               newPrimitiveList.add(new LineSegment(p.vIndexList.get(0),
                                                    p.vIndexList.get(i),
                                                    p.cIndexList.get(0),
                                                    p.cIndexList.get(i)));
            }
         }
         else if (p instanceof Points)
         {
            newPrimitiveList.add(p);
         }
         else if (p instanceof Point)
         {
            // Convert the Point object into a Points object.
            final Points points = new Points();
            points.radius = ((Point)p).radius;
            points.addIndices(p.vIndexList.get(0),
                              p.cIndexList.get(0));
            newPrimitiveList.add(points);
         }
      }

      // Replace the model's original list of primitives
      // with the list of line segments and points.
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



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private PrimitiveAssembly() {
      throw new AssertionError();
   }
}
