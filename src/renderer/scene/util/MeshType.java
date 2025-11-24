/*
 * Renderer 18. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.scene.util;

/**
   An enum to be used by any {@link renderer.scene.Model} that
   implements the {@link MeshMaker_TP} interface.
   <p>
   A {@link renderer.scene.Model} that implements {@link MeshMaker_TP}
   can build its geometric mesh in any of three structures: using
   horizontal triangle strips, vertical triangle strips, or individual
   quads (each made up out of a two triangle strip).
*/
public enum MeshType
{
   HORIZONTAL,
   VERTICAL,
   CHECKER
   {
      @Override
      public MeshType next()
      {
         return values()[0]; // rollover to the first value
      };
    };


   /**
      Provide a cyclic increment like operation for this enum.
   <p>
      https://stackoverflow.com/questions/17664445/is-there-an-increment-operator-for-java-enum

      @return the next {@code MeshType} in the cycle
   */
   public MeshType next()
   {
      return values()[ordinal() + 1];
   }
}
