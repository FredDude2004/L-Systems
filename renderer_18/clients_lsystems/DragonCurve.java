import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.LSystem2D;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

import java.awt.*;

public class DragonCurve {
    public static void main(String[] args) {
        final Scene scene = new Scene("Dragon Curve");
        scene.addPosition(new Position(new Model(), "p0"));
        scene.camera.viewTranslate(5, 0, 50);

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        Model dragonCurve = LSystem2D.dragonCurve(10);

        ModelShading.setColor(dragonCurve, Color.black);
        scene.getPosition(0).setModel(dragonCurve);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("DragonCurve.ppm");
    }
}
