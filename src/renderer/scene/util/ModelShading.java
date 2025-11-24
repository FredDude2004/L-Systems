/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.util;

import renderer.scene.*;
import renderer.scene.primitives.*;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;

/**
   This is a library of static methods that
   add color shading to a {@link Model}.
*/
public class ModelShading
{
   /**
      Set each front face {@link Color} in the {@link Model}'s color list
      to the same {@link Color}.

      @param model  {@link Model} whose color list is being manipulated
      @param c      {@link Color} for all of this model's {@link Vertex} objects
   */
   public static void setColor(final Model model, final Color c)
   {
      if (model.colorList.isEmpty())
      {
         for (int i = 0; i < model.vertexList.size(); ++i)
         {
            model.colorList.add(c);
         }
      }
      else
      {
         for (final Primitive p : model.primitiveList)
         {
            for (final int i : p.cIndexList)
            {
               model.colorList.set(i, c);
            }
         }
      }
      for (final Model m : model.nestedModels)
      {
         setColor(m, c); // recursion
      }
   }


   /**
      Set each front face {@link Color} in the {@link Model}'s color list
      to the same random {@link Color}.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRandomColor(final Model model)
   {
      setColor(model, randomColor());
   }


   /**
      Set each front face {@link Color} in the {@link Model}'s color list
      to a different random {@link Color}.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRandomColors(final Model model)
   {
      if (model.colorList.isEmpty())
      {
         for (int i = 0; i < model.vertexList.size(); ++i)
         {
            model.colorList.add(randomColor());
         }
      }
      else
      {
         for (final Primitive p : model.primitiveList)
         {
            for (final int i : p.cIndexList)
            {
               model.colorList.set(i, randomColor());
            }
         }
      }
      for (final Model m : model.nestedModels)
      {
         setRandomColors(m); // recursion
      }
   }


   /**
      Set each front face {@link Color} in the {@link Model}'s color list
      to the same {@link Color} but randonly shaded brighter and darker.

      @param model  {@link Model} whose color list is being manipulated
      @param c      {@link Color} for all of this model's {@link Vertex} objects
   */
   public static void setColorShaded(final Model model, final Color c)
   {
      final Random generator = new Random();

      if (model.colorList.isEmpty())
      {
         for (int i = 0; i < model.vertexList.size(); ++i)
         {
            final Color c2;
            final int choice = generator.nextInt(7);
            if (0 == choice)
            {
               c2 = c.darker();
            }
            else if (1 == choice)
            {
               c2 = c.darker().darker();
            }
            else if (2 == choice)
            {
               c2 = c.darker().darker().darker();
            }
            else if (3 == choice)
            {
               c2 = c.brighter();
            }
            else if (4 == choice)
            {
               c2 = c.brighter().brighter();
            }
            else if (5 == choice)
            {
               c2 = c.brighter().brighter().brighter();
            }
            else // 6 == choice
            {
               c2 = c;
            }
            model.colorList.add(c2);
         }
      }
      else
      {
         for (final Primitive p : model.primitiveList)
         {
            for (final int i : p.cIndexList)
            {
               final Color c2;
               final int choice = generator.nextInt(7);
               if (0 == choice)
               {
                  c2 = c.darker();
               }
               else if (1 == choice)
               {
                  c2 = c.darker().darker();
               }
               else if (2 == choice)
               {
                  c2 = c.darker().darker().darker();
               }
               else if (3 == choice)
               {
                  c2 = c.brighter();
               }
               else if (4 == choice)
               {
                  c2 = c.brighter().brighter();
               }
               else if (5 == choice)
               {
                  c2 = c.brighter().brighter().brighter();
               }
               else // 6 == choice
               {
                  c2 = c;
               }
               model.colorList.set(i, c2);
            }
         }
      }
      for (final Model m : model.nestedModels)
      {
         setColorShaded(m, c); // recursion
      }
   }


