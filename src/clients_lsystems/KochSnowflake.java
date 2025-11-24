import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.LSystem2D;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

import java.awt.Color;

public class KochSnowflake {
    public static void main(String[] args) {
        final Scene scene = new Scene("Koch Snowflake");
        scene.addPosition(new Position(new Model(), "p0"));
        scene.camera.viewTranslate(5, 0, 50);

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        Model kochSnowflake = LSystem2D.kochSnowflake(5);

        ModelShading.setColor(kochSnowflake, Color.black);
        scene.getPosition(0).setModel(kochSnowflake);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochSnowflake.ppm");
    }
}
