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
   Create a solid model of a disk
   in the xy-plane centered at the origin.
<p>
   See <a href="https://en.wikipedia.org/wiki/Disk_(mathematics)" target="_top">
                https://en.wikipedia.org/wiki/Disk_(mathematics)</a>

   @see DiskSector
*/
public class Disk extends Model implements MeshMaker_TP
{
   public final double r;
   public final int n;
   public final int k;
   public final MeshType type;

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
      this(r, n, k, MeshType.HORIZONTAL);
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
      @param type  choose between striped and checkerboard triangle strips
      @throws IllegalArgumentException if {@code n} is less than 1
      @throws IllegalArgumentException if {@code k} is less than 3
   */
   public Disk(final double r, final int n, final int k,
               final MeshType type)
   {
      super(String.format("Disk(%.2f,%d,%d)", r, n, k));

      if (n < 1)
         throw new IllegalArgumentException("n must be greater than 0");
      if (k < 3)
         throw new IllegalArgumentException("k must be greater than 2");

      this.r = r;
      this.n = n;
      this.k = k;
      this.type = type;

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


      // Create all the triangle strips.
      if (MeshType.HORIZONTAL == type)
      {
         for (int i = 0; i < n - 1; ++i) // choose a circle of latitude
         {
            final Primitive triStrip = new TriangleStrip();
            for (int j = 0; j < k; ++j) // choose a line of longitude
            {
               triStrip.addIndex(   i * k + j); // v[i  ][j]
               triStrip.addIndex((1+i)* k + j); // v[i+1][j]
            }
            triStrip.addIndex(   i * k + 0); // v[i  ][0]
            triStrip.addIndex((1+i)* k + 0); // v[i+1][0]
            addPrimitive(triStrip);
         }
      }
      else if (MeshType.VERTICAL == type)
      {
         for (int j = 0; j < k - 1; ++j) // choose a line of longitude
         {
            final Primitive triStrip = new TriangleStrip();
            triStrip.addIndex(centerIndex);
            for (int i = 0; i < n; ++i) // choose a circle of latitude
            {
               triStrip.addIndex((i+0) * k + j);    // v[i][j  ]
               triStrip.addIndex((i+0) * k + j+1);  // v[i][j+1]
            }
            addPrimitive(triStrip);
         }
         final Primitive triStrip = new TriangleStrip();
         triStrip.addIndex(centerIndex);
         for (int i = 0; i < n; ++i) // choose a circle of latitude
         {
            triStrip.addIndex((i+0) * k + k-1);  // v[i][k-1]
            triStrip.addIndex((i+0) * k + 0);    // v[i][0  ]
         }
         addPrimitive(triStrip);
      }
      else // MeshType.CHECKER triangle strips
      {
         for (int i = 0; i < n - 1; ++i) // choose a circle of latitude
         {
            for (int j = 0; j < k - 1; ++j) // choose a line of longitude
            {
               addPrimitive(
                  new TriangleStrip(  i * k + j,       // v[i  ][j  ]
                                   (1+i)* k + j,       // v[i+1][j  ]
                                      i * k + j + 1,   // v[i  ][j+1]
                                   (1+i)* k + j + 1)); // v[i+1][j+1]
            }
            addPrimitive(
               new TriangleStrip(  i * k + k - 1, // v[i  ][k-1]
                                (1+i)* k + k - 1, // v[i+1][k-1]
                                   i * k + 0,     // v[i  ][0]
                                (1+i)* k + 0));   // v[i+1][0]
         }
      }

      if ( MeshType.HORIZONTAL == type
        || MeshType.CHECKER == type )
      {
         // Create the triangle fan at the center.
         final Primitive centerFan = new TriangleFan();
         centerFan.addIndex(centerIndex);
         for (int j = 0; j < k; ++j) // choose a line of longitude
         {
            centerFan.addIndex( j ); // v[0][j]
         }
         centerFan.addIndex( 0 ); // v[0][0]
         addPrimitive(centerFan);
      }
   }



   // Implement the MeshMaker_TP interface (four methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override public MeshType getType() {return type;}

   @Override
   public Disk remake(final int n, final int k, final MeshType type)
   {
      return new Disk(this.r, n, k, type);
   }
}//Disk
