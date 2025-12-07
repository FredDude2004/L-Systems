/*
 * Renderer Models. The MIT License.
 * Copyright (c) 2022 rlkraft@pnw.edu
 * See LICENSE for details.
*/

package renderer.models_L.turtlegraphics;

import renderer.models_L.turtlegraphics.TurtleState2D;
import renderer.scene.*;
import renderer.scene.primitives.*;

/**
   https://www.clear.rice.edu/comp360/lectures/K10188_C001.pdf
*/
public class Turtle2D
{
   public final Model model;
   public final String name;
   public final double z;
   public final double xHome;
   public final double yHome;
   private double xPos;
   private double yPos;
   private double heading;
   private boolean penDown;
   private double stepSize;  // see the resize() method

   /**
      @param model  a reference to the {link Model} that this {@code Turtle2D} is builing
      @throws NullPointerException if {@code model} is {@code null}
   */
   public Turtle2D(final Model model)
   {
      this(model, model.name, 0, 0, 0);
   }


   /**
      @param model  a reference to the {@link Model} that this {@code Turtle2D} is builing
      @param name   a {@link String} that is a name for this {@code Turtle2D}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
   */
   public Turtle2D(final Model model, final String name)
   {
      this(model, name, 0, 0, 0);
   }


   /**
      @param model  a reference to the {@link Model} that this {@code Turtle2D} is builing
      @param z      the z-plane for this {@code Turtle2D}
      @throws NullPointerException if {@code model} is {@code null}
   */
   public Turtle2D(final Model model, final double z)
   {
      this(model, model.name, 0, 0, z);
   }


   /**
      @param model  a reference to the {@link Model} that this {@code Turtle2D} is builing
      @param name   a {@link String} that is a name for this {@code Turtle2D}
      @param z      the z-plane for this {@code Turtle2D}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
   */
   public Turtle2D(final Model model, final String name, final double z)
   {
      this(model, name, 0, 0, z);
   }


   /**
      @param model   a reference to the {@link Model} that this {@code Turtle2D} is builing
      @param xHome   the intial x-coordinate for this {@code Turtle2D}
      @param yHome   the intial y-coordinate for this {@code Turtle}
      @param z       the z-plane for this {@code Turtle}
      @throws NullPointerException if {@code model} is {@code null}
   */
   public Turtle2D(final Model model, final double xHome, final double yHome, final double z)
   {
      this(model, model.name, xHome, yHome, z);
   }


   /**
      @param model   a reference to the {@link Model} that this {@code Turtle} is builing
      @param name    a {@link String} that is a name for this {@code Turtle}
      @param xHome   the intial x-coordinate for this {@code Turtle}
      @param yHome   the intial y-coordinate for this {@code Turtle}
      @param z       the z-plane for this {@code Turtle}
      @throws NullPointerException if {@code model} is {@code null}
      @throws NullPointerException if {@code name} is {@code null}
   */
   public Turtle2D(final Model model, final String name, final double xHome, final double yHome, final double z)
   {
      if (null == model)
         throw new NullPointerException("Turtle's Model must not be null");
      if (null == name)
         throw new NullPointerException("Turtle's name must not be null");

      this.model = model;
      this.name = name;
      this.xHome = xHome;
      this.yHome = yHome;
      this.z = z;

      this.xPos = xHome;
      this.yPos = yHome;
      this.heading = 0;
      this.penDown = true;
      this.stepSize = 1;
   }


   /**
      Check if this {@code Turtle2D}'s pen is down.

      @return true if down else false
   */
   public boolean isPenDown()
   {
      return penDown;
   }


   /**
      Set this {@code Turtle2D}'s penDown variable.

      @param value  value for this {@code Turtle2D}'s penDown variable
   */
   public void setPenDown(final boolean value)
   {
      penDown = value;
   }


   /**
      Set this {@code Turtle2D}'s pen down.
   */
   public void penDown()
   {
      penDown = true;
   }


   /**
      Lift this {@code Turtle2D}'s pen up.
   */
   public void penUp()
   {
      penDown = false;
   }


   /**
      Get the current x position of this {@code Turtle2D}.

      @return the x position of this {@code Turtle2D}
   */
   public double getXPos()
   {
      return xPos;
   }


   /**
      Get the current y position of this {@code Turtle2D}.

      @return the y position of this {@code Turtle2D}
   */
   public double getYPos()
   {
      return yPos;
   }


   /**
      Get the current heading of this {@code Turtle2D}.

      @return the heading in degrees of this {@code Turtle2D}
   */
   public double getHeading()
   {
      return this.heading;
   }

   /**
      Get the XPos, YPos, and heading encapsulated in a {@link TurtleState2D} Object

      @return TurtleState2D
   */
   public TurtleState2D getTurtleState() {
       return new TurtleState2D(xPos, yPos, 0.0, heading);
   }

   /**
      Set the heading of this {@code Turtle2D}.

      @param heading  new heading in degrees for this {@code Turtle2D}
   */
   public void setHeading(final double heading)
   {
      this.heading = heading;
   }


   /**
      Turn this {@code Turtle2D} 90 degrees clockwise.
   */
   public void right()
   {
      turn(90);
   }


   /**
      Turn this {@code Turtle2D} 90 degrees counterclockwise.
   */
   public void left()
   {
      turn(-90);
   }


