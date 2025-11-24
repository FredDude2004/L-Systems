/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

/**
   This {@code Camera} data structure represents a camera that
   can be translated and oriented within world coordinates.
<p>
   This {@code Camera} has a {@link viewMatrix} which gives this
   {@code Camera} a location and an orientation in the world
   coordinate system. A {@code Camera} object is positioned and
   aimed within world coordinates using its {@link #viewTranslate}
   and {@link #viewRotate} methods.
<p>
   The {@link viewTranslate} and {@link viewRotate} methods are used
   to position and orient the {@code Camera} in world coordinates
   exactly as the {@link Matrix#translate} and {@link Matrix#rotate}
   methods are used to position and orient a {@link Model} in world
   coordinates by setting the {@link Position#matrix} in the
   {@link Position} holding the {@link Model}.
<p>
   The renderer uses the camera's {@link viewMatrix} in its
   {@link renderer.pipeline.World2View} pipeline stage. In that stage
   the renderer multiplies the camera's {@link viewMatrix} with every
   vertex of every model in the scene to convert each vertex from
   world coordinates to the camera's view coordinate system.
<p>
   A {@code Camera}'s {@link viewMatrix} is the inverse of what we
   might expect it to be. If this {@code Camera} is told to translate
   forward (in the positive z-direction) 5 units, then the view matrix
   will be a translation in the z-direction of -5 units. If this
   {@code Camera} is told to look to the right (rotate around the
   y-axis) by say 20 degrees, then the view matrix will be a rotation
   around by y-axis by -20 degrees.
<p>
   The reason for this is that a {@code Camera}'s view matrix is really
   a modeling matrix that should be applied to every model in a scene
   (in addition to each model's individual model matrix). When the camera
   moves forward by five units in the z-direction, every vertex in the
   scene becomes 5 units closer to the camera (the camera which is fixed
   at the origin and looking down the negative z-axis). So every vertex
   in the scene needs a modeling transformation that translates by -5 in
   the z-direction.
<p>
   This {@code Camera} has a configurable "view volume" that
   determines what part of world space the camera "sees" when
   we use the camera to take a picture (that is, when we render
   a {@link Scene}).
<p>
   This {@code Camera} can "take a picture" two ways, using
   a perspective projection or a parallel (orthographic)
   projection. Each way of taking a picture has a different
   shape for its view volume. The data in this data structure
   determines the shape of each of the two view volumes.
<p>
   For the perspective projection, the view volume (in view
   coordinates!) is the infinitely long frustum that is formed
   by cutting at the near clipping plane, {@code z = -near},
   the infinitely long pyramid with its apex at the origin
   and its cross section in the image plane, {@code z = -1},
   with edges {@code x = -1}, {@code x = +1}, {@code y = -1},
   and {@code y = +1}. The perspective view volume's shape is
   set by the {@link projPerspective} method.
<p>
   For the orthographic projection, the view volume (in view
   coordinates!) is the semi-infinite rectangular cylinder
   parallel to the z-axis, with base in the near clipping plane,
   {@code z = -near}, and with edges {@code x = left},
   {@code x = right}, {@code y = bottom}, {@code y = top} (a
   semi-infinite parallelepiped). The orthographic view volume's
   shape is set by the {@link projOrtho} method.
<p>
   When the graphics rendering {@link renderer.pipeline.Pipeline}
   uses this {@code Camera} to render a {@link Scene}, the renderer
   only "sees" the geometry from the scene that is contained in this
   camera's view volume. (Notice that this means the orthographic
   camera can see geometry that is behind the camera. In fact, the
   perspective camera can also sees geometry that is behind the
   camera.) The renderer's {@link renderer.pipeline.NearClip} and
   {@link renderer.pipeline.Clip} pipeline stages are responsible
   for making sure that the scene's geometry that is outside of
   this camera's view volume is not visible.
<p>
   The plane {@code z = -1} (in view coordinates) is the camera's
   "image plane". The rectangle in the image plane with corners
   {@code (left, bottom, -1)} and {@code (right, top, -1)} is the
   camera's "view rectangle". The view rectangle is like the film
   in a real camera, it is where the camera's image appears when you
   take a picture. The contents of the camera's view rectangle (after
   it gets "normalized" to camera coordinates by the renderer's
   {@link renderer.pipeline.View2Camera} stage) is what gets rasterized,
   by the renderer's {@link renderer.pipeline.Rasterize}
   pipeline stage, into a {@link renderer.framebuffer.FrameBuffer}'s
   {@link renderer.framebuffer.FrameBuffer.Viewport}.
<p>
   For both the perspective and the parallel projections, the camera's
   near plane is there to prevent the camera from seeing what is "behind"
   the near plane. For the perspective projection, the near plane also
   prevents the renderer from incorrectly rasterizing line segments that
   cross the camera plane, {@code z = 0}.
*/
public final class Camera
{
   // Choose either perspective or parallel projection.
   public final boolean perspective;
   // The following five numbers define the camera's view volume.
   // The first four will be encoded into the camera's normalization matrix.
   public final double left;
   public final double right;
   public final double bottom;
   public final double top;
   public final double n;  // near clipping plane
   // This Matrix determines the Camera's location and orientation in world space.
   private Matrix viewMatrix;

