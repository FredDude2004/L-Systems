/*
 * L-Systems will start with an axiom and is rewritten with a set of rules
 *
 * This is a simple example of a DOL-System which are the simplest class of
 * L-Systems, which are deterministic and context free.
 *
 * The System in this program has these properties
 * axiom: F-F-F-F
 * p: F -> F-F+F+FF-F-F+F
 *
 * Where: F represents a line with a length of l
 *        + represents a turn in the positive direction by the angle delta
 *        - represent a turn in the negative direction by the angle delta
*/


import renderer.framebuffer.FrameBuffer;
import renderer.models_L.lsystems.LSystem2D;
import renderer.models_L.lsystems.Production;
import renderer.pipeline.Pipeline;
import renderer.scene.Model;
import renderer.scene.Position;
import renderer.scene.Scene;
import renderer.scene.util.ModelShading;

import java.awt.Color;

public class KochCurves {
    public static void main(String[] args) {
        final Scene scene = new Scene("TurtleGraphics");
        scene.addPosition(new Position(new Model(), "p0"));
        scene.camera.viewTranslate(5, 0, 50);

        final int width = 1024;
        final int height = 1024;
        final FrameBuffer fb = new FrameBuffer(width, height, Color.white);

        Model kochCurveA = LSystem2D.kochCurveA(3);
        Model kochCurveB = LSystem2D.kochCurveB(2);
        Model kochCurveC = LSystem2D.kochCurveC(4);
        Model kochCurveD = LSystem2D.kochCurveD(2);
        Model kochCurveE = LSystem2D.kochCurveE(4);
        Model kochCurveF = LSystem2D.kochCurveF(4);
        Model kochCurveG = LSystem2D.kochCurveG(3);
        Model kochCurveH = LSystem2D.kochCurveH(4);
        Model kochCurveI = LSystem2D.kochCurveI(5);
        Model kochCurveJ = LSystem2D.kochCurveJ(4);


        ModelShading.setColor(kochCurveA, Color.black);
        scene.getPosition(0).setModel(kochCurveA);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveA.ppm");

        ModelShading.setColor(kochCurveB, Color.black);
        scene.getPosition(0).setModel(kochCurveB);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveB.ppm");

        ModelShading.setColor(kochCurveC, Color.black);
        scene.getPosition(0).setModel(kochCurveC);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveC.ppm");

        ModelShading.setColor(kochCurveD, Color.black);
        scene.getPosition(0).setModel(kochCurveD);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveD.ppm");

        ModelShading.setColor(kochCurveE, Color.black);
        scene.getPosition(0).setModel(kochCurveE);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveE.ppm");

        ModelShading.setColor(kochCurveF, Color.black);
        scene.getPosition(0).setModel(kochCurveF);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveF.ppm");

        ModelShading.setColor(kochCurveG, Color.black);
        scene.getPosition(0).setModel(kochCurveG);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveG.ppm");

        ModelShading.setColor(kochCurveH, Color.black);
        scene.getPosition(0).setModel(kochCurveH);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveH.ppm");

        ModelShading.setColor(kochCurveI, Color.black);
        scene.getPosition(0).setModel(kochCurveI);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveI.ppm");

        ModelShading.setColor(kochCurveJ, Color.black);
        scene.getPosition(0).setModel(kochCurveJ);
        fb.clearFB();
        Pipeline.render(scene, fb);
        fb.dumpFB2File("KochCurveJ.ppm");
    }
}
