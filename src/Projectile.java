import javafx.scene.Camera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class Projectile extends Sphere {
    private int lenght = 10;
    private double angle;
    private double angle1;
    private Camera camera;

    public void setLenght(int lenght) { this.lenght = lenght; }

    public int getLenght() { return lenght; }

    Projectile(PhongMaterial material, Camera camera, double angle, double angle1){
        super();

        double dx = lenght * Math.sin(Math.toRadians(angle));
        double dy = lenght * Math.cos(Math.toRadians(angle));
        double dz = lenght * Math.sin(Math.toRadians(angle1));
        double x = camera.getTranslateX();
        double y = camera.getTranslateY();
        double z = camera.getTranslateZ();

        this.angle = angle;
        this.angle1 = angle1;
        this.camera = camera;
        this.setRadius(0.5);
        this.setMaterial(material);
        Game.setTranslates(this, x + dx, z + dy, y - dz);
    }

    public void recompute(){
        double dx = lenght * Math.sin(Math.toRadians(this.angle));
        double dy = lenght * Math.cos(Math.toRadians(this.angle));
        double dz = lenght * Math.sin(Math.toRadians(this.angle1));
        double x = this.camera.getTranslateX();
        double y = this.camera.getTranslateY();
        double z = this.camera.getTranslateZ();

        Game.setTranslates(this, x + dx, z + dy, y - dz);
    }
}
