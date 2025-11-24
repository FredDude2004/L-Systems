/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.primitives;

import java.util.List;
import java.util.ArrayList;

/**
   A {@code Triangle} object has two lists of three integers each.
   One list represents the three corners of the triangle in space.
   The integers are indices into the {@link renderer.scene.Vertex}
   list of a {@link renderer.scene.Model} object. The other list
   holds integer indices into the {@link java.awt.Color} list of
   that {@link renderer.scene.Model} object.
<p>
   Since a {@code Triangle} is made of three points, and three points
   determine a plane, a {@code Triangle} is always "flat".
<p>
   A {@code Triangle} is also always "convex".
<p>
   The combination of flat and convex means that we can give a
   {@code Triangle} an orientation and we can determine if the
   {@code Triangle} if "front facing" or "back facing".
*/
public class Triangle extends OrientablePrimitive
{
   // A Triangle object can have an index into its Model's list of Textures
   // and a list of indices to texture coordinates in the Model.
   public final boolean textured;
   public final int textureIndex;
   public final List<Integer> tcIndexList;

   /**
      Construct a {@code Triangle} object using three integer indices.
      Use the given indices for both the {@link renderer.scene.Vertex}
      list and the front and back {@link java.awt.Color} lists.

      @param i0  index for 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1  index for 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2  index for 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
   */
   public Triangle(final int i0, final int i1, final int i2)
   {
      this(i0, i1, i2,
           i0, i1, i2);
   }


   /**
      Construct a {@code Triangle} object using three integer indices
      for the {@link renderer.scene.Vertex} list and one integer index
      for the {@link java.awt.Color} list. Make the back face color the
      same as the front face color.

      @param i0  index of 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1  index of 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2  index of 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param c   index of the {@link java.awt.Color} of the new {@code Triangle}
   */
   public Triangle(final int i0, final int i1, final int i2, final int c)
   {
      this(i0, i1, i2,
           c,  c,  c);
   }


   /**
      Construct a {@code Triangle} object using three integer indices
      for the {@link renderer.scene.Vertex} list and three integer indices
      for the {@link java.awt.Color} list. Make the back face colors the
      same as the front face colors.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@code Triangle}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@code Triangle} gets rendered).

      @param i0  index of 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1  index of 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2  index of 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param c0  index of 1st {@link java.awt.Color} of the new {@code Triangle}
      @param c1  index of 2nd {@link java.awt.Color} of the new {@code Triangle}
      @param c2  index of 3rd {@link java.awt.Color} of the new {@code Triangle}
   */
   public Triangle(final int i0, final int i1, final int i2,
                   final int c0, final int c1, final int c2)
   {
      this(i0, i1, i2,
           c0, c1, c2,
           c0, c1, c2);
   }


   /**
      Construct a {@code Triangle} object using three integer indices
      for the {@link renderer.scene.Vertex} list, three integer indices
      for the front face {@link java.awt.Color} list, and three indices for
      the back face {@link java.awt.Color} list.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} objects into this {@code Triangle}'s
      {@link renderer.scene.Model} object. This constructor assumes that
      the given indices are valid (or will be valid by the time this
      {@code Triangle} gets rendered).

      @param i0  index of 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1  index of 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2  index of 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param c0  index of 1st {@link java.awt.Color} of the new {@code Triangle}
      @param c1  index of 2nd {@link java.awt.Color} of the new {@code Triangle}
      @param c2  index of 3rd {@link java.awt.Color} of the new {@code Triangle}
      @param c3  index of 1st backface {@link java.awt.Color} of the new {@code Triangle}
      @param c4  index of 2nd backface {@link java.awt.Color} of the new {@code Triangle}
      @param c5  index of 3rd backface {@link java.awt.Color} of the new {@code Triangle}
   */
   public Triangle(final int i0, final int i1, final int i2,
                   final int c0, final int c1, final int c2,
                   final int c3, final int c4, final int c5)
   {
      super();

      vIndexList.add(i0);
      vIndexList.add(i1);
      vIndexList.add(i2);
      cIndexList.add(c0);
      cIndexList.add(c1);
      cIndexList.add(c2);
      cIndexList2.add(c3);
      cIndexList2.add(c4);
      cIndexList2.add(c5);
      this.textured = false;
      this.textureIndex = -1;
      this.tcIndexList = new ArrayList<>();
   }


