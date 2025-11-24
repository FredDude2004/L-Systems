/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_TP;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker_TP;
import renderer.scene.util.MeshType;

/**
   Create a solid model of a sector of a ring (an annulus)
   in the xy-plane centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Annulus_(mathematics)" target="_top">
                https://en.wikipedia.org/wiki/Annulus_(mathematics)</a>
<p>
   See <a href="https://en.wikipedia.org/wiki/Circular_sector" target="_top">
                https://en.wikipedia.org/wiki/Circular_sector</a>

   @see Ring
   @see CircleSector
   @see DiskSector
   @see ConeSector
   @see CylinderSector
   @see SphereSector
   @see TorusSector
*/
public class RingSector extends Model implements MeshMaker_TP
{
   public final double r1;
   public final double r2;
   public final double theta1;
   public final double theta2;
   public final int n;
   public final int k;
   public final MeshType type;

   /**
      Create half a ring (annulus) in the xy-plane
      with outer radius 1, inner radius 0.33, with 8
      spokes coming out of the center, and with 5
      concentric circles.
   */
   public RingSector( )
   {
      this(1.0, 0.33, 0, Math.PI, 5, 8);
   }


   /**
      Create a sector of a ring (annulus) in the xy-plane
      with outer radius {@code r1}, inner radius {@code r2},
      starting angle {@code theta1}, ending angle {@code theta2},
      with {@code k} spokes coming out of the center, and
      with {@code n} concentric circles.
   <p>
      If there are {@code k} spokes, then each (partial) circle
      around the center will have {@code k-1} line segments.
      If there are {@code n} concentric circles around the center,
      then each spoke will have {@code n-1} line segments.
   <p>
      There must be at least four spokes and at least two concentric circle.

      @param r1      outer radius of the ring
      @param r2      inner radius of the ring
      @param theta1  beginning angle of the sector (in radians)
      @param theta2  ending angle of the sector (in radians)
      @param n       number of concentric circles
      @param k       number of spokes in the ring
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 4
   */
   public RingSector(final double r1, final double r2,
                     final double theta1, final double theta2,
                     final int n, final int k)
   {
      this(r1, r2, theta1, theta2, n, k, MeshType.HORIZONTAL);
   }


   /**
      Create a sector of a ring (annulus) in the xy-plane
      with outer radius {@code r1}, inner radius {@code r2},
      starting angle {@code theta1}, ending angle {@code theta2},
      with {@code k} spokes coming out of the center, and
      with {@code n} concentric circles.
   <p>
      If there are {@code k} spokes, then each (partial) circle
      around the center will have {@code k-1} line segments.
      If there are {@code n} concentric circles around the center,
      then each spoke will have {@code n-1} line segments.
   <p>
      There must be at least four spokes and at least two concentric circle.

      @param r1      outer radius of the ring
      @param r2      inner radius of the ring
      @param theta1  beginning angle of the sector (in radians)
      @param theta2  ending angle of the sector (in radians)
      @param n       number of concentric circles
      @param k       number of spokes in the ring
      @param type  choose between striped and checkerboard triangle strips
      @throws IllegalArgumentException if {@code n} is less than 2
      @throws IllegalArgumentException if {@code k} is less than 4
   */
   public RingSector(final double r1, final double r2,
                     double theta1, double theta2,
                     final int n, final int k,
                     final MeshType type)
   {
      super(String.format("Ring Sector(%.2f,%.2f,%.2f,%.2f,%d,%d)",
                                       r1, r2, theta1, theta2, n, k));

      if (n < 2)
         throw new IllegalArgumentException("n must be greater than 1");
      if (k < 4)
         throw new IllegalArgumentException("k must be greater than 3");

      theta1 = theta1 % (2*Math.PI);
      theta2 = theta2 % (2*Math.PI);
      if (theta1 < 0) theta1 = 2*Math.PI + theta1;
      if (theta2 < 0) theta2 = 2*Math.PI + theta2;
      if (theta2 <= theta1) theta2 = theta2 + 2*Math.PI;

      this.r1 = r1;
      this.r2 = r2;
      this.theta1 = theta1;
      this.theta2 = theta2;
      this.n = n;
      this.k = k;
      this.type = type;

      // Create the rings's geometry.

      final double deltaR = (r1 - r2) / (n - 1),
                   deltaTheta = (theta2 - theta1) / (k - 1);

      // An array of vertices to be used to create triangles.
      final Vertex[][] v = new Vertex[n][k];

      // Create all the vertices.
      for (int j = 0; j < k; ++j) // choose a spoke (an angle)
      {
         final double c = Math.cos(theta1 + j * deltaTheta),
                      s = Math.sin(theta1 + j * deltaTheta);
         for (int i = 0; i < n; ++i) // move along the spoke
         {
            final double ri = r2 + i * deltaR;
            v[i][j] = new Vertex(ri * c,
                                 ri * s,
                                 0);
         }
      }

      // Add all of the vertices to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < k; ++j)
         {
            addVertex( v[i][j] );
         }
      }

