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
   Create a solid model of a tetrahedron as a
   triangular pyramid with an equilateral triangle
   base (centered at the origin in the xz-plane)
   whose three vertices are connected to a 4th vertex
   on the positive y-axis.

   @see Tetrahedron
*/
public class TriangularPyramid extends Model implements MeshMaker_TP
{
   public final double r;
   public final double h;
   public final int n;
   public final int k;
   public final MeshType type;

   /**
      Create a regular tetrahedron having side length
      {@code sqrt(3)/sqrt(2)}, with one face in the
      xz-plane with its center at the origin, and the
      4th vertex on the positive y-axis at height 1.
   */
   public TriangularPyramid( )
   {
      this(Math.sqrt(3)/Math.sqrt(2)); // makes the height = 1
      //or
      //this(Math.sqrt(3));  // make the height = sqrt(2) > 1
   }


   /**
      Create a regular tetrahedron having side length {@code s},
      with one face in the xz-plane with its center at the origin,
      and with the 4th vertex on the positive y-axis at
      height {@code s*sqrt(2)/sqrt(3)}.

      @param s  the length of the regular tetrahedron's sides
   */
   public TriangularPyramid(final double s)
   {
      this(s/Math.sqrt(3), s*Math.sqrt(2)/Math.sqrt(3));
   }


   /**
      Create a tetrahedron with one face being an equilateral triangle
      inscribed in a circle of radius {@code r} centered at the origin
      of the xz-plane and with the 4th vertex on the y-axis at height
      {@code h}.
   <p>
      If {@code h = r * sqrt(2)}, then the tetrahedron is a regular tetrahedron.
      with side length {@code s = r * sqrt(3)}.
   <p>
      Another way to state this is, if an equilateral triangle is inscribed
      in a circle of radius {@code r}, then the edge length of the triangle
      is {@code r*sqrt(3)} and the height of the regular tetrahedron made
      from the triangle is {@code r*sqrt(2)}.

      @param r  radius of circle in xz-plane that the equilateral base is inscribed in
      @param h  coordinate on the y-axis of the apex
   */
   public TriangularPyramid(final double r, final double h)
   {
      super(String.format("Triangular Pyramid(%.2f,%.2f)", r, h));

      this.r = r;
      this.h = h;
      this.n = 1;
      this.k = 1;
      this.type = MeshType.HORIZONTAL;

      // Create the tetrahedron's geometry.
      final double sqrt3 = Math.sqrt(3.0);
      addVertex(new Vertex( r,   0,    0),  // three vertices around the bottom face
                new Vertex(-r/2, 0,  r*0.5*sqrt3),
                new Vertex(-r/2, 0, -r*0.5*sqrt3),
                new Vertex( 0,   h,    0)); // vertex at the top

      // Create 4 triangles.
      //  https://gamedev.stackexchange.com/questions/34959/triangle-strips-of-tetraheron
      //  https://www.khronos.org/opengl/wiki/Tutorial4:_Using_Indices_and_Geometry_Shaders_(C_/SDL)
      //  https://cs.lmu.edu/~ray/notes/openglexamples/
      addPrimitive(new Triangle(0, 1, 2),   // bottom face
                   new TriangleFan(3, 0, 2, 1, 0));
   }


