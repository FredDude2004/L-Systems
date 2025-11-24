/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

/**
   A {@code LineLoop} object has {@code n} integers that
   represent the endpoints of {@code n} contiguous line segments
   that form a closed loop and another {@code n} integers that
   represent the {@link java.awt.Color} at each of those endpoints.
<p>
   The integers are indices into the {@link renderer.scene.Vertex}
   and {@link java.awt.Color} lists of a {@link renderer.scene.Model}
   object.
*/
public class LineLoop extends Primitive
{
   /**
      Construct a {@code LineLoop} with the given array of indices for
      the {@link renderer.scene.Vertex} and {@link java.awt.Color} index lists.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param indices  array of {@link renderer.scene.Vertex} and {@link java.awt.Color} indices to place in this {@code LineLoop}
   */
   public LineLoop(final int... indices)
   {
      super(indices);
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code LineLoop} object
   */
   @Override
   public String toString()
   {
      String result =  "LineLoop: ([";
      for (int i = 0; i < vIndexList.size() - 1; ++i)
      {
         result += vIndexList.get(i) + ", ";
      }
      result += vIndexList.get(vIndexList.size() - 1) + "], [";
      for (int i = 0; i < cIndexList.size() - 1; ++i)
      {
         result += cIndexList.get(i) + ", ";
      }
      result += cIndexList.get(cIndexList.size() - 1) + "])";
      return result;
   }
}
