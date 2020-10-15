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
        Nevýhody: Rekurze (pomalejší)
        Výhody: Jednoduchá implementace
        Specifika: Metoda půlení úsečky - Nalezne střed, vykreslí ho. Propojuje středové body, dokud se nedostane k počátečnímu
        Metoda ini - omezení duplicitního kódu - algoritmus se nachází právě v této metodě.
         */

        ini(x1, x2, y1, y2, this.color.getRGB());
    }

    public void redraw(ArrayList<Line> lines) {
        for (Line e : lines) {
            rasterize(e);
        }
    }

    private void ini(int x1, int x2, int y1, int y2, int i) {
        int sx, sy;
        sx = (x1 + x2) / 2;
        sy = (y1 + y2) / 2;
        raster.setPixel(sx, sy, i);
        if ((abs(x1 - sx) > 1) || (abs(y1 - sy) > 1)) drawLine(x1, y1, sx, sy);
        if ((abs(x2 - sx) > 1) || (abs(y2 - sy) > 1)) drawLine(sx, sy, x2, y2);
    }
}
