/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_LP;

import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   Create a wireframe model of a camera's perspective view frustum.
   That is, create a frustum of a pyramid along the negative z-axis
   with its apex at the origin.
<p>
   This class has two constructors that mimic the projPerspective()
   methods from the {@link Camera} class.
<p>
   See <a href="https://en.wikipedia.org/wiki/Viewing_frustum" target="_top">
                https://en.wikipedia.org/wiki/Viewing_frustum</a>
*/
public class ViewFrustumModel extends Model
{
   /**
      Create a frustum of a pyramid along the negative z-axis with
      a front face at {@code z = -0.25}, a back face at {@code z = -1},
      and the front face bounded by {@code -0.25 <= x <= 0.25} and
      {@code -0.25 <= y <= 0.25}.
   */
   public ViewFrustumModel( )
   {
      this(-0.25, 0.25, -0.25, 0.25, 0.25, 1);
   }


   /**
      Create a frustum of a pyramid along the negative z-axis that
      mimics a {@link Camera}'s perspective view volume.

      @param left    left edge of the front face in the plane z = -near
      @param right   right edge of the front face in the plane z = -near
      @param bottom  bottom edge of the front face in the plane z = -near
      @param top     top edge of the front face in the plane z = -near
      @param near    distance from the origin to the front face
      @param far     distance from the origin to the back face
   */
   public ViewFrustumModel(final double left,   final double right,
                           final double bottom, final double top,
                           final double near,   final double far)
   {
      super("View Frustum Model");

      addVertex(new Vertex(left,  top,    -near),
                new Vertex(right, top,    -near),
                new Vertex(right, bottom, -near),
                new Vertex(left,  bottom, -near),
                new Vertex( (left/near)*far,    (top/near)*far, -far),
                new Vertex((right/near)*far,    (top/near)*far, -far),
                new Vertex((right/near)*far, (bottom/near)*far, -far),
                new Vertex( (left/near)*far, (bottom/near)*far, -far));


      addPrimitive(new LineLoop(0, 1, 2, 3),       // front (near) face
                   new LineLoop(4, 5, 6, 7),       // back (far) face
                   new Lines(0,4, 1,5, 2,6, 3,7)); // lines from front to back
   }


   /**
      Here, the frustum is determined by a vertical "field of view"
      angle and an aspect ratio for the front face.

      @param fovy    angle in the y-direction subtended by the front face
      @param aspect  aspect ratio of the front face
      @param near    distance from the origin to the front face
      @param far     distance from the origin to the back face
   */
   public ViewFrustumModel(final double fovy, final double aspect,
                           final double near, final double far)
   {
      // top    =  near * Math.tan((Math.PI/180.0)*fovy/2.0)
      // left   = -top * aspect
      // right  =  top * aspect
      // bottom = -top
      this(-near * Math.tan((Math.PI/180.0)*fovy/2.0) * aspect,
            near * Math.tan((Math.PI/180.0)*fovy/2.0) * aspect,
           -near * Math.tan((Math.PI/180.0)*fovy/2.0),
            near * Math.tan((Math.PI/180.0)*fovy/2.0),
            near,
            far);
   }
}//ViewFrustumModel
