package clip;

import model.Line;
import model.Point;
import model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Clipper {
    private static List<Line> clipperEdges = new ArrayList<>();
    public static Polygon clip(Polygon inputPolygon, Polygon clipperPoly) { //TODO return clipped polygon
        ArrayList<Point> output = new ArrayList<>();
        if (inputPolygon.getPoints().size() < 2) return inputPolygon;
        getEdges(clipperPoly);
        for (Line edge : clipperEdges) {
            output.clear();
            Point v1 = inputPolygon.getPoints().get(inputPolygon.getPoints().size() - 1);
            for (Point v2 : inputPolygon.getPoints()) {
                if (isInside(v2, edge)) {
                    if (!isInside(v1, edge)) output.add(intersection(v1, v2, edge));
                    output.add(v2);
                } else {
                    if (isInside(v1, edge)) output.add(intersection(v1, v2, edge));
                }
                v1 = v2;
            }
        }
        Polygon result = new Polygon(output);
        return result;
    }

    private static Point intersection(Point v1, Point v2, Line edge) {
        double x1, x2, x3, x4, y1, y2, y3, y4;
        x1 = v1.getX();
        y1 = v1.getY();
        x2 = v2.getX();
        y2 = v2.getY();
        x3 = edge.getX1();
        y3 = edge.getY1();
        x4 = edge.getX2();
        y4 = edge.getY2();
        double v = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        double x0 = ((x1 * y2 - x2 * y1) * (x3 - x4) - (x3 * y4 - x4 * y3) * (x1 - x2)) / v;
        double y0 = ((x1 * y2 - x2 * y1) * (y3 - y4) - (x3 * y4 - x4 * y3) * (y1 - y2)) / v;
        return new Point((int) x0, (int) y0);
    }

    private static boolean isInside(Point v2, Line edge) {
        double x0 = v2.getX();
        double y0 = v2.getY();
        double x1 = edge.getX1();
        double x2 = edge.getX2();
        double y1 = edge.getY1();
        double y2 = edge.getY2();
        return (y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1 > 0.0;

    }

    private static void getEdges(Polygon clipper) {
        for (int i = 0; i < clipper.getPoints().size() - 1; i++) {
            clipperEdges.add(new Line(clipper.getPoints().get(i).getX(), clipper.getPoints().get(i).getY(), clipper.getPoints().get(i + 1).getX(), clipper.getPoints().get(i + 1).getY(), 0xff0000));
            if (i == clipper.getPoints().size()) {
                clipperEdges.add(new Line(clipper.getPoints().get(i).getX(), clipper.getPoints().get(i).getY(), clipper.getPoints().get(0).getX(), clipper.getPoints().get(0).getY(), 0xff0000));
            }
        }
        return;
    }

}
