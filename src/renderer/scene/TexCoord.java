/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene;

/**

*/
public class TexCoord
{
   public final double s;
   public final double t;

   /**
      @param s  1st texture coordinate
      @param t  2nd texture coordinate
   */
   public TexCoord(final double s, final double t)
   {
      this.s = s;
      this.t = t;
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code TexCoord} object
   */
   @Override
   public String toString()
   {
      // Here is one way to get programmable precision.
      final int p = 5; // the precision for the following format string
      final String format = "(s, t) = (% ."+p+"f  % ."+p+"f)";
      return String.format(format, s, t);
   }
}
