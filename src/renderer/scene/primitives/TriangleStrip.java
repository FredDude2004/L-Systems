/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

/**
   A {@code TriangleStrip} object has {@code n+2} integers that
   represent the vertices of {@code n} contiguous triangles and
   another {@code n+2} integers that represent the
   {@link java.awt.Color} at each of those vertices.
<p>
   The integers are indices into the {@link renderer.scene.Vertex} and
   {@link java.awt.Color} lists of a {@link renderer.scene.Model} object.
<p>
   See
     <a href="https://en.wikipedia.org/wiki/Triangle_strip" target="_top">
              https://en.wikipedia.org/wiki/Triangle_strip</a>
*/
public class TriangleStrip extends OrientablePrimitive
{
   /**
      Construct a {@code TriangleStrip} with the given array of indices for
      the {@link renderer.scene.Vertex} and {@link java.awt.Color} index lists.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param indices  array of vertex and color indices to place in this {@code TriangleStrip}
   */
   public TriangleStrip(int... indices)
   {
      super(indices);
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code TriangleStrip} object
   */
   @Override
   public String toString()
   {
      String result =  "TriangleStrip: ([";
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
