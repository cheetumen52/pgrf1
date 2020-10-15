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

/**
 * trida pro kresleni na platno: vyuzita tridy RasterBufferedImage
 *
 * @author PGRF FIM UHK
 * @version 2020
 */

public class Polygon {

	private JPanel panel;
	private RasterBufferedImage raster;
	private boolean first = true;
	private int x;
	private int mx = -1; // kresleni samostatne usecky (mid button) - -1 -> ridici cislo
	private int my = -1; // kresleni samostatne usecky (mid button)
	private int y;
	private int x2;
	private int y2;
	private String lastAction = "";
	private ArrayList<Line> lines = new ArrayList<>();
	private Point start, last;
	private model.Polygon pl = new model.Polygon();
	private FilledLineRasterizer rasterizer;

	public Polygon(int width, int height) {
		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());

		frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		raster = new RasterBufferedImage(width, height);
		rasterizer = new FilledLineRasterizer(raster);
		DashedLineRasterizer dashedRasterizer = new DashedLineRasterizer(raster);
		PolygonRasterizer polygonRasterizer = new PolygonRasterizer(dashedRasterizer);

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
		panel.requestFocus();
		panel.requestFocusInWindow();

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (first) {
						x = e.getX();
						y = e.getY();
						Point p = new Point(x, y);
						start = p;
						pl.addPoints(p);
						first = false;
						lastAction = "polygon"; //motion decisive variable
					} else {
						x2 = e.getX();
						y2 = e.getY();
						Point p = new Point(x2, y2);
						last = p;
						dashedRasterizer.rasterize(new Line(x, y, x2, y2, 0xff0000));
						x = x2;
						y = y2;
						pl.addPoints(p);
						lastAction = "polygon"; //motion decisive variable
					}
				}
				if (e.getButton() == MouseEvent.BUTTON2) {
					if (mx == -1 && my == -1) {
						mx = e.getX();
						my = e.getY();
						lastAction = "line"; //motion decisive variable
					} else {
						Line ln = new Line(mx, my, e.getX(), e.getY(), Color.CYAN.getRGB());
						rasterizer.rasterize(ln);
						mx = -1;
						my = -1;
						lines.add(ln);
						lastAction = "line"; //motion decisive variable
					}
				}

				if (e.getButton() == MouseEvent.BUTTON3) {
					//TODO
				}
				panel.repaint();
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (pl.getPoints().size() > 1) {
						System.out.println("Test");
						System.out.println(start.getX() + " " + start.getY() + " " + last.getX() + " " + last.getY());
						Line ln = new Line(start, last, 0xff0000);
						rasterizer.rasterize(ln);

					}
				}
			}
		});

		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_C) {
					clear(0xaaaaaa);
					pl = new model.Polygon();
					lines = new ArrayList<>();
					x = 0;
					y = 0;
					mx = -1;
					my = -1;
					first = true;
					panel.repaint();
				}
			}
		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {

				clear(0xaaaaaa);
				if (lastAction.equals("polygon")) rasterizer.rasterize(x, y, e.getX(), e.getY());
				if (lastAction.equals("polygon") && pl.getPoints().size() > 1) {
					rasterizer.rasterize(new Point(e.getX(), e.getY()), start);
				}
				if (lastAction.equals("line") && mx != -1 && my != -1) rasterizer.rasterize(mx, my, e.getX(), e.getY());

				rasterizer.redraw(lines);
				polygonRasterizer.rasterize(pl);
				panel.repaint();

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

			}
		});

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Polygon(800, 600).start());
	}

	public void clear(int color) {
		raster.setClearColor(color);
		raster.clear();
		raster.getGraphics().drawString("Use mouse buttons and try resize the window", 5, 15);
	}

	public void present(Graphics graphics) {
		raster.repaint(graphics);
	}

	public void start() {
		clear(0xaaaaaa);
		raster.getGraphics().drawString("Use mouse buttons and try resize the window", 5, 15);
		panel.repaint();
	}

}
