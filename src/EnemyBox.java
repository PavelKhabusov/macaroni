import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class EnemyBox extends Box {
    private MacaroniMonster enemy;
    private int WIDTH = 15;
    private final int HEIGHT = 15;
    private int DEPTH = 15;
    private PhongMaterial enemyMaterial = new PhongMaterial();
    private Coordinates coordinates;
    private boolean texture = true;
    private double angle;

    public MacaroniMonster getEnemy() { return enemy; }

    public Coordinates getCoordinates() { return coordinates; }

    public String getType() { return enemy.getType(); }

    public void setTexture(String path) { this.enemyMaterial.setDiffuseMap(new Image(path)); }

    public void setAngle(double angle) {
        this.angle = angle;
        Rotate rotate = (Rotate) getTransforms().get(0);
        getTransforms().clear();
        rotate.setAngle(angle);
        getTransforms().add(rotate);
    }

    public void changeProperties() {
        switch (enemy.getType()) {
            case "SUN":
                WIDTH = 15;
                DEPTH = 15;
                break;
            case "PIG":
                WIDTH = 7;
                DEPTH = 7;
                break;
            default:
                break;
        }
        setWidth(WIDTH);
        setHeight(HEIGHT);
        setDepth(DEPTH);
    }

    EnemyBox(
        Coordinates coords, 
        PhongMaterial enemyMaterial, 
        MacaroniMonster enemy
    ){
        super();
        this.coordinates = coords;
        this.enemy = enemy;
        this.enemyMaterial = enemyMaterial;
        this.angle = -90;
        changeProperties();
        Game.setTranslates(this, coordinates.getCoordinateX(), coordinates.getCoordinateZ(), coordinates.getCoordinateY());
        setMaterial(this.enemyMaterial);
        
        getTransforms().add(new Rotate(this.angle, Rotate.Y_AXIS));

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(250), o -> { // Texture speed
            update();
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void update() {
        if (texture) {
            setTexture(enemy.getTextures().get(1));
            texture = false;
        } else {
            setTexture(enemy.getTextures().get(0));
            texture = true;
        }
    }

    public void recompute(){
        Game.setTranslates(this, this.coordinates.getCoordinateX(), this.coordinates.getCoordinateZ(), this.coordinates.getCoordinateY());
    }
}