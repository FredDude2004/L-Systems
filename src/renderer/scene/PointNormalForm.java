import renderer.scene.Vector;
import renderer.scene.Vertex;

@FunctionalInterface
public interface PointNormalForm {
    boolean test(Vertex v);
}
