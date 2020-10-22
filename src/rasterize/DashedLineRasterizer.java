package rasterize;

import model.Point;
import model.Polygon;

import static java.lang.Math.abs;

public class DashedLineRasterizer extends LineRasterizer {

    int i;
    Polygon pl;

    public DashedLineRasterizer(Raster raster, Polygon pl) {
        super(raster);
        this.pl = pl;
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {
        i++;
        int sx, sy;
        sx = (x1 + x2) / 2;
        sy = (y1 + y2) / 2;

        for (Point p : pl.getPoints()) { // vykreslení počátků a konečných bodů - nejsou mezery v počátku a na konci - lepší řešení mě nenapadlo
            raster.setPixel(p.getX(), p.getY(), this.getColor().getRGB());
        }
        if (i % 8 < 4) {
            raster.setPixel(sx, sy, this.color.getRGB());
        }
        if ((abs(x1 - sx) > 1) || (abs(y1 - sy) > 1)) drawLine(x1, y1, sx, sy);
        if ((abs(x2 - sx) > 1) || (abs(y2 - sy) > 1)) drawLine(sx, sy, x2, y2);
    }



}
