/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.pipeline;

import renderer.scene.*;
import renderer.scene.util.CheckModel;
import renderer.framebuffer.*;
import static renderer.pipeline.PipelineLogger.*;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**
   This renderer takes as its input a {@link Scene} data structure
   and a {@link FrameBuffer.Viewport} within a {@link FrameBuffer}
   data structure. This renderer mutates the {@link FrameBuffer.Viewport}
   so that it is filled in with the rendered image of the geometric
   scene represented by the {@link Scene} object.
<p>
    This implements our fourteenth rendering pipeline. This renderer
    adds higher order triangle {@link Primitive}s to the {@link Model}s.
    The new {@link Primitive}s change the {@link PrimitiveAssembly}
    stage and force the back face culling to be done in two stages
    (one before and one after {@link PrimitiveAssembly}). There are
    now ten pipeline stages.
*/
public final class Pipeline2
{
   // Mostly for compatibility with renderers 1 through 3.
   public static Color DEFAULT_COLOR = Color.white;

   // Make all the intermediate Scene objects
   // available for special effects processing.
   public static Scene scene1 = null;
   public static Scene scene2 = null;
   public static Scene scene3 = null;
   public static Scene scene4 = null;
   public static Scene scene5 = null;
   public static Scene scene6 = null;
   public static Scene scene7 = null;
   public static Scene scene8 = null;
   public static Scene scene9 = null;

   /**
      Mutate the {@link FrameBuffer}'s default {@link FrameBuffer.Viewport}
      so that it holds the rendered image of the {@link Scene} object.

      @param scene  {@link Scene} object to render
      @param fb     {@link FrameBuffer} to hold rendered image of the {@link Scene}
   */
   public static void render(final Scene scene, final FrameBuffer fb)
   {
      render(scene, fb.vp); // render into the default viewport
   }


   /**
      Mutate the {@link FrameBuffer}'s given {@link FrameBuffer.Viewport}
      so that it holds the rendered image of the {@link Scene} object.

      @param scene  {@link Scene} object to render
      @param vp     {@link FrameBuffer.Viewport} to hold rendered image of the {@link Scene}
   */
   public static void render(final Scene scene, final FrameBuffer.Viewport vp)
   {
      logMessage("\n= Begin Rendering of Scene (Pipeline 2): " + scene.name + " =");

      logMessage("- Current Camera:\n" + scene.camera);

      scene1 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 1. Begin model-to-world transformation of Scene ====");
      for (final Position position : scene.positionList)
      {
         if ( position.visible )
         {
            final Position renderedPosition = model2world(position, Matrix.identity());
            scene1.addPosition( renderedPosition );
         }
         else
         {
            logMessage("==== 1. Hidden position: " + position.name + " ====");
         }
      }
      logMessage("== 1. End model-to-world transformation of Scene ====");

      scene2 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 2. Begin world-to-view transformation of Scene ====");
      for (final Position position : scene1.positionList)
      {
         scene2.addPosition( world2view(position, scene.camera) );
      }
      logMessage("== 2. End world-to-view transformation of Scene ====");

      scene3 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 3. Begin view-to-camera transformation of Scene ====");
      for (final Position position : scene2.positionList)
      {
         scene3.addPosition( view2camera(position, scene.camera) );
      }
      logMessage("== 3. End view-to-camera transformation of Scene ====");

      scene4 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 4. Begin backface culling_1 of Scene. ====");
      for (final Position position : scene3.positionList)
      {
         if ( position.visible )
         {
            scene4.addPosition( backFaceCulling(position, scene.camera) );
         }
      }
      logMessage("== 4. End backface culling_1 of Scene. ====");

      scene5 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 5. Begin primitive assembly of Scene ====");
      for (final Position position : scene4.positionList)
      {
         scene5.addPosition( primitiveAssembly(position) );
      }
      logMessage("== 5. End primitive assembly of Scene ====");

      scene6 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 6. Begin backface culling_2 of Scene. ====");
      for (final Position position : scene5.positionList)
      {
         if ( position.visible )
         {
            scene6.addPosition( backFaceCulling2(position, scene.camera) );
         }
      }
      logMessage("== 6. End backface culling_2 of Scene. ====");

      scene7 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 7. Begin near-plane clipping of Scene ====");
      for (final Position position : scene6.positionList)
      {
         scene7.addPosition( nearClip(position, scene.camera) );
      }
      logMessage("== 7. End near-plane clipping of Scene ====");

      scene8 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 8. Begin projection transformation of Scene ====");
      for (final Position position : scene7.positionList)
      {
         scene8.addPosition( project(position, scene.camera) );
      }
      logMessage("== 8. End projection transformation of Scene ====");

      scene9 = new Scene(scene.name, scene.camera, new ArrayList<>(), scene.debug);

      logMessage("== 9. Begin primitive clipping of Scene ====");
      for (final Position position : scene8.positionList)
      {
         scene9.addPosition( clip(position) );
      }
      logMessage("== 9. End primitive clipping of Scene ====");

      logMessage("== 10. Begin primitive rasterization of Scene ====");
      for (final Position position : scene9.positionList)
      {
         rasterize(position, vp);
      }
      logMessage("== 10. End primitive rasterization of Scene ====");

      logMessage("= End Rendering of Scene (Pipeline 2) =");
   }


