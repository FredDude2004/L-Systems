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
public final class Pipeline
{
   // Mostly for compatibility with renderers 1 through 3.
   public static Color DEFAULT_COLOR = Color.white;

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
      PipelineLogger.debugScene = scene.debug;

      logMessage("\n== Begin Rendering of Scene (Pipeline 1): " + scene.name + " ==");

      logMessage("-- Current Camera:\n" + scene.camera);

      // For every Position in the Scene, render the Position's Model.
      for (final Position position : scene.positionList)
      {
         PipelineLogger.debugPosition = position.debug;

         if ( position.visible )
         {
            // Begin a pre-order, depth-first-traversal from this Position.
            render_position(scene, position, Matrix.identity(), vp);
         }
         else
         {
            logMessage("==== Hidden position: " + position.name);
         }
      }
      logMessage("== End Rendering of Scene (Pipeline 1) ==");
   }


   /**
      Recursively renderer a {@link Position}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Position}'s rooted at the parameter {@code position}.
      <p>
      The pre-order "visit node" operation in this traversal first updates the
      "current transformation matrix", ({@code ctm}), using the {@link Matrix}
      in {@code position} and then renders the {@link Model} in {@code position}
      using the updated {@code ctm} in the {@link Model2View} stage.

      @param scene     the {@link Scene} that we are rendering
      @param position  the current {@link Position} object to recursively render
      @param ctm       current model-to-view transformation {@link Matrix}
      @param vp       {@link FrameBuffer.Viewport} to hold rendered image of the {@link Scene}
   */
   private static void render_position(final Scene scene,
                                       final Position position,
                                       final Matrix ctm,
                                       final FrameBuffer.Viewport vp)
   {
      logMessage("==== Render position: " + position.name + " ====");

      logMessage("---- Transformation matrix:\n" + position.getMatrix());

      // Update the current model-to-view transformation matrix.
      final Matrix ctm2 = ctm.times( position.getMatrix() );

      // Recursively render this Position's Model, if the model is visible.
      if ( position.getModel().visible )
      {
         render_model(scene, position.getModel(), ctm2, vp);
      }
      else
      {
         logMessage("====== Hidden model: " + position.getModel().name + " ======");
      }

      // Recursively render every nested Position of this Position.
      for (final Position p : position.nestedPositions)
      {
         if ( p.visible )
         {
            // Do a pre-order, depth-first-traversal from this nested Position.
            render_position(scene, p, ctm2, vp); // recursion
         }
         else
         {
            logMessage("====== Hidden position " + position.name + " ====");
         }
      }

      logMessage("==== End position: " + position.name + " ====");
   }


   /**
      Recursively renderer a {@link Model}.
      <p>
      This method does a pre-order, depth-first-traversal of the tree of
      {@link Model}'s rooted at the parameter {@code model}.
      <p>
      The pre-order "visit node" operation in this traversal first updates the
      "current transformation matrix", ({@code ctm}), using the {@link Matrix}
      in {@code model} and then renders the geometry in {@code model}
      using the updated {@code ctm} in the {@link Model2View} stage.

      @param scene  the {@link Scene} that we are rendering
      @param model  the current {@link Model} object to recursively render
      @param ctm    current model-to-view transformation {@link Matrix}
      @param vp     {@link FrameBuffer.Viewport} to hold rendered image of the {@link Scene}
   */
   private static void render_model(final Scene scene,
                                    final Model model,
                                    final Matrix ctm,
                                    final FrameBuffer.Viewport vp)
   {

      logMessage("====== Render Model: " + model.name + " ======");

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

      logVertexList("0. Model       ", model);

      // Update the current model-to-world transformation matrix.
      final Matrix ctm2 = ctm.times( model.getMatrix() );

      // 1. Apply the current model-to-world coordinate transformation.
      final Model model1 = Model2World.model2world(model, ctm2);

      logVertexList("1. World       ", model1);

      // 2. Apply the Camera's world-to-view coordinate transformation.
      final Model model2 = World2View.world2view(model1, scene.camera);

      logVertexList("2. View        ", model2);

      // 3. Apply the Camera's normalizing view-to-camera coordinate transformation.
      final Model model3 = View2Camera.view2camera(model2, scene.camera);

      logVertexList("3. Camera      ", model3);

      // 4. Do the first back face culling operation (of Face primitives).
      logMessage("4. Culling_1 (Faces)" + ": doBackFaceCulling = "
                                        + model.doBackFaceCulling
                                        + ", frontFacingIsCCW = "
                                        + model.frontFacingIsCCW
                                        + ", facesHaveTwoSides = "
                                        + model.facesHaveTwoSides);
      final Model model4 = BackFaceCulling.cull(model3, scene.camera);

      logPrimitiveList("4. Culled_1", model4);

      // 5. Do the primitive assembly operation.
      final Model model5 = PrimitiveAssembly.assemble(model4);

      logPrimitiveList("5. Primitive Assembly ", model4);
      logPrimitiveList("5. Primitive Assembled", model5);

      // 6. Do the second back face culling operation (of Triangle primitives).
      logMessage("6. Culling_2 (Triangles)" + ": doBackFaceCulling = "
                                            + model.doBackFaceCulling
                                            + ", frontFacingIsCCW = "
                                            + model.frontFacingIsCCW
                                            + ", facesHaveTwoSides = "
                                            + model.facesHaveTwoSides);
      final Model model6 = BackFaceCulling2.cull(model5, scene.camera);

      logPrimitiveList("6. Culled_2", model6);

      // 7. Clip primitives to the camera's near plane.
      final Model model7 = NearClip.clip(model6, scene.camera);

      logVertexList("7. Near_Clipped", model7);
      logColorList("7. Near_Clipped", model7);
      logPrimitiveList("7. Near_Clipped", model7);

      // 8. Apply the Camera's projection transformation.
      final Model model8 = Projection.project(model7, scene.camera);

      logVertexList("8. Projected   ", model8);

      // 9. Clip primitives to the camera's view rectangle.
      final Model model9 = Clip.clip(model8);

      logVertexList("9. Clipped     ", model9);
      logColorList("9. Clipped     ", model9);
      logPrimitiveList("9. Clipped     ", model9);

      // 10. Rasterize every visible primitive into pixels.
      Rasterize.rasterize(model9, vp);

      // Recursively transform every nested Model of this Model.

      // Do a pre-order, depth-first-traversal from this Model.
      for (final Model m : model.nestedModels)
      {
         if ( m.visible )
         {
            render_model(scene, m, ctm2, vp); // recursion
         }
         else
         {
            logMessage("====== Hidden model: " + m.name + " ======");
         }
      }

      logMessage("====== End Model: " + model.name + " ======");
   }



   // Private default constructor to enforce noninstantiable class.
   // See Item 4 in "Effective Java", 3rd Ed, Joshua Bloch.
   private Pipeline() {
      throw new AssertionError();
   }
}
