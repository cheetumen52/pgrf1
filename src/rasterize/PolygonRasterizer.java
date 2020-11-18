package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    LineRasterizer lr;


    public PolygonRasterizer(LineRasterizer lr) {
        this.lr = lr;
    }

    public void rasterize(Polygon polygon) {
        for (int i = 0; i < polygon.getPoints().size(); i++) { // vykreslení bodů polygonu
            Point p1 = polygon.getPoints().get(i);
            Point p2 = polygon.getPoints().get((i + 1) % polygon.getPoints().size());
            Line line = new Line(p1, p2, polygon.getColor());
            lr.rasterize(line);

        }
    }

}
