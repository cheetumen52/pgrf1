package model;
public class Point {

    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getDistance(int xs, int ys) { // výpočet vzdálenosti mezi souřadnicemi bodů - input = souřadnice bodu k výpočtu
        double distance;
        distance = Math.sqrt(Math.pow((xs - x), 2) + Math.pow((ys - y), 2));
        return distance;
    }

    @Override
    public String toString() { // Vypíše info o bodu
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
