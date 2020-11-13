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
        seedFill(seed, fillColor, borderColor);
    }

    private void seedFill(Point seed, int fillColor, int borderColor) {
        if (borderColor != fillColor) {
            if (seed.getX() >= 0 && seed.getY() >= 0 && seed.getX() < raster.getWidth() && seed.getY() < raster.getHeight()) {
                if (raster.getPixel(seed.getX(), seed.getY()) != borderColor && raster.getPixel(seed.getX(), seed.getY()) != fillColor) {
                    raster.setPixel(seed.getX(), seed.getY(), fillColor);
                    seedFill(new Point(seed.getX() + 1, seed.getY()), fillColor, borderColor);
                    seedFill(new Point(seed.getX(), seed.getY() + 1), fillColor, borderColor);
                    seedFill(new Point(seed.getX() - 1, seed.getY()), fillColor, borderColor);
                    seedFill(new Point(seed.getX(), seed.getY() - 1), fillColor, borderColor);
                }

            }
        } else {
            System.out.println("Je třeba změnit barvu - border != fill");
        }
    }

    public void setBorderColor(Point borderPoint) {
        borderColor = raster.getPixel(borderPoint.getX(), borderPoint.getY());
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }
}
