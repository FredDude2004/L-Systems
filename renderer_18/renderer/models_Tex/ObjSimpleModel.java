/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_Tex;

import renderer.scene.*;
import renderer.scene.primitives.*;

import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.regex.*;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;

/**
<p>
   A simple demonstration of loading and drawing a basic OBJ file.
<p>
   A basic OBJ file is a text file that contains three kinds of lines:
   lines that begin with the character {@code 'v'}, lines that begin
   with the character {@code 'f'}, and lines that begin with the
   character {@code '#'}.
<p>
   A line in an OBJ file that begins with {@code '#'} is a comment line
   and can be ignored.
<p>
   A line in an OBJ file that begins with {@code 'v'} is a line that
   describes a vertex in 3-dimensional space. The {@code 'v'} will always
   be followed on the line by three doubles, the {@code x}, {@code y},
   and {@code z} coordinates of the vertex.
<p>
   A line in an OBJ file that begins with {@code 'f'} is a line that
   describes a "face". The {@code 'f'} will be followed on the line by
   a sequence of positive integers. The integers are the indices of the
   vertices that make up the face. The "index" of a vertex is the order
   in which the vertex was listed in the OBJ file. So a line like this
<pre>{@code
      f  2  4  1
}</pre>
   would represent a triangle made up of the 2nd vertex read from the file,
   the 4th vertex read from the file, and the 1st vertex read from the file.
   And a line like this
<pre>{@code
      f  2  4  3  5
}</pre>
   would represent a quadrilateral made up of the 2nd vertex read from the file,
   the 4th vertex read from the file, the 3rd vertex read from the file, and
   the 5th vertex read from the file.
<p>
   See <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file" target="_top">
                https://en.wikipedia.org/wiki/Wavefront_.obj_file</a>
*/
public class ObjSimpleModel extends Model
{
   /**
      Create a textured model from the contents of an OBJ file.
   <p>
      Give all the triangles in a face the same (random) color.
      Our {@link Model} data structure has no way of remembering
      which triangles make up one face of a model, but this
      information is available at the time the OBJ file is read.
      We can use this load time information to uniformly color
      each face of the model.

      @param texture  {@link Texture} to use with this {@link Model}
      @param objFile  {@link File} object for the OBJ data file
   */
   public ObjSimpleModel(final Texture texture, final File objFile)
   {
      super(objFile.getPath());

      // Add the given texture to this model.
      addTexture(texture);

      // Add texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(0.5, 1.0),
                      new TexCoord(1.0, 0.0));

      // Open the OBJ file.
      FileInputStream fis = null;
      try
      {
         fis = new FileInputStream( objFile );
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace(System.err);
         System.err.printf("ERROR! Could not find OBJ file: %s\n", objFile);
         System.exit(-1);
      }

      // Get the geometry from the OBJ file.
      try
      {
         // Pattern for parsing lines that start with "f".
         final Pattern p = Pattern.compile("^(\\d*)[/]?(\\d*)[/]?(\\d*)");

         final Scanner scanner = new Scanner(fis);
         while ( scanner.hasNext() )
         {
            final String token = scanner.next();
            if ( token.startsWith("#")
              || token.startsWith("vt")
              || token.startsWith("vn")
              || token.startsWith("s")
              || token.startsWith("g")
              || token.startsWith("o")
              || token.startsWith("usemtl")
              || token.startsWith("mtllib") )
            {
               scanner.nextLine(); // skip over these lines
            }
            else if ( token.startsWith("v") )
            {
               final double x = scanner.nextDouble();
               final double y = scanner.nextDouble();
               final double z = scanner.nextDouble();
               final Vertex v = new Vertex(x, y, z);
               this.addVertex( v );
            }// parse vertex
            else if ( token.startsWith("f") )
            {
               // Create a Color object for all the Triangles in this face.
               final Random generator = new Random();
               final float r = generator.nextFloat();
               final float g = generator.nextFloat();
               final float b = generator.nextFloat();
               addColor(new Color(r, g, b));
               int colorIndex = colorList.size() -  1;

               // Tokenize the rest of the line.
               final String restOfLine = scanner.nextLine();
               final Scanner scanner2 = new Scanner( restOfLine );
               // Parse three vertices and make a Triangle primitive.
               final int[] vIndex = new int[3];
               for (int i = 0; i < 3; ++i)
               {
                  // Parse a "v/vt/vn" group.
                  final String faceGroup = scanner2.next();
                  final Matcher m = p.matcher( faceGroup );
                  if ( m.find() )
                  {
                     vIndex[i] = Integer.parseInt( m.group(1) ) - 1;
                     final String vt = m.group(2);  // don't need
                     final String vn = m.group(3);  // don't need
                  }
                  else
                     System.err.println("Error: bad face: " + faceGroup);
               }
               addPrimitive(new Triangle(vIndex[1],
                                         vIndex[0],
                                         vIndex[2],
                                         1, 0, 2, // texture coordinates
                                         0));     // texture index

               // Parse another vertex and make a Triangle primitive.
               while (scanner2.hasNext())
               {
                  vIndex[1] = vIndex[2];
                  final String faceGroup = scanner2.next();
                  final Matcher m = p.matcher( faceGroup );
                  if ( m.find() )
                  {
                     vIndex[2] = Integer.parseInt( m.group(1) ) - 1;
                     final String vt = m.group(2);  // don't need
                     final String vn = m.group(3);  // don't need
                  }
                  else
                     System.err.println("Error: bad face: " + faceGroup);

                  addPrimitive(new Triangle(vIndex[1],
                                            vIndex[0],
                                            vIndex[2],
                                            1, 0, 2, // texture coordinates
                                            0));     // texture index
               }
            }// parse face
         }// parse one line
         fis.close();
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
         System.err.printf("ERROR! Could not read OBJ file: %s\n", objFile);
         System.exit(-1);
      }
   }
}//ObjSimpleModel
