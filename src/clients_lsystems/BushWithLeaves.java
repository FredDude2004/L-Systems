import renderer.models_L.lsystems.LSystem3D;
import renderer.models_L.lsystems.Polygon;
import renderer.models_L.lsystems.Production;
import renderer.scene.primitives.Triangle;
import renderer.scene.util.ModelShading;
import renderer.scene.Vertex;
import renderer.scene.Camera;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.pipeline.Pipeline;
import renderer.framebuffer.FrameBuffer;

import java.awt.Color;
import java.awt.Rectangle;

public class BushWithLeaves {
    public static void main(String[] args) {
        final Scene scene = new Scene("Polygon");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        // n=7, δ=22.5◦
        // ω : A
        // p1 : A → [&FL!A]/////’[&FL!A]///////’[&FL!A]
        // p2 : F → S ///// F
        // p3 : S → F L
        // p4 : L → [’’’∧∧{-f+f+f-|-f+f+f}]
        LSystem3D bushWithLeaves = new LSystem3D("A", 1.0, 22.5);
        bushWithLeaves.addColor(new Color(0, 113, 0),
                                new Color(0, 100, 0),
                                new Color(0, 130, 0));
        Production p1 = new Production('A', "[&FL!A]/////’[&FL!A]///////’[&FL!A]");
        Production p2 = new Production('F', "S ///// F");
        Production p3 = new Production('S', "F L");
        Production p4 = new Production('L', "[’’’∧∧{-f+f+f-|-f+f+f}]");
        bushWithLeaves.addProduction(p1, p2, p3, p4);
        bushWithLeaves.expand(3);
        bushWithLeaves.build();

        System.out.println(bushWithLeaves);

        double maxX, maxY, maxZ;
        maxX = bushWithLeaves.maxX;
        maxY = bushWithLeaves.maxY;
        maxZ = bushWithLeaves.maxZ;
        double m = Math.max(maxX, maxY);
        double max = Math.max(maxZ, m);

        double left   = -1.73205;
        double right  =  1.73205;
        double bottom = -1.73205;
        double top    =  1.73205;

        scene.changeCamera(Camera.projPerspective(left, right, bottom, top));
        scene.camera.viewTranslate(0, 0, max);

        scene.getPosition(0).setModel(bushWithLeaves);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("BushWithLeaves.ppm");
    }
}

