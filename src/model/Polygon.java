package model;

import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;
    private int color;

    public Polygon(int color) {
        this.color = color;
        points = new ArrayList<>();
    }

    public Polygon() {
        points = new ArrayList<>();
        color = 0xff0000;
    }

    public Polygon(ArrayList<Point> in) {
        points = in;
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

}
