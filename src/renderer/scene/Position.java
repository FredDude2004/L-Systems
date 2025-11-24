/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

import java.util.List;
import java.util.ArrayList;

/**
   A {@code Position} data structure represents a group of geometric objects
   positioned (both location and orientation) in three-dimensional space as
   part of a {@link Scene}. A {@code Position} is a recursive data structure.
   Every {@code Position} object contains a {@link List} of nested
   {@code Position} objects. The list of nested {@code Position}s lets us
   define what are called hierarchical, or nested, scenes. These are scenes
   that are made up of groups of models and each group can be moved around
   in the scene as a single unit while the individual models in the group
   can also be moved around within the group.
<p>
   A {@code Position} object holds references to a {@link Model} object,
   a {@link Matrix} object, and a {@code List} of {@code Position} objects.
   A {@code Position}'s {@code List} of {@code Position} objects creates
   a tree data structure of {@code Position} objects. A {@code Position}'s
   {@link Model} object represents a geometric shape in the {@link Scene}.
   The role of a {@code Position}'s  {@link Matrix} can be understood two
   ways. First, the {@link Matrix} determines the {@link Model}'s location
   and orientation within the local coordinate system determined by the
   {@code Position}'s parent {@code Position} (in the {@link Scene}'s forest
   of {@code Position} objects). Second, the {@link Matrix} determines a new
   local coordinate system within which the {@link Model} (and all the nested
   models lower in the tree) is plotted. The two ways of understanding a
   {@code Position}'s  {@link Matrix} correspond to reading a matrix
   transformation expression
   <pre>{@code
                T * v
   }</pre>
   from either right-to-left or left-to-right.
<p>
   When the renderer renders a {@code Position} object, the renderer
   traverses the tree of {@code Position}s rooted at the given
   {@code Position}. The renderer does a recursive, pre-order
   depth-first-traversal of the tree. As the renderer traverses the tree,
   it accumulates a "current-transformation-matrix" that multiplies each
   {@code Position}'s {@link Matrix} along the path from the tree's root
   {@code Position} to wherever the traversal is in the tree (this is the
   "pre-order" step in the traversal). The {@code ctm} is the current
   model-to-view transformation {@link Matrix}. The first stage of the
   rendering pipeline, {@link renderer.pipeline.Model2World}, multiplies every
   {@link Vertex} in a {@link Model}'s vertex list by this {@code ctm}, which
   converts the coordinates in each {@link Vertex} from the model's own local
   coordinate system to the {@code Camera}'s view coordinate system (which is "shared"
   by all the other models). Multiplication by the {@code ctm} has the effect
   of "placing" the model in view space at an appropriate location (using the
   translation part of the {@code ctm}) and in the appropriate orientation
   (using the rotation part of the {@code ctm}). Notice the difference between
   a {@code Position}'s {@code ctm} and the {@code Position}'s {@link Matrix}.
   At any specific node in the {@link Scene}'s forest of {@code Position} nodes,
   the {@code Position}'s {@link Matrix} places the {@code Position}'s
   {@link Model} within the local coordinate system of the {@code Position}'s
   parent {@code Position}. But the {@code Position}'s {@code ctm} places the
   {@code Position}'s {@link Model} within the {@link Camera}'s view coordinate
   system.
*/
public final class Position
{
   private Model model;
   private Matrix matrix;
   public final String name;
   public final List<Position> nestedPositions;
   public boolean visible;
   public boolean debug;

   /**
      Construct a {@code Position} with the identity {@link Matrix},
      the given {@link Model} object, and no nested {@code Position}s.

      @param model  {@link Model} object to place at this {@code Position}
      @throws NullPointerException if {@code model} is {@code null}
   */
   public Position(final Model model)
   {
      this(model,
           model.name,        // default Position name
           Matrix.identity(), // default matrix
           new ArrayList<>(), // nestedPositions
           true,              // visible
           false);            // debug
   }


   /**
      Construct a {@code Position} with the identity {@link Matrix},
      the given {@link String} name, the given {@link Model} object,
      and no nested {@code Position}s.

      @param model  {@link Model} object to place at this {@code Position}
      @param name   {@link String} name for this {@code Position}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
   */
   public Position(final Model model, final String name)
   {
      this(model,
           name,
           Matrix.identity(), // default matrix
           new ArrayList<>(), // nestedPositions
           true,              // visible
           false);            // debug
   }


