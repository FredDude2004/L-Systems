/*
 * Renderer 15. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

import renderer.scene.primitives.*;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

/**
   A {@code Model} object represents a distinct geometric object in a
   {@link Scene}. A {@code Model} data structure is mainly a {@link List}
   of {@link Vertex} objects, a {@link List} of {@link Primitive} objects,
   and a list of {@link Color} objects.
<p>
   Each {@link Vertex} object contains the xyz-coordinates, in the
   {@code Model}'s own coordinate system, for one point from the
   {@code Model}.
<p>
   Each {@link Color} object represents a color associated to one
   (or more) {@link Vertex} objects.
<p>
   The {@link Vertex} objects represents points from the geometric object
   that we are modeling. In the real world, a geometric object has an infinite
   number of points. In 3D graphics, we "approximate" a geometric object by
   listing just enough points to adequately describe the object. For example,
   in the real world, a rectangle contains an infinite number of points, but
   it can be adequately modeled by just its four corner points. (Think about
   a circle. How many points does it take to adequately model a circle? Look
   at the {@link renderer.models_L.Circle} model.)
<p>
   {@link Primitive} objects represent "geometric primitives" that the model's
   geometry is build out of. There are twelve kinds of geometric primitives,
   <ul>
   <li>{@link LineSegment},
   <li>{@link LineStrip},
   <li>{@link LineLoop},
   <li>{@link LineFan},
   <li>{@link Lines},
   <li>{@link Point},
   <li>{@link Points},
   <li>{@link Face},
   <li>{@link Triangle},
   <li>{@link TriangleStrip},
   <li>{@link TriangleFan},
   <li>{@link Triangles}.
   </ul>
   Each {@link Primitive} object contains two lists of integers, one list
   made up of indices of {@link Vertex} objects from the {@code Model}'s
   vertex list, and the other list made up of indices of {@link Color}
   objects from the {@code Model}'s color list. Each {@link Vertex}
   object contains the coordinates, in the model's local coordinate
   system, for one point in the primitive. Each {@link Color} object
   contains the rgb values for a point in the primitive.
<p>
   A {@code Model} is also a recursive data structure. Every {@code Model}
   object contains a {@link List} of {@code Model} objects. The list of
   {@link Model}s lets us define what are called hierarchical, or nested,
   models. These are models that are made up of distinct parts and the
   distinct parts need to move both individually and also move together as
   a group. Each {@link Model} object nested inside of a {@code Model}
   contains its own {@link Matrix} which determines a local coordinate
   system for the vertices in that nested {@code Model} and for all the
   other {@code Model} objects that might be nested within that one.
<p>
   Each {@code Model}'s nested {@link Matrix} will place the nested
   {@link Model}'s {@link Model} within its parent's local coordinate
   system. Another way to say this is that the {@link Matrix} of a nested
   {@link Model} determines its {@link Model}'s location and orientation
   within the local coordinate system of the nested {@link Model}'s
   parent {@link Model}. In addition, the nested {@link Model}'s
   {@link Matrix} determines a new local coordinate system within which all
   the nested {@link Model}s below a {@link Model} will place their
   {@link Model}s
<p>
   We use {@link LineSegment} objects to represent the space between the
   model's vertices. For example, while a rectangle can be approximated by
   its four corner points, those same four points could also represent two
   parallel line segments, or they could represent two lines that cross each
   other. By using four line segments that connect around the four points,
   we get a good, unambiguous representation of a rectangle.
<p>
   If we modeled a circle using just points, we would probably need to use
   hundreds of points. But if we connect every two adjacent points with a
   short line segment, we can get a good model of a circle with just a few
   dozen points.
<p>
   Our {@code Model}'s represent geometric objects as a "wire-frame" of line
   segments, that is, a geometric object is drawn as a collection of "edges".
   This is a fairly simplistic way of doing 3D graphics and we will improve
   this in later renderers.
<p>
   See
<br> <a href="https://en.wikipedia.org/wiki/Wire-frame_model" target="_top">
              https://en.wikipedia.org/wiki/Wire-frame_model</a>
<br>or
<br> <a href="https://www.google.com/search?q=computer+graphics+wireframe&tbm=isch" target="_top">
              https://www.google.com/search?q=computer+graphics+wireframe&tbm=isch</a>
*/
public class Model
{
   public final List<Vertex> vertexList;
   public final List<Primitive> primitiveList;
   public final List<Color> colorList;
   public final String name;

