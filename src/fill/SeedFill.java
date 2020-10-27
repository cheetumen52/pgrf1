package fill;

import model.Point;
import rasterize.Raster;

public class SeedFill implements Filler {
    private final Raster raster;
    private Point seed;
    private int backgroundColor;
    private int fillColor;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    public void setSeed(Point seed) {
        this.seed = seed;
        backgroundColor = raster.getPixel(seed.getX(), seed.getY());
    }

    @Override
    public void fill() {
        seedFill(seed, fillColor, backgroundColor);
    }

    private void seedFill(Point seed, int fillColor, int backgroundColor) {
        if (raster.getPixel(seed.getX(), seed.getY()) == backgroundColor) {
            raster.setPixel(seed.getX(), seed.getY(), fillColor);

            seedFill(new Point(seed.getX() + 1, seed.getY()), fillColor, backgroundColor);
            seedFill(new Point(seed.getX() - 1, seed.getY()), fillColor, backgroundColor);
            seedFill(new Point(seed.getX(), seed.getY() + 1), fillColor, backgroundColor);
            seedFill(new Point(seed.getX(), seed.getY() - 1), fillColor, backgroundColor);
        }
    }

}
