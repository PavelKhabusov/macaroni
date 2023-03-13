import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;

public class Map {
  private String file;
  private Group group;
  private Game game;
  private char[][] map = new char[12][12];
  private int countOfEnemies = 0;
  private PhongMaterial boxMaterial = new PhongMaterial();
  private PhongMaterial floorMaterial = new PhongMaterial();
  private PhongMaterial roofMaterial = new PhongMaterial();
  private PhongMaterial firstAidMaterial = new PhongMaterial();
  private PhongMaterial ammoMaterial = new PhongMaterial();
  private PhongMaterial columnMaterial = new PhongMaterial();

  public int getEnemyCount() { return countOfEnemies; }
  
  Map(String file, Group group, Game game){
    super();
    this.file = file;
    this.group = group;
    this.game = game;

    readMapFromFile();
    loadMap();
    createFloorAndRoof();
  }

  private void readMapFromFile() {
    try {
      Scanner input = new Scanner(new File(file));
      int row = 0;
      int column = 0;

      while (input.hasNext()) {
        String c = input.next();
        if (!c.equals(" ") && !c.equals(",")) {
          map[row][column] = c.charAt(0);
          column++;
        } else {
          column = 0;
          row++;
        }
      }
      input.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
  }

  private void loadMap() {
    int Z = 0;
    for (int i = map.length - 1; i >= 0; i--) {
      int X = 0;
      for (int j = 0; j < map[0].length; j++) {
        if (map[i][j] == '#') {
          createWall(new Coordinates(X, Z));
        } else if (map[i][j] == 'h') {
          createFirstAid(new Coordinates(X, Z));
        } else if (map[i][j] == 'a') {
          createAmmo(new Coordinates(X, Z));
        } else if (map[i][j] == 'T') {
          createColumn(new Coordinates(X, Z));
        } else if (map[i][j] == 's') {
          new MacaroniMonster(
              game,
              new Coordinates(X, Z),
              new Tefteli("chick"),
              new Macaroni(""));
          countOfEnemies++;
        } else if (map[i][j] == 'p') {
          new MacaroniMonster(
              game,
              new Coordinates(X, Z),
              new Tefteli("chick"),
              new Macaroni("spaghetti"));
          countOfEnemies++;
        }
        X += 50;
      }
      Z += 50;
    }
  }

  private void createWall(Coordinates coords) {
    Wall box = new Wall(coords, boxMaterial);
    group.getChildren().add(box);
  }

  private void createFirstAid(Coordinates coords) {
    Food firstAid = new Food(coords, firstAidMaterial);
    group.getChildren().add(firstAid);
  }

  private void createAmmo(Coordinates coords) {
    Ammo ammo = new Ammo(coords, ammoMaterial);
    group.getChildren().add(ammo);
  }

  private void createColumn(Coordinates coords) {
    Cylinder cylinder = new Cylinder(5, 50);
    cylinder.setTranslateX(coords.getCoordinateX());
    cylinder.setTranslateZ(coords.getCoordinateZ());
    cylinder.setMaterial(columnMaterial);
    group.getChildren().add(cylinder);
  }  

  private void createFloorAndRoof() {
    boxMaterial.setDiffuseMap(new Image("textures/m-010-min.jpg"));
    floorMaterial.setDiffuseMap(new Image("textures/m-001-min.jpg"));
    roofMaterial.setDiffuseMap(new Image("textures/m-003-min.jpg"));
    firstAidMaterial.setDiffuseMap(new Image("textures/firstaid.png"));
    firstAidMaterial.setSpecularColor(Color.RED);
    ammoMaterial.setDiffuseMap(new Image("textures/bullet.png"));
    columnMaterial.setDiffuseMap(new Image("textures/m-009-min.jpg"));

    int floorX = 0;
    int floorZ = 0;

    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[0].length; j++) {
        Box boxFlr = new Box(50, 1, 50);
        Box boxRf = new Box(50, 1, 50);
        Game.setTranslates(boxFlr, floorX, floorZ, 15);
        Game.setTranslates(boxRf, floorX, floorZ, -25);
        floorX += 50;
        boxFlr.setMaterial(floorMaterial);
        boxRf.setMaterial(roofMaterial);
        group.getChildren().add(boxFlr);
        group.getChildren().add(boxRf);
      }
      floorX = 0;
      floorZ += 50;
    }
  }
}
