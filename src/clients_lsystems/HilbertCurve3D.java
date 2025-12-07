import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.LSystem3D;
import renderer.models_L.lsystems.Production;
import renderer.pipeline.Pipeline;
import renderer.scene.Camera;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

import java.awt.Color;

public class HilbertCurve3D
{
    public static void main(String[] args)
    {
        final double fov = 35.0;
        final double aspect = 1.0;
        final Camera camera = Camera.projPerspective(fov, aspect);
        final Scene scene = new Scene("HilbertCurve", camera);
        scene.camera.viewTranslate(3, 0, 30);
//        scene.camera.viewRotate(45, 0, 1, 0);

        scene.addPosition(new Position(new Model(), "p0"));

        final int width  = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

        LSystem3D lSystem = new LSystem3D("A", 1.0, 90.0);
        Production productionOne   = new Production('A', "B-F+CFC+F-D&F^D-F+&&CFC+F+B//");
        Production productionTwo   = new Production('B', "A&F^CFB^F^D^^-F-D^|F^B|FC^F^A//");
        Production productionThree = new Production('C', "|D^|F^B-F+C^F^A&&FA&F^C+F+B^F^D//");
        Production productionFour  = new Production('D', "|CFB-F+B|FA&F^A&&FB-F+B|FC//");
        lSystem.addProduction(productionOne, productionTwo, productionThree, productionFour);

        lSystem.expand(3);
        Model lSystemModel = lSystem.build();

        ModelShading.setColor(lSystemModel, Color.white);
        scene.getPosition(0).setModel(lSystemModel);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("HilbertCurve3D.ppm");
    }
}
