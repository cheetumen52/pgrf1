package fill;


import model.Point;
import rasterize.Raster;

public class SeedFillBorder implements Filler {

    private final Raster raster;
    private Point seed;
    private int borderColor;
    private int fillColor;

    public SeedFillBorder(Raster raster) {
        this.raster = raster;
    }

    public void setSeed(Point seed) {
        this.seed = seed;
    }

    @Override
    public void fill() {
        if (borderColor != fillColor) {
            seedFill(seed);
        } else {
            System.out.println("Je třeba změnit barvu - border != fill");
        }
    }

    private void seedFill(Point seed) {
        if (seed.getX() >= 0 && seed.getY() >= 0 && seed.getX() < raster.getWidth() && seed.getY() < raster.getHeight()) {

            int current_color = raster.getPixel(seed.getX(), seed.getY()) & 0xffffff;

            if (current_color != borderColor && current_color != fillColor) {
                raster.setPixel(seed.getX(), seed.getY(), fillColor);
                seedFill(new Point(seed.getX() + 1, seed.getY()));
                seedFill(new Point(seed.getX() - 1, seed.getY()));
                seedFill(new Point(seed.getX(), seed.getY() + 1));
                seedFill(new Point(seed.getX(), seed.getY() - 1));
            }
        }
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor & 0xffffff;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor & 0xffffff;
    }
}
