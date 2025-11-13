import renderer.scene.*;
import renderer.framebuffer.*;
import renderer.pipeline.*;
import renderer.models_L.lsystems.*;
import renderer.scene.util.ModelShading;

import java.awt.Color;
import java.util.Random;


public class RandomLSystemDemo {
    public static void main(String[] args) {
        final Scene scene = new Scene("TurtleGraphics" );
        scene.addPosition(new Position(new Model(), "p0"));
        final FrameBuffer fb = new FrameBuffer(1024, 1024, Color.black);

        Random rand = new Random();

        // Randomly pick 1, 2, or 3
        int choice = rand.nextInt(3) + 1;

        StochasticLSystem lSystem;
        switch (choice) {
            case 1:
                // 1 Square-curve
                lSystem = new StochasticLSystem("F-F-F-F", 1.0, 90.0);
                lSystem.addProduction(
                    new StochasticProduction('F', "F-F+F+FF-F-F+F", 0.6),
                    new StochasticProduction('F', "F-F+FF+F-F-F+F", 0.2),
                    new StochasticProduction('F', "F-F+F+F-F-FF+F", 0.2)
                );
                lSystem.expand(2);
                break;

            case 2:
                // 2 Branching plant
                lSystem = new StochasticLSystem("F", 0.5, 22.5);
                lSystem.addProduction(
                    new StochasticProduction('F', "FF+[+F-F-F]-[-F+F+F]", 0.45),
                    new StochasticProduction('F', "F[+F]F[-F]F", 0.30),
                    new StochasticProduction('F', "FF[+F-F]F[-F+F]", 0.25)
                );
                lSystem.expand(5);
                break;

            default:
                // 3 Koch snowflake
                lSystem = new StochasticLSystem("F--F--F", 1.0, 60.0);
                lSystem.addProduction(
                    new StochasticProduction('F', "F+F--F+F", 0.55),
                    new StochasticProduction('F', "F+F--F++F", 0.20),
                    new StochasticProduction('F', "F++F--F+F", 0.25)
                );
                lSystem.expand(3);
                break;
        }

        // Draw the selected L-system
        Model model = lSystem.draw();
        ModelShading.setColor(model, Color.white);
        scene.getPosition(0).setModel(model);

        fb.clearFB();
        Pipeline.render(scene, fb);

        // Optional: include which one was drawn in filename
        fb.dumpFB2File("Random_LSystem_Demo" + "_Case" + choice + ".ppm");

        System.out.println("TurtleGraphics #" + choice);
    }
}