import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game extends Application {
    private final int START_X = 50;
    private final int START_Z = 50;
    private HBox panel = new HBox();
    private Group group = new Group();
    private Camera camera = new PerspectiveCamera(true);
    private Cylinder weapon;
    private SubScene scene = new SubScene(group, 1600, 800, true, SceneAntialiasing.BALANCED);
    private PhongMaterial weaponMaterial = new PhongMaterial();
    private PhongMaterial projectileMaterial = new PhongMaterial();
    private BorderPane layout = new BorderPane();
    private Scene root = new Scene(layout, 1600, 800);
    private Text text;
    private Player player = new Player(this);
    private int countOfEnemies = 0;
    private boolean checkEnemyConflict = false;
    private boolean first = false;
    private AmbientLight ambientLight = new AmbientLight(Color.WHITE);

    public static void setTranslates(Node box, double x, double z, double y) {
        box.setTranslateX(x);
        box.setTranslateZ(z);
        box.setTranslateY(y);
    }

    private void setWeapon() {
        weaponMaterial.setDiffuseMap(new Image("textures/MP_diff_orange.png"));
        weapon = new Cylinder(0.025, 2);
        setTranslates(weapon, START_X, START_Z, 0.1);
        weapon.getTransforms().addAll(new Rotate(-90, 0, 0, 0, Rotate.X_AXIS), new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS));
        weapon.setMaterial(weaponMaterial);
        group.getChildren().add(weapon);
    }
    
    public void setPanel(int health, int ammo) {
        text.setText("Здоровье: " + health + "%" + "      Патроны: " + player.getAmmo());
    }

    public Group getGroup() { return group; }

    public Camera getCamera() { return camera; }

    public Cylinder getWeapon() { return weapon; }

    @Override
    public void start(Stage primaryStage) {
        preparePanel();

        layout.setCenter(scene);
        layout.setBottom(panel);
        scene.heightProperty().bind(layout.heightProperty().subtract(50));
        scene.widthProperty().bind(layout.widthProperty());

        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        setTranslates(camera, START_X, START_Z, 0);
        camera.setFarClip(700);
        setWeapon();

        Map map = new Map("src/level.txt", group, this);
        countOfEnemies = map.getEnemyCount();

        group.getChildren().add(ambientLight);
        setEvents();
        player.start();

        primaryStage.setTitle("Macaroni");
        primaryStage.setScene(root);
        primaryStage.show();

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), o -> { // Bullet speed
            update();
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
        root.setCursor(Cursor.DEFAULT);
    }

    private void preparePanel() {
        panel.setMinHeight(50);
        panel.setStyle("-fx-background-image: url(\"textures/m-004-min.jpg\");");
        panel.setAlignment(Pos.CENTER);
        panel.setSpacing(50);
        text = new Text();
        setPanel(player.getHealth(), player.getAmmo());
        text.setFill(Color.web("#fff"));
        text.setStyle("-fx-font: bold 24 arial;");
        panel.getChildren().add(text);
    }

    /**
     * function which sets events
     */
    private void setEvents() {
        final Set<KeyCode> pressed = new TreeSet<KeyCode>();

        root.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            pressed.add(code);

            if (pressed.size() > 1) {
                if (pressed.contains(KeyCode.W) && pressed.contains(KeyCode.S) || 
                    pressed.contains(KeyCode.A) && pressed.contains(KeyCode.D)) {
                    player.stopMovement();
                } else if (pressed.contains(KeyCode.W) && pressed.contains(KeyCode.A)) {
                    player.setDirection("WA");
                } else if (pressed.contains(KeyCode.W) && pressed.contains(KeyCode.D)) {
                    player.setDirection("WD");
                } else if (pressed.contains(KeyCode.A) && pressed.contains(KeyCode.S)) {
                    player.setDirection("SA");
                } else if (pressed.contains(KeyCode.D) && pressed.contains(KeyCode.S)) {
                    player.setDirection("SD");
                }
            } else {
                if (code == KeyCode.SPACE) {
                    checkEnemyConflict = true;
                    shoot();
                } else if (code == KeyCode.W) {
                    player.setDirection("W");
                } else if (code == KeyCode.A) {
                    player.setDirection("A");
                } else if (code == KeyCode.S) {
                    player.setDirection("S");
                } else if (code == KeyCode.D) {
                    player.setDirection("D");
                }
            }

        });

        root.setOnKeyReleased(event -> {
            pressed.remove(event.getCode());
            if (pressed.size() < 1) {
                player.stopMovement();
            }
        });
        camera.getTransforms().addAll(new Rotate(-90, Rotate.Y_AXIS), new Rotate(45, Rotate.X_AXIS), new Rotate(45, Rotate.Z_AXIS));

        root.setOnMouseMoved(event -> player.mouseMoved(event));
    }

    synchronized boolean checkCameraCollision() {
        CopyOnWriteArrayList<Node> c = new CopyOnWriteArrayList<Node>(group.getChildren());
        Box playerBound = new Box(20, 20, 20);
        playerBound.setTranslateX(camera.getTranslateX());
        playerBound.setTranslateY(camera.getTranslateY());
        playerBound.setTranslateZ(camera.getTranslateZ());
        for (Node n : c) {
            if (n instanceof Box && camera != n) {
                Box b = (Box) n;
                n.setOnScrollStarted(null);
                try {
                    Bounds camBounds = playerBound.getBoundsInParent(); 
                    Bounds nBounds = n.getBoundsInParent();
                    boolean collision  = nBounds.intersects(camBounds); 
                    if (collision) {
                        if (b instanceof Ammo) {
                            player.setAmmo(player.getAmmo() + ((Ammo) b).getSum());
                            Platform.runLater(() -> {
                                group.getChildren().remove(n);
                                setPanel(player.getHealth(), player.getAmmo());
                            });
                        } else if (b instanceof Food) {
                            if (player.getHealth() + ((Food) b).getSum() > 100) {
                                player.setHealth(100);
                            } else {
                                player.setHealth(player.getHealth() + ((Food) b).getSum());
                            }
                            Platform.runLater(() -> {
                                group.getChildren().remove(n);
                                setPanel(player.getHealth(), player.getAmmo());
                            });
                        } else if (b instanceof EnemyBox) {
                            player.setHealth(player.getHealth() - ((EnemyBox) b).getEnemy().getHealthFactor());

                            Platform.runLater(() -> {
                                setPanel(player.getHealth(), player.getAmmo());

                                if (player.getHealth() <= 0) {
                                    player.setAlive(false);
                                    text.setText("Беда!");
                                }
                            });
                        } else {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * function which provides shooting
     */ 
    private synchronized void shoot() {
        if(player.getAmmo() > 0 && player.getAlive()){
            Rotate rotate = (Rotate) weapon.getTransforms().get(0);
            Rotate rotate1 = (Rotate) weapon.getTransforms().get(1);

            double angle = rotate.getAngle();
            double angle1 = rotate1.getAngle();

            if(!first && angle == -90){
                //deault camera position is -90 deg ?!
                angle = 0;
                first = true;
            }

            projectileMaterial.setDiffuseMap(new Image("textures/flaretest.png"));
            Projectile projectile = new Projectile(projectileMaterial, camera, angle, angle1);

            group.getChildren().add(projectile);
            player.setAmmo(player.getAmmo() - 1);
            text.setText("Здоровье: "+ player.getHealth() + "%" + "      Патроны: " + player.getAmmo());
        }
    }

    /**
     * function for updating enemy and projectile positions
     */
    private synchronized void update() {
        CopyOnWriteArrayList<Node> c = new CopyOnWriteArrayList<Node>(group.getChildren());
        for (Node n : c) {
            if (n instanceof Projectile) {
                Sphere s = (Sphere) n;
                ((Projectile) n).setLenght(((Projectile) n).getLenght() + 10);
                ((Projectile) n).recompute();
                checkProjectileConflicts(s);
            } else if (n instanceof EnemyBox){
                if(player.getAlive()){
                    Coordinates coordinates = ((EnemyBox) n).getCoordinates();
                    double diffX = camera.getTranslateX() - coordinates.getCoordinateX();
                    double diffZ = camera.getTranslateZ() - coordinates.getCoordinateZ();
                    double angle = Math.atan2(diffZ, diffX);
                    coordinates.setCoordinateX(coordinates.getCoordinateX() + ((EnemyBox) n).getEnemy().getEnemySpeed() * Math.cos(angle));
                    coordinates.setCoordinateZ(coordinates.getCoordinateZ() + ((EnemyBox) n).getEnemy().getEnemySpeed() * Math.sin(angle));
                    ((EnemyBox) n).setAngle(360 - ((2*3.14 + angle)) * 360 / (2*3.14));
                    
                    ((EnemyBox) n).recompute();
                    if(diffX <= 5 || diffZ <= 5) {
                        checkCameraCollision();
                    }
                    checkEnemyEat((EnemyBox) n);
                }
            } else if (n instanceof Food) {
                Coordinates coordinates = ((Food) n).getCoordinates();
                double diffX = camera.getTranslateX() - coordinates.getCoordinateX();
                double diffZ = camera.getTranslateZ() - coordinates.getCoordinateZ();
                double angleFirstAid = Math.atan2(diffZ, diffX);
                ((Food) n).setAngle(360 - ((2*3.14 + angleFirstAid)) * 360 / (2*3.14));
            } else if (n instanceof Ammo) {
                Coordinates coordinates = ((Ammo) n).getCoordinates();
                double diffX = camera.getTranslateX() - coordinates.getCoordinateX();
                double diffZ = camera.getTranslateZ() - coordinates.getCoordinateZ();
                double angleAmmo = Math.atan2(diffZ, diffX);
                ((Ammo) n).setAngle(360 - ((2 * 3.14 + angleAmmo)) * 360 / (2 * 3.14));
            }
        }
    }

    private synchronized void checkEnemyEat(EnemyBox enemy) {
        Set<Node> toRemove = new HashSet<>();
        CopyOnWriteArrayList<Node> c = new CopyOnWriteArrayList<Node>(group.getChildren());
        for (Node n : c) {
            if (n instanceof Food) {
                if (n.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    enemy.getEnemy().addMacaroni(new Macaroni("spaghetti"));
                    enemy.changeProperties();
                    toRemove.add(n);
                }
            }
        }
        group.getChildren().removeAll(toRemove);
    }

    private synchronized void checkProjectileConflicts(Sphere projectile){
        Set<Node> toRemove = new HashSet<>();
        CopyOnWriteArrayList<Node> c = new CopyOnWriteArrayList<Node>(group.getChildren());
        for (Node n : c) {
            if (n instanceof Box) {
                Box b = (Box) n;
                if (b.getBoundsInParent().intersects(projectile.getBoundsInParent()) && b instanceof EnemyBox) {
                    if (checkEnemyConflict) {
                        ((EnemyBox) b).getEnemy().setHealth(((EnemyBox) b).getEnemy().getHealth() - 20);
                        checkEnemyConflict = false;
                    }
                    if (((EnemyBox) b).getEnemy().getHealth() <= 0) {
                        countOfEnemies--;
                        ((EnemyBox) b).getEnemy().setAlive(false);
                        toRemove.add(n);
                    }
                    Platform.runLater(() -> {
                        if (countOfEnemies == 0) {
                            player.setAlive(false);
                            text.setText("Победа!");
                        }
                    });
                } else if (b.getBoundsInParent().intersects(projectile.getBoundsInParent())) {
                    toRemove.add(projectile);
                }
            }
        }
        group.getChildren().removeAll(toRemove);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
