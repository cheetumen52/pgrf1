package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    public Polygon(ArrayList<Point> points) {
        this.points = points;
    }

    public Polygon() {
        points = new ArrayList<>();
    }


    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public void addPoints(Point pl) {
        points.add(pl);
    }

}