   /**
      Construct a {@code Triangle} object using three integer indices
      for the {@link renderer.scene.Vertex} list and three indices
      for the {@link renderer.scene.TexCoord} list.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link renderer.scene.TexCoord} objects into this {@code Triangle}'s
      {@link renderer.scene.Model} object. This constructor assumes that the
      given indices are valid (or will be valid by the time this {@code Triangle}
      gets rendered).

      @param i0   index of 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1   index of 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2   index of 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param tc0  index of 1st {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param tc1  index of 2nd {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param tc2  index of 3rd {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param textureIndex  integer index into a {@link renderer.scene.Texture} list
   */
   public Triangle(final int i0,  final int i1,  final int i2,
                   final int tc0, final int tc1, final int tc2,
                   final int textureIndex)
   {
      super();

      vIndexList.add(i0);
      vIndexList.add(i1);
      vIndexList.add(i2);
      cIndexList.add(0);  // assume the model has at least one color
      cIndexList.add(0);
      cIndexList.add(0);
      cIndexList2.add(0);
      cIndexList2.add(0);
      cIndexList2.add(0);
      this.tcIndexList = new ArrayList<>();
      tcIndexList.add(tc0);
      tcIndexList.add(tc1);
      tcIndexList.add(tc2);
      this.textured = true;
      this.textureIndex = textureIndex;
   }


   /**
      Construct a {@code Triangle} object using three integer indices
      for the {@link renderer.scene.Vertex} list, three integer indices
      for the front face {@link java.awt.Color} list, and three indices
      for the {@link renderer.scene.TexCoord} list.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} or {@link renderer.scene.TexCoord} objects
      into this {@code Triangle}'s {@link renderer.scene.Model} object. This
      constructor assumes that the given indices are valid (or will be valid
      by the time this {@code Triangle} gets rendered).

      @param i0   index of 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1   index of 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2   index of 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param c0   index of 1st {@link java.awt.Color} of the new {@code Triangle}
      @param c1   index of 2nd {@link java.awt.Color} of the new {@code Triangle}
      @param c2   index of 3rd {@link java.awt.Color} of the new {@code Triangle}
      @param tc0  index of 1st {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param tc1  index of 2nd {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param tc2  index of 3rd {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param textureIndex  integer index into a {@link renderer.scene.Texture} list
   */
   public Triangle(final int i0,  final int i1,  final int i2,
                   final int c0,  final int c1,  final int c2,
                   final int tc0, final int tc1, final int tc2,
                   final int textureIndex)
   {
      super();

      vIndexList.add(i0);
      vIndexList.add(i1);
      vIndexList.add(i2);
      cIndexList.add(c0);
      cIndexList.add(c1);
      cIndexList.add(c2);
      cIndexList2.add(c0);
      cIndexList2.add(c1);
      cIndexList2.add(c2);
      this.tcIndexList = new ArrayList<>();
      tcIndexList.add(tc0);
      tcIndexList.add(tc1);
      tcIndexList.add(tc2);
      this.textured = true;
      this.textureIndex = textureIndex;
   }


