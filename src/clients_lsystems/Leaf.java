import java.awt.Color;

import renderer.models_L.lsystems.LSystem2D;
import renderer.framebuffer.FrameBuffer;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

public class Leaf {
    public static void main(String[] args) {
        final Scene scene = new Scene("Polygon");
        scene.addPosition(new Position(new Model(), "p0"));
        scene.camera.viewTranslate(0, 0, 5);

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);
        /* ω : [A][B]
           p1 : A → [+A{.].C.}
           p2 : B → [-B{.].C.}
           p3 : C → GC */

        /*
         * {[++++G.][++GG.][+GGG.][GGGGG.][-GGG.][--GG.][----G.]}
         */

        String axiom = "{[++++G.][++GG.][+GGG.][GGGGG.][-GGG.][--GG.][----G.]}";
        LSystem2D leaf = new LSystem2D(axiom, 1.0, 30.0);
        leaf.addColor(Color.green);
        leaf.build();
        leaf.doBackFaceCulling = false;

        // System.out.println(leaf);

        scene.getPosition(0).setModel(leaf);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("Leaf.ppm");

    }
}
