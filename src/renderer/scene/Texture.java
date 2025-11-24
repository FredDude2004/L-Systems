/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

import renderer.framebuffer.FrameBuffer;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
   This represents a texture with transparency (RGBA).
*/
public class Texture
{
   public int width;
   public int height;
   public int[] pixel_buffer; // contains each pixel's color data
   public int[] alpha_buffer; // contains each pixel's transparency data
   public final String name;  // used for debugging


   /**
      Create an empty {@code Texture} with the given size and name.

      @param name    {@link String} name for this {@code Texture}
      @param width   width of this {@code Texture}
      @param height  height of this {@code Texture}
   */
   public Texture(final int width, final int height, final String name)
   {
      this.name = name;
      this.width = width;
      this.height = height;
      // Create the pixel buffers.
      this.pixel_buffer = new int[width * height];
      this.alpha_buffer = new int[width * height];
   }


   /**
      Create a {@code Texture} directly from RGBA pixel data.
      <p>
      The {@code data} array is assumed to contain
      {@code 4 * width * height} ints where every
      four ints represent the RGBA color and transparency
      values for a pixel of the texture.

      @param name    {@link String} name for this {@code Texture}
      @param width   width of this {@code Texture}
      @param height  height of this {@code Texture}
      @param data    array holding the RGBA color values for each pixel of this {@code Texture}
   */
   public Texture(final String name, final int width, final int height, final int[] data)
   {
      this.name = name;
      this.width = width;
      this.height = height;
      this.pixel_buffer = new int[width * height];
      this.alpha_buffer = new int[width * height];

      for (int i = 0; i < height; ++i)
      {
         for (int j = 0; j < width; ++j)
         {
            final int r = data[4*(i*width + j) + 0];
            final int g = data[4*(i*width + j) + 1];
            final int b = data[4*(i*width + j) + 2];
            final int a = data[4*(i*width + j) + 3];
            pixel_buffer[(height - 1 - i)*width + j] = (new Color(r, g, b)).getRGB();
            alpha_buffer[(height - 1 - i)*width + j] = a;  // alpha
         }
      }
   }


   /**
      Create a {@code Texture} from pixel data in a PPM file.

      @param inputFileName  must name a PPM image file with magic number P6.
   */
   public Texture(String filename) // name of the ppm file to load texture data from
   {
      this.name = filename;

      // Read the pixel data in a PPM file.
      // http://stackoverflow.com/questions/2693631/read-ppm-file-and-store-it-in-an-array-coded-with-c
      BufferedInputStream bis = null;
      try
      {
         final FileInputStream fis = new FileInputStream(filename);
         bis = new BufferedInputStream(fis);
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace(System.err);
         System.err.printf("ERROR! Could not open texture %s\n", filename);
         System.exit(-1);
      }

      try
      {
         // Read image format string "P6".
         String magicNumber = "";
         char c = (char)bis.read();
         while (c != '\n')
         {
            magicNumber += c;
            c = (char)bis.read();
         }
         if (! magicNumber.trim().startsWith("P6"))
         {
            System.err.printf("ERROR! Improper PPM number in file %s\n", filename);
            System.exit(-1);
         }

         c = (char)bis.read();
         if ( '#' == c ) // read (and discard) IrfanView comment
         {
            while (c != '\n')
            {
               c = (char)bis.read();
            }
            c = (char)bis.read();
         }

         // Read image dimensions.
         String widthDim = "";
         while (c != ' ' && c != '\n')
         {
            widthDim += c;
            c = (char)bis.read();
         }
         width = Integer.parseInt(widthDim.trim());

         String heightDim = "";
         c = (char)bis.read();
         while (c != '\n')
         {
            heightDim += c;
            c = (char)bis.read();
         }
         height = Integer.parseInt(heightDim.trim());

         //System.err.printf("Texture %s: width=%d, height=%d\n\n",filename,width,height);

         // Read image rgb dimensions (which we don't use).
         c = (char)bis.read();
         while (c != '\n')
         {
            c = (char)bis.read();
         }

         // Create the pixel buffers.
         pixel_buffer = new int[this.width * this.height];
         alpha_buffer = new int[this.width * this.height];

         // Create a pixel data array.
         final byte[] pixelData = new byte[3];

         // Read pixel data, one pixel at a time, from the PPM file.
         for (int y = height - 1; y >= 0; y--)
         {
            for (int x = 0; x < width; x++)
            {
               if ( bis.read(pixelData, 0, 3) != 3 )
               {
                  System.err.printf("ERROR! Could not load %s\n", filename);
                  System.exit(-1);
               }
               int r = pixelData[0];
               int g = pixelData[1];
               int b = pixelData[2];
               if (r < 0) r = 256+r; // NOTE: Java doesn't have an unsigned
               if (g < 0) g = 256+g; // byte data type so we need to convert
               if (b < 0) b = 256+b; // negative bytes into postive ints.
               pixel_buffer[(y * width) + x] = (new Color(r, g, b)).getRGB();
               alpha_buffer[(y * width) + x] = 255;  // opaque
            }
         }
         bis.close();
      }
      catch (IOException e)
      {
         e.printStackTrace(System.err);
         System.err.printf("ERROR! Could not read texture %s\n", filename);
         System.exit(-1);
      }
   }


   /**
      Create a {@code Texture} from a {@link FrameBuffer}'s {@link FrameBuffer.Viewport}

      @param vp  {@link FrameBuffer.Viewport} whose pixel data will become a {@code Texture}
   */
   public Texture(final FrameBuffer.Viewport vp)
   {
      this.name = "from Viewport";
      this.width  = vp.getWidthVP();
      this.height = vp.getHeightVP();
      // Create the pixel buffers.
      this.pixel_buffer = new int[this.width * this.height];
      this.alpha_buffer = new int[this.width * this.height];

      for (int i = 0; i < height; ++i)
      {
         for (int j = 0; j < width; ++j)
         {
            pixel_buffer[(height - 1 - i)*width + j] = vp.getPixelVP(j,i).getRGB();
            alpha_buffer[(height - 1 - i)*width + j] = 255;  // opaque
         }
      }
   }


   /**
      Read the {@code Texture} data from a file
      using Java's {@link ImageIO} library.

      @param filename  name of a PNG, JPG, or GIF image file
   */
   private static Texture makeTextureNotPPM(final String filename)
   {
      BufferedImage img = null;

      try
      {
         img = ImageIO.read(new File(filename));
      }
      catch(IOException e)
      {
         e.printStackTrace(System.err);
         System.err.println("Cannot read image file.");
         System.exit(-1);
      }

      final int width  = img.getWidth();
      final int height = img.getHeight();

      final Texture texture = new Texture(width, height, filename);

      for (int i = 0; i < height; ++i)
      {
         for (int j = 0; j < width; ++j)
         {
            texture.pixel_buffer[(height - 1 - i)*width + j] = img.getRGB(j,i);
            texture.alpha_buffer[(height - 1 - i)*width + j] = 255;  // opaque
         }
      }

      System.err.printf("Texture %s: width=%d, height=%d\n\n",filename,width,height);

      return texture;
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Texture} object
   */
   @Override
   public String toString()
   {
      String result = "Texture: (width="+width+", height="+height+")";
      result += " name = " + "\"" + name + "\"";
      return result;
   }
}
