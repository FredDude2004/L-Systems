/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

import  renderer.scene.*;
import  renderer.scene.primitives.Triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
   Build a {@link Model} of a maze by parsing a text file
   description of the maze's layout of walls, floor tiles,
   and ceiling tiles.
*/
public class Maze extends Model
{
   private int cellSize;
   private int cellsWide;
   private int cellsHigh;
   private int wallHeight;
   private int numTex;

   /**
      Build a {@link Model} of a maze by parsing a text file
      description of the maze's layout of walls, floor tiles,
      and ceiling tiles.

      @param pathName  name of text file to parse
   */
   public Maze(String pathName)
   {
      super(pathName);

      Scanner scanner = null;
      try
      {
         File f = new File( pathName );
         scanner = new Scanner(f);
      }
      catch (FileNotFoundException e)
      {
         System.err.println("Err: file not found");
         System.exit(1);
      }

      buildMaze(scanner);
      scanner.close();
   }


   /**
      Parse the maze description file.

      @param
   */
   private void buildMaze(Scanner scanner)
   {
      // Add four texture coordinates to this model.
      addTextureCoord(new TexCoord(0.0, 0.0),
                      new TexCoord(1.0, 0.0),
                      new TexCoord(1.0, 1.0),
                      new TexCoord(0.0, 1.0));

      while (scanner.hasNext())
      {
         String nextToken = scanner.next();
       //System.out.println(nextToken);

         if (nextToken.equals("DIMENSIONS"))
         {
            cellsWide = scanner.nextInt();
            cellsHigh = scanner.nextInt();
         }

         else if (nextToken.equals("HEIGHT"))
         {
            wallHeight = scanner.nextInt();
         }

         else if (nextToken.equals("CELLSIZE"))
         {
            cellSize = scanner.nextInt();
         }

         else if (nextToken.equals("TEXTURES"))
         {
            numTex = scanner.nextInt();

            // Ignore comments.
            while (scanner.hasNext("#")) scanner.nextLine();

            // Loop through textures.
            for (int i = 0; i < numTex; ++i)
            {
               String next = scanner.next();
               if (next.equals("#"))
               {
                  scanner.nextLine();
                  next = scanner.next();
               }
               addTexture( new Texture(next) );
               // notice texture numbers are offset by 1 from their
               // list index, e.g. texture n will be at getTexture(n-1)
            }
         }

         else if (nextToken.equals("WALLPLAN"))
         {
            // Ignore comments.
            while (scanner.hasNext("#")) scanner.nextLine();

            // (x, z) model coords for a tile
            double z = 0;
            for (int i = 0; i < 2*cellsHigh; ++i)
            {
               double x = 0;
               for (int j = 0; j < cellsWide; ++j)
               {
                  // Ignore comment.
                  if (scanner.hasNext("#")) scanner.nextLine();

                  int tex = scanner.nextInt();
                  if (tex != 0)
                  {
                     if (i%2 == 0) // Wall in xy-plane, z is constant.
                     {
                        addWall(x, z, x + cellSize, z, tex-1);
                     }
                     else // Wall in yz-plane, x is constant.
                     {
                        addWall(x, z, x, z + cellSize, tex-1);
                     }
                  }
                  x += cellSize;
               }
               if (i%2 != 0) z += cellSize;
            }
         }// WALLPLAN

         else if (nextToken.equals("FLOORPLAN"))
         {
            // Ignore comments.
            while (scanner.hasNext("#")) scanner.nextLine();

            // (x, z) model coords for a tile
            double z = 0;
            for (int i = 0; i < cellsHigh; ++i)
            {
               double x = 0;
               for (int j = 0; j < cellsWide; ++j)
               {
                  int tex = scanner.nextInt();
                  if (tex != 0)
                  {
                     addFloor(x, z, tex-1);
                  }
                  x += cellSize;
               }
               z += cellSize;
            }
         }//FLOORPLAN

         else if (nextToken.equals("CEILINGPLAN"))
         {
            // Ignore comments.
            while (scanner.hasNext("#")) scanner.nextLine();

            if (! scanner.hasNextInt()) // If blank, there is no ceiling
            {
               scanner.nextLine();
               System.out.println("No Ceiling");
               continue;
            }

            // (x, z) model coords for a tile
            double z = 0;
            for (int i = 0; i < cellsHigh; ++i)
            {
               double x = 0;
               for (int j = 0; j < cellsWide; ++j)
               {
                  int tex = scanner.nextInt();
                  if (tex != 0)
                  {
                     addCeiling(x, z, tex-1);
                  }
                  x += cellSize;
               }
               z += cellSize;
            }
         }//CEILINGPLAN

       //else if (nextToken.equals("#")) scan.nextLine();

         scanner.nextLine();
      }
   }//buildMaze()