   /**
      Construct a {@code Position} with the given transformation {@link Matrix},
      the given {@link String} name, the given {@link Model} object, and no
      nested {@code Position}s.

      @param model   {@link Model} object to place at this {@code Position}
      @param name    {@link String} name for this {@code Position}
      @param matrix  transformation {@link Matrix} for this {@code Position}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code matrix} is {@code null}
   */
   public Position(final Model model,
                   final String name,
                   final Matrix matrix)
   {
      this(model,
           name,
           matrix,
           new ArrayList<>(), // nestedPositions
           true,              // visible
           false);            // debug
   }


   /**
      Construct a {@code Position} with the given transformation {@link Matrix},
      the given {@link Model} object, and no nested {@code Position}s.

      @param model   {@link Model} object to place at this {@code Position}
      @param matrix  transformation {@link Matrix} for this {@code Position}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code matrix} is {@code null}
   */
   public Position(final Model model,
                   final Matrix matrix)
   {
      this(model,
           model.name,        // default Position name
           matrix,
           new ArrayList<>(), // nestedPositions
           true,              // visible
           false);            // debug
   }


   /**
      Construct a {@code Position} with the given translation {@link Vector},
      the given {@link String} name, and the given {@link Model} object.

      @deprecated  This constructor is here for compatibility with renderers 1 through 8.

      @param model        {@link Model} object to place at this {@code Position}
      @param name         {@link String} name for this {@code Position}
      @param translation  translation {@link Vector} for this {@code Position}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code translation} is {@code null}
   */
   @Deprecated
   public Position(final Model model,
                   final String name,
                   final Vector translation)
   {
      this(model,
           name,
           Matrix.translate(translation.x, translation.y, translation.z),
           new ArrayList<>(), // nestedPositions
           true,   // visible
           false); // debug
   }


   /**
      Construct a {@code Position} with the given translation {@link Vector}
      and the given {@link Model} object.

      @deprecated  This constructor is here for compatibility with renderers 1 through 8.

      @param model        {@link Model} object to place at this {@code Position}
      @param translation  translation {@link Vector} for this {@code Position}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code translation} is {@code null}
   */
   @Deprecated
   public Position(final Model model,
                   final Vector translation)
   {
      this(model,
           model.name,  // default Position name
           Matrix.translate(translation.x, translation.y, translation.z),
           new ArrayList<>(), // nestedPositions
           true,        // visible
           false);      // debug
   }


   /**
      Construct a {@code Position} object with all the given data.

      @deprecated  This constructor is here for compatibility with renderers 1 through 8.

      @param model        {@link Model} object to place at this {@code Position}
      @param name         {@link String} name for this {@code Position}
      @param translation  translation {@link Vector} for this {@code Position}
      @param visible      boolean that determines this {@code Position}'s visibility
      @param debug        boolean that determines if this {@code Position} is logged
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code translation} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
   */
   @Deprecated
   public Position(final Model model,
                   final String name,
                   final Vector translation,
                   final boolean visible,
                   final boolean debug)
   {
      this(model,
           model.name,  // default Position name
           Matrix.translate(translation.x, translation.y, translation.z),
           new ArrayList<>(), // nestedPositions
           visible,
           debug);
   }


   /**
      Construct a {@code Position} object with all the given data.

      @param model            {@link Model} object to place at this {@code Position}
      @param name             {@link String} name for this {@code Position}
      @param matrix           transformation {@link Matrix} for this {@code Position}
      @param nestedPositions  a {link List} of nested {@link Position}s
      @param visible          boolean that determines this {@code Position}'s visibility
      @param debug            boolean that determines if this {@code Position} is logged
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
      @throws NullPointerException if {@code matrix} is {@code null}
      @throws NullPointerException if {@code nestedPositions} is {@code null}
   */
   public Position(final Model model,
                   final String name,
                   final Matrix matrix,
                   final List<Position> nestedPositions,
                   final boolean visible,
                   final boolean debug)
   {
      if (null == model)
         throw new NullPointerException("model must not be null");
      if (null == name)
         throw new NullPointerException("name must not be null");
      if (null == matrix)
         throw new NullPointerException("matrix must not be null");
      if (null == nestedPositions)
         throw new NullPointerException("nestedPositions must not be null");

      this.model = model;
      this.matrix = matrix;
      this.name = name;
      this.nestedPositions = nestedPositions;
      this.visible = visible;
      this.debug = debug;
   }


