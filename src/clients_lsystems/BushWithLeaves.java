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

        // n=7, δ=22.5◦
        // ω : A
        // p1 : A → [&FL!A]/////’[&FL!A]///////’[&FL!A]
        // p2 : F → S ///// F
        // p3 : S → F L
        // p4 : L → [’’’∧∧{-f+f+f-|-f+f+f}]

        Polygon square = new Polygon(".f+.f+.f+.f", 90.0);
        square.doBackFaceCulling = false;

        scene.getPosition(0).setModel(square);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Square.ppm");
    }
}

