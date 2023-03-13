import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class Food extends Box {
    private int sum = 40;
    private final int WIDTH = 7;
    private final int HEIGHT = 7;
    private final int DEPTH = 7;
    private Coordinates coordinates;
    private double angle;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    Food(Coordinates coords,  PhongMaterial firstAidMaterial){
        super();
        this.coordinates = coords;
        this.angle = -90;
        setWidth(WIDTH);
        setHeight(HEIGHT);
        setDepth(DEPTH);
        Game.setTranslates(this, coords.getCoordinateX(), coords.getCoordinateZ(), coords.getCoordinateY());
        setMaterial(firstAidMaterial);
        getTransforms().add(new Rotate(this.angle, Rotate.Y_AXIS));
    }

    public int getSum() {
        return sum;
    }
    
    public void setAngle(double angle) {
        this.angle = angle;
        Rotate rotate = (Rotate) getTransforms().get(0);
        getTransforms().clear();
        rotate.setAngle(angle);
        getTransforms().add(rotate);
    }
}