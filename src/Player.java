import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;

public class Player extends Thread {
    private final float MOUSE_SENSITIVITY = 0.15f;
    private enum Dir {FORWARD, LEFT, BACKWARD, RIGHT, LEFT_FORWARD, RIGHT_FORWARD, LEFT_BACKWARD, RIGHT_BACKWARD, STOP}
    private Dir direction = Dir.STOP;
    private Game game;
    private boolean alive = true;
    private double angle = 0;
    private double lastMouseX;
    private double lastMouseY;
    private int health = 100;
    private int ammo = 30;

    public void setAlive(boolean alive) { this.alive = alive; } 

    public void setHealth(int health) { this.health = health; }

    public void setAmmo(int ammo) { this.ammo = ammo; }

    public void setLastMouseX(double lastMouseX) { this.lastMouseX = lastMouseX; }

    public void setLastMouseY(double lastMouseY) { this.lastMouseY = lastMouseY; }

    void setDirection(String event) {
        switch (event) {
            case "W" -> direction = Dir.FORWARD;
            case "A" -> direction = Dir.LEFT;
            case "S" -> direction = Dir.BACKWARD;
            case "D" -> direction = Dir.RIGHT;
            case "WA" -> direction = Dir.LEFT_FORWARD;
            case "WD" -> direction = Dir.RIGHT_FORWARD;
            case "SA" -> direction = Dir.LEFT_BACKWARD;
            case "SD" -> direction = Dir.RIGHT_BACKWARD;
            case "STOP" -> direction = Dir.STOP;
            default -> throw new IllegalStateException("Invalid event: " + event);
        }
    }
    
    public boolean getAlive(){ return alive; }

    public int getHealth() { return health; }

    public int getAmmo() { return ammo; }

    Player(Game game) { this.game = game; }

    @Override
    public void run() {
        while (alive) {
            try {
                movement();
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void mouseMoved(MouseEvent event) {
        Rotate rotateH = (Rotate) game.getCamera().getTransforms().get(0);
        Rotate rotateV = (Rotate) game.getCamera().getTransforms().get(1);
        Rotate rotateShake = new Rotate();

        game.getCamera().getTransforms().clear();
        game.getWeapon().getTransforms().clear();

        rotateH.setAngle(rotateH.getAngle() + (event.getSceneX() - lastMouseX) * MOUSE_SENSITIVITY);
        rotateV.setAngle(rotateV.getAngle() - (event.getSceneY() - lastMouseY) * MOUSE_SENSITIVITY);
        rotateShake.setAngle(rotateShake.getAngle() - (event.getSceneY() - lastMouseY + event.getSceneX() - lastMouseX) * MOUSE_SENSITIVITY); //Shaking

        angle = rotateH.getAngle();
        game.getCamera().getTransforms().add(rotateH);
        game.getCamera().getTransforms().add(rotateV);
        game.getCamera().getTransforms().add(rotateShake);
        game.getWeapon().getTransforms().add(rotateH);
        game.getWeapon().getTransforms().add(rotateV);
        game.getWeapon().getTransforms().add(rotateShake);
        game.getWeapon().getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.X_AXIS));

        setLastMouseX(event.getSceneX());
        setLastMouseY(event.getSceneY());
    }

    void stopMovement() { direction = Dir.STOP; }

    private void movement() {
        if (direction == Dir.STOP) return;
        double dx = Math.sin(Math.toRadians(angle));
        double dz = Math.cos(Math.toRadians(angle));
        double dxl = Math.sin(Math.toRadians(angle + 90));
        double dzl = Math.cos(Math.toRadians(angle + 90));
        double dxd = Math.sin(Math.toRadians(angle + 45));
        double dzd = Math.cos(Math.toRadians(angle + 45));
        double oldZ = game.getCamera().getTranslateZ();
        double oldX = game.getCamera().getTranslateX();
        
        switch (direction) {
            case FORWARD        -> move(new Coordinates(oldX + dx, oldZ + dz),
                                        new Coordinates(oldX + dx, oldZ + dz));

            case LEFT           -> move(new Coordinates(oldX - dxl, oldZ - dzl),
                                        new Coordinates(oldX - dxl, oldZ - dzl));

            case BACKWARD       -> move(new Coordinates(oldX - dx, oldZ - dz),
                                        new Coordinates(oldX - dx, oldZ - dz));

            case RIGHT          -> move(new Coordinates(oldX + dxl, oldZ + dzl),
                                        new Coordinates(oldX + dxl, oldZ + dzl));

            case LEFT_FORWARD   -> move(new Coordinates(oldX - dxd, oldZ + dzd),
                                        new Coordinates(oldX - dxd, oldZ + dzd));

            case RIGHT_FORWARD  -> move(new Coordinates(oldX + dxd, oldZ + dzd),
                                        new Coordinates(oldX + dxd, oldZ + dzd));

            case LEFT_BACKWARD  -> move(new Coordinates(oldX - dxd, oldZ - dzd),
                                        new Coordinates(oldX - dxd, oldZ - dzd));

            case RIGHT_BACKWARD -> move(new Coordinates(oldX + dxd, oldZ - dzd),
                                        new Coordinates(oldX + dxd, oldZ - dzd));
                            
            default -> throw new IllegalStateException("Invalid direction: " + direction);
        }
        
        if (game.checkCameraCollision()) {
            move(new Coordinates(oldX, oldZ), new Coordinates(oldX, oldZ));
        }
    }
    private void move(Coordinates cameraCoords, Coordinates cylinderCoords) {
        game.getCamera().translateXProperty().set(cameraCoords.getCoordinateX());
        game.getCamera().translateZProperty().set(cameraCoords.getCoordinateZ());
        game.getWeapon().translateXProperty().set(cylinderCoords.getCoordinateX());
        game.getWeapon().translateZProperty().set(cylinderCoords.getCoordinateZ());
    }
}
