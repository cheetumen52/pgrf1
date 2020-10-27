package fill;

import model.Line;
import model.Polygon;
import rasterize.Raster;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScanLine implements Filler {

    private Polygon pl;
    private Color fillColor;
    private Color borderColor;
    private Raster raster;

    public ScanLine(Raster raster) {
        this.raster = raster;
    }

    private void process() {
        List<Line> lines = new ArrayList<>();
        List<Integer> xArray = new ArrayList<>();
        int yMin = 0, yMax = 0;
        for (int i = 0; i < pl.getPoints().size(); i++) {
            Line line = new Line(pl.getPoints().get(i), pl.getPoints().get(i + 1), 0xff0000);
            if (!line.isHorizontal()) {
                lines.add(line.setOrientation());
                if (line.getY1() > line.getY2()) {
                    yMax = line.getY1();
                    yMin = line.getY2();
                } else {
                    yMax = line.getY2();
                    yMin = line.getY1();
                }
            }
        }
        for (int y = yMin; y <= yMax; y++) {
            for (Line lineY : lines) {
                if (lineY.isIntersection(y)) {
                    int x = lineY.getIntersection(y);
                    xArray.add(x);
                }
            }
            xArray = sortXArray(xArray);
            for (Integer x : xArray) {
                raster.setPixel(x, y, 0xff0000);
            }
        }
    }

    private List<Integer> sortXArray(List<Integer> xArray) {
        //selection sort
        for (int i = 0; i < xArray.size() - 1; i++) {
            int index = i;
            for (int j = i + 1; j < xArray.size(); j++) {
                if (xArray.get(j) < xArray.get(index)) {
                    index = j;//searching for lowest index
                }
            }
            int smallerNumber = xArray.get(index);
            xArray.set(index, xArray.get(i));
            xArray.set(i, smallerNumber);
        }
        return xArray;
    }

    @Override
    public void fill() {

    }

    public void setPolygon(Polygon pl) {
        this.pl = pl;
    }

    public void setFillColor(Color cl) {
        this.fillColor = cl;
    }


}
