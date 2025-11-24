import renderer.framebuffer.FrameBuffer;
import renderer.models_L.turtlegraphics.Turtle3D;
import renderer.pipeline.Pipeline;
import renderer.scene.Camera;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

import java.awt.Color;

public class Tetrahedron3D {
    public static void main(String[] args) {

        final double fov = 35.0;
        final double aspect = 1.0;
        final Camera camera = Camera.projPerspective(fov, aspect);
        final Scene scene = new Scene("3DTurtleGraphics", camera);
        scene.camera.viewTranslate(0, 0, 4);

        scene.addPosition(new Position(new Model(), "p0"));

        final int width = 200;
        final int height = 200;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        final Model turtleModel = new Model("turtleModel");
        final Turtle3D turtle = new Turtle3D(turtleModel, "turtleModel", -1.0, 0.0, 0.0);

        turtle.pitch(90);

        // Draw a tetrahedron
        turtle.forward(1);
        turtle.yaw(-90);
        turtle.forward(1);
        turtle.yaw(-135);
        turtle.forward(1.4142);

        turtle.yaw(-135);
        turtle.pitch(-45);
        turtle.forward(1.4142);
        turtle.pitch(-45);
        turtle.forward(-1);
        turtle.penUp();
        turtle.forward(1);
        turtle.penDown();

        turtle.yaw(-135);
        turtle.forward(1.4142);

        // for debugging
        System.out.println(scene);
        System.out.println("\n\n");
        System.out.println(turtleModel);

        ModelShading.setColor(turtleModel, Color.black);
        scene.getPosition(0).setModel(turtleModel);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Tetrahedron3D.ppm");

        System.out.println("\n\n");
        System.out.println(scene);
    }
}
