package model;

public class Line {

    private final int x1, x2, y1, y2;
    private int color = 0xff0000;

    public Line(int x1, int y1, int x2, int y2, int color) {
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

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public int getColor() {
        return color;
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public Line setOrientation() {
        if (y1 > y2)
        return this;
        return new Line(new Point(x2, y2), new Point(x1, y1), color);
    }

    public boolean isIntersection(int y) {
        return (y <= y1) && (y > y2);
    }

    public int getIntersection(int y) {
        double k = ((double) (x2 - x1)) / ((y2 - y1));
        double q = x1 - k * y1;
        return (int) Math.round(k * y + q);
    }
}