   /**
      Construct a {@code Triangle} object using three integer indices
      for the {@link renderer.scene.Vertex} list, three integer indices
      for the front face {@link java.awt.Color} list, three integer indices
      for the back face {@link java.awt.Color} list, and three indices
      for the {@link renderer.scene.TexCoord} list.
      <p>
      NOTE: This constructor does not put any {@link renderer.scene.Vertex}
      or {@link java.awt.Color} or {@link renderer.scene.TexCoord} objects
      into this {@code Triangle}'s {@link renderer.scene.Model} object. This
      constructor assumes that the given indices are valid (or will be valid
      by the time this {@code Triangle} gets rendered).

      @param i0   index of 1st {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i1   index of 2nd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param i2   index of 3rd {@link renderer.scene.Vertex} of the new {@code Triangle}
      @param c0   index of 1st {@link java.awt.Color} of the new {@code Triangle}
      @param c1   index of 2nd {@link java.awt.Color} of the new {@code Triangle}
      @param c2   index of 3rd {@link java.awt.Color} of the new {@code Triangle}
      @param c3   index of 1st backface {@link java.awt.Color} of the new {@code Triangle}
      @param c4   index of 2nd backface {@link java.awt.Color} of the new {@code Triangle}
      @param c5   index of 3rd backface {@link java.awt.Color} of the new {@code Triangle}
      @param tc0  index of 1st {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param tc1  index of 2nd {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param tc2  index of 3rd {@link renderer.scene.TexCoord} of the new {@code Triangle}
      @param textureIndex  integer index into a {@link renderer.scene.Texture} list
   */
   public Triangle(final int i0,  final int i1,  final int i2,
                   final int c0,  final int c1,  final int c2,
                   final int c3,  final int c4,  final int c5,
                   final int tc0, final int tc1, final int tc2,
                   final int textureIndex)
   {
      super();

      vIndexList.add(i0);
      vIndexList.add(i1);
      vIndexList.add(i2);
      cIndexList.add(c0);
      cIndexList.add(c1);
      cIndexList.add(c2);
      cIndexList2.add(c3);
      cIndexList2.add(c4);
      cIndexList2.add(c5);
      this.tcIndexList = new ArrayList<>();
      tcIndexList.add(tc0);
      tcIndexList.add(tc1);
      tcIndexList.add(tc2);
      this.textured = true;
      this.textureIndex = textureIndex;
   }


   /**
      Construct a {@code Triangle} object using the two given
      {@link List}s of integer indices. Make the back face colors
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
      @throws IllegalArgumentException if the size of {@code vIndexList} or {@code cIndexList} is not 3
   */
   public Triangle(final List<Integer> vIndexList,
                   final List<Integer> cIndexList)
   {
      super(vIndexList, cIndexList);
      this.textured = false;
      this.textureIndex = -1;
      this.tcIndexList = new ArrayList<>();
   }


   /**
      Construct a {@code Triangle} object using the three given
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
   public Triangle(final List<Integer> vIndexList,
                   final List<Integer> cIndexList,
                   final List<Integer> cIndexList2)
   {
      super(vIndexList, cIndexList, cIndexList2);
      this.textured = false;
      this.textureIndex = -1;
      this.tcIndexList = new ArrayList<>();
   }


   /**
      Construct a {@code Triangle} object using the four given
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
      @param tcIndexList  {@link List} of integer indices into a {@link java.awt.TexCoord} list
      @param textureIndex  integer index into a {@link renderer.scene.Texture} list
      @throws NullPointerException if {@code vIndexList} is {@code null}
      @throws NullPointerException if {@code cIndexList} is {@code null}
      @throws NullPointerException if {@code cIndexList2} is {@code null}
      @throws NullPointerException if {@code tcIndexList2} is {@code null}
      @throws IllegalArgumentException if the size of any one of the index lists is not 3
   */
   public Triangle(final List<Integer> vIndexList,
                   final List<Integer> cIndexList,
                   final List<Integer> cIndexList2,
                   final List<Integer> tcIndexList,
                   final int textureIndex)
   {
      super(vIndexList, cIndexList, cIndexList2);

      if (null == tcIndexList)
         throw new NullPointerException("tcIndexList must not be null");
      if (vIndexList.size() != tcIndexList.size() )
         throw new IllegalArgumentException("vIndexList and tcIndexList must be the same size");

      this.tcIndexList = tcIndexList;
      this.textured = true;
      this.textureIndex = textureIndex;
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Triangle} object
   */
   @Override
   public String toString()
   {
      String result = "Triangle: (["
                           + vIndexList.get(0)  + ", "
                           + vIndexList.get(1)  + ", "
                           + vIndexList.get(2)  + "], ["
                           + cIndexList.get(0)  + ", "
                           + cIndexList.get(1)  + ", "
                           + cIndexList.get(2)  + "], ["
                           + cIndexList2.get(0) + ", "
                           + cIndexList2.get(1) + ", "
                           + cIndexList2.get(2) + "]";
      if (this.textured)
      {
         result += " with texture index " + textureIndex;
         result += ", tc: [" + tcIndexList.get(0) + ", "
                             + tcIndexList.get(1) + ", "
                             + tcIndexList.get(2) + "]";
      }
      result += ")";
      return result;
   }
}
