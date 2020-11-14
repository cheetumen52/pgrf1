package model;

import clip.DoublePoint;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;
    private ArrayList<DoublePoint> doublePoints;
    private int color;

    public Polygon(int color) {
        this.color = color;
        points = new ArrayList<>();
    }

    public Polygon() {
        doublePoints = new ArrayList<>();
        points = new ArrayList<>();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public ArrayList<DoublePoint> getDoublePoints() {
        return doublePoints;
    }

    public void addDoublePoint(DoublePoint pl) {
        doublePoints.add(pl);
    } // přidání bodů do polygonu
}