   private Matrix matrix;
   public final List<Model> nestedModels;

   public final List<Texture> textureList;
   public final List<TexCoord> texCoordList;

   public boolean visible;
   public boolean doBackFaceCulling;
   public boolean frontFacingIsCCW;
   public boolean facesHaveTwoSides;

   /**
      Construct an empty {@code Model} object.
   */
   public Model()
   {
      this(new ArrayList<>(),  // vertexList
           new ArrayList<>(),  // primitiveList
           new ArrayList<>(),  // colorList
           "",                 // name
           Matrix.identity(),  // nestedMatrix
           new ArrayList<>(),  // nestedModels
           new ArrayList<>(),  // textureList
           new ArrayList<>(),  // texCoordList
           true,               // visible
           true,               // doBackFaceCulling
           true,               // frontFacingIsCCW
           false);             // facesHaveTwoSides
   }


   /**
      Construct an empty {@code Model} object with the given
      {link String} name.

      @param name  a {link String} that is a name for this {@code Model}
      @throws NullPointerException if {@code name} is {@code null}
   */
   public Model(final String name)
   {
      this(new ArrayList<>(),  // vertexList
           new ArrayList<>(),  // primitiveList
           new ArrayList<>(),  // colorList
           name,               // name
           Matrix.identity(),  // nestedMatrix
           new ArrayList<>(),  // nestedModels
           new ArrayList<>(),  // textureList
           new ArrayList<>(),  // texCoordList
           true,               // visible
           true,               // doBackFaceCulling
           true,               // frontFacingIsCCW
           false);             // facesHaveTwoSides
   }


   /**
      Construct an empty {@code Model} object with the given
      {link String} name and with the given transformation
      {@link Matrix}.

      @param name    {@link String} name for this {@code Model}
      @param matrix  transformation {@link Matrix} for this {@code Model}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code matrix} is {@code null}
   */
   public Model(final String name,
                final Matrix matrix)
   {
      this(new ArrayList<>(),  // vertexList
           new ArrayList<>(),  // primitiveList
           new ArrayList<>(),  // colorList
           name,               // name
           matrix,             // nestedMatrix
           new ArrayList<>(),  // nestedModels
           new ArrayList<>(),  // textureList
           new ArrayList<>(),  // texCoordList
           true,               // visible
           true,               // doBackFaceCulling
           true,               // frontFacingIsCCW
           false);             // facesHaveTwoSides
   }


   /**
      Construct a {@code Model} object with the given data. (For backwards
      compatability with the previous renderers.)

      @param vertexList     a {@link Vertex} {link List} for this {@code Model}
      @param primitiveList  a {@link Primitive} {link List} for this {@code Model}
      @param colorList      a {@link Color} {link List} for this {@code Model}
      @param name           a {link String} that is a name for this {@code Model}
      @param visible        a {@code boolean} that determines this {@code Model}'s visibility
      @throws NullPointerException if {@code vertexList} is {@code null}
      @throws NullPointerException if {@code primitiveList} is {@code null}
      @throws NullPointerException if {@code colorList} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
   */
   public Model(final List<Vertex> vertexList,
                final List<Primitive> primitiveList,
                final List<Color> colorList,
                final String name,
                final boolean visible)
   {
      this(vertexList,
           primitiveList,
           colorList,
           name,
           Matrix.identity(), // nestedMatrix
           new ArrayList<>(), // nestedModels
           new ArrayList<>(), // textureList
           new ArrayList<>(), // texCoordList
           visible,
           true,              // doBackFaceCulling
           true,              // frontFacingIsCCW
           false);            // facesHaveTwoSides
   }


