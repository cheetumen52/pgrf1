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
    private boolean first = true;
    private boolean leadLine = true;
    private Point start, last;

    private Polygon pl = new Polygon(0xff0000);
    private PolygonRasterizer polygonRasterizer;
    private Polygon plClipper = new Polygon(0xffff00);
    private boolean firstClipper = true;
    private Point startClipper;
    private Point lastClipper;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        seedFill = new SeedFill(raster);
        rasterizer = new LineRasterizerGraphics(raster);
        scanline = new ScanLine(rasterizer);
        pl = new Polygon(0xff0000);
        plClipper = new Polygon(0xffff00);
        polygonRasterizer = new PolygonRasterizer(rasterizer);
        seedFillBorder = new SeedFillBorder(raster);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
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
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        pl.addStartPoint(start, pl.getPoints().size());
                        plClipper.addStartPoint(startClipper, plClipper.getPoints().size());
                        Polygon clipped = Clipper.clip(pl, plClipper);
                        scanline.setPolygon(clipped);
                        scanline.setFillColor(new Color(0x00ffff));
                        scanline.fill();
                        pl.getPoints().remove(pl.getPoints().size() - 1);
                        plClipper.getPoints().remove(plClipper.getPoints().size() - 1);
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
                    pl.addStartPoint(start, pl.getPoints().size());
                    scanline.setPolygon(pl);
                    scanline.fill();
                    pl.getPoints().remove(pl.getPoints().size() - 1);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        //TODO
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        //TODO
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (pl.getPoints().size() > 1 && leadLine) { // propojení nejstaršího bodu s počátkem - vodící
                        rasterizer.rasterize(new Line(new Point(e.getX(), e.getY()), start, Color.CYAN.getRGB()));
                    }

                    if (x != 0 && y != 0 && leadLine)
                        rasterizer.rasterize(x, y, e.getX(), e.getY()); // vodící čára pro polygon
                    update();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                }
                update();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    //TODO
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

    private void update() {
        panel.clear();
        polygonRasterizer.rasterize(pl); // znovu vykreslení polygonu
        polygonRasterizer.rasterize(plClipper);
        panel.repaint();
    }

    private void hardClear() {
        panel.clear();
        initObjects(panel.getRaster());
    }

}
