import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Wall extends Box {
  private final int WIDTH = 50;
  private final int HEIGHT = 50;
  private final int DEPTH = 50;
  private Coordinates coordinates;

  public Coordinates getCoordinates() {
    return coordinates;
  }

  Wall(Coordinates coords, PhongMaterial ammoMaterial) {
    super();
    this.coordinates = coords;
    // this.angle = -90;
    setWidth(WIDTH);
    setHeight(HEIGHT);
    setDepth(DEPTH);
    Game.setTranslates(this, coords.getCoordinateX(), coords.getCoordinateZ(), coords.getCoordinateY());
    setMaterial(ammoMaterial);
    // getTransforms().add(new Rotate(this.angle, Rotate.Y_AXIS));
  }
}