   /**
      Construct a {@code Model} object with the given data. (For backwards
      compatability with the previous renderers.)

      @param vertexList     a {@link Vertex} {link List} for this {@code Model}
      @param primitiveList  a {@link Primitive} {link List} for this {@code Model}
      @param colorList      a {@link Color} {link List} for this {@code Model}
      @param name           a {link String} that is a name for this {@code Model}
      @param nestedMatrix   a {@link Matrix} to apply to the nested {@code Model}s
      @param nestedModels   a {link List} of nested {@code Model}s
      @param visible        a {@code boolean} that determines this {@code Model}'s visibility
      @throws NullPointerException if {@code vertexList} is {@code null}
      @throws NullPointerException if {@code primitiveList} is {@code null}
      @throws NullPointerException if {@code colorList} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code nestedMatrix} is {@code null}
      @throws NullPointerException if {@code nestedModels} is {@code null}
   */
   public Model(final List<Vertex> vertexList,
                final List<Primitive> primitiveList,
                final List<Color> colorList,
                final String name,
                final Matrix nestedMatrix,
                final List<Model> nestedModels,
                final boolean visible)
   {
      this(vertexList,
           primitiveList,
           colorList,
           name,
           nestedMatrix,
           nestedModels,
           new ArrayList<>(), // textureList
           new ArrayList<>(), // texCoordList
           visible,
           true,    // doBackFaceCulling
           true,    // frontFacingIsCCW
           false);  // facesHaveTwoSides
   }


   /**
      Construct a {@code Model} object with all the given data. (For backwards
      compatability with the previous renderers.)

      @param vertexList     a {@link Vertex} {link List} for this {@code Model}
      @param primitiveList  a {@link Primitive} {link List} for this {@code Model}
      @param colorList      a {@link Color} {link List} for this {@code Model}
      @param name           a {link String} that is a name for this {@code Model}
      @param nestedMatrix   a {@link Matrix} to apply to the nested {@code Model}s
      @param nestedModels   a {link List} of nested {@code Model}s
      @param visible        a {@code boolean} that determines this {@code Model}'s visibility
      @param doBackFaceCulling  a {@code boolean} that turns culling off/on for this {@code Model}
      @param frontFacingIsCCW   a {@code boolean} that determines face orientation for this {@code Model}
      @param facesHaveTwoSides  a {@code boolean} that determines face sidedness for this {@code Model}
      @throws NullPointerException if {@code vertexList} is {@code null}
      @throws NullPointerException if {@code primitiveList} is {@code null}
      @throws NullPointerException if {@code colorList} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code nestedMatrix} is {@code null}
      @throws NullPointerException if {@code nestedModels} is {@code null}
   */
   public Model(final List<Vertex> vertexList,
                final List<Primitive> primitiveList,
                final List<Color> colorList,
                final String name,
                final Matrix nestedMatrix,
                final List<Model> nestedModels,
                final boolean visible,
                final boolean doBackFaceCulling,
                final boolean frontFacingIsCCW,
                final boolean facesHaveTwoSides)
   {
      this(vertexList,
           primitiveList,
           colorList,
           name,
           nestedMatrix,
           nestedModels,
           new ArrayList<>(), // textureList
           new ArrayList<>(), // texCoordList
           visible,
           doBackFaceCulling,
           frontFacingIsCCW,
           facesHaveTwoSides);
   }


