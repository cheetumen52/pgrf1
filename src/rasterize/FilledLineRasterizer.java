package rasterize;

import model.Line;

import java.util.ArrayList;

import static java.lang.Math.abs;


public class FilledLineRasterizer extends LineRasterizer {
    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {

        /*
        Algoritmus Midpoint

        Nevýhody:
        Rekurze (pomalejší) - při vykreslování delší úsečky může nastat problém s přeplněným zásobníkem
        Horší provedení čarkované, tečkované čáry - právě kvůli rekurzi - složitě se modifikuje algoritmus aby fungoval

        Výhody:
        Jednoduchá implementace, funguje pro všechny kvadranty.

        Specifika:
        Metoda půlení - Nalezne střed úsečky, rozpůlí jí a vykreslí středový bod. Pokračuje dokud není celá původní úsečka vykreslena.
         */

        int sx, sy;
        sx = (x1 + x2) / 2;
        sy = (y1 + y2) / 2;
        raster.setPixel(sx, sy, this.getColor().getRGB());
        if ((abs(x1 - sx) > 1) || (abs(y1 - sy) > 1)) drawLine(x1, y1, sx, sy);
        if ((abs(x2 - sx) > 1) || (abs(y2 - sy) > 1)) drawLine(sx, sy, x2, y2);
    }

    public void redraw(ArrayList<Line> lines) { // vykreslení pole úseček
        for (Line e : lines) {
            rasterize(e);
        }
    }
}
