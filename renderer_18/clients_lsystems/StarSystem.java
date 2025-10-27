import renderer.scene.*;
import renderer.framebuffer.*;
import renderer.pipeline.*;
import renderer.models_L.lsystems.*;
import renderer.models_L.lsystems.Production;
import renderer.scene.util.ModelShading;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class StarSystem
{
    public static void main(String[] args) 
    {
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));

        final int width  = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.black);

        LSystem lSystem = new LSystem("F--F--F", 1.0, 60.0);
        Production productionOne = new Production('F', "F+F--F+F");
        lSystem.addProduction(productionOne);

        lSystem.expand(3);
        Model lSystemModel = lSystem.draw();

        ModelShading.setColor(lSystemModel, Color.white);
        scene.getPosition(0).setModel(lSystemModel);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("StarSystem.ppm");
    }
}
