/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_F;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a wireframe model of a circle in the xy-plane centered at the origin.
*/
public class Circle extends Model implements MeshMaker
{
   public final double r;
   public final int n;

   /**
      Create a circle in the xy-plane with radius 1 and
      with 12 line segments around the circumference.
   */
   public Circle( )
   {
      this(1, 12);
   }


   /**
      Create a circle in the xy-plane with radius {@code r}
      and with 12 line segments around the circumference.

      @param r  radius of the circle
   */
   public Circle(double r)
   {
      this(r, 12);
   }


   /**
      Create a circle in the xy-plane with radius {@code r}
      and with {@code n} line segments around the circumference.

      @param r  radius of the circle
      @param n  number of line segments in the circle's circumference
      @throws IllegalArgumentException if {@code n} is less than 3
   */
   public Circle(double r, int n)
   {
      super(String.format("Circle(%.2f,%d)", r, n));

      if (n < 3)
         throw new IllegalArgumentException("n must be greater than 2");

      this.r = r;
      this.n = n;

      // Create the circle's geometry.

      // An array of vertices to be used to create the geometry.
      final Vertex[] v = new Vertex[n];

      // Create all the vertices.
      for (int i = 0; i < n; ++i)
      {
         final double c = Math.cos(i*(2.0*Math.PI)/n);
         final double s = Math.sin(i*(2.0*Math.PI)/n);
         v[i] = new Vertex(r * c, r * s, 0);
      }

      // Add the circle's vertices to the model.
      addVertex(v);

      // Create the line segments around the circle.
      final LineLoop lineLoop = new LineLoop();
      for (int i = 0; i < n; ++i)
      {
         lineLoop.addIndex(i);
      }
      addPrimitive(lineLoop);
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return n;}

   @Override
   public Circle remake(final int n, final int k)
   {
      final int newN;
      if (n != this.n)
         newN = n;
      else
         newN = k;;

      return new Circle(this.r, newN);
   }
}//Circle
