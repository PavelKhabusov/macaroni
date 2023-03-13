public class Coordinates {
  private double coordinateX;
  private double coordinateY;
  private double coordinateZ;

  public Coordinates() {
    this.coordinateX = 0;
    this.coordinateY = -1;
    this.coordinateZ = 0;
  }
  
  public Coordinates(double X, double Z) {
    this.coordinateX = X;
    this.coordinateY = -1;
    this.coordinateZ = Z;
  }

  public double getCoordinateX() {
    return coordinateX;
  }

  public double getCoordinateY() {
    return coordinateY;
  }

  public double getCoordinateZ() {
    return coordinateZ;
  }

  public void setCoordinateX(double coordinateX) {
    this.coordinateX = coordinateX;
  }

  public void setCoordinateY(double coordinateY) {
    this.coordinateY = coordinateY;
  }

  public void setCoordinateZ(double coordinateZ) {
    this.coordinateZ = coordinateZ;
  }
}
