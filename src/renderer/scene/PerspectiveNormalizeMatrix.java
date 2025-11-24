/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

/**
   We use two steps to transform the camera's configurable perspective
   view volume into the standard perspective view volume. The first step
   skews the camera's view volume so that its center line is the negative
   z-axis (this takes an asymmetric view volume and makes it symmetric).
   The second step scales the skewed view volume so that it intersects
   the image plane, {@code z = -1}, with corners {@code (-1, -1, -1)}
   and {@code (+1, +1, -1)} (this gives the symmetric view volume a 90
   degree field-of-view).
<p>
   Let us derive the matrix for transforming the camera's perspective
   view volume into the standard perspective view volume. Suppose the
   camera's perspective view volume has an asymmetrical cross section
   in the yz-plane that is determined by the top and bottom points
   {@code (t, -1)} and {@code (b, -1)}. The center line of this cross
   section is determined by the point {@code ((t+b)/2, -1)}. We want to
   skew the yz-plane in the y-direction along the z-axis so that the
   field-of-view's center line becomes the z-axis. So we need to solve
   this matrix equation for the value of the skew factor {@code s}.
   <pre>{@code
      [ 1  s ] * [ (t+b)/2 ] = [  0 ]
      [ 0  1 ]   [    -1   ]   [ -1 ]
   }</pre>
   This simplifies to the equation
   <pre>{@code
      (t + b)/2 - s = 0,
      s = (t + b)/(2).
   }</pre>
<p>
   A similar calculation can be made for skewing the field-of-view in
   the xz-plane.
<p>
   Once the field-of-view in the yz-plane has been made symmetric with
   respect to the z-axis, we want to scale it in the y-direction so that
   the scaled field-of-view has an angle at the origin of 90 degrees. We
   need to scale the point {@code ((t-b)/2, -1)} to the point {@code (1, -1)}
   (and the point {@code ((b-t)/2, -1)} to the point {@code (-1, -1)}). So
   we need to solve this matrix equation for the value of the scale factor
   {@code s}.
   <pre>{@code
      [ s  0 ] * [ (t-b)/2 ] = [  1 ]
      [ 0  1 ]   [    -1   ]   [ -1 ]
   }</pre>
   This simplifies to the equation
   <pre>{@code
      s * (t - b)/2 = 1,
      s = 2/(t - b).
   }</pre>
<p>
   A similar calculation can be made for scaling the skewed field-of-view
   in the xz-plane.
<p>
   The following matrix skews the camera's view volume along the z-axis so
   that the transformed view volume will be centered on the negative z-axis.
   <pre>{@code
     [ 1  0  (r+l)/(2)  0 ]
     [ 0  1  (t+b)/(2)  0 ]
     [ 0  0      1      0 ]
     [ 0  0      0      1 ]
   }</pre>
   The following matrix scales the skewed view volume so that it will
   be 2 units wide and 2 units tall at the image plane {@code z = -1}.
   <pre>{@code
     [ 2/(r-l)      0     0  0 ]
     [    0      2/(t-b)  0  0 ]
     [    0         0     1  0 ]
     [    0         0     0  1 ]
   }</pre>
   The matrix product looks like this.
   <pre>{@code
     [ 1  0  (r+l)/2  0 ]   [ 2/(r-l)      0     0  0 ]
     [ 0  1  (t+b)/2  0 ] * [    0      2/(t-b)  0  0 ]
     [ 0  0     1     0 ]   [    0         0     1  0 ]
     [ 0  0     0     1 ]   [    0         0     0  1 ]

         [ 2/(r-l)      0     (r+l)/(r-l)  0 ]
       = [    0      2/(t-b)  (t+b)/(t-b)  0 ]
         [    0         0          1       0 ]
         [    0         0          0       1 ]
   }</pre>
   This product matrix transforms the camera's configurable perspective
   view volume into the standard normalized perspective view volume
   whose intersection with the image plane, {@code z = -1}, has
   {@code left = -1}, {@code right = +1}, {@code bottom = -1},
   and {@code top = +1}.
*/
public final class PerspectiveNormalizeMatrix
{
   /**
      This is a static factory method.
      <p>
      Construct the {@link Matrix} that transforms from the
      {@link Camera}'s perspective view coordinate system to
      the normalized perspective camera coordinate system.

      @param l  left edge of view rectangle in the image plane z = -1
      @param r  right edge of view rectangle in the image plane z = -1
      @param b  bottom edge of view rectangle in the image plane z = -1
      @param t  top edge of view rectangle in the image plane z = -1
      @return a new {@code Matrix} object containing the perspective normalization matrix
   */
   public static Matrix build(final double l, final double r,
                              final double b, final double t)
   {
      final Matrix m1, m2;

      m1 = Matrix.buildFromColumns(
               new Vector(  1.0,      0.0,    0.0,  0.0),
               new Vector(  0.0,      1.0,    0.0,  0.0),
               new Vector((r+l)/2,  (t+b)/2,  1.0,  0.0),
               new Vector(  0.0,      0.0,    0.0,  1.0));

      m2 = Matrix.buildFromColumns(
               new Vector(2/(r-l),     0.0,    0.0,  0.0),
               new Vector(  0.0,     2/(t-b),  0.0,  0.0),
               new Vector(  0.0,       0.0,    1.0,  0.0),
               new Vector(  0.0,       0.0,    0.0,  1.0));

      return m2.times(m1);
   }
}
