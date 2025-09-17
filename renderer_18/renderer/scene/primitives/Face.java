/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

import java.util.List;
import java.util.ArrayList;

/**
   A {@code Face} object has a list of integers that represent
   the endpoints of a closed loop of line segments. The integers
   are indices into the {@link renderer.scene.Vertex} list of
   a {@link renderer.scene.Model} object.
<p>
   A {@code Face} is closed loop of line segments that is assumed
   to be "flat" and "convex".
<p>
   Flat means that the vertices of the {@code Face} all lie in
   the same plane.
<p>
   Convex means that a line segment joining any two vertices of
   the {@code Face} must be completely contained inside of the
   {@code Face}.
<p>
   The combination of flat and convex means that we can give a
   {@code Face} an orientation and we can determine if the
   {@code Face} if "front facing" or "back facing".
*/
public class Face extends OrientablePrimitive
{
   /**
      Construct an empty {@code Face}.
   */
   public Face()
   {
      super();
   }


   /**
      Construct a {@code Face} with the given array of indices for
      the {@link renderer.scene.Vertex} and {@link java.awt.Color} index lists.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param indices  array of {@link renderer.scene.Vertex} and {@link java.awt.Color} indices to place in this {@code Face}
   */
   public Face(final int... indices)
   {
      super(indices);
   }


   /**
      Construct a {@code Face} object using the two given
      {@link List}s of integer indices.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param vIndexList  {@link List} of integer indices into a {@link renderer.scene.Vertex} list
      @param cIndexList  {@link List} of integer indices into a {@link java.awt.Color} list
      @throws NullPointerException if {@code vIndexList} is {@code null}
      @throws NullPointerException if {@code cIndexList} is {@code null}
      @throws IllegalArgumentException if {@code vIndexList} and {@code cIndexList} are not the same size
   */
   public Face(final List<Integer> vIndexList,
               final List<Integer> cIndexList)
   {
      super(vIndexList, cIndexList);
   }


   /**
      Construct a {@code Face} object using the three given
      {@link List}s of integer indices.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link Primitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link Primitive} gets rendered).

      @param vIndexList   {@link List} of integer indices into a {@link renderer.scene.Vertex} list
      @param cIndexList   {@link List} of integer indices into a {@link java.awt.Color} list
      @param cIndexList2  {@link List} of integer indices into a {@link java.awt.Color} list
      @throws NullPointerException if {@code vIndexList} is {@code null}
      @throws NullPointerException if {@code cIndexList} is {@code null}
      @throws NullPointerException if {@code cIndexList2} is {@code null}
      @throws IllegalArgumentException if the size of any one of the index lists is not 3
   */
   public Face(final List<Integer> vIndexList,
               final List<Integer> cIndexList,
               final List<Integer> cIndexList2)
   {
      super(vIndexList, cIndexList, cIndexList2);
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Face} object
   */
   @Override
   public String toString()
   {
      String result =  "Face: ([";
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
      result += cIndexList2.get(cIndexList2.size() - 1) + "])\n";
      return result;
   }
}
