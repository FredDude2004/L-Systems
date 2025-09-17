/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_LP;

import renderer.scene.*;
import renderer.scene.primitives.*;
import renderer.scene.util.MeshMaker;

/**
   Create a wireframe model of a disk
   in the xy-plane centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Disk_(mathematics)" target="_top">
                https://en.wikipedia.org/wiki/Disk_(mathematics)</a>

   @see DiskSector
*/
public class Disk extends Model implements MeshMaker
{
   public final double r;
   public final int n;
   public final int k;

   /**
      Create a disk in the xy-plane with radius 1,
      with 12 spokes coming out of the center, and
      with 6 concentric circles around the disk.
   */
   public Disk( )
   {
      this(1, 6, 12);
   }


   /**
      Create a disk in the xy-plane with radius
      {@code r}, with 12 spokes coming out of the
      center, and with 6 concentric circles around
      the disk.

      @param r  radius of the disk
   */
   public Disk(final double r)
   {
      this(r, 6, 12);
   }


   /**
      Create a disk in the xy-plane with radius
      {@code r}, with {@code k} spokes coming out
      of the center, and with {@code n} concentric
      circles around the disk.
   <p>
      If there are {@code k} spokes, then each circle around
      the center will have {@code k} line segments.
      If there are {@code n} concentric circles around the
      center, then each spoke will have {@code n} line segments.
   <p>
      There must be at least three spokes and at least
      one concentric circle.

      @param r  radius of the disk
      @param n  number of concentric circles
      @param k  number of spokes in the disk
      @throws IllegalArgumentException if {@code n} is less than 1
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public Disk(final double r, final int n, final int k)
   {
      super(String.format("Disk(%.2f,%d,%d)", r, n, k));

      if (n < 1)
         throw new IllegalArgumentException("n must be greater than 0");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      this.r = r;
      this.n = n;
      this.k = k;

      // Create the disk's geometry.

      final double deltaR = r / n,
                   deltaTheta = 2 * Math.PI / k;

      // An array of vertices to be used to create line segments.
      final Vertex[][] v = new Vertex[n][k];

      // Create all the vertices.
      for (int j = 0; j < k; ++j) // choose a spoke (an angle)
      {
         final double c = Math.cos(j * deltaTheta),
                      s = Math.sin(j * deltaTheta);
         for (int i = 0; i < n; ++i) // move along the spoke
         {
            final double ri = (i + 1) * deltaR;
            v[i][j] = new Vertex( ri * c,
                                  ri * s,
                                  0 );
         }
      }
      final Vertex center = new Vertex(0,0,0);

      // Add all of the vertices to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < k; ++j)
         {
            addVertex( v[i][j] );
         }
      }
      addVertex( center );
      final int centerIndex = n * k;

      // Create a line fan connecting the center to the inner circle.
      final LineFan lineFan = new LineFan();
      lineFan.addIndex(centerIndex);
      for (int j = 0; j < k; ++j)
      {
         lineFan.addIndex( (0 * k) + j );  // v[0][j]
      }
      addPrimitive(lineFan);

      // Create the spokes connecting the inner circle to the outer circle.
      for (int j = 0; j < k; ++j) // choose a spoke
      {
         final LineStrip lineStrip = new LineStrip();
         for (int i = 0; i < n; ++i)
         {
            lineStrip.addIndex( (i * k) + j );  // v[i][j]
         }
         addPrimitive(lineStrip);
      }

      // Create a line loop for each concentric circle.
      for (int i = 0; i < n; ++i)  // choose a circle
      {
         final LineLoop lineLoop = new LineLoop();
         for (int j = 0; j < k; ++j)
         {
            lineLoop.addIndex( (i * k) + j );  // v[i][j]
         }
         addPrimitive(lineLoop);
      }
   }



   // Implement the MeshMaker interface (three methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override
   public Disk remake(final int n, final int k)
   {
      return new Disk(this.r, n, k);
   }
}//Disk
