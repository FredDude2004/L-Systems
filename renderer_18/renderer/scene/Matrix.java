/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

/**
   A {@code Matrix} object has four {@link Vector} objects.
<p>
   The four {@link Vector} objects represent the four column vectors
   of the 4-by-4 matrix (as in a Linear Algebra course).
<p>
   In computer graphics, the points and vectors of 3-dimensional space
   are represented using 4-dimensional homogeneous coordinates.
   So each transformation of 3-dimensional space is represented by
   a 4-by-4 (homogeneous) matrix.
<p>
   A 4-by-4 matrix represents a transformation of 3-dimensional space.
   The most common transformations are translation, rotation, and
   scaling. A 4-by-4 matrix can also represent a projection transformation.
*/
public final class Matrix
{
   public final Vector v1, v2, v3, v4; // these are column vectors

   /**
      Construct an arbitrary 4-by-4 {@code Matrix}
      with the given column {@link Vector}s.
      <p>
      Notice that this is a private constructor. Other
      objects should use the static facory methods to
      create new {@code Matrix} objects.

      @param v1  1st column {@link Vector} for the new {@code Matrix}
      @param v2  2nd column {@link Vector} for the new {@code Matrix}
      @param v3  3rd column {@link Vector} for the new {@code Matrix}
      @param v4  4th column {@link Vector} for the new {@code Matrix}
      @return a new {@code Matrix} object
   */
   private Matrix(final Vector v1, final Vector v2,
                  final Vector v3, final Vector v4)
   {
      this.v1 = v1;  // Notice that we are not making
      this.v2 = v2;  // copies of the column vectors,
      this.v3 = v3;  // We are just making references
      this.v4 = v4;  // to them.
   }


   /**
      This is a static facory method.
      <p>
      Construct an arbitrary 4-by-4 {@code Matrix}
      using the given column {@link Vector}s.

      @param c1  1st column {@link Vector} for the new {@code Matrix}
      @param c2  2nd column {@link Vector} for the new {@code Matrix}
      @param c3  3rd column {@link Vector} for the new {@code Matrix}
      @param c4  4th column {@link Vector} for the new {@code Matrix}
      @return a new {@code Matrix} object
   */
   public static Matrix buildFromColumns(final Vector c1, final Vector c2,
                                         final Vector c3, final Vector c4)
   {
      return new Matrix(c1, c2, c3, c4);
   }


   /**
      This is a static facory method.
      <p>
      Construct an arbitrary 4-by-4 {@code Matrix}
      using the given row {@link Vector}s.

      @param r1  1st row {@link Vector} for the new {@code Matrix}
      @param r2  2nd row {@link Vector} for the new {@code Matrix}
      @param r3  3rd row {@link Vector} for the new {@code Matrix}
      @param r4  4th row {@link Vector} for the new {@code Matrix}
      @return a new {@code Matrix} object
   */
   public static Matrix buildFromRows(final Vector r1, final Vector r2,
                                      final Vector r3, final Vector r4)
   {
      Vector c1 = new Vector(r1.x, r2.x, r3.x, r4.x);
      Vector c2 = new Vector(r1.y, r2.y, r3.y, r4.y);
      Vector c3 = new Vector(r1.z, r2.z, r3.z, r4.z);
      Vector c4 = new Vector(r1.w, r2.w, r3.w, r4.w);
      return new Matrix(c1, c2, c3, c4);
   }


   /**
      This is a static facory method.
      <p>
      Construct an identity {@code Matrix}.

      @return a new {@code Matrix} object containing an identity {@code Matrix}
   */
   public static Matrix identity()
   {
      return scale(1.0, 1.0, 1.0);
   }


