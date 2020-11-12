package control;

import fill.ScanLine;
import fill.SeedFill;
import model.Line;
import model.Point;
import rasterize.LineRasterizerGraphics;
import rasterize.Raster;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final Panel panel;

    private int x, y;
    private LineRasterizerGraphics rasterizer;
    private SeedFill seedFill;
    private ScanLine scanline;
    private boolean first = true;
    private int x2;
    private int y2;
    private Point start, last;
    private model.Polygon pl = new model.Polygon();

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        seedFill = new SeedFill(raster);
        rasterizer = new LineRasterizerGraphics(raster);
        scanline = new ScanLine(rasterizer);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) {
                    scanline.fill();
                }

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (first) { // první bod v polygonu
                        x = e.getX();
                        y = e.getY();
                        Point p = new Point(x, y);
                        start = p; // potřeba dočasně uložit tento bod
                        pl.addPoints(p);
                        first = false;
                    } else {
                        x2 = e.getX();
                        y2 = e.getY();
                        Point p = new Point(x2, y2);
                        last = p; // poslední vykreslený bod
                        rasterizer.rasterize(new Line(x, y, x2, y2, 0xff0000));
                        x = x2; // napojení předchozího bodu s novým
                        y = y2;
                        pl.addPoints(p);
                    }
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    scanline.setPolygon(pl);
                    scanline.fill();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    seedFill.setSeed(new Point(e.getX(), e.getY()));
                    seedFill.fill();
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
                    //TODO
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
//        panel.clear();
        //TODO
        /*
        Point origin = new Point(0,0);
        Point vectorX = new Point(1,0);
        Point vectorY = new Point(0,1);
        Line lineH = new Line(origin,vectorX,0xFF);
        Line lineV = new Line(origin,vectorX,0xFF);

        origin = origin.withY(10);
        //origin.x = 10;
        Line line = new Line(origin,new Point(1,2,0xFF);

        Polygon p3 = Clipper.clip(p1,p2); -> rasterize pomocí ScanLine
         */
    }

    private void hardClear() {
        panel.clear();
    }

}