   /**
      Turn this {@code Turtle2D} by the given angle in degrees.
      Use positive angles to turn clockwise and negative angles to turn counterclockwise.

      @param degrees  the amount to turn this {@code Turtle2D} in degrees
   */
   public void turn(final double degrees)
   {
      heading = (heading + degrees) % 360;
   }


   /**
      Turn this {@code Turtle2D} to face another {@code Turtle}.

      @param turtle  the {@code Turtle2D} to turn towards
   */
   public void turnToFace(final Turtle2D turtle)
   {
      turnToFace(turtle.xPos, turtle.yPos);
   }


   /**
      Turn this {@code Turtle2D} towards the given (x, y).

      @param x  the x to turn this {@code Turtle2D} towards
      @param y  the y to turn this {@code Turtle2D} towards
   */
   public void turnToFace(final double x, final double y)
   {
      final double dx = x - xPos;
      final double dy = y - yPos;

      if (0 == dx)         // avoid a division by 0
      {
         if (dy > 0)       // if below the turtle
         {
            heading = 180;
         }
         else if (dy < 0)  // if above the turtle
         {
            heading = 0;
         }
      }
      else // dx isn't 0 so can divide by it
      {
         final double arcTan = Math.toDegrees(Math.atan(dy / dx));
         if (dx < 0)
         {
            heading = arcTan - 90;
         }
         else
         {
            heading = arcTan + 90;
         }
      }
   }


   /**
      Move this {@code Turtle2D} to the coordinates (0, 0) and give it the heading of 0 degrees.
   */
   public void home()
   {
      xPos = xHome;
      yPos = yHome;
      heading = 0;
   }


   /**
      Move this {@code Turtle2D} to the given (x, y) location.

      @param x  the x-coordinate to move this {@code Turtle2D} to
      @param y  the y-coordinate to move this {@code Turtle2D} to
   */
   public void moveTo(final double x, final double y)
   {
      xPos = x;
      yPos = y;
   }


   /**
      Move this {@code Turtle2D} foward one unit in the heading direction.
   */
   public void forward()
   {
      forward(1);
   }


   /**
      Move this {@code Turtle2D} backward one unit.
   */
   public void backward()
   {
      backward(1);
   }


   /**
      Move this {@code Turtle2D} backward the given number of units.

      @param distance  the distance to walk this {@code Turtle2D} backward
   */
   public void backward(final double distance)
   {
      forward(-distance);
   }


   /**
      Move this {@code Turtle2D} forward the given number of units
      in the heading direction. If the pen is down, then add two
      {@link Vertex} objects and a {@link LineSegment} object to
      the underlying {@code Turtle2D}.

      @param distance  the distance to walk this {@code Turtle2D} forward in the heading direction
   */
   public void forward(final double distance)
   {
      final double xOld = xPos;
      final double yOld = yPos;

      // change the current position
      xPos = xOld + (stepSize * distance * Math.sin(Math.toRadians(heading)));
      yPos = yOld + (stepSize * distance * Math.cos(Math.toRadians(heading)));

      if (penDown)
      {
         final int index = this.model.vertexList.size();

         final Vertex oldVertex = new Vertex(xOld, yOld, z);
         final Vertex newVertex = new Vertex(xPos, yPos, z);

         this.model.addVertex(oldVertex, newVertex);
         this.model.addPrimitive(new LineSegment(index, index+1));
      }
   }

   /**
    * record a {@code Vertex} in the {@code Turtle2D}'s current position.
    */
   public void recordVertex() {
       final int index = this.model.vertexList.size();
       final Vertex v = new Vertex(xPos, yPos, z);
       this.model.addVertex(v);
   }


   /**
      Same as the forward() method but without building a {@link LineSegment}.
      <p>
      This is part of "Turtle2D Geometry" as defined by Ronald Goldman.
      <p>
      https://www.clear.rice.edu/comp360/lectures/old/Turtle2DsGraphicL1New.pdf
      https://people.engr.tamu.edu/schaefer/research/Turtle2DsforCADRevised.pdf
      https://www.routledge.com/An-Integrated-Introduction-to-Computer-Graphics-and-Geometric-Modeling/Goldman/p/book/9781138381476

      @param distance  the distance to walk this {@code Turtle2D} forward in the heading direction
   */
   public void move(final double distance)
   {
      // change the current position
      xPos = xPos + (stepSize * distance * Math.sin(Math.toRadians(heading)));
      yPos = yPos + (stepSize * distance * Math.cos(Math.toRadians(heading)));
   }


   /**
      Change the length of the step size by the factor {@code s}.
      <p>
      This is part of "Turtle2D Geometry" as defined by Ronald Goldman.

      @param s  scaling factor for the new {@code stepSize}
   */
   public void resize(final double s)
   {
      stepSize = s * stepSize;
   }


   /**
      For debugging.

      @return {@link String} representation of this {@code Turtle2D} object
   */
   @Override
   public String toString()
   {
      String result = "";
      result += "Turtle2D: " + name + "\n";
      result += "z-plane: " + z + "\n";
      result += "origin: (" + xPos + ", " + yPos + ")\n";
      result += model.toString() + "\n";
      return result;
   }
}//Turtle2D
