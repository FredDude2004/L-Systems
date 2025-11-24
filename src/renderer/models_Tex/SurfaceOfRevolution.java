/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.Texture;

import java.util.function.DoubleFunction;
import java.util.function.ToDoubleFunction;    // could use this instead
import java.util.function.DoubleUnaryOperator; // could use this instead
//https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html

/**
   Create a textured model of a surface of revolution around the y-axis.
<p>
   See <a href="https://en.wikipedia.org/wiki/Surface_of_revolution#Rotating_a_function" target="_top">
                https://en.wikipedia.org/wiki/Surface_of_revolution#Rotating_a_function</a>

   @see ParametricSurface
*/
public class SurfaceOfRevolution extends ParametricSurface
{
   /**
      Create a surface of revolution around the y-axis
      based on a cosine function.

      @param texture  {@link Texture} to use with this {@link Model}
   */
   public SurfaceOfRevolution(final Texture texture)
   {
      this(texture,
           t -> 0.5 * (1 + Math.cos(Math.PI * t)),
           -1.0, 1.0, 49, 49);
   }


   /**
      Create a surface of revolution around the y-axis
      with the given radial function, {@code r = r(y)},
      the given parameter range along the y-axis, and
      the given number of circles of latitude.

      @param texture  {@link Texture} to use with this {@link Model}
      @param r   radius function
      @param y1  beginning value along the y-axis
      @param y2  ending value along the y-axis
      @param n   number of circles of latitude
      @param k   number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 2
   */
   public SurfaceOfRevolution(final Texture texture,
                              final DoubleFunction<Double> r,
                              final double y1, final double y2,
                              final int n, final int k)
   {
      this(texture, r, y1, y2, 0, 2*Math.PI, n, k);
   }


   /**
      Create a surface of revolution around the y-axis with
      the given radial function, {@code r = r(y)}, the given
      angular range for the sector of revolution, the given
      parameter range along the y-axis, and the given number
      of circles of latitude.

      @param texture  {@link Texture} to use with this {@link Model}
      @param r       radius function
      @param y1      beginning value along the y-axis
      @param y2      ending value along the y-axis
      @param theta1  beginning value of angular parameter range
      @param theta2  ending value of angular parameter range
      @param n       number of circles of latitude
      @param k       number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 2
   */
   public SurfaceOfRevolution(final Texture texture,
                              final DoubleFunction<Double> r,
                              final double y1, final double y2,
                              final double theta1, final double theta2,
                              final int n, final int k)
   {
      super( texture,
             (y,t) -> r.apply(y) * Math.cos(t),
             (y,t) -> y,
             (y,t) -> r.apply(y) * Math.sin(t),
             y1, y2,
             theta1, theta2,
             n, k );
   }


   /**
      Create a surface of revolution around the y-axis
      of the given radial parametric curve.

      @param texture  {@link Texture} to use with this {@link Model}
      @param x   first component function of the parametric curve
      @param y   second component function of the parametric curve
      @param s1  beginning parameter value
      @param s2  ending parameter value
      @param n   number of circles of latitude
      @param k   number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 2
   */
   public SurfaceOfRevolution(final Texture texture,
                              final DoubleFunction<Double> x,
                              final DoubleFunction<Double> y,
                              final double s1, final double s2,
                              final int n, final int k)
   {
      this(texture, x, y, s1, s2, 0, 2*Math.PI, n, k );
   }


   /**
      Create a surface of revolution around the y-axis
      of the given radial parametric curve and the given
      angular range for the sector of revolution.

      @param texture  {@link Texture} to use with this {@link Model}
      @param x       first component function of the parametric curve
      @param y       second component function of the parametric curve
      @param s1      beginning parameter value
      @param s2      ending parameter value
      @param theta1  beginning value of angular parameter range
      @param theta2  ending value of angular parameter range
      @param n       number of circles of latitude
      @param k       number of lines of longitude
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 2
   */
   public SurfaceOfRevolution(final Texture texture,
                              final DoubleFunction<Double> x,
                              final DoubleFunction<Double> y,
                              final double s1, final double s2,
                              final double theta1, final double theta2,
                              final int n, final int k)
   {
      super( texture,
             (s,t) -> x.apply(s) * Math.cos(t),
             (s,t) -> y.apply(s),
             (s,t) -> x.apply(s) * Math.sin(t),
             s1, s2,
             theta1, theta2,
             n, k,
             String.format("SurfaceOfRevolution(%d,%d)", n, k) );
   }
}//Surface of Revolution