   /**
      Set each front face {@link Color} in the {@link Model}'s color list
      to the same random {@link Color} but randonly shaded brighter and darker.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRandomColorShaded(final Model model)
   {
      setColorShaded(model, randomColor());
   }


   /**
      Give each of this {@code Model}'s nested models
      a different random front face {@link Color}.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRandomNestedModelColors(final Model model)
   {
      final Color c = randomColor();
      if (model.colorList.isEmpty())
      {
         for (int i = 0; i < model.vertexList.size(); ++i)
         {
            model.colorList.add(c);
         }
      }
      else
      {
         for (final Primitive p : model.primitiveList)
         {
            for (final int i : p.cIndexList)
            {
               model.colorList.set(i, c);
            }
         }
      }
      for (final Model m : model.nestedModels)
      {
         setRandomNestedModelColors(m); // recursion
      }
   }


   /**
      Set each {@link Vertex} in the {@link Model}
      to a different random {@link Color}.
      <p>
      This creates a "rainbow model" effect.
      <p>
      NOTE: This will destroy whatever "color structure"
      the model might possess. AND it deletes any back
      face colors that the model might have.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRandomVertexColors(final Model model)
   {
      model.colorList.clear(); // remove all the current colors
      for (int i = 0; i < model.vertexList.size(); ++i)
      {
         model.colorList.add( randomColor() );
      }
      for (final Primitive p : model.primitiveList)
      {
         for (int i = 0; i < p.vIndexList.size(); ++i)
         {
            p.cIndexList.set(i, p.vIndexList.get(i));
         }
      }
      for (final Model m : model.nestedModels)
      {
         setRandomVertexColors(m); // recursion
      }
   }


   /**
      Set each {@link Primitive} in the {@link Model}
      to a different (uniform) random {@link Color}.
      <p>
      NOTE: This will destroy whatever "color structure"
      the model might possess. AND it deletes any back
      face colors that the model might have.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRandomPrimitiveColors(final Model model)
   {
      model.colorList.clear(); // remove all the current colors
      int cIndex = 0;
      for (final Primitive p : model.primitiveList)
      {
         model.colorList.add( randomColor() );
         for (int i = 0; i < p.cIndexList.size(); ++i)
         {
            p.cIndexList.set(i, cIndex);
         }
         ++cIndex;
      }
      for (final Model m : model.nestedModels)
      {
         setRandomPrimitiveColors(m); // recursion
      }
   }


   /**
      Set each {@link Primitive} in the {@link Model} to a
      different (uniform) shade of the given {@link Color}.
      <p>
      NOTE: This will destroy whatever "color structure"
      the model might possess. AND it deletes any back
      face colors that the model might have.

      @param model  {@link Model} whose color list is being manipulated
      @param c      {@link Color} for this model's {@link Primitive} objects
   */
   public static void setPrimitiveColorShaded(final Model model, final Color c)
   {
      final Random generator = new Random();

      model.colorList.clear(); // remove all the current colors
      int cIndex = 0;
      for (final Primitive p : model.primitiveList)
      {
         final Color c2;
         final int choice = generator.nextInt(7);
         if (0 == choice)
         {
            c2 = c.darker();
         }
         else if (1 == choice)
         {
            c2 = c.darker().darker();
         }
         else if (2 == choice)
         {
            c2 = c.darker().darker().darker();
         }
         else if (3 == choice)
         {
            c2 = c.brighter();
         }
         else if (4 == choice)
         {
            c2 = c.brighter().brighter();
         }
         else if (5 == choice)
         {
            c2 = c.brighter().brighter().brighter();
         }
         else // 6 == choice
         {
            c2 = c;
         }
         model.colorList.add( c2 );
         for (int i = 0; i < p.cIndexList.size(); ++i)
         {
            p.cIndexList.set(i, cIndex);
         }
         ++cIndex;
      }
      for (final Model m : model.nestedModels)
      {
         setPrimitiveColorShaded(m, c); // recursion
      }
   }


   /**
      Set each {@link Primitive} in the {@link Model}
      to a different random {@link Color} at each endpoint.
      <p>
      This creates a "rainbow primitive" effect.
      <p>
      NOTE: This will destroy whatever "color structure"
      the model might possess. AND it deletes any back
      face colors that the model might have.

      @param model  {@link Model} whose color list is being manipulated
   */
   public static void setRainbowPrimitiveColors(final Model model)
   {
      model.colorList.clear(); // remove all the current colors
      int cIndex = 0;
      for (final Primitive p : model.primitiveList)
      {
         for (int i = 0; i < p.cIndexList.size(); ++i)
         {
            model.colorList.add( randomColor() );
            p.cIndexList.set(i, cIndex);
            ++cIndex;
         }
      }
      for (final Model m : model.nestedModels)
      {
         setRainbowPrimitiveColors(m); // recursion
      }
   }


