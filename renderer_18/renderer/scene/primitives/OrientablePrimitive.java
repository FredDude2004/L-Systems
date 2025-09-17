/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

import java.util.List;
import java.util.ArrayList;

/**
   An {@code OrientablePrimitive} is a {@link Primitive} that can
   be given an orientation, so it has a both a front and a back face.
<p>
   We have five orientable geometric primitives,
   <ul>
   <li>{@link Face},
   <li>{@link Triangle},
   <li>{@link TriangleStrip},
   <li>{@link TriangleFan},
   <li>{@link Triangles}.
   </ul>
<p>
   Each {@code OrientablePrimitive} holds three lists of integer indices.
<p>
   One list is of indices into its {@link renderer.scene.Model}'s {@link List}
   of {@link renderer.scene.Vertex} objects. These are the vertices
   that determine the primitive's geometry.
<p>
   The other two lists are lists of indices into its
   {@link renderer.scene.Model}'s {@link List} of {@link java.awt.Color}
   objects. One index list determines the {@code OrientablePrimitive}'s
   front facing colors and the other index list determines the
   {@code OrientablePrimitive}'s back facing colors.
<p>
   The three lists of integer indices must always have the same length.
   For every {@link renderer.scene.Vertex} index in a
   {@code OrientablePrimitive} there must be a front face
   {@link java.awt.Color} index and a back face {@link java.awt.Color}
   index.
*/
public abstract class OrientablePrimitive extends Primitive
{
   // A OrientablePrimitive object has an extra
   // color list for back facing colors.
   public final List<Integer> cIndexList2;

   /**
      Construct an empty {@code OrientablePrimitive}.
   */
   protected OrientablePrimitive()
   {
      super();
      this.cIndexList2 = new ArrayList<>();
   }


   /**
      Construct a {@code OrientablePrimitive} with the given array of
      indices for the {@link renderer.scene.Vertex} and
      {@link java.awt.Color} index lists. Make the backface colors the
      same as the front face colors.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link OrientablePrimitive}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@link OrientablePrimitive} gets rendered).

      @param indices  array of {@link renderer.scene.Vertex} and {@link java.awt.Color} indices to place in this {@code OrientablePrimitive}
   */
   protected OrientablePrimitive(final int... indices)
   {
      this();

      for (final int i : indices)
      {
         addIndices(i, i, i);
      }
   }


   /**
      Construct a {@code OrientablePrimitive} object using the two given
      {@link List}s of integer indices. Make the backface colors
      the same as the front face colors.
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
   public OrientablePrimitive(final List<Integer> vIndexList,
                              final List<Integer> cIndexList)
   {
      super(vIndexList, cIndexList);
      this.cIndexList2 = cIndexList;
   }


   /**
      Construct a {@code OrientablePrimitive} object using the three given
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
      @throws IllegalArgumentException if the size of any one of the index lists is not the same as the others
   */
   public OrientablePrimitive(final List<Integer> vIndexList,
                              final List<Integer> cIndexList,
                              final List<Integer> cIndexList2)
   {
      super(vIndexList, cIndexList);
      if (null == cIndexList2)
         throw new NullPointerException("cIndexList2 must not be null");
      if (vIndexList.size() != cIndexList2.size() )
         throw new IllegalArgumentException("vIndexList and cIndexList2 must be the same size");
      this.cIndexList2 = cIndexList2;
   }


   /**
      Add the given array of indices to the {@link renderer.scene.Vertex}
      and {@link java.awt.Color} index lists. Make the backface colors the
      same as the front face colors.
      <p>
      NOTE: This method does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@link OrientablePrimitive}'s
      {@link renderer.scene.Model} object. This method assumes that
      the given indices are valid (or will be valid by the time this
      {@link OrientablePrimitive} gets rendered).

      @param indices  array of {@link renderer.scene.Vertex} and {@link java.awt.Color} indices to add to this {@code OrientablePrimitive}
   */
   @Override public void addIndex(final int... indices)
   {
      for (final int i : indices)
      {
         addIndices(i, i, i);
      }
   }


   /**
      Add the given indices to the {@link renderer.scene.Vertex} and
      both {@link java.awt.Color} index lists. Make the backface color
      the same as the front face color.
      <p>
      NOTE: This method does not put any {@link renderer.scene.Vertex} or
      {@link java.awt.Color} objects into this {@link OrientablePrimitive}'s
      {@link renderer.scene.Model} object. This method assumes that
      the given indices are valid (or will be valid by the time this
      {@link OrientablePrimitive} gets rendered).

      @param vIndex  integer {@link renderer.scene.Vertex} index to add to this {@code OrientablePrimitive}
      @param cIndex  integer {@link java.awt.Color} index to add to this {@code OrientablePrimitive}
   */
   @Override public void addIndices(final int vIndex, final int cIndex)
   {
      addIndices(vIndex, cIndex, cIndex);
   }


   /**
      Add the given indices to the {@link renderer.scene.Vertex} and
      {@link java.awt.Color} index lists.
      <p>
      NOTE: This method does not put any {@link renderer.scene.Vertex} or
      {@link java.awt.Color} objects into this {@link OrientablePrimitive}'s
      {@link renderer.scene.Model} object. This method assumes that
      the given indices are valid (or will be valid by the time this
      {@link OrientablePrimitive} gets rendered).

      @param vIndex  integer {@link renderer.scene.Vertex} index to add to this {@code OrientablePrimitive}
      @param cIndex  integer front face {@link java.awt.Color} index to add to this {@code OrientablePrimitive}
      @param bIndex  integer back face {@link java.awt.Color} index to add to this {@code OrientablePrimitive}
   */
   public void addIndices(final int vIndex, final int cIndex, final int bIndex)
   {
      vIndexList.add(vIndex);
      cIndexList.add(cIndex);
      cIndexList2.add(bIndex);
   }


   /**
      Give this {@code OrientablePrimitive} the back face {@link java.awt.Color}
      indexed by the given color index. Assign multiple backface
      indices in a cyclical manner.
      <p>
      NOTE: This method does not put any {@link java.awt.Color} objects
      into this {@code OrientablePrimitive}'s {@link renderer.scene.Model}
      object. This method assumes that the given indices is valid (or will
      be valid by the time this {@code Primitive} gets rendered).

      @param cIndex  integer back face color index for this {@code OrientablePrimitive}
   */
   public void setBackFaceColorIndex(int... cIndex)
   {
      for (int i = 0; i < cIndexList2.size(); ++i)
      {
         cIndexList2.set(i, cIndex[i % cIndex.length]);
      }
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code OrientablePrimitive} object
   */
   @Override
   public abstract String toString();
}
