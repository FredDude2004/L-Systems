/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

/**
   A {@code Triangles} object has {@code 3n} integers that
   represent the vertices of {@code n} triangles and another
   {@code 3n} integers that represent the
   {@link java.awt.Color} at each of those vertices.
<p>
   The integers are indices into the {@link renderer.scene.Vertex} and
   {@link java.awt.Color} lists of a {@link renderer.scene.Model} object.
*/
public class Triangles extends OrientablePrimitive
{
   /**
      Construct a {@code Triangles} with the given array of indices for
      the {@link renderer.scene.Vertex} and {@link java.awt.Color} index lists.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param indices  array of vertex and color indices to place in this {@code Triangles}
   */
   public Triangles(int... indices)
   {
      super(indices);
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Triangles} object
   */
   @Override
   public String toString()
   {
      String result =  "Triangles: ([";
      for (int i = 0; i < vIndexList.size() - 1; i++)
      {
         result += vIndexList.get(i) + ", ";
      }
      result += vIndexList.get(vIndexList.size() - 1) + "], [";
      for (int i = 0; i < cIndexList.size() - 1; i++)
      {
         result += cIndexList.get(i) + ", ";
      }
      result += cIndexList.get(cIndexList.size() - 1) + "], [";
      for (int i = 0; i < cIndexList2.size() - 1; i++)
      {
         result += cIndexList2.get(i) + ", ";
      }
      result += cIndexList2.get(cIndexList2.size() - 1) + "])";
      return result;
   }
}
