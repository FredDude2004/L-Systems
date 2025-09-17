/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.util;

import renderer.scene.Model;

/**
   A {@link Model} that implements {@code MeshMaker_TP}
   can build its geometric mesh in any of three structures:
   using horizontal triangle strips, vertical triangle
   strips, or individual quads (each made up out of a
   two triangle strip).
*/
public interface MeshMaker_TP extends MeshMaker
{
   /**
      @return the {@link MeshType} that the {@link Model} contains
   */
   public MeshType getType();

   /**
      Build an instance of the {@link Model} with new values for the number
      of lines of latitude and longitude while keeping all the other model
      parameters the same.

      @param n  number of lines of latitude for the returned {@link Model}
      @param k  number of lines of longitude for the returned {@link Model}
      @return a new instance of the {@link Model} with the updated parameters
   */
   default public Model remake(int n, int k)
   {
      return remake(n, k, MeshType.CHECKER);
   }

   /**
      Build an instance of the {@link Model} with new values for the number
      of lines of latitude and longitude while keeping all the other model
      parameters the same.

      @param n  number of lines of latitude for the returned {@link Model}
      @param k  number of lines of longitude for the returned {@link Model}
      @param type  the {@link MeshType} that the returned {@link Model} should contain
      @return a new instance of the {@link Model} with the updated parameters and {@link MeshType}
   */
   public Model remake(int n, int k, MeshType type);
}