   /**
      This is a static facory method.
      <p>
      Construct a translation {@code Matrix} that translates by the
      given amounts in the {@code x}, {@code y}, and {@code z} directions..

      @param x  translation factor for the x-direction
      @param y  translation factor for the y-direction
      @param z  translation factor for the z-direction
      @return a new {@code Matrix} object containing a translation {@code Matrix}
   */
   public static Matrix translate(final double x, final double y, final double z)
   {
      return new Matrix(new Vector(1.0, 0.0, 0.0, 0.0),
                        new Vector(0.0, 1.0, 0.0, 0.0),
                        new Vector(0.0, 0.0, 1.0, 0.0),
                        new Vector(  x,   y,   z, 1.0));
   }


   /**
      This is a static facory method.
      <p>
      Construct a diagonal {@code Matrix} with the given number
      on the diagonal.
      <p>
      This is also a uniform scaling matrix.

      @param d  the diagonal value for the new {@code Matrix}
      @return a new {@code Matrix} object containing a scaling {@code Matrix}
   */
   public static Matrix scale(final double d)
   {
      return scale(d, d, d);
   }


   /**
      This is a static facory method.
      <p>
      Construct a (diagonal) {@code Matrix} that scales in
      the x, y, and z directions by the given factors.

      @param x  scale factor for the x-direction
      @param y  scale factor for the y-direction
      @param z  scale factor for the z-direction
      @return a new {@code Matrix} object containing a scaling {@code Matrix}
   */
   public static Matrix scale(final double x, final double y, final double z)
   {
      return new Matrix(new Vector(  x, 0.0, 0.0, 0.0),
                        new Vector(0.0,   y, 0.0, 0.0),
                        new Vector(0.0, 0.0,   z, 0.0),
                        new Vector(0.0, 0.0, 0.0, 1.0));
   }


   /**
      This is a static facory method.
      <p>
      Construct a rotation {@code Matrix} that rotates around
      the x-axis by the angle {@code theta}.

      @param theta  angle (in degrees) to rotate by around the x-axis
      @return a new {@code Matrix} object containing a rotation {@code Matrix}
   */
   public static Matrix rotateX(final double theta)
   {
      return rotate(theta, 1,0,0);
   }


   /**
      This is a static facory method.
      <p>
      Construct a rotation {@code Matrix} that rotates around
      the y-axis by the angle {@code theta}.

      @param theta  angle (in degrees) to rotate by around the y-axis
      @return a new {@code Matrix} object containing a rotation {@code Matrix}
   */
   public static Matrix rotateY(final double theta)
   {
      return rotate(theta, 0,1,0);
   }


   /**
      This is a static facory method.
      <p>
      Construct a rotation {@code Matrix} that rotates around
      the z-axis by the angle {@code theta}.

      @param theta  angle (in degrees) to rotate by around the z-axis
      @return a new {@code Matrix} object containing a rotation {@code Matrix}
   */
   public static Matrix rotateZ(final double theta)
   {
      return rotate(theta, 0,0,1);
   }


   /**
      This is a static facory method.
      <p>
      Construct a rotation {@code Matrix} that rotates around
      the axis vector {@code (x,y,z)} by the angle {@code theta}.
      <p>
      See
      <a href="https://www.opengl.org/sdk/docs/man2/xhtml/glRotate.xml" target="_top">
               https://www.opengl.org/sdk/docs/man2/xhtml/glRotate.xml</a>

      @param theta  angle (in degrees) to rotate by around the axis vector
      @param x      x-component of the axis vector for the rotation
      @param y      y-component of the axis vector for the rotation
      @param z      z-component of the axis vector for the rotation
      @return a new {@code Matrix} object containing a rotation {@code Matrix}
   */
   public static Matrix rotate(final double theta, final double x, final double y, final double z)
   {
      final double norm = Math.sqrt(x*x + y*y + z*z);
      final double ux = x/norm;
      final double uy = y/norm;
      final double uz = z/norm;

      final double c = Math.cos( (Math.PI/180.0)*theta );
      final double s = Math.sin( (Math.PI/180.0)*theta );

      return new Matrix(
        new Vector(ux*ux*(1-c)+c,      uy*ux*(1-c)+(uz*s), uz*ux*(1-c)-(uy*s), 0.0),
        new Vector(ux*uy*(1-c)-(uz*s), uy*uy*(1-c)+c,      uz*uy*(1-c)+(ux*s), 0.0),
        new Vector(ux*uz*(1-c)+(uy*s), uy*uz*(1-c)-(ux*s), uz*uz*(1-c)+c,      0.0),
        new Vector(0.0,                0.0,                0.0,                1.0));
   }