   /**
      Construct a {@code Model} object with all the given data.

      @param vertexList     a {@link Vertex} {link List} for this {@code Model}
      @param primitiveList  a {@link Primitive} {link List} for this {@code Model}
      @param colorList      a {@link Color} {link List} for this {@code Model}
      @param name           a {link String} that is a name for this {@code Model}
      @param nestedMatrix   a {@link Matrix} to apply to the nested {@code Model}s
      @param nestedModels   a {link List} of nested {@code Model}s
      @param textureList    a {link List} of {@link Texture}s used by this {@code Model}
      @param texCoordList   a {link List} of {@link TexCoord}s used by this {@code Model}
      @param visible        a {@code boolean} that determines this {@code Model}'s visibility
      @param doBackFaceCulling  a {@code boolean} that turns culling off/on for this {@code Model}
      @param frontFacingIsCCW   a {@code boolean} that determines face orientation for this {@code Model}
      @param facesHaveTwoSides  a {@code boolean} that determines face sidedness for this {@code Model}
      @throws NullPointerException if {@code vertexList} is {@code null}
      @throws NullPointerException if {@code primitiveList} is {@code null}
      @throws NullPointerException if {@code colorList} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code nestedMatrix} is {@code null}
      @throws NullPointerException if {@code nestedModels} is {@code null}
      @throws NullPointerException if {@code textureList} is {@code null}
      @throws NullPointerException if {@code texCoordList} is {@code null}
   */
   public Model(final List<Vertex> vertexList,
                final List<Primitive> primitiveList,
                final List<Color> colorList,
                final String name,
                final Matrix nestedMatrix,
                final List<Model> nestedModels,
                final List<Texture> textureList,
                final List<TexCoord> texCoordList,
                final boolean visible,
                final boolean doBackFaceCulling,
                final boolean frontFacingIsCCW,
                final boolean facesHaveTwoSides)
   {
      if (null == vertexList)
         throw new NullPointerException("vertexList must not be null");
      if (null == primitiveList)
         throw new NullPointerException("primitiveList must not be null");
      if (null == colorList)
         throw new NullPointerException("colorList must not be null");
      if (null == name)
         throw new NullPointerException("name must not be null");
      if (null == nestedMatrix)
         throw new NullPointerException("nestedMatrix must not be null");
      if (null == nestedModels)
         throw new NullPointerException("nestedModels must not be null");
      if (null == textureList)
         throw new NullPointerException("textureList must not be null");
      if (null == texCoordList)
         throw new NullPointerException("texCoordList must not be null");

      this.vertexList = vertexList;
      this.primitiveList = primitiveList;
      this.colorList = colorList;
      this.matrix = nestedMatrix;
      this.nestedModels = nestedModels;
      this.name = name;
      this.textureList = textureList;
      this.texCoordList = texCoordList;
      this.visible = visible;
      this.doBackFaceCulling = doBackFaceCulling;
      this.frontFacingIsCCW = frontFacingIsCCW;
      this.facesHaveTwoSides = facesHaveTwoSides;
   }


   /**
      Add a {@link Vertex} (or vertices) to this {@code Model}'s
      {@link List} of vertices.

      @param vArray  array of {@link Vertex} objects to add to this {@code Model}
      @throws NullPointerException if any {@link Vertex} is {@code null}
   */
   public final void addVertex(final Vertex... vArray)
   {
      for (final Vertex v : vArray)
      {
         if (null == v)
            throw new NullPointerException("Vertex must not be null");

         vertexList.add( v );
      }
   }


   /**
      Get a {@link Primitive} from this {@code Model}'s
      {@link List} of primitives.

      @param index  integer index of a {@link Primitive} from this {@code Model}
      @return the {@link Primitive} object at the given index
   */
   public final Primitive getPrimitive(final int index)
   {
      return primitiveList.get(index);
   }


   /**
      Add a {@link Primitive} (or Primitives) to this {@code Model}'s
      {@link List} of primitives.
      <p>
      NOTE: This method does not add any vertices to the {@code Model}'s
      {@link Vertex} list. This method assumes that the appropriate vertices
      have been added to the {@code Model}'s {@link Vertex} list.

      @param pArray  array of {@link Primitive} objects to add to this {@code Model}
      @throws NullPointerException if any {@link Primitive} is {@code null}
   */
   public final void addPrimitive(final Primitive... pArray)
   {
      for (final Primitive p : pArray)
      {
         if (null == p)
            throw new NullPointerException("Primitive must not be null");

         primitiveList.add(p);
      }
   }


