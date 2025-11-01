import renderer.scene.*;
import renderer.scene.util.ModelShading;
import renderer.models_L.turtlegraphics.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import java.io.IOException;

public class Turtle3DTestTwo {
    public static void main(String[] args) {
        final Scene scene = new Scene("3DTurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        final Model turtleModel = new Model("turtleModel");
        final Turtle3D turtle = new Turtle3D(turtleModel, "turtleModel", 0.0, 0.0, -3.0);

        // Draw a tetrahedron

        // bottom triangle
        turtle.forward();
        turtle.yaw(-45);
        turtle.forward();
        turtle.yaw(-45);
        turtle.forward();
        turtle.yaw(-90);

        // draw the line along the y-axis from (0, 0, 0) => (0, 1, 0)
        turtle.pitch(90);
        turtle.forward();
        turtle.penUp();
        turtle.backward();
        turtle.pitch(-90);
        turtle.penDown();

        turtle.forward();

        turtle.yaw(180);
        turtle.pitch(45);
        turtle.forward();
        turtle.yaw(90);
        turtle.pitch(-90);

        turtle.forward();

        // for debugging
        System.out.println(scene);
        System.out.println("\n\n");
        System.out.println(turtleModel);

        ModelShading.setColor(turtleModel, Color.black);
        scene.getPosition(0).setModel(turtleModel);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Turtle3D.ppm");


        System.out.println("\n\n");
        System.out.println(scene);
    }
}
