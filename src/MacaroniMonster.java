import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import java.util.ArrayList;
import java.util.List;

public class MacaroniMonster extends Thread {
    private double enemySpeed = 1.001;
    private boolean alive = true;
    private int health = 40;
    private int healthFactor = 5;
    private PhongMaterial enemyMaterial = new PhongMaterial();
    private List<Tefteli> tefteliList;
    private List<Macaroni> macaroniList;
    private enum MType {SUN, PIG}
    private MType monsterType;
    private ArrayList<String> textures = new ArrayList<String>();

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getEnemySpeed() {
        return enemySpeed;
    }

    public int getHealth() {
        return health;
    }

    public int getHealthFactor() {
        return healthFactor;
    }
    
    public String getType() {
        return switch (monsterType) {
            case SUN -> "SUN";
            case PIG -> "PIG";
        };
    }

    public ArrayList<String> getTextures() {
        return textures;
    }

    public void addTefteli(Tefteli tefteli) {
        tefteliList.add(tefteli);
    }

    public List<Tefteli> getTefteliList() {
        return tefteliList;
    }

    public List<Macaroni> getMacaroniList() {
        return macaroniList;
    }

    public void addMacaroni(Macaroni macaroni) {
        macaroniList.add(macaroni);
        this.monsterType = changeType(getTefteliList(), getMacaroniList());
    }

    public MType changeType(List<Tefteli> tefteli, List<Macaroni> macaroni) {
        if(containsMacaroni(macaroni, "spaghetti")) {
            textures.set(0, "textures/enemy_macaroni.png");
            textures.set(1, "textures/enemy_macaroni2.png");
            this.health = 80;
            return MType.PIG;
        } else if(containsTefteli(tefteli, "chick")) {
            textures.set(0, "textures/enemy.png");
            textures.set(1, "textures/enemy2.png");
            return MType.SUN;
        }
        return null;
    }
    
    public boolean containsMacaroni(final List<Macaroni> list, final String name) {
        return list.stream().filter(o -> o.getType().equals(name)).findFirst().isPresent();
    }
    
    public boolean containsTefteli(final List<Tefteli> list, final String name) {
        return list.stream().filter(o -> o.getType().equals(name)).findFirst().isPresent();
    }

    public MacaroniMonster(Game game, Coordinates coords, Tefteli tefteli, Macaroni macaroni) {
        this.tefteliList = new ArrayList<>();
        this.macaroniList = new ArrayList<>();
        this.tefteliList.add(tefteli);
        this.macaroniList.add(macaroni);
        textures.add(0, "textures/enemy.png");
        textures.add(1, "textures/enemy2.png");

        this.monsterType = changeType(tefteliList, macaroniList);
        
        enemyMaterial.setDiffuseMap(new Image(this.textures.get(0)));
        EnemyBox enemy = new EnemyBox(coords, enemyMaterial, this);
        game.getGroup().getChildren().add(enemy);
    }

    @Override
    public void run() {
        while (alive) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}