   /**
      Add a {@link Color} (or colors) to this {@code Model}'s
      {@link List} of colors.

      @param cArray  array of {@link Color} objects to add to this {@code Model}
      @throws NullPointerException if any {@link Color} is {@code null}
   */
   public final void addColor(final Color... cArray)
   {
      for (final Color c : cArray)
      {
         if (null == c)
            throw new NullPointerException("Color must not be null");

         this.colorList.add(c);
      }
   }


   /**
      Add a nested {@code Model} (or Models) to this {@code Model}'s
      {@link List} of nested {@code Model}s.

      @param mArray  array of nested {@code Model}s to add to this {@code Model}
      @throws NullPointerException if any {@link Model} is {@code null}
   */
   public void addNestedModel(final Model... mArray)
   {
      for (final Model m : mArray)
      {
         if (null == m)
            throw new NullPointerException("Model must not be null");

         nestedModels.add(m);
      }
   }


   /**
      Get a reference to the nested {@code Model} at the given index in
      this {@code Model}'s {@link List} of nested {@code Model}s.

      @param index  index of the nested {@code Model} to return
      @return nested {@code Model} at the specified index in the {@link List} of nested {@code Model}s
      @throws IndexOutOfBoundsException if the index is out of range
   */
   public Model getNestedModel(final int index)
   {
      return nestedModels.get(index);
   }


   /**
      Set a reference to the given {@link Model} object at the given index in this {@code Model}'s
      {@link List} of nested {@link Model}s.

      @param index  index of the nested {@link Model} to set
      @param model  {@link Model} object to place at the specified index in the {@link List} of nested {@link Model}s
      @throws NullPointerException if {@code model} is {@code null}
      @throws IndexOutOfBoundsException if the index is out of range
   */
   public void setNestedModel(final int index, final Model model)
   {
      if (null == model)
         throw new NullPointerException("model must not be null");

      nestedModels.set(index, model);
   }


   /**
      Get a reference to this {@code Model}'s {@link Matrix} object.

      @return a reference to this {@code Model}'s {@link Matrix} object
   */
   public Matrix getMatrix()
   {
      return this.matrix;
   }


   /**
      Set this {@code Model}'s nested transformation {@link Matrix}.

      @param matrix  {@link Matrix} object to use in this {@code Model}
      @return a reference to this {@link Model} object to facilitate chaining method calls
      @throws NullPointerException if {@code matrix} is {@code null}
   */
   public Model transform(final Matrix matrix)
   {
      if (null == matrix)
         throw new NullPointerException("matrix must not be null");

      this.matrix = matrix;
      return this;
   }


   /**
      Add a {@link Texture} (or Textures) to this {@code Model}'s
      {@link List} of {@link Texture}s.

      @param tArray  array of {@link Texture}s to add to this {@code Model}
      @throws NullPointerException if any {@link Texture} is {@code null}
   */
   public void addTexture(final Texture... tArray)
   {
      for (final Texture t : tArray)
      {
         if (null == t)
            throw new NullPointerException("Texture must not be null");

         this.textureList.add(t);
      }
   }


   /**
      Get a {@link Texture} from this {@code Model}'s
      {@link List} of {@link Texture}s.

      @param index  index of the {@link Texture} to return
      @return {@link Texture} at the specified index in the {@link List} of {@link Texture}s
      @throws IndexOutOfBoundsException if the index is out of range
              {@code (index < 0 || index >= textureList.size())}
   */
   public Texture getTexture(final int index)
   {
      return this.textureList.get(index);
   }


   /**
      Replace a {@link Texture} in this {@code Model}'s
      {@link List} of {@link Texture}s.

      @param index  index of the {@link Texture} to replace
      @param tex    {@link Texture} to be stored at the specified index
      @return       {@link Texture} previously at the specified index
      @throws NullPointerException if {@code tex} is {@code null}
      @throws IndexOutOfBoundsException if the index is out of range
              {@code (index < 0 || index >= textureList.size())}
   */
   public Texture setTexture(final int index, final Texture tex)
   {
      if (null == tex)
         throw new NullPointerException("Texture must not be null");

      return this.textureList.set(index, tex);
   }


