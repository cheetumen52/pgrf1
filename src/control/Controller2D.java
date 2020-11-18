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

    private int x, y, x2, y2, x3, y3, x4, y4;
    private SeedFill seedFill;
    private ScanLine scanline;
    private LineRasterizer rasterizer;
    private SeedFillBorder seedFillBorder;
    private boolean first;
    private String mode;
    private int customColor;
    private int seedFillColor;
    private boolean plClipperEdit;
    private Point start, last;
    private Point nearest;
    private boolean edit; // rozhodujicí proměnná pro editaci bodu
    private Polygon pl;
    private PolygonRasterizer polygonRasterizer;
    private Polygon plClipper;
    private boolean firstClipper;
    private Point startClipper;
    private Point lastClipper;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        mode = "Fill mode: Constant";
        customColor = 0x00ffff;
        seedFillColor = 0x00ff00;
        panel.drawString(mode, 10, 20);
        seedFill = new SeedFill(raster);
        rasterizer = new LineRasterizerGraphics(raster);
        scanline = new ScanLine(rasterizer);
        pl = new Polygon(0xff0000);
        plClipper = new Polygon(0xFFff00);
        polygonRasterizer = new PolygonRasterizer(rasterizer);
        seedFillBorder = new SeedFillBorder(raster);
        firstClipper = true;
        first = true;
        edit = false;
        plClipperEdit = true;
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        seedFillBorder.setFillColor(0x0000ff);
                        seedFillBorder.setBorderColor(start);
                        seedFillBorder.setSeed(new Point(e.getX(), e.getY()));
                        seedFillBorder.fill();
                    }
                }
                if (e.isShiftDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
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
                    if (SwingUtilities.isRightMouseButton(e)) {
                        pl.addPoints(start);
                        scanline.setPolygon(pl);
                        scanline.setFillColor(new Color(customColor));
                        scanline.fill();
                        pl.getPoints().remove(pl.getPoints().size() - 1);

                    }

                } else if (SwingUtilities.isLeftMouseButton(e)) {
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
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    seedFill.setSeed(new Point(e.getX(), e.getY()));
                    seedFill.fill();
                } else if (SwingUtilities.isRightMouseButton(e)) {
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
                                plClipperEdit = !plClipperEdit;
                            }
                        }
                        edit = true; // vyhození do else
                    } else {
                        nearest.setX(e.getX()); //přesunutí bodu
                        nearest.setY(e.getY()); //přesunutí bodu
                        if (!plClipperEdit) {
                            colorClipper();
                            plClipperEdit = !plClipperEdit;
                        }
                        update();
                        edit = false;
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

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
                    case KeyEvent.VK_1:
                        seedFill.setChoice(1);
                        mode = "Fill mode: Constant";
                        break;
                    case KeyEvent.VK_2:
                        seedFill.setChoice(2);
                        mode = "Fill mode: Circle";
                        break;
                    case KeyEvent.VK_3:
                        seedFill.setChoice(3);
                        mode = "Fill mode: Chessboard";
                        break;
                    case KeyEvent.VK_4:
                        seedFill.setChoice(4);
                        mode = "Fill mode: Stripes";
                        break;
                    case KeyEvent.VK_5:
                        Object response = JOptionPane.showInputDialog(null, "Kterou barvu mám změnit?", "Změna barvy", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Polygon 1", "Clipper Polygon", "SeedFill", "Scanline"}, "Polygon 1");
                        if (response == null) {
                            JOptionPane.showMessageDialog(null, "Změna barev zrušena, nebudou aplikovány žádné změny!", "Změna barvy", 1);
                            return;
                        }
                        if (response.equals("Polygon 1")) {
                            stringColor = showColorDialog();
                            try {
                                pl.setColor(Integer.decode(stringColor));
                            } catch (Exception c) {
                                badColorInput(c);
                            }
                        }
                        if (response.equals("Clipper Polygon")) {
                            stringColor = showColorDialog();
                            try {
                                plClipper.setColor(Integer.decode(stringColor));
                            } catch (Exception c) {
                                badColorInput(c);
                            }
                        }
                        if (response.equals("Scanline")) {
                            stringColor = showColorDialog();
                            try {
                                customColor = Integer.decode(stringColor);
                            } catch (Exception c) {
                                badColorInput(c);
                            }
                        }
                        if (response.equals("SeedFill")) {
                            stringColor = showColorDialog();
                            try {
                                seedFillColor = Integer.decode(stringColor);
                                seedFill.setFillColor(seedFillColor);
                            } catch (Exception c) {
                                badColorInput(c);
                            }
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

    private String showColorDialog() {
        return JOptionPane.showInputDialog("Zadej barvu ve formátu: 0xffffff .");
    }

    private void badColorInput(Exception c) {
        if (c.getClass().getCanonicalName().equals("java.lang.NullPointerException")) {
            JOptionPane.showMessageDialog(null, "Nastavení barvy bylo zrušeno, ponechávám defaultní!", "Změna barvy", 1);
        } else {
            JOptionPane.showMessageDialog(null, "Špatně zadaná barva, ponechávám defaultní!", "Změna barvy", 0);
        }
        return;
    }

    private void colorClipper() {
        pl.addPoints(start);
        plClipper.addPoints(startClipper);
        Polygon clipped = Clipper.clip(pl, plClipper);
        scanline.setPolygon(clipped);
        scanline.setFillColor(new Color(customColor));
        scanline.fill();
        pl.getPoints().remove(pl.getPoints().size() - 1);
        plClipper.getPoints().remove(plClipper.getPoints().size() - 1);
    }

    private void drawMovingLine(MouseEvent e, Polygon pl, Point start, Point last, int lineColor) {
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
        panel.drawString(mode + " | Scanline color: " + customColor + " | SeedFillColor: " + seedFillColor, 10, 20);
        panel.repaint();
    }

    private void hardClear() {
        panel.clear();
        initObjects(panel.getRaster());
    }

}
