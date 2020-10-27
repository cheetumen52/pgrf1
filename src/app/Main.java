package app;

import model.Line;
import model.Point;
import rasterize.DashedLineRasterizer;
import rasterize.FilledLineRasterizer;
import rasterize.PolygonRasterizer;
import rasterize.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main {

    private JPanel panel;
    private RasterBufferedImage raster;
    private boolean first = true;
    private int x;
    private Point firstLine;
    private int y;
    private int x2;
    private int y2;
    private boolean leadLine = true;
    private Point nearest;
    private boolean edit = false; // rozhodujicí proměnná pro editaci bodu
    private String lastAction = ""; // proměnná pro poslední akci - pohyblivá čára atd.
    private ArrayList<Line> lines = new ArrayList<>();
    private Point start, last;
    private model.Polygon pl = new model.Polygon();
    private FilledLineRasterizer rasterizer;
    private DashedLineRasterizer dashedRasterizer;
    private PolygonRasterizer polygonRasterizer;

    public Main(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        rasterizer = new FilledLineRasterizer(raster);
        dashedRasterizer = new DashedLineRasterizer(raster, pl);
        polygonRasterizer = new PolygonRasterizer(dashedRasterizer);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        panel.requestFocus(); // !!! - Potřebné, jinak nefunguje keyListener
        panel.requestFocusInWindow(); // !!! - ^

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Polygon
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
                        dashedRasterizer.rasterize(new Line(x, y, x2, y2, 0xff0000));
                        x = x2; // napojení předchozího bodu s novým
                        y = y2;
                        pl.addPoints(p);
                    }
                    lastAction = "polygon";
                }
                if (e.getButton() == MouseEvent.BUTTON2) { // úsečka
                    if (firstLine == null) { // rozlišení prvního a posledního kliknutí
                        firstLine = new Point(e.getX(), e.getY()); // první krok - uložení bodu
                    } else {
                        Line ln = new Line(firstLine, new Point(e.getX(), e.getY()), Color.CYAN.getRGB());
                        rasterizer.rasterize(ln); //vykreslení úsečky
                        firstLine = null; // vrácení k prvnímu kroku
                        lines.add(ln);
                    }
                    lastAction = "line";
                }

                if (e.getButton() == MouseEvent.BUTTON3) {
                    lastAction = "edit";
                    if (!edit) { // určení nejbližšího bodu
                        int nx = e.getX();
                        int ny = e.getY();
                        double temp = 99999999; // init hodnota, k zamezení chyby v podmínce pro porovnání
                        if (pl.getPoints().size() > 1) { // vyhledání nejbližšího bodu pomocí getDistance();
                            for (Point nearestPoint : pl.getPoints()) {
                                if (nearestPoint.getDistance(nx, ny) < temp) {
                                    temp = nearestPoint.getDistance(nx, ny);
                                    nearest = nearestPoint;
                                }
                            }
                        }
                        edit = true; // vyhození do else
                    } else {
                        nearest.setX(e.getX()); //přesunutí bodu
                        nearest.setY(e.getY()); //přesunutí bodu
                        clear(0xaaaaaa);
                        redrawAll(); // vykreslení nově přesunutého bodu
                        edit = false;
                    }

                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (pl.getPoints().size() > 1) {
                        Line ln = new Line(start, last, 0xff0000); // propojeni prvniho a posledniho bodu - mimo polygon rasterizer - propoj se neukládá
                        dashedRasterizer.rasterize(ln);
                    }
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) { //vynulování/smazání celého rasteru
                    clear(0xaaaaaa);
                    pl = new model.Polygon();
                    dashedRasterizer = new DashedLineRasterizer(raster, pl);
                    polygonRasterizer = new PolygonRasterizer(dashedRasterizer);
                    lines = new ArrayList<>();
                    x = 0;
                    y = 0;
                    firstLine = null;
                    first = true;
                    start = null;
                    last = null;
                    nearest = null;
                    edit = false;
                    lastAction = "";
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_L) leadLine = !leadLine; // vypnutí plovoucích/vodících čar
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                clear(0xaaaaaa);
                if (lastAction.equals("polygon") && pl.getPoints().size() > 1 && leadLine) { // propojení nejstaršího bodu s počátkem - vodící
                    rasterizer.rasterize(new Line(new Point(e.getX(), e.getY()), start, Color.CYAN.getRGB()));
                }

                if (lastAction.equals("polygon") && x != 0 && y != 0 && leadLine)
                    rasterizer.rasterize(x, y, e.getX(), e.getY()); // vodící čára pro polygon
                if (lastAction.equals("line") && firstLine != null && leadLine)
                    rasterizer.rasterize(firstLine.getX(), firstLine.getY(), e.getX(), e.getY()); // vodící čára pro úsečku
                redrawAll();
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (panel.getWidth() < 1 || panel.getHeight() < 1)
                    return;
                if (panel.getWidth() <= raster.getWidth() && panel.getHeight() <= raster.getHeight()) //no resize if new one is smaller
                    return;
                RasterBufferedImage newRaster = new RasterBufferedImage(panel.getWidth(), panel.getHeight());
                newRaster.draw(raster);
                raster = newRaster;
                rasterizer = new FilledLineRasterizer(raster);
                dashedRasterizer = new DashedLineRasterizer(raster, pl);
                polygonRasterizer = new PolygonRasterizer(dashedRasterizer);
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main(1366, 768).start());
    }

    private void redrawAll() { // zamezení duplicitnímu kódu
        rasterizer.redraw(lines); // znovu vykreslení úseček
        polygonRasterizer.rasterize(pl); // znovu vykreslení polygonu
        panel.repaint();
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
        raster.getGraphics().drawString("LMB - Polygon add points, RMB - Edit nearest polygon points, MMB - Make line", 5, 15);
        raster.getGraphics().drawString("C - clear raster, L - turn off moving lines", 5, 30);
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        raster.getGraphics().drawString("LMB - Polygon add points, RMB - Edit nearest polygon points, MMB - Make line", 5, 15);
        raster.getGraphics().drawString("C - clear raster, L - turn off moving lines", 5, 30);
        panel.repaint();
    }

}