      // Create all the triangle strips.
      if (r2 == 0) // prevent degenerate triangles
      {
         final Primitive triFan = new TriangleFan();
         triFan.addIndex(0);
         for (int j = 0; j < k; ++j)
         {
            triFan.addIndex(k + j); // v[1][j]
         }
         addPrimitive(triFan);
      }
      else
      {
         if (MeshType.HORIZONTAL == type)
         {
            final Primitive triStrip = new TriangleStrip();
            for (int j = 0; j < k; ++j)
            {
               triStrip.addIndex(    j); // v[0][j]
               triStrip.addIndex(k + j); // v[1][j]
            }
            addPrimitive(triStrip);
         }
         else if (MeshType.VERTICAL == type)
         {
         }
         else // MeshType.CHECKER triangle strips
         {
            for (int j = 0; j < k - 1; ++j)
            {
               addPrimitive(
                  new TriangleStrip( 0 * k + j,       // v[0][j  ]
                                     1 * k + j,       // v[1][j  ]
                                     0 * k + j + 1,   // v[0][j+1]
                                     1 * k + j + 1)); // v[1][j+1]
            }
         }
      }
      for (int i = 1; i < n - 1; ++i)
      {
         if (MeshType.HORIZONTAL == type)
         {
            Primitive triStrip = new TriangleStrip();
            for (int j = 0; j < k; ++j)
            {
               triStrip.addIndex(   i * k + j); // v[i  ][j]
               triStrip.addIndex((1+i)* k + j); // v[i+1][j]
            }
            addPrimitive(triStrip);
         }
         else if (MeshType.VERTICAL == type)
         {
         }
         else // Mesh.CHECKER triangle strips
         {
            for (int j = 0; j < k - 1; ++j)
            {
               addPrimitive(
                  new TriangleStrip(  i * k + j,       // v[i  ][j  ]
                                   (1+i)* k + j,       // v[i+1][j  ]
                                      i * k + j + 1,   // v[i  ][j+1]
                                   (1+i)* k + j + 1)); // v[i+1][j+1]
            }
         }
      }
/*
      // Create all the triangle strips.
      // If r2 == 0, then this code will put degenerate triangles
      // into the triangle strip at the center (which should be
      // a triangle fan).
      for (int i = 0; i < n - 1; ++i)
      {
         final Primitive triStrip = new TriangleStrip();
         for (int j = 0; j < k; ++j)
         {
            triStrip.addIndex(   i * k + j); // v[i  ][j]
            triStrip.addIndex((1+i)* k + j); // v[i+1][j]
         }
         addPrimitive(triStrip);
      }
*/
   }



   // Implement the MeshMaker_TP interface (four methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override public MeshType getType() {return type;}

   @Override
   public RingSector remake(final int n, final int k, final MeshType type)
   {
      return new RingSector(this.r1, this.r2,
                            this.theta1, this.theta2,
                            n, k,
                            type);
   }
}//RingSector
