package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    public Polygon() {
        points = new ArrayList<>();
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoints(Point pl) {
        points.add(pl);
    } // přidání bodů do polygonu

    public void addStartPoint(Point pl, int index) {
        points.add(index, pl);
    }
}