   /**
      Add a nested {@code Position} (or Positions) to this {@code Position}'s
      {@link List} of nested {@code Position}s.

      @param pArray  array of nested {@code Position}s to add to this {@code Position}
      @throws NullPointerException if any {@link Position} is {@code null}
   */
   public void addNestedPosition(final Position... pArray)
   {
      for (final Position p : pArray)
      {
         if (null == p)
            throw new NullPointerException("Position must not be null");

         nestedPositions.add(p);
      }
   }


   /**
      Get a reference to the nested {@code Position} at the given index in
      this {@code Position}'s {@link List} of nested {@code Position}s.

      @param index  index of the nested {@code Position} to return
      @return nested {@code Position} at the specified index in the {@link List} of nested {@code Position}s
      @throws IndexOutOfBoundsException if the index is out of range
   */
   public Position getNestedPosition(final int index)
   {
      return nestedPositions.get(index);
   }


   /**
      Set a reference to the given {@link Position} object at the given index in this {@code Position}'s
      {@link List} of nested {@link Position}s.

      @param index     index of the nested {@link Position} to set
      @param position  {@link Position} object to place at the specified index in the {@link List} of nested {@link Position}s
      @throws NullPointerException if {@code position} is {@code null}
      @throws IndexOutOfBoundsException if the index is out of range
   */
   public void setNestedPosition(final int index, final Position position)
   {
      if (null == position)
         throw new NullPointerException("position must not be null");

      nestedPositions.set(index, position);
   }


   /**
      Get a reference to this {@code Position}'s {@link Model} object.

      @return a reference to this {@code Position}'s {@link Model} object
   */
   public Model getModel()
   {
      return this.model;
   }


   /**
      Set this {@code Position}'s {@link Model} object.

      @param model  {@link Model} object to place at this {@code Position}
      @return a reference to this {@link Position} object to facilitate chaining method calls
      @throws NullPointerException if {@code model} is {@code null}
   */
   public Position setModel(final Model model)
   {
      if (null == model)
         throw new NullPointerException("model must not be null");

      this.model = model;
      return this;
   }


   /**
      Get a reference to this {@code Position}'s {@link Matrix} object.

      @return a reference to this {@code Position}'s {@link Matrix} object
   */
   public Matrix getMatrix()
   {
      return this.matrix;
   }


   /**
      Set this {@code Position}'s transformation {@link Matrix}.

      @param matrix  {@link Matrix} object to use in this {@code Position}
      @return a reference to this {@link Position} object to facilitate chaining method calls
      @throws NullPointerException if {@code matrix} is {@code null}
   */
   public Position transform(final Matrix matrix)
   {
      if (null == matrix)
         throw new NullPointerException("matrix must not be null");

      this.matrix = matrix;
      return this;
   }


   /**
      Set this {@code Position}'s translation vector within
      this {@code Position}'s {@link Matrix} object.

      @deprecated  Use the {@link Matrix} API instead of this method.
      This method is here for compatibility with renderers 1 through 8.

      @param x  translation amount in the x-direction
      @param y  translation amount in the y-direction
      @param z  translation amount in the z-direction
      @return a reference to this {@link Position} object to facilitate chaining method calls
   */
   @Deprecated
   public Position translate(final double x,
                             final double y,
                             final double z)
   {
      this.matrix = Matrix.translate(x, y, z);
      return this;
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Position} object
   */
   @Override
   public String toString()
   {
      String result = "";
      result += "Position: " + name + "\n";
      result += "This Position's visibility is: " + visible + "\n";
      result += "This Position's Matrix is\n";
      result += matrix + "\n";
      result += "This Position's Model is\n";
      result += (null == model) ? "null\n" : model;
      result += "This Position has " + nestedPositions.size() + " nested Positions\n";
      for (Position p : this.nestedPositions)
      {
         result += p.toString();
      }
      return result;
   }
}
