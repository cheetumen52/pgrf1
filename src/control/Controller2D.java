package control;

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
import java.util.ArrayList;

public class Controller2D implements Controller {

    private final Panel panel;

    private int x, y;
    boolean ctrlDown = false;
    boolean shiftDown = false;
    private SeedFill seedFill;
    private ScanLine scanline;
    private LineRasterizer rasterizer;
    private SeedFillBorder seedFillBorder;
    private boolean first = true;
    private int x2;
    private int y2;
    private boolean leadLine = true;
    private ArrayList<Line> lines = new ArrayList<>();
    private Point start, last;

    private Polygon pl = new Polygon();
    private PolygonRasterizer polygonRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        seedFill = new SeedFill(raster);
        rasterizer = new LineRasterizerGraphics(raster);
        scanline = new ScanLine(rasterizer);
        pl = new Polygon();
        polygonRasterizer = new PolygonRasterizer(rasterizer);
        polygonRasterizer.setColor(0x00ff00);
        seedFillBorder = new SeedFillBorder(raster);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) {
                    ctrlDown = !ctrlDown;
                }
                if (e.isShiftDown()) {
                    shiftDown = !shiftDown;
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
                    update();
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    if (ctrlDown) {
                        pl.addStartPoint(start, pl.getPoints().size());
                        scanline.setPolygon(pl);
                        scanline.fill();
                        pl.getPoints().remove(pl.getPoints().size() - 1);
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (ctrlDown) {
                        seedFill.setSeed(new Point(e.getX(), e.getY()));
                        seedFill.fill();
                    } else {
                        seedFillBorder.setFillColor(0x0000ff);
                        seedFillBorder.setBorderColor(start);
                        seedFillBorder.setSeed(new Point(e.getX(), e.getY()));
                        seedFillBorder.fill();
                    }
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
        panel.repaint();
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
        initObjects(panel.getRaster());
    }

}