   /**
      Add a {@link TexCoord} (or TexCoords) to this {@code Model}'s
      {@link List} of {@link TexCoord}s.

      @param tcArray  array of {@link TexCoord}s to add to this {@code Model}
      @throws NullPointerException if any {@link TexCoord} is {@code null}
   */
   public void addTextureCoord(final TexCoord... tcArray)
   {
      for (final TexCoord tc : tcArray)
      {
         if (null == tc)
            throw new NullPointerException("TexCoord must not be null");

         this.texCoordList.add(tc);
      }
   }


   /**
      Add a {@link TexCoord} to this {@code Model}'s
      {@link List} of {@link TexCoord}s.

      @param s  first coordinate of new {@link TexCoord}
      @param t  second coordinate of new {@link TexCoord}
   */
   public void addTextureCoord(final double s, final double t)
   {
      this.texCoordList.add(new TexCoord(s, t));
   }


   /**
      Recursively set the value of {@code doBackFaceCulling} for
      this {@code Model} and all of its nested models.

      @param doBackFaceCulling  new boolean value for {@code doBackFaceCulling}
   */
   public void setBackFaceCulling(final boolean doBackFaceCulling)
   {
      this.doBackFaceCulling = doBackFaceCulling;

      for (final Model m : this.nestedModels)
      {
         m.setBackFaceCulling(doBackFaceCulling);
      }
   }


   /**
      Recursively set the value of {@code frontFacingIsCCW} for
      this {@code Model} and all of its nested models.

      @param frontFacingIsCCW  new boolean value for {@code frontFacingIsCCW}
   */
   public void setFrontFacingIsCCW(final boolean frontFacingIsCCW)
   {
      this.frontFacingIsCCW = frontFacingIsCCW;

      for (final Model m : this.nestedModels)
      {
         m.setFrontFacingIsCCW(frontFacingIsCCW);
      }
   }


   /**
      Recursively set the value of {@code facesHaveTwoSides} for
      this {@code Model} and all of its nested models.

      @param facesHaveTwoSides  new boolean value for {@code facesHaveTwoSides}
   */
   public void setFacesHaveTwoSides(final boolean facesHaveTwoSides)
   {
      this.facesHaveTwoSides = facesHaveTwoSides;

      for (final Model m : this.nestedModels)
      {
         m.setFacesHaveTwoSides(facesHaveTwoSides);
      }
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Model} object
   */
   @Override
   public String toString()
   {
      String result = "";
      result += "Model: " + name + "\n";
      result += "This Model's visibility is: " + visible + "\n";
      result += "doBackFaceCulling is: " + doBackFaceCulling + "\n";
      result += "frontFacingIsCCW is: " + frontFacingIsCCW + "\n";
      result += "facesHaveTwoSides is: " + facesHaveTwoSides + "\n";
      result += "Model has " + vertexList.size() + " vertices.\n";
      result += "Model has " + colorList.size() + " colors.\n";
      result += "Model has " + primitiveList.size() + " primitives.\n";
      result += "Model has " + nestedModels.size() + " nested models\n";
      result += "Model has " + textureList.size() + " textures.\n";
      result += "Model has " + texCoordList.size() + " texture coordinate pairs.\n";
      int i = 0;
      for (final Vertex v : this.vertexList)
      {
         result += i + ": " + v.toString() + "\n";
         ++i;
      }
      i = 0;
      for (final Color c : this.colorList)
      {
         result += i + ": " + c.toString() + "\n";
         ++i;
      }
      i = 0;
      for (final Primitive p : this.primitiveList)
      {
         result += i + ": " + p.toString() + "\n";
         ++i;
      }
      i = 0;
      for (final Texture tex : this.textureList)
      {
         result += i + ": " + tex.toString() + "\n";
         ++i;
      }
      i = 0;
      for (final TexCoord tc : this.texCoordList)
      {
         result += i + ": " + tc.toString() + "\n";
         ++i;
      }
      result += matrix.toString() + "\n";
      for (final Model m : this.nestedModels)
      {
         result += m.toString();
      }
      return result;
   }
}
