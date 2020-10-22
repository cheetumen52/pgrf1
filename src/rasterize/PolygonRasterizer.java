package rasterize;

import model.Line;
import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    DashedLineRasterizer lr;

    public PolygonRasterizer(DashedLineRasterizer lr) {
        this.lr = lr;
    }

    public void rasterize(Polygon polygon) {
        for (int i = 0; i <= polygon.getPoints().size(); i++) {
            if (i + 1 < polygon.getPoints().size()) {
                Point p1 = polygon.getPoints().get(i);
                Point p2 = polygon.getPoints().get(i + 1);
                Line line = new Line(p1, p2, 0xff0000);
                lr.rasterize(line);
            }
        }
        if (polygon.getPoints().size() > 1) {
            lr.rasterize(polygon.getPoints().get(0), polygon.getPoints().get(polygon.getPoints().size() - 1)); // poslední point s prvním
        }
    }

}