   /**
      A scalar times this {@code Matrix} returns a new {@code Matrix}.

      @param s  scalar value to multiply this {@code Matrix} by
      @return a new {@code Matrix} object containing the scalar s times this {@code Matrix}
   */
   public Matrix times(final double s) // return s * this
   {
      return new Matrix(v1.times(s), v2.times(s), v3.times(s), v4.times(s));
   }


   /**
      This {@code Matrix} times a {@link Vector} returns a new {@link Vector}.

      @param v  {@link Vector} to be multiplied by this {@code Matrix}
      @return new {@link Vector} object containing this {@code Matrix} times the {@link Vector} v
   */
   public Vector times(final Vector v) // return this * v
   {
      /*
      return v1.times(v.x).plus(v2.times(v.y).plus(v3.times(v.z).plus(v4.times(v.w))));
      */
      /*
      // Here is what this works out to be.
      final Vector v1x = this.v1.times(v.x);
      final Vector v2y = this.v2.times(v.y);
      final Vector v3z = this.v3.times(v.z);
      final Vector v4w = this.v4.times(v.w);
      final Vector sum1 = v1x.plus(v2y);
      final Vector sum2 = sum1.plus(v3z);
      final Vector sum3 = sum2.plus(v4w);
      return sum3;
      */
      // dot product of each row of this matrix with the vector v
      final double x = (v1.x * v.x) + (v2.x * v.y) + (v3.x * v.z) + (v4.x * v.w);
      final double y = (v1.y * v.x) + (v2.y * v.y) + (v3.y * v.z) + (v4.y * v.w);
      final double z = (v1.z * v.x) + (v2.z * v.y) + (v3.z * v.z) + (v4.z * v.w);
      final double w = (v1.w * v.x) + (v2.w * v.y) + (v3.w * v.z) + (v4.w * v.w);
      return new Vector(x, y, z, w);
   }


   /**
      This {@code Matrix} times {@code Matrix} {@code m} returns a new {@code Matrix}.

      @param m  {@code Matrix} value to be multiplied on the right of this {@code Matrix}
      @return new {@code Matrix} object containing this {@code Matrix} times {@code Matrix} {@code m}
   */
   public Matrix times(final Matrix m) // return this * m
   {
      return new Matrix(this.times(m.v1),   // 1st column vector of the result
                        this.times(m.v2),   // 2nd column vector of the result
                        this.times(m.v3),   // 3rd column vector of the result
                        this.times(m.v4) ); // 4th column vector of the result
   }


   /**
      This {@code Matrix} times a {@link Vertex} returns a new {@link Vertex}.

      @param v  {@link Vertex} to be multiplied by this {@code Matrix}
      @return new {@link Vertex} object containing this {@code Matrix} times the {@link Vertex} v
   */
   public Vertex times(final Vertex v) // return this * v
   {
      /*
      final Vector v = v1.times(v.x).plus(v2.times(v.y).plus(v3.times(v.z).plus(v4.times(v.w))));
      return new Vertex(v.x, v.y, v.z, v.w);
      */
      // dot product of each row of this matrix with the vertex v
      final double x = (v1.x * v.x) + (v2.x * v.y)  + (v3.x * v.z) + (v4.x * v.w);
      final double y = (v1.y * v.x) + (v2.y * v.y)  + (v3.y * v.z) + (v4.y * v.w);
      final double z = (v1.z * v.x) + (v2.z * v.y)  + (v3.z * v.z) + (v4.z * v.w);
      final double w = (v1.w * v.x) + (v2.w * v.y)  + (v3.w * v.z) + (v4.w * v.w);
      return new Vertex(x, y, z, w);
   }


