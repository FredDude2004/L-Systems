import renderer.models_L.lsystems.LSystem2D;
import renderer.models_L.lsystems.Polygon;
import renderer.scene.primitives.Triangle;
import renderer.scene.util.ModelShading;
import renderer.scene.Vertex;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.pipeline.Pipeline;
import renderer.framebuffer.FrameBuffer;

import java.awt.Color;

public class PolygonTest {
    public static void main(String[] args) {
        final Scene scene = new Scene("Polygon");
        scene.addPosition(new Position(new Model(), "p0"));
        scene.camera.viewTranslate(0, 0, 5);

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);
        //
        // final Model manualSquare = new Model("manual square");
        // manualSquare.addVertex(new Vertex(0.0, 0.0, 0.0),
        //         new Vertex(0.0, 1.0, 0.0),
        //         new Vertex(1.0, 1.0, 0.0),
        //         new Vertex(1.0, 0.0, 0.0));
        //
        // manualSquare.addColor(Color.black);
        //
        // manualSquare.addPrimitive(new Triangle(0, 1, 2, 0));
        // manualSquare.addPrimitive(new Triangle(0, 2, 3, 0));
        //
        // manualSquare.doBackFaceCulling = false;
        // System.out.println(manualSquare);
        //
        //
        // scene.getPosition(0).setModel(manualSquare);
        // fb.clearFB();
        // Pipeline.render(scene, fb);
        // fb.dumpFB2File("manualSquare.ppm");

        Model square = new Polygon(".f+.f+.f+.f", 90.0);
        square.doBackFaceCulling = false;
        // System.out.println(square);

        // scene.debug = true;
        scene.getPosition(0).setModel(square);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Square.ppm");
    }
}

