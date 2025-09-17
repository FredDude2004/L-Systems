/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

/**
   A {@code TriangleFan} object has {@code n+2} integers that
   represent the vertices of {@code n} triangles that radiate
   from a central point and another {@code n+1} integers that
   represent the {@link java.awt.Color} at each of those vertices.
<p>
   The integers are indices into the {@link renderer.scene.Vertex} and
   {@link java.awt.Color} lists of a {@link renderer.scene.Model} object.
<p>
   See
     <a href="https://en.wikipedia.org/wiki/Triangle_fan" target="_top">
              https://en.wikipedia.org/wiki/Triangle_fan</a>
*/
public class TriangleFan extends OrientablePrimitive
{
   /**
      Construct a {@code TriangleFan} with the given array of indices for
      the {@link renderer.scene.Vertex} and {@link java.awt.Color} index lists.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param indices  array of vertex and color indices to place in this {@code TriangleFan}
   */
   public TriangleFan(final int... indices)
   {
      super(indices);
   }


   /**
      Give the triangles of this {@code TriangleFan} the back face colors
      indexed by the given color indices. The first color is for the center
      of the triangle fan. The other two colors will alternate around the
      periphery of the triangle fan.
      <p>
      NOTE: This method does not put any {@link java.awt.Color} objects
      into this {@code TriangleFan}'s {@link renderer.scene.Model}
      object. This method assumes that the given indices is valid (or will
      be valid by the time this {@code TriangleFan} gets rendered).

      @param c0  integer back face color index for this {@code TriangleFan}
      @param c1  integer back face color index for this {@code TriangleFan}
      @param c2  integer back face color index for this {@code TriangleFan}
   */
   public void setBackFaceColorIndex(int c0, int c1, int c2)
   {
      cIndexList2.set(0, c0);

      final int[] c = {c2, c1};
      for (int i = 1; i < cIndexList2.size(); ++i)
      {
         cIndexList2.set(i, c[i % 2]);
      }
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code TriangleFan} object
   */
   @Override
   public String toString()
   {
      String result =  "TriangleFan: ([";
      for (int i = 0; i < vIndexList.size() - 1; ++i)
      {
         result += vIndexList.get(i) + ", ";
      }
      result += vIndexList.get(vIndexList.size() - 1) + "], [";
      for (int i = 0; i < cIndexList.size() - 1; ++i)
      {
         result += cIndexList.get(i) + ", ";
      }
      result += cIndexList.get(cIndexList.size() - 1) + "], [";
      for (int i = 0; i < cIndexList2.size() - 1; ++i)
      {
         result += cIndexList2.get(i) + ", ";
      }
      result += cIndexList2.get(cIndexList2.size() - 1) + "])";
      return result;
   }
}
