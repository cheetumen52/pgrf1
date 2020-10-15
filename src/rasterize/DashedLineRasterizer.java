package rasterize;

import model.Line;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class DashedLineRasterizer extends LineRasterizer {

    int dashSpace;

    public DashedLineRasterizer(Raster raster) {
        super(raster);
    }


    protected void drawLine(int x1, int y1, int x2, int y2) {
        int sx, sy;
        sx = (x1 + x2) / 2;
        sy = (y1 + y2) / 2;
        raster.setPixel(sx, sy, this.color.getRGB());
        if ((abs(x1 - sx) > 1) || (abs(y1 - sy) > 1)) drawLine(x1, y1, sx, sy);
        if ((abs(x2 - sx) > 1) || (abs(y2 - sy) > 1)) drawLine(sx, sy, x2, y2);
    }

    public void redraw(ArrayList<Line> lines) {
        for (Line e : lines) {
            rasterize(e);
        }
    }
}
