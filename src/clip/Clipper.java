package clip;

import model.Point;
import model.Polygon;

public class Clipper {
    public static Polygon clip(Polygon inputPolygon, Polygon clipperPoly) { //TODO return clipped polygon
        Polygon result = null;
        return result;
    }

    private class Vertex {
        double x;
        double y;

        Vertex(Point p) {
            x = p.getX();
            y = p.getY();
        }
    }
}