   /**
      Function for adding the walls to the maze.
   */
   private void addWall(double x1, double z1, double x2, double z2, int textureIndex)
   {
      //create vertices of Triangle
      final Vertex v0 = new Vertex(x1, 0, z1);
      final Vertex v1 = new Vertex(x2, 0, z2);
      final Vertex v2 = new Vertex(x2, wallHeight ,z2 );
      final Vertex v3 = new Vertex(x1, wallHeight, z1);

      final int vIndex = vertexList.size();
      addVertex(v0, v1, v2, v3);

      final Triangle t0 = new Triangle(vIndex + 0, vIndex + 1, vIndex + 2,
                                       0, 1, 2,
                                       textureIndex);

      final Triangle t1 = new Triangle(vIndex + 0, vIndex + 2, vIndex + 3,
                                       0, 2, 3,
                                       textureIndex);

      addPrimitive(t0, t1);
   }//addWall()


   /**
      Function for adding floor as one tile per cell.
   */
   private void addFloor(double x, double z, int textureIndex)
   {
      //create vertices to add to triangle
      final Vertex v0 = new Vertex(x,            0,  z + cellSize); //near left
      final Vertex v1 = new Vertex(x + cellSize, 0,  z + cellSize); //near right
      final Vertex v2 = new Vertex(x + cellSize, 0,  z); //far right
      final Vertex v3 = new Vertex(x,            0,  z); //far left

      final int vIndex = vertexList.size();
      addVertex(v0, v1, v2, v3);

      final Triangle t0 = new Triangle(vIndex + 0, vIndex + 1, vIndex + 2,
                                       0, 1, 2,
                                       textureIndex);

      final Triangle t1 = new Triangle(vIndex + 0, vIndex + 2, vIndex + 3,
                                       0, 2, 3,
                                       textureIndex);

      addPrimitive(t0, t1);
   }//addFloor()


   /**
      Function for adding ceiling as one tile per cell.
   */
   private void addCeiling(double x, double z, int textureIndex)
   {
      //create vertices to add to triangle
      final Vertex v0 = new Vertex(x,            wallHeight,  z + cellSize); //near left
      final Vertex v1 = new Vertex(x + cellSize, wallHeight,  z + cellSize); //near right
      final Vertex v2 = new Vertex(x + cellSize, wallHeight,  z); //far right
      final Vertex v3 = new Vertex(x,            wallHeight,  z); //far left

      final int vIndex = vertexList.size();
      addVertex(v0, v1, v2, v3);

      final Triangle t0 = new Triangle(vIndex + 0, vIndex + 3, vIndex + 2,
                                       0, 3, 2,
                                       textureIndex);

      final Triangle t1 = new Triangle(vIndex + 0, vIndex + 2, vIndex + 1,
                                       0, 2, 1,
                                       textureIndex);

      addPrimitive(t0, t1);
   }//addCeiling()


   /**
      For debugging.

      @return {@link String} representation of this {@code Maze} object
   */
   @Override
   public String toString()
   {
      String result = super.toString();

      result += "cellSize: "   + cellSize   + "\n";
      result += "cellsWide: "  + cellsWide  + "\n";
      result += "cellsHigh: "  + cellsHigh  + "\n";
      result += "wallHeight: " + wallHeight + "\n";
      result += "NumTex: "     + numTex     + "\n";

      return result;
   }
}
