package fill;

import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScanLine implements Filler {

    private Polygon pl;
    private Color fillColor;
    private LineRasterizer lineRasterizer;

    public ScanLine(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    private void process() {
        List<Line> lines = new ArrayList<>();
        List<Integer> xArray = new ArrayList<>();
        Line line;
        int yMin = -1, yMax = -1;
        for (int i = 0; i <= pl.getPoints().size() - 1; i++) {

            if (i == pl.getPoints().size() - 1) {
                line = new Line(pl.getPoints().get(0), pl.getPoints().get(i), 0xff0000);
            } else {
                line = new Line(pl.getPoints().get(i), pl.getPoints().get(i + 1), 0xff0000);
            }
            if (!line.isHorizontal()) {
                line = line.setOrientation();
                lines.add(line);
                if (yMin == -1) yMin = line.getY2();
                if (yMax == -1) yMax = line.getY1();
                if (yMin > line.getY2()) yMin = line.getY2();
                if (yMax < line.getY1()) yMax = line.getY1();
            }
        }

        for (int y = yMin; y < yMax; y++) {
            xArray.clear();
            for (Line lineY : lines) {
                if (lineY.isIntersection(y)) {
                    int x = lineY.getIntersection(y);
                    xArray.add(x);
                }
            }
            sortXArray(xArray);
            for (int i = 0; i < xArray.size(); i += 2) {
                if (xArray.size() > i + 1) {
                    lineRasterizer.rasterize(new Line(new Point(xArray.get(i), y), new Point(xArray.get(i + 1), y), fillColor.getRGB()));
                }
            }
        }
    }

    private void sortXArray(List<Integer> xArray) { //select sort
        for (int i = 0; i < xArray.size() - 1; i++) {
            int index = i;
            for (int j = i + 1; j < xArray.size(); j++) {
                if (xArray.get(j) < xArray.get(index)) {
                    index = j; //searching for lowest index
                }
            }
            int smallerNumber = xArray.get(index);
            xArray.set(index, xArray.get(i));
            xArray.set(i, smallerNumber);
        }
    }

    @Override
    public void fill() {
        if (pl != null) {
            if (pl.getPoints().size() > 2) {
                process();
            }
        } else {
            System.out.println("Je třeba nastavit polygon pomocí .setPolygon()");
        }
    }

    public void setPolygon(Polygon pl) {
        this.pl = pl;
    }

    public void setFillColor(Color cl) {
        this.fillColor = cl;
    }


}