   /**
      Pipeline stage 1, model-to-world transformation.
      <p>
      Recursively transform a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.
      <p>
      The pre-order "visit node" operation in this traversal first updates the
      "current transformation matrix", ({@code ctm}), using the {@link Matrix}
      in {@code position} and then transforms the {@link Model} in {@code position}
      using the updated {@code ctm}.

      @param scene     the {@link Scene} that we are rendering
      @param position  the current {@link Position} object to recursively transform
      @param ctm       current model-to-view transformation {@link Matrix}
      @return a tree of transformed {@link Position} objects
   */
   private static Position model2world(final Position position,
                                       final Matrix ctm)
   {
      logMessage("==== 1. Render position: " + position.name + " ====");

      logMessage("---- Transformation matrix:\n" + position.getMatrix());

      // Update the current model-to-view transformation matrix.
      final Matrix ctm2 = ctm.times( position.getMatrix() );

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            model2world(position.getModel(), ctm2),
                            position.name);
      }
      else
      {
         logMessage("====== 1. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         if ( p.visible )
         {
            final Position renderedPosition = model2world(p, ctm2); // recursion
            if ( renderedPosition.getModel() != null
              || ! renderedPosition.nestedPositions.isEmpty() )
            {
               position2.addNestedPosition( renderedPosition );
            }
         }
         else
         {
            logMessage("==== Hidden position: " + p.name + " ====");
         }
      }

      logMessage("==== 1. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 2, world-to-view transformation.
      <p>
      Recursively transform a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively transform
      @param camera    the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of transformed {@link Position} objects
   */
   private static Position world2view(final Position position,
                                      final Camera camera)
   {
      logMessage("==== 2. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            world2view(position.getModel(), camera),
                            position.name);
      }
      else
      {
         logMessage("====== 2. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         position2.addNestedPosition( view2camera(p, camera) ); // recursion
      }

      logMessage("==== 2. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 3, view-to-camera transformation.
      <p>
      Recursively transform a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively transform
      @param camera    the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of transformed {@link Position} objects
   */
   private static Position view2camera(final Position position,
                                       final Camera camera)
   {
      logMessage("==== 3. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            view2camera(position.getModel(), camera),
                            position.name);
      }
      else
      {
         logMessage("====== 3. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         position2.addNestedPosition( view2camera(p, camera) ); // recursion
      }

      logMessage("==== 3. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 4, first backface culling (of Face primitives).
      <p>
      Recursively cull a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively cull
      @param camera    the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of culled {@link Position} objects
   */
   private static Position backFaceCulling(final Position position,
                                           final Camera camera)
   {
      logMessage("==== 4. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            backFaceCulling(position.getModel(), camera),
                            position.name);
      }
      else
      {
         logMessage("==== 4. Hidden model: "
                          + position.getModel().name + " ====");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         if ( p.visible )
         {
            position2.addNestedPosition( backFaceCulling(p, camera) ); // recursion
         }
      }

      logMessage("==== 4. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 5, primitive assembly.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to assemble
      @return a tree of assembled {@link Position} objects
   */
   private static Position primitiveAssembly(final Position position)
   {
      logMessage("==== 5. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            primitiveAssembly(position.getModel()),
                            position.name);
      }
      else
      {
         logMessage("====== 5. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         position2.addNestedPosition( primitiveAssembly(p) ); // recursion
      }

      logMessage("==== 5. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 6, second backface culling (of Triangle primitives).
      <p>
      Recursively cull a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively cull
      @param camera    the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of culled {@link Position} objects
   */
   private static Position backFaceCulling2(final Position position,
                                            final Camera camera)
   {
      logMessage("==== 6. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            backFaceCulling2(position.getModel(), camera),
                            position.name);
      }
      else
      {
         logMessage("==== 6. Hidden model: "
                          + position.getModel().name + " ====");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         if ( p.visible )
         {
            position2.addNestedPosition( backFaceCulling(p, camera) ); // recursion
         }
      }

      logMessage("==== 6. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 7, near-plane clipping.
      <p>
      Recursively clip a {@link Position} at the {@link Camera}'s near plane.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively clip
      @param camera  {@link Camera} that determines the near clipping plane
      @return a tree of clipped {@link Position} objects
   */
   private static Position nearClip(final Position position,
                                    final Camera camera)
   {
      logMessage("==== 7. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            nearClip(position.getModel(), camera),
                            position.name);
      }
      else
      {
         logMessage("====== 7. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         position2.addNestedPosition( nearClip(p, camera) ); // recursion
      }

      logMessage("==== 7. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 8, projection transformation.
      <p>
      Recursively project a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively project
      @param camera    the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of projected {@link Position} objects
   */
   private static Position project(final Position position,
                                   final Camera camera)
   {
      logMessage("==== 8. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            project(position.getModel(), camera),
                            position.name);
      }
      else
      {
         logMessage("====== 8. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         position2.addNestedPosition( project(p, camera) ); // recursion
      }

      logMessage("==== 8. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 9, clipping.
      <p>
      Recursively clip a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively clip
      @return a tree of clipped {@link Position} objects
   */
   private static Position clip(final Position position)
   {
      logMessage("==== 9. Render position: " + position.name + " ====");

      // Create a new Position to hold the newly rendered
      // Model and the newly rendered sub-Positions.
      final Position position2;

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         position2 = new Position(
                            clip(position.getModel()),
                            position.name);
      }
      else
      {
         logMessage("====== 9. Hidden model: "
                            + position.getModel().name + " ======");
         position2 = new Position(position.getModel(), position.name);
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         position2.addNestedPosition( clip(p) ); // recursion
      }

      logMessage("==== 9. End position: " + position.name + " ====");

      return position2;
   }


   /**
      Pipeline stage 10, rasterization.
      <p>
      Recursively rasterize a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.

      @param position  the current {@link Position} object to recursively rasterize
      @param vp       {@link FrameBuffer.Viewport} to hold rendered image of the {@link Scene}
   */
   private static void rasterize(final Position position,
                                 final FrameBuffer.Viewport vp)
   {
      logMessage("==== 10. Render position: " + position.name + " ====");

      // Render this Position's model, if the model is visible.
      if ( position.getModel().visible )
      {
         rasterize(position.getModel(), vp);
      }
      else
      {
         logMessage("====== 10. Hidden model: "
                            + position.getModel().name + " ======");
      }

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Position p : position.nestedPositions)
      {
         rasterize(p, vp); // recursion
      }

      logMessage("==== 10. End position: " + position.name + " ====");
   }


/****** Pipeline stages for Models ********/

   /**
      Pipeline stage 1, model-to-world transformation.
      <p>
      Recursively renderer a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.
      <p>
      The pre-order "visit node" operation in this traversal first updates the
      "current transformation matrix", ({@code ctm}), using the {@link Matrix}
      in {@code model} and then renders the geometry in {@code model}
      using the updated {@code ctm} in the {@link Model2View} stage.

      @param model  the current {@link Model} object to recursively render
      @param ctm    current model-to-view transformation {@link Matrix}
      @return a tree of transformed {@link Model} objects
   */
   private static Model model2world(final Model model,
                                    final Matrix ctm)
   {
      logMessage("====== 1. Model-to-world transformation of: "
                         + model.name + " ======");

      CheckModel.check(model);

      // Mostly for compatibility with renderers 1 through 3.
      if (  model.colorList.isEmpty()
        && !model.vertexList.isEmpty())
      {
         for (int i = 0; i < model.vertexList.size(); ++i)
         {
            model.addColor( DEFAULT_COLOR );
         }
         System.err.println("***WARNING: Added default color to model: "
                           + model.name + ".");
      }

      logVertexList("0. Model    ", model);

      // Update the current model-to-view transformation matrix.
      final Matrix ctm2 = ctm.times( model.getMatrix() );

      final Model model2 = Model2World.model2world(model, ctm2);

      logVertexList("1. World    ", model2);

      // Recursively transform every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this Model.
      for (final Model m : model.nestedModels)
      {
         if ( m.visible )
         {
            newNestedModelList.add( model2world(m, ctm2) ); // recursion
         }
         else
         {
            logMessage("====== 1. Hidden model: " + m.name + " ======");
         }
      }

      logMessage("====== 1. End Model: " + model2.name + " ======");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       Matrix.identity(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 2, world-to-view transformation.
      <p>
      Recursively transform a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model   the current {@link Model} object to recursively transform
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of transformed {@link Model} objects
   */
   private static Model world2view(final Model model,
                                   final Camera camera)
   {
      logMessage("==== 2. World-to-View transformation of: "
                       + model.name + " ====");

      final Model model2 = World2View.world2view(model, camera);

      logVertexList("2. View     ", model2);

      // Recursively transform every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this Model.
      for (final Model m : model.nestedModels)
      {
         newNestedModelList.add( world2view(m, camera) ); // recursion
      }

      logMessage("==== 2. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 3, view-to-camera transformation.
      <p>
      Recursively transform a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model   the current {@link Model} object to recursively transform
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of transformed {@link Model} objects
   */
   private static Model view2camera(final Model model,
                                    final Camera camera)
   {
      logMessage("==== 3. View-to-Camera transformation of: "
                       + model.name + " ====");

      final Model model2 = View2Camera.view2camera(model, camera);

      logVertexList("3. Camera   ", model2);

      // Recursively transform every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this Model.
      for (final Model m : model.nestedModels)
      {
         newNestedModelList.add( view2camera(m, camera) ); // recursion
      }

      logMessage("==== 3. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 4, first backface culling (of Face primitives).
      <p>
      Recursively cull a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model   the current {@link Model} object to recursively cull
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of culled {@link Model} objects
   */
   private static Model backFaceCulling(final Model model,
                                        final Camera camera)
   {
      logMessage("==== 4. Culling_1 model: " + model.name + " ====");
      logMessage("=====4. Culling_1, Faces" + ": doBackFaceCulling = "
                                            + model.doBackFaceCulling
                                            + ", frontFacingIsCCW = "
                                            + model.frontFacingIsCCW
                                            + ", facesHaveTwoSides = "
                                            + model.facesHaveTwoSides);

      final Model model2 = BackFaceCulling.cull(model, camera);

      logPrimitiveList("4. Culled_1", model2);

      // Recursively cull every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList =
                           new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Model m : model.nestedModels)
      {
         if ( m.visible )
         {
            newNestedModelList.add( backFaceCulling(m, camera) ); // recursion
         }
      }

      logMessage("==== 4. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 5, primitive assembly.
      <p>
      Recursively assemble a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model   the current {@link Model} object to recursively assemble
      @return a tree of transformed {@link Model} objects
   */
   private static Model primitiveAssembly(final Model model)
   {
      logMessage("==== 5. Primitive assembly of: " + model.name + " ====");

      logPrimitiveList("5. Primitive Assembly ", model);

      final Model model2 = PrimitiveAssembly.assemble(model);

      logPrimitiveList("5. Primitive Assembled", model2);

      // Recursively assemble every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this Model.
      for (final Model m : model.nestedModels)
      {
         newNestedModelList.add( primitiveAssembly(m) ); // recursion
      }

      logMessage("==== 5. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 6, second backface culling (of Triangle primitives).
      <p>
      Recursively cull a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model   the current {@link Model} object to recursively cull
      @param camera  the {@link Scene}'s {@link Camera} with the view volume data
      @return a tree of culled {@link Model} objects
   */
   private static Model backFaceCulling2(final Model model,
                                         final Camera camera)
   {
      logMessage("==== 6. Culling_2 model: " + model.name + " ====");
      logMessage("=====6. Culling_2, Triangles" + ": doBackFaceCulling = "
                                                + model.doBackFaceCulling
                                                + ", frontFacingIsCCW = "
                                                + model.frontFacingIsCCW
                                                + ", facesHaveTwoSides = "
                                                + model.facesHaveTwoSides);

      final Model model2 = BackFaceCulling.cull(model, camera);

      logPrimitiveList("6. Culled_2", model2);

      // Recursively cull every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList =
                           new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Model m : model.nestedModels)
      {
         if ( m.visible )
         {
            newNestedModelList.add( backFaceCulling2(m, camera) ); // recursion
         }
      }

      logMessage("==== 6. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 7, near-plane clipping.
      <p>
      Recursively clip a {@link Model} at the {@link Camera}'s near plane.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model  the current {@link Model} object to recursively clip
      @param camera  {@link Camera} that determines the near clipping plane
      @return a tree of clipped {@link Model} objects
   */
   private static Model nearClip(final Model model, final Camera camera)
   {
      logMessage("==== 7. Near_Clip model: " + model.name + " ====");

      final Model model2 = NearClip.clip(model, camera);

      logVertexList("7. Near_Clipped  ", model2);
      logColorList("7. Near_Clipped  ", model2);
      logPrimitiveList("7. Near_Clipped  ", model2);

      // Recursively clip every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Model m : model.nestedModels)
      {
         newNestedModelList.add( nearClip(m, camera) ); // recursion
      }

      logMessage("==== 7. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 8, projection transformation.
      <p>
      Recursively project a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model   the current {@link Model} object to recursively project
      @param camera  a reference to the {@link Scene}'s {@link Camera} object
      @return a tree of projected {@link Model} objects
   */
   private static Model project(final Model model,
                                final Camera camera)
   {
      logMessage("==== 8. Project model: " + model.name + " ====");

      final Model model2 = Projection.project(model, camera);

      logVertexList("8. Projected", model2);

      // Recursively project every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Model m : model.nestedModels)
      {
         newNestedModelList.add( project(m, camera) ); // recursion
      }

      logMessage("==== 8. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 9, clipping.
      <p>
      Recursively clip a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model  the current {@link Model} object to recursively clip
      @return a tree of clipped {@link Model} objects
   */
   private static Model clip(final Model model)
   {
      logMessage("==== 9. Clip model: " + model.name + " ====");

      final Model model2 = Clip.clip(model);

      logVertexList("9. Clipped  ", model2);
      logColorList("9. Clipped  ", model2);
      logPrimitiveList("9. Clipped  ", model2);

      // Recursively clip every nested Model of this Model.

      // A new model list to hold the transformed nested models.
      final List<Model> newNestedModelList
                           = new ArrayList<>(model.nestedModels.size());

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Model m : model.nestedModels)
      {
         newNestedModelList.add( clip(m) ); // recursion
      }

      logMessage("==== 9. End Model: " + model2.name + " ====");

      return new Model(model2.vertexList,
                       model2.primitiveList,
                       model2.colorList,
                       model2.name,
                       model2.getMatrix(),
                       newNestedModelList,
                       model2.visible,
                       model2.doBackFaceCulling,
                       model2.frontFacingIsCCW,
                       model2.facesHaveTwoSides);
   }


   /**
      Pipeline stage 10, rasterization.
      <p>
      Recursively rasterize a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.

      @param model  the current {@link Model} object to recursively rasterize
      @param vp     {@link FrameBuffer.Viewport} to hold rendered image of the {@link Scene}
   */
   private static void rasterize(final Model model,
                                 final FrameBuffer.Viewport vp)
   {
      logMessage("==== 10. Rasterize model: " + model.name + " ====");

      Rasterize.rasterize(model, vp);

      // Recursively rasterize every nested Model of this Model.

      // Do a pre-order, depth-first-traversal from this nested Position.
      for (final Model m : model.nestedModels)
      {
         rasterize(m, vp); // recursion
      }

      logMessage("==== 10. End Model: " + model.name + " ====");
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Pipeline2() {
      throw new AssertionError();
   }
}
