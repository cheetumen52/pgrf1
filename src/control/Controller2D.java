package control;

import clip.Clipper;
import fill.ScanLine;
import fill.SeedFill;
import fill.SeedFillBorder;
import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.PolygonRasterizer;
import rasterize.Raster;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final Panel panel;

    private int x, y, x2, y2, x3, y3, x4, y4; // pomocné proměnné pro tvorbu polygonů
    private SeedFill seedFill;
    private ScanLine scanline;
    private LineRasterizer rasterizer;
    private SeedFillBorder seedFillBorder;
    private boolean first;
    private String mode;
    // Barvy
    private int customColor;
    private int seedFillColor;
    private int borderFillColor;
    //Temp body
    private Point start, last;
    private Point nearest; // nejbližší bod - editace
    private boolean edit; // rozhodujicí proměnná pro editaci bodu
    private Polygon pl;
    private PolygonRasterizer polygonRasterizer;
    private Polygon plClipper; // polygon - clipper
    private boolean firstClipper;
    private Point startClipper, lastClipper; //pomocné body k polygonu

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        mode = "Fill mode: Constant";
        customColor = 0x00ffff; // defaultní barvy
        seedFillColor = 0x00ff00;
        borderFillColor = 0x0000ff;
        panel.drawString(mode, 10, 20); //vykreslení stringu -> pattern modu
        seedFill = new SeedFill(raster);
        rasterizer = new LineRasterizerGraphics(raster);
        scanline = new ScanLine(rasterizer);
        pl = new Polygon(0xff0000); //defaultní barvy polygonů
        plClipper = new Polygon(0xFFff00);
        polygonRasterizer = new PolygonRasterizer(rasterizer);
        seedFillBorder = new SeedFillBorder(raster);
        firstClipper = true;
        first = true;
        edit = false;

    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isShiftDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) { //tvorba clipper polygonu
                        if (firstClipper) {
                            x3 = e.getX();
                            y3 = e.getY();
                            Point p = new Point(x3, y3);
                            startClipper = p;
                            plClipper.addPoints(p);
                            firstClipper = !firstClipper;
                        } else {
                            x4 = e.getX();
                            y4 = e.getY();
                            Point p = new Point(x4, y4);
                            lastClipper = p; // poslední vykreslený bod
                            rasterizer.rasterize(new Line(x3, y3, x4, y4));
                            x3 = x4; // napojení předchozího bodu s novým
                            y3 = y4;
                            plClipper.addPoints(p);
                        }
                        update();
                        if (plClipper.getPoints().size() > 2) {
                            colorClipper();
                        }
                    }
                    if (SwingUtilities.isRightMouseButton(e)) { //scanline
                        scanline.setPolygon(pl);
                        scanline.setFillColor(new Color(customColor));
                        scanline.fill();
                    }

                } else if (SwingUtilities.isLeftMouseButton(e)) { //Tvorba polygonu
                    if (first) { // první bod v polygonu
                        x = e.getX();
                        y = e.getY();
                        Point p = new Point(x, y);
                        start = p; // potřeba dočasně uložit tento bod
                        pl.addPoints(p);
                        first = !first;
                    } else {
                        x2 = e.getX();
                        y2 = e.getY();
                        Point p = new Point(x2, y2);
                        last = p; // poslední vykreslený bod
                        rasterizer.rasterize(new Line(x, y, x2, y2));
                        x = x2; // napojení předchozího bodu s novým
                        y = y2;
                        pl.addPoints(p);
                    }
                    update();
                } else if (SwingUtilities.isMiddleMouseButton(e)) { //seedFill
                    try {
                        seedFill.setSeed(new Point(e.getX(), e.getY()));
                        seedFill.fill();
                        if (e.isControlDown()) {
                            seedFillBorder.setFillColor(borderFillColor);
                            seedFillBorder.setBorderColor(pl.getColor());
                            seedFillBorder.setSeed(new Point(e.getX(), e.getY()));
                            seedFillBorder.fill();
                        }
                    } catch (StackOverflowError s) {
                        JOptionPane.showMessageDialog(null, "Přetekl zásobník! Algoritmus vybarvil co mohl. Aplikujte ho znovu na nevyplněné místo nebo se pokuste vyplnit menší tvar");
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) { //editace bodů polygonu
                    if (!edit) { // určení nejbližšího bodu
                        int nx = e.getX();
                        int ny = e.getY();
                        double temp = 99999999; // init hodnota, k zamezení chyby v podmínce pro porovnání
                        if (!e.isControlDown()) {
                            if (pl.getPoints().size() > 1) { // vyhledání nejbližšího bodu pomocí getDistance();
                                for (Point nearestPoint : pl.getPoints()) {
                                    if (nearestPoint.getDistance(nx, ny) < temp) {
                                        temp = nearestPoint.getDistance(nx, ny);
                                        nearest = nearestPoint;
                                    }
                                }
                            }
                        } else {
                            if (plClipper.getPoints().size() > 1) { // vyhledání nejbližšího bodu pomocí getDistance();
                                for (Point nearestPoint : plClipper.getPoints()) {
                                    if (nearestPoint.getDistance(nx, ny) < temp) {
                                        temp = nearestPoint.getDistance(nx, ny);
                                        nearest = nearestPoint;
                                    }
                                }
                            }
                        }
                        edit = true; // vyhození do else
                    } else {
                        nearest.setX(e.getX()); //přesunutí bodu
                        nearest.setY(e.getY()); //přesunutí bodu
                        update();
                        colorClipper();


                        edit = false;
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { // pohyblivá čára při tažení myší -> viz metoda drawMovingLine()
                if (e.isShiftDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        drawMovingLine(e, plClipper, startClipper, lastClipper, plClipper.getColor());
                    }
                } else {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        drawMovingLine(e, pl, start, last, pl.getColor());
                    }
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String stringColor;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_H:
                        // help dialog
                        JOptionPane.showMessageDialog(null, "Přehled ovládání: \n P - Menu patternů \n B - Menu barev \n H - Toto menu \n LMB - přidání bodu polygonu \n LMB + shift - Přidání bodu do clipper polygonu \n RMB - Editace bodů (Polygon #1)\n RMB + ctrl - Editace bodů (Clipper polygon) \n RMB + shift - Scanline (Polygon #1) \n MMB - SeedFill\n MMB + ctrl - SeedFillBorder", "Help - Ovládání", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case KeyEvent.VK_P:
                        //vybírací menu - změna patternu
                        Object response2 = JOptionPane.showInputDialog(null, "Který pattern chceš v Seedfillu??", "Změna patternu", JOptionPane.QUESTION_MESSAGE, null, new String[]{"ConstantColor", "Circle", "Chessboard", "Stripes"}, "ConstantColor");
                        if (response2 == null) {
                            JOptionPane.showMessageDialog(null, "Výběr patternu zrušen, nebudou aplikovány žádné změny!", "Změna patternu", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        switch (response2.toString()) {
                            case "ConstantColor":
                                seedFill.setChoice(1);
                                mode = "Fill mode: Constant";
                                break;
                            case "Circle":
                                seedFill.setChoice(2);
                                mode = "Fill mode: Circle";
                                break;
                            case "Chessboard":
                                seedFill.setChoice(3);
                                mode = "Fill mode: Chessboard";
                                break;
                            case "Stripes":
                                seedFill.setChoice(4);
                                mode = "Fill mode: Stripes";
                                break;
                        }
                        update();
                        break;
                    case KeyEvent.VK_B:
                        // vybírací menu - změna barev
                        Object response = JOptionPane.showInputDialog(null, "Kterou barvu mám změnit?", "Změna barvy", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Polygon 1", "Clipper Polygon", "SeedFill", "Scanline", "BorderFill"}, "Polygon 1");
                        if (response == null) {
                            JOptionPane.showMessageDialog(null, "Změna barev zrušena, nebudou aplikovány žádné změny!", "Změna barvy", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        switch (response.toString()) {
                            case "Polygon 1":
                                stringColor = showColorDialog();
                                try {
                                    pl.setColor(Integer.decode(stringColor));
                                } catch (Exception c) {
                                    badColorInput(c);
                                }
                                break;
                            case "Clipper Polygon":
                                stringColor = showColorDialog();
                                try {
                                    plClipper.setColor(Integer.decode(stringColor));
                                } catch (Exception c) {
                                    badColorInput(c);
                                }
                                break;
                            case "Scanline":
                                stringColor = showColorDialog();
                                try {
                                    customColor = Integer.decode(stringColor);
                                    update();
                                } catch (Exception c) {
                                    badColorInput(c);
                                }
                                break;
                            case "SeedFill":
                                stringColor = showColorDialog();
                                try {
                                    seedFillColor = Integer.decode(stringColor);
                                    seedFill.setFillColor(seedFillColor);
                                } catch (Exception c) {
                                    badColorInput(c);
                                }
                                break;
                            case "BorderFill":
                                stringColor = showColorDialog();
                                try {
                                    borderFillColor = Integer.decode(stringColor);
                                    seedFillBorder.setFillColor(borderFillColor);
                                } catch (Exception c) {
                                    badColorInput(c);
                                }
                                break;
                        }

                        update();
                        break;
                    case KeyEvent.VK_C:
                        hardClear();
                        break;
                }
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private String showColorDialog() { //metoda pro zobrazení nastavení barvy
        return JOptionPane.showInputDialog("Zadej barvu ve formátu: 0xffffff .");
    }

    private void badColorInput(Exception c) { // metoda pro chybu v menu - cancel a špatně zadaná barva
        if (c.getClass().getCanonicalName().equals("java.lang.NullPointerException")) {
            JOptionPane.showMessageDialog(null, "Nastavení barvy bylo zrušeno, ponechávám defaultní!", "Změna barvy", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Špatně zadaná barva, ponechávám defaultní!", "Změna barvy", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void colorClipper() { // vybarví ořezaný polygon
        Polygon clipped = Clipper.clip(pl, plClipper);
        scanline.setPolygon(clipped);
        scanline.setFillColor(new Color(customColor));
        scanline.fill();
    }

    private void drawMovingLine(MouseEvent e, Polygon pl, Point start, Point last, int lineColor) { //vykreslí pohyblivou čáru při tažení myší
        if (pl.getPoints().size() > 1) {
            update();
            rasterizer.rasterize(new Line(new Point(e.getX(), e.getY()), start, lineColor));
            rasterizer.rasterize(new Line(new Point(e.getX(), e.getY()), last, lineColor));

        }
    }

    private void update() {
        panel.clear();
        polygonRasterizer.rasterize(pl); // znovu vykreslení polygonu
        polygonRasterizer.rasterize(plClipper);
        panel.drawString(mode + " | Scanline color: " + customColor + " | SeedFillColor: " + seedFillColor, 10, 20); // aktuální mód a barvy
        panel.repaint();
    }

    private void hardClear() { // úplné smazání
        panel.clear();
        initObjects(panel.getRaster());
    }

}