   /**
      Set a uniform back face color of each {@link OrientablePrimitive}
      in the {@link Model} to the given {@link Color}. The given
      {@link Color} object {@code c} is added to the
      {@link Model}'s color list.

      @param model  {@link Model} whose back face color is being manipulated
      @param c      back face {@link Color} for all of the {@link Model}'s {@link Face} primitives
   */
   public static void setBackFaceColor(final Model model, final Color c)
   {
      final int bfColorIndex = model.colorList.size();
      model.colorList.add(c);
      for (final Primitive p : model.primitiveList)
      {
         if (p instanceof OrientablePrimitive)
         {
            ((OrientablePrimitive)p).setBackFaceColorIndex(bfColorIndex);
         }
      }
      for (final Model m : model.nestedModels)
      {
         setBackFaceColor(m, c); // recursion
      }
   }


   /**
      Set a uniform back face color of each {@link OrientablePrimitive}
      in the {@link Model} to the {@link Color} with the given
      color index into the {@link Model}'s color list.

      @param model   {@link Model} whose back face color is being manipulated
      @param cIndex  back face {@link Color} index for all of the {@link Model}'s {@link Face} primitives
   */
   public static void setBackFaceColor(final Model model, final int cIndex)
   {
      for (final Primitive p : model.primitiveList)
      {
         if (p instanceof OrientablePrimitive)
         {
            ((OrientablePrimitive)p).setBackFaceColorIndex(cIndex);
         }
      }
      for (final Model m : model.nestedModels)
      {
         setBackFaceColor(m, cIndex); // recursion
      }
   }


   /**
      Set the back face colors of each {@link OrientablePrimitive}
      in the {@link Model} to the given {@link Color}s. The given
      {@link Color} objects are added to the {@link Model}'s
      color list.

      @param model  {@link Model} whose color list is being manipulated
      @param c0     back face {@link Color} for all of the {@code Model}'s {@link Triangle} primitives
      @param c1     back face {@link Color} for all of the {@code Model}'s {@link Triangle} primitives
      @param c2     back face {@link Color} for all of the {@code Model}'s {@link Triangle} primitives
   */
   public static void setBackFaceColor(final Model model,
                                       final Color c0, final Color c1, final Color c2)
   {
      int i = model.colorList.size();
      model.colorList.add(c0);
      model.colorList.add(c1);
      model.colorList.add(c2);
      for (Primitive p : model.primitiveList)
      {
         if (p instanceof OrientablePrimitive)
         {
            ((OrientablePrimitive)p).setBackFaceColorIndex(i, i+1, i+2);
         }
      }
      for (Model m : model.nestedModels)
      {
         setBackFaceColor(m, c0, c1, c2); // recursion
      }
   }


   /**
      Set the back face colors of each {@link OrientablePrimitive}
      in the {@link Model} to the {@link Color}s with the given
      color indices into the {@link Model}'s color list.

      @param model    {@link Model} whose color list is being manipulated
      @param cIndex0  back face {@link Color} index for all of the {@link Model}'s {@link Face} primitives
      @param cIndex1  back face {@link Color} index for all of the {@link Model}'s {@link Face} primitives
      @param cIndex2  back face {@link Color} index for all of the {@link Model}'s {@link Face} primitives
   */
   public static void setBackFaceColor(final Model model,
                                       final int cIndex0, final int cIndex1, final int cIndex2)
   {
      for (Primitive p : model.primitiveList)
      {
         if (p instanceof OrientablePrimitive)
         {
            ((OrientablePrimitive)p).setBackFaceColorIndex(cIndex0,
                                                           cIndex1,
                                                           cIndex2);
         }
      }
      for (Model m : model.nestedModels)
      {
         setBackFaceColor(m, cIndex0, cIndex1, cIndex2); // recursion
      }
   }


   /**
      Create a {@link Color} object with randomly generated {@code r},
      {@code g}, and {@code b} values.

      @return a reference to a randomly generated {@link Color} object
   */
   public static Color randomColor()
   {
      final Random generator = new Random();
      final float r = generator.nextFloat();
      final float g = generator.nextFloat();
      final float b = generator.nextFloat();
      return new Color(r, g, b);
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private ModelShading() {
      throw new AssertionError();
   }
}
