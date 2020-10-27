package clip;

import model.Point;

class Line {

    private final double x1, x2, y1, y2;
    private final int color;

    public Line(double x1, double y1, double x2, double y2, int color) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.color = color;
    }

    public Line(Point p1, Point p2, int color) {
        this.x1 = p1.getX();
        this.x2 = p2.getX();
        this.y1 = p1.getY();
        this.y2 = p2.getY();
        this.color = color;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public double getColor() {
        return color;
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public Line setOrientation() {
        if (y2 > y1) {

        }
        return this;
    }

    public boolean isIntersection(int y) {
        //TODO I GUESS
        return y == y1 || y == y2;
    }

    public double getIntersection(int y) {
        if (y == y1) {
            return x1;
        } else {
            return x2;
        }

    }
}