   /**
      Create a tetrahedron with one face being an equilateral triangle
      inscribed in a circle of radius {@code r} centered at the origin
      of the xz-plane and with the 4th vertex on the y-axis at height
      {@code h}.
   <p>
      If {@code h = r * sqrt(2)}, then the tetrahedron is a regular tetrahedron.
      with side length {@code s = r * sqrt(3)}.
   <p>
      Another way to state this is, if an equilateral triangle is inscribed
      in a circle of radius {@code r}, then the edge length of the triangle
      is {@code r*sqrt(3)} and the height of the regular tetrahedron made
      from the triangle is {@code r*sqrt(2)}.

      @param r  radius of circle in xz-plane that the equilateral base is inscribed in
      @param h  coordinate on the y-axis of the apex
      @param n  number of lines of latitude around the body of the pyramid
      @param k  number of triangles in the triangle fan at the top of each side
      @throws IllegalArgumentException if {@code n} is less than 1
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public TriangularPyramid(final double r, final double h,
                            final int n, final int k)
   {
      this(r, h, n, k, MeshType.HORIZONTAL);
   }


   /**
      Create a tetrahedron with one face being an equilateral triangle
      inscribed in a circle of radius {@code r} centered at the origin
      of the xz-plane and with the 4th vertex on the y-axis at height
      {@code h}.
   <p>
      If {@code h = r * sqrt(2)}, then the tetrahedron is a regular tetrahedron.
      with side length {@code s = r * sqrt(3)}.
   <p>
      Another way to state this is, if an equilateral triangle is inscribed
      in a circle of radius {@code r}, then the edge length of the triangle
      is {@code r*sqrt(3)} and the height of the regular tetrahedron made
      from the triangle is {@code r*sqrt(2)}.

      @param r  radius of circle in xz-plane that the equilateral base is inscribed in
      @param h  coordinate on the y-axis of the apex
      @param n  number of lines of latitude around the body of the pyramid
      @param k  number of triangles in the triangle fan at the top of each side
      @param type  choose between striped and checkerboard triangle strips
      @throws IllegalArgumentException if {@code n} is less than 1
      @throws IllegalArgumentException if {@code k} is less than 1
   */
   public TriangularPyramid(final double r, final double h,
                            final int n, final int k,
                            final MeshType type)
   {
      super(String.format("Triangular Pyramid(%.2f,%.2f,%d,%d)",
                                              r,   h,   n, k));

      if (n < 1)
         throw new IllegalArgumentException("n must be greater than 0");
      if (k < 1)
         throw new IllegalArgumentException("k must be greater than 0");

      this.r = r;
      this.h = h;
      this.n = n;
      this.k = k;
      this.type = type;

      // Create the pyramid's geometry.
      // An array of vertices to be used to create faces.
      final Vertex[][] v = new Vertex[n][3*k];

      // Create the vertices.
      final Vertex apex = new Vertex(0, h, 0);
      final Vertex centerVertex = new Vertex(0, 0, 0);

      // three vertices around the bottom face
      final double sqrt3 = Math.sqrt(3.0);
      Vertex v0 = new Vertex( r,   0,    0);
      Vertex v1 = new Vertex(-r/2, 0,  r*0.5*sqrt3);
      Vertex v2 = new Vertex(-r/2, 0, -r*0.5*sqrt3);

      for (int i = 0; i < n; i++) // choose a height of latitude
      {
         final double y = i * (h / n);

         // Use linear interpolation (lerp).
         final double t = i * (1.0 / n);
         final Vertex vA = new Vertex( (1-t)*v0.x + t*apex.x,
                                                           y,
                                       (1-t)*v0.z + t*apex.z );
         final Vertex vB = new Vertex( (1-t)*v1.x + t*apex.x,
                                                           y,
                                       (1-t)*v1.z + t*apex.z );
         final Vertex vC = new Vertex( (1-t)*v2.x + t*apex.x,
                                                           y,
                                       (1-t)*v2.z + t*apex.z );

         // use linear interpolation again
         for (int j = 0; j < k; ++j)
         {
            final double s = j * (1.0 / k);
            v[i][j] = new Vertex( (1-s)*vA.x + s*vB.x,
                                                    y,
                                  (1-s)*vA.z + s*vB.z );

            v[i][k+j] = new Vertex( (1-s)*vB.x + s*vC.x,
                                                      y,
                                    (1-s)*vB.z + s*vC.z );

            v[i][2*k+j] = new Vertex( (1-s)*vC.x + s*vA.x,
                                                        y,
                                      (1-s)*vC.z + s*vA.z );
         }
      }

      // Add the vertices to this model.
      for (int i = 0; i < n; ++i)
      {
         for (int j = 0; j < 3*k; ++j)
         {
            addVertex( v[i][j] );
         }
      }
      addVertex(apex,
                centerVertex);
      final int apexIndex = n * 3*k,
                centerIndex = apexIndex + 1;

      // Create all the square strips around the pyramid wall.
      if (MeshType.HORIZONTAL == type)
      {
         for (int i = 0; i < n - 1; ++i) // choose a height of latitude
         {
            final Primitive triStrip = new TriangleStrip();
            for (int j = 0; j < 3*k - 1; ++j) // choose a line of longitude
            {
               triStrip.addIndex(     i*(3*k)+j ); // v[i  ][j]
               triStrip.addIndex( (1+i)*(3*k)+j ); // v[i+1][j]
            }
            triStrip.addIndex(     i*(3*k)+0 ); // v[i  ][0]
            triStrip.addIndex( (1+i)*(3*k)+0 ); // v[i+1][0]
            addPrimitive(triStrip);
         }
      }
      else if (MeshType.VERTICAL == type)
      {
         for (int j = 0; j < 3*k - 1; ++j) // choose a line of longitude
         {
            final Primitive triStrip = new TriangleStrip();
            triStrip.addIndex(centerIndex);
            for (int i = 0; i < n; ++i) // choose a height of latitude
            {
               triStrip.addIndex(i * (3*k) + j);    // v[i][j  ]
               triStrip.addIndex(i * (3*k) + j+1);  // v[i][j+1]
            }
            triStrip.addIndex(apexIndex);
            addPrimitive(triStrip);
         }
         // Last vertical strip to close the pyramid.
         final Primitive triStrip = new TriangleStrip();
         triStrip.addIndex(centerIndex);
         for (int i = 0; i < n; ++i) // choose a height of latitude
         {
            triStrip.addIndex(i * (3*k) + 3*k-1);  // v[i][3k-1 ]
            triStrip.addIndex(i * (3*k) + 0);      // v[i][0    ]
         }
         triStrip.addIndex(apexIndex);
         addPrimitive(triStrip);
      }
      else // MeshType.CHECKER triangle strips
      {
         for (int i = 0; i < n - 1; ++i) // choose a height of latitude
         {
            for (int j = 0; j < 3*k; ++j) // choose a line of longitude
            {
               addPrimitive(
                  new TriangleStrip(    i*(3*k)+j,      // v[i  ][j  ]
                                    (1+i)*(3*k)+j,      // v[i+1][j  ]
                                        i*(3*k)+j+1,    // v[i  ][j+1]
                                    (1+i)*(3*k)+j+1 )); // v[i+1][j+1]
            }
            addPrimitive(
               new TriangleStrip(    i*(3*k)+3*k-1,  // v[i  ][3k-1]
                                 (1+i)*(3*k)+3*k-1,  // v[i+1][3k-1]
                                     i*(3*k)+0     , // v[i  ][0]
                                 (1+i)*(3*k)+0 ));   // v[i+1][0]
         }
      }


      if ( MeshType.HORIZONTAL == type
        || MeshType.CHECKER == type )
      {
         // Create the triangle fan at the apex.
         final Primitive apexFan = new TriangleFan();
         apexFan.addIndex(apexIndex);
         for (int j = 3*k - 1; j >= 0; --j) // choose a line of longitude
         {
            apexFan.addIndex( (n-1)*(3*k)+j );   // v[n-1][j]
         }
         apexFan.addIndex( (n-1)*(3*k)+3*k-1 );  // v[n-1][3k-1]
         addPrimitive(apexFan);

         // Create the triangle fan in the base.
         final Primitive baseFan = new TriangleFan();
         baseFan.addIndex(centerIndex);
         for (int j = 0; j < 3*k - 1; ++j) // choose a line of longitude
         {
            baseFan.addIndex( j ); // v[0][j]
         }
         baseFan.addIndex( 0 );    // v[0][0]
         addPrimitive(baseFan);
      }
   }



   // Implement the MeshMaker_TP interface (four methods).
   @Override public int getHorzCount() {return n;}

   @Override public int getVertCount() {return k;}

   @Override public MeshType getType() {return type;}

   @Override
   public TriangularPyramid remake(final int n, final int k, final MeshType type)
   {
      return new TriangularPyramid(this.r, this.h,
                                   n, k,
                                   type);
   }
}//TriangularPyramid