   /**
      Assuming that the 3-by-3 "rotation part" of this 4-by-4
      {@code Matrix} represents a pure rotation, return the
      rotation's three Euler angles, in radians, in the
      order {@code [x, y, z]} for rotations in the order
      {@code R_z * R_y * R_x}.
   <p>
      A 3-by-3 matrix is a rotation matrix if its inverse is
      equal to its transpose and its determinant is equal to 1.
   <p>
      See <a href="http://eecs.qmul.ac.uk/~gslabaugh/publications/euler.pdf" target="_top">
                   http://eecs.qmul.ac.uk/~gslabaugh/publications/euler.pdf</a>

      @return an array of 3 doubles which are this rotation's Euler angles in radians
   */
   public double[] rot2euler()
   {
      final double r_11 = v1.x,
                   r_12 = v2.x,
                   r_13 = v3.x,
                   r_21 = v1.y,
                   r_31 = v1.z,
                   r_32 = v2.z,
                   r_33 = v3.z;

      final double r_x,
                   r_y,
                   r_z;

      if (r_31 != 1.0 && r_31 != -1.0)
      {
         r_y = -Math.asin(r_31);
         r_x = Math.atan2(r_32 / Math.cos(r_y),
                          r_33 / Math.cos(r_y));
         r_z = Math.atan2(r_21 / Math.cos(r_y),
                          r_11 / Math.cos(r_y));
      }
      else
      {
         if (r_31 == -1.0)
         {
            r_y = Math.PI / 2.0;
            r_x = Math.atan2(r_12, r_13);
         }
         else // r_31 == 1.0
         {
            r_y = -Math.PI / 2.0;
            r_x = Math.atan2(-r_12, -r_13);
         }
         r_z = 0.0;
      }

      return new double[]{r_x, r_y, r_z};
   }


   /**
      Assuming that this {@code Matrix} represents a 3D rotation,
      return the rotation matrix formed by multiplying this matrix's
      three Euler angle rotations in the order {@code R_z * R_y * R_x}.
      <p>
      This is mainly for debugging. If this matrix is really a pure
      rotation, then this method will return a copy of this matrix.

      @return the "eulerized" version of this {@code Matrix}
   */
   public Matrix eulerize()
   {
      double[] euler = this.rot2euler();
      return Matrix.rotateZ(euler[2]*(180.0/Math.PI)).times(
             Matrix.rotateY(euler[1]*(180.0/Math.PI)).times(
             Matrix.rotateX(euler[0]*(180.0/Math.PI))));
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Matrix} object
   */
   @Override
   public String toString()
   {
      String result = "";
      final int p = 5;      // the precision for the following format string
      final int w = p + 4;  // the width for the following format string
      final String format = "% "+w+"."+p+"f  % "+w+"."+p+"f  % "+w+"."+p+"f  % "+w+"."+p+"f";
      result += String.format("[[" + format + " ]\n",  v1.x, v2.x, v3.x, v4.x);
      result += String.format(" [" + format + " ]\n",  v1.y, v2.y, v3.y, v4.y);
      result += String.format(" [" + format + " ]\n",  v1.z, v2.z, v3.z, v4.z);
      result += String.format(" [" + format + " ]]",   v1.w, v2.w, v3.w, v4.w);
    //result += String.format("[[% .5f  % .5f  % .5f  % .5f ]\n",  v1.x, v2.x, v3.x, v4.x);
    //result += String.format(" [% .5f  % .5f  % .5f  % .5f ]\n",  v1.y, v2.y, v3.y, v4.y);
    //result += String.format(" [% .5f  % .5f  % .5f  % .5f ]\n",  v1.z, v2.z, v3.z, v4.z);
    //result += String.format(" [% .5f  % .5f  % .5f  % .5f ]]",   v1.w, v2.w, v3.w, v4.w);
      return result;
   }
}
