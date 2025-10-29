import renderer.scene.*;
import renderer.scene.util.ModelShading;
import renderer.models_L.turtlegraphics.*;
import renderer.pipeline.*;
import renderer.framebuffer.*;

import java.awt.Color;
import java.io.IOException;

public class Turtle3DTest {
    public static void main(String[] args) {
        final Scene scene = new Scene("3DTurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        final Model turtleModel = new Model("turtleModel");
        final Turtle3D turtle = new Turtle3D(turtleModel, "turtleModel", -1.5, -0.5, -3.0);

        turtle.forward();
        turtle.yaw(90.0);
        turtle.forward();
        turtle.yaw(90.0);
        turtle.forward();
        turtle.yaw(90.0);
        turtle.forward();
        turtle.yaw(90.0);

        turtle.pitch(-90.0);
        turtle.forward();
        turtle.yaw(90.0);
        turtle.forward();
        turtle.yaw(-90.0);
        turtle.backward();
        turtle.forward();

        turtle.pitch(90.0);
        turtle.forward();
        turtle.pitch(-90.0);
        turtle.backward();
        turtle.forward();

        turtle.yaw(-90.0);
        turtle.forward();
        turtle.yaw(90.0);
        turtle.backward();
        turtle.forward();
        turtle.pitch(-90.0);
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