   /**
      A private {@code Camera} constructor for
      use by the static factory methods.
   */
   private Camera(final boolean perspective,
                  final double left,
                  final double right,
                  final double bottom,
                  final double top,
                  final double n,
                  final Matrix viewMatrix)
   {
      this.perspective = perspective;
      this.left = left;
      this.right = right;
      this.bottom = bottom;
      this.top = top;
      this.n = n;
      this.viewMatrix = viewMatrix;
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a perspective projection
      of the normalized infinite view pyramid extending along the negative
      z-axis.

      @return a new {@code Camera} object with the default perspective parameters
   */
   public static Camera projPerspective()
   {
      return projPerspective(-1.0, +1.0, -1.0, +1.0); // left, right, bottom, top
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a perspective projection
      of an infinite view pyramid extending along the negative z-axis.

      @param left    left edge of view rectangle in the image plane
      @param right   right edge of view rectangle in the image plane
      @param bottom  bottom edge of view rectangle in the image plane
      @param top     top edge of view rectangle in the image plane
      @return a new {@code Camera} object with the given parameters
   */
   public static Camera projPerspective(final double left,   final double right,
                                        final double bottom, final double top)
   {
      return projPerspective(left, right, bottom, top, 1.0);
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a perspective projection
      of an infinite view pyramid extending along the negative z-axis.
      <p>
      Use {@code focalLength} to determine the image plane. So the
      {@code left}, {@code right}, {@code bottom}, {@code top}
      parameters are used in the plane {@code z = -focalLength}.
      <p>
      The {@code focalLength} parameter can be used to zoom an
      asymmetric view volume, much like the {@code fovy} parameter
      for the symmetric view volume, or the "near" parameter for
      the OpenGL gluPerspective() function.

      @param left    left edge of view rectangle in the image plane
      @param right   right edge of view rectangle in the image plane
      @param bottom  bottom edge of view rectangle in the image plane
      @param top     top edge of view rectangle in the image plane
      @param focalLength  distance from the origin to the image plane
      @return a new {@code Camera} object with the given parameters
   */
   public static Camera projPerspective(final double left,   final double right,
                                        final double bottom, final double top,
                                        final double focalLength)
   {
      return new Camera(true,
                        left / focalLength,
                        right / focalLength,
                        bottom / focalLength,
                        top / focalLength,
                        -0.1,  // near clipping plane (near = +0.1)
                        Matrix.identity());  // viewMatrix
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a symmetric infinite
      view pyramid extending along the negative z-axis.
      <p>
      Here, the view volume is determined by a vertical "field of view"
      angle and an aspect ratio for the view rectangle in the image plane.

      @param fovy    angle in the y-direction subtended by the view rectangle in the image plane
      @param aspect  aspect ratio of the view rectangle in the image plane
      @return a new {@code Camera} object with the given parameters
   */
   public static Camera projPerspective(final double fovy, final double aspect)
   {
      final double top    =  Math.tan((Math.PI/180.0)*fovy/2.0);
      final double bottom = -top;
      final double right  =  top * aspect;
      final double left   = -right;

      return projPerspective(left, right, bottom, top);
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a parallel (orthographic)
      projection of the normalized infinite view parallelepiped extending
      along the z-axis.

      @return a new {@code Camera} object with the default orthographic parameters
   */
   public static Camera projOrtho()
   {
      return projOrtho(-1.0, +1.0, -1.0, +1.0); // left, right, bottom, top
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a parallel (orthographic)
      projection of an infinite view parallelepiped extending along the
      z-axis.

      @param left    left edge of view rectangle in the xy-plane
      @param right   right edge of view rectangle in the xy-plane
      @param bottom  bottom edge of view rectangle in the xy-plane
      @param top     top edge of view rectangle in the xy-plane
      @return a new {@code Camera} object with the given parameters
   */
   public static Camera projOrtho(final double left,   final double right,
                                  final double bottom, final double top)
   {
      return new Camera(false,
                        left,
                        right,
                        bottom,
                        top,
                        +1.0,  // near clipping plane (near = -1.0)
                        Matrix.identity());  // viewMatrix
   }


   /**
      This is a static factory method.
      <p>
      Set up this {@code Camera}'s view volume as a symmetric infinite
      view parallelepiped extending along the z-axis.
      <p>
      Here, the view volume is determined by a vertical "field-of-view"
      angle and an aspect ratio for the view rectangle in the image plane.

      @param fovy    angle in the y-direction subtended by the view rectangle in the image plane
      @param aspect  aspect ratio of the view rectangle in the image plane
      @return a new {@code Camera} object with the given parameters
   */
   public static Camera projOrtho(final double fovy, final double aspect)
   {
      final double top    =  Math.tan((Math.PI/180.0)*fovy/2.0);
      final double bottom = -top;
      final double right  =  top * aspect;
      final double left   = -right;

      return projOrtho(left, right, bottom, top);
   }


   /**
      Create a new {@code Camera} that is essentially the same as this
      {@code Camera} but with the given distance from the camera to
      the near clipping plane.
      <p>
      When {@code near} is positive, the near clipping plane is in
      front of the camera. When {@code near} is negative, the near
      clipping plane is behind the camera.

      @param near  distance from the new {@code Camera} to its near clipping plane
      @return a new {@code Camera} object with the given value for near
   */
   public Camera changeNear(final double near)
   {
      return new Camera(this.perspective,
                        this.left,
                        this.right,
                        this.bottom,
                        this.top,
                        -near,
                        this.viewMatrix);
   }


   /**
      Create a new {@code Camera} that is essentially the same as
      this {@code Camera} but with the given location.

      @deprecated  Use the caera's "view" API instead of this method.
      This method is here for compatibility with renderers 8 through 11.

      @param x  translated location, in the x-direction, for the new {@code Camera}
      @param y  translated location, in the y-direction, for the new {@code Camera}
      @param z  translated location, in the z-direction, for the new {@code Camera}
      @return a new {@code Camera} object with the given translated location
   */
   @Deprecated
   public Camera translate(final double x,
                           final double y,
                           final double z)
   {
      return new Camera(this.perspective,
                        this.left,
                        this.right,
                        this.bottom,
                        this.top,
                        this.n,
                        Matrix.translate(-x, -y, -z));  // viewMatrix
   }


   /**
      Set the location and orientation of this (@code Camera} in the world
      coordinate system.
   <p>
      Compare with
      <a href="https://www.opengl.org/sdk/docs/man2/xhtml/gluLookAt.xml" target="_top">
               https://www.opengl.org/sdk/docs/man2/xhtml/gluLookAt.xml</a>

      @param eyex     x-coordinate of the camera's location
      @param eyey     y-coordinate of the camera's location
      @param eyez     z-coordinate of the camera's location
      @param centerx  x-coordinate of the camera's look-at point
      @param centery  y-coordinate of the camera's look-at point
      @param centerz  z-coordinate of the camera's look-at point
      @param upx      x-component of the camera's up vector
      @param upy      y-component of the camera's up vector
      @param upz      z-component of the camera's up vector
   */
   public void viewLookAt(final double eyex,    final double eyey,    final double eyez,
                          final double centerx, final double centery, final double centerz,
                          final double upx,     final double upy,     final double upz)
   {
      final Vector F  = new Vector(centerx - eyex, centery - eyey, centerz - eyez);
      final Vector UP = new Vector(upx, upy, upz);
      final Vector f  = F.normalize();
      final Vector up = UP.normalize();
      final Vector s  = f.crossProduct(up);
      final Vector u  = s.crossProduct(f);
      viewMatrix = Matrix.buildFromColumns(
                       new Vector(s.x, u.x, -f.x, 0.0),
                       new Vector(s.y, u.y, -f.y, 0.0),
                       new Vector(s.z, u.z, -f.z, 0.0),
                       new Vector(0.0, 0.0,  0.0, 1.0));

      this.viewMatrix = viewMatrix.times( Matrix.translate(-eyex, -eyey, -eyez) );
   }


   /**
      Set this (@code Camera}'s view matrix to the identity {@link Matrix}.
   <p>
      This places the camera at the origin of world coordinates,
      looking down the negative z-axis.
   */
   public void view2Identity()
   {
      this.viewMatrix = Matrix.identity();
   }


   /**
      Translate this (@code Camera} in world coordinates by the amount of
      the given translation vector.
   <p>
      This means that we should left-multiply this camera's view matrix
      with a translation {@link Matrix} that is the inverse of the
      given translation.

      @param x  x-component of the Camera's translation vector
      @param y  y-component of the Camera's translation vector
      @param z  z-component of the Camera's translation vector
   */
   public void viewTranslate(final double x, final double y, final double z)
   {
      // Notice that the order of the multiplication is the oppposite
      // of what we usually use. This is because the viewMatrix should
      // be the inverse of the matrix that would position the camera
      // in world coordinates. If A*B would be the position matrix,
      // its inverse (A*B)^(-1) = B^(-1) * A^(-1), so we see a reversal
      // in the order of multiplication.
      this.viewMatrix = (Matrix.translate(-x, -y, -z)).times(viewMatrix);
   }


   /**
      Rotate this {@code Camera} in world coordinates by the given angle
      around the given axis vector.
   <p>
      This means that we should left-multiply this {@code Camera}'s view
      matrix with a rotation {@link Matrix} that is the inverse of
      the given rotation.

      @param theta  angle, in degrees, to rotate the Camera by
      @param x      x-component of the axis vector for the Camera's rotation
      @param y      y-component of the axis vector for the Camera's rotation
      @param z      z-component of the axis vector for the Camera's rotation
   */
   public void viewRotate(final double theta,
                          final double x, final double y, final double z)
   {
      // See the comment in the viewTranslate() method.
      this.viewMatrix = (Matrix.rotate(-theta, x, y, z)).times(viewMatrix);
   }

   /**
      Rotate this {@code Camera} in world coordinates by the given angle
      around the x-axis.
   <p>
      This means that we should left-multiply this {@code Camera}'s view
      matrix with a rotation {@link Matrix} that is the inverse of
      the given rotation.

      @param theta  angle, in degrees, to rotate the Camera by
   */
   public void viewRotateX(final double theta)
   {
      // See the comment in the viewTranslate() method.
      this.viewMatrix = (Matrix.rotate(-theta, 1, 0, 0)).times(viewMatrix);
   }


   /**
      Rotate this {@code Camera} in world coordinates by the given angle
      around the y-axis.
   <p>
      This means that we should left-multiply this {@code Camera}'s view
      matrix with a rotation {@link Matrix} that is the inverse of
      the given rotation.

      @param theta  angle, in degrees, to rotate the Camera by
   */
   public void viewRotateY(final double theta)
   {
      // See the comment in the viewTranslate() method.
      this.viewMatrix = (Matrix.rotate(-theta, 0, 1, 0)).times(viewMatrix);
   }


   /**
      Rotate this {@code Camera} in world coordinates by the given angle
      around the z-axis.
   <p>
      This means that we should left-multiply this {@code Camera}'s view
      matrix with a rotation {@link Matrix} that is the inverse of
      the given rotation.

      @param theta  angle, in degrees, to rotate the Camera by
   */
   public void viewRotateZ(final double theta)
   {
      // See the comment in the viewTranslate() method.
      this.viewMatrix = (Matrix.rotate(-theta, 0, 0, 1)).times(viewMatrix);
   }


   /**
      Get a reference to this {@code Camera}'s view {@link Matrix}.

      @return a reference to this {@code Camera}'s {@link Matrix} object
   */
   public Matrix getViewMatrix()
   {
      return this.viewMatrix;
   }


   /**
      Get a reference to this {@code Camera}'s view {@link Vector}.

      @return a reference to this {@code Camera}'s {@link Vector} object
   */
   public Vector getViewVector()
   {
      return new Vector(viewMatrix.v4.x,
                        viewMatrix.v4.y,
                        viewMatrix.v4.z);
   }


   /**
      Get a reference to this {@code Camera}'s normalization {@link Matrix}.

      @return a reference to this {@code Camera}'s normalizing {@link Matrix} object
   */
   public Matrix getNormalizeMatrix()
   {
      if (perspective)
      {
         return PerspectiveNormalizeMatrix.build(left, right, bottom, top);
      }
      else
      {
         return OrthographicNormalizeMatrix.build(left, right, bottom, top);
      }
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Camera} object
   */
   public String toString()
   {
      final double fovy = (180./Math.PI) * Math.atan(top)
                        + (180./Math.PI) * Math.atan(-bottom);
      final double ratio = (right - left) / (top - bottom);
      String result = "";
      result += "Camera: \n";
      result += "  perspective = " + perspective + "\n";
      result += "  left = "   + left + ", "
             +  "  right = "  + right + "\n"
             +  "  bottom = " + bottom + ", "
             +  "  top = "    + top + "\n"
             +  "  near = "   + -n + "\n"
             +  "  (fovy = " + fovy
             +  ", aspect ratio = " + String.format("%.2f", ratio) + ")\n"
             +  "Normalization Matrix\n"
             +  getNormalizeMatrix() + "\n"
             +  "View Matrix\n"
             +  viewMatrix;
      return result;
   }
}
