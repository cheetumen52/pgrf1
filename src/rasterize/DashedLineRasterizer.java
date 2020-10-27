package rasterize;

import model.Point;
import model.Polygon;

import static java.lang.Math.abs;

public class DashedLineRasterizer extends LineRasterizer {

    int dash;
    Polygon pl;

    public DashedLineRasterizer(Raster raster, Polygon pl) { // Polygon - kvůli poli s body, potřebné pro zamezení mezer v počátku a konci
        super(raster);
        this.pl = pl;
    }

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

    protected void drawLine(int x1, int y1, int x2, int y2) {
        dash++;
        int sx, sy;
        sx = (x1 + x2) / 2;
        sy = (y1 + y2) / 2;

        for (Point p : pl.getPoints()) { // Vykreslení počátečního a konečného bodu - aby nebyli mezery v počátku a konci
            raster.setPixel(p.getX(), p.getY(), this.getColor().getRGB());
        }
        if (dash % 8 < 4) { // Vykreslení mezer
            raster.setPixel(sx, sy, this.color.getRGB());
        }
        if ((abs(x1 - sx) > 1) || (abs(y1 - sy) > 1)) drawLine(x1, y1, sx, sy);
        if ((abs(x2 - sx) > 1) || (abs(y2 - sy) > 1)) drawLine(sx, sy, x2, y2);
    }



}
