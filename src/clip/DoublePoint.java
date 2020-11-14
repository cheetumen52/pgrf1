package clip;

public class DoublePoint {
    private double x, y;

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
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

