/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

import java.util.List;

/**
   A {@code Points} object has {@code n} integers that
   represent points in space and another {@code n}
   integers that represent the {@link java.awt.Color} at
   each of those points.
<p>
   The integers are indices into the {@link renderer.scene.Vertex}
   and {@link java.awt.Color} lists of a {@link renderer.scene.Model}
   object.
*/
public class Points extends Primitive
{
   public int radius = 0;

   /**
      Construct a {@code Points} with the given array of indices for
      the {@link renderer.scene.Vertex} and {@link java.awt.Color} index lists.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param indices  array of {@link renderer.scene.Vertex} and {@link java.awt.Color} indices to place in this {@code Points}
   */
   public Points(final int... indices)
   {
      super(indices);
   }


   /**
      Construct a {@code Points} object using the two given
      {@link List}s of integer indices.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param vIndexList  {@link List} of integer indices into a {@link renderer.scene.Vertex} list
      @param cIndexList  {@link List} of integer indices into a {@link java.awt.Color} list
   */
   public Points(final List<Integer> vIndexList,
                 final List<Integer> cIndexList)
   {
      super(vIndexList, cIndexList);
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Points} object
   */
   @Override
   public String toString()
   {
      String result =  "Points: ([";
      for (int i = 0; i < vIndexList.size() - 1; ++i)
      {
         result += vIndexList.get(i) + ", ";
      }
      result += vIndexList.get(vIndexList.size() - 1) + "], [";
      for (int i = 0; i < cIndexList.size() - 1; ++i)
      {
         result += cIndexList.get(i) + ", ";
      }
      result += cIndexList.get(cIndexList.size() - 1) + "]) radius = " + radius;
      return result;
   }
}
