package fill;

import model.Point;
import rasterize.Raster;

public class SeedFill implements Filler {
    private final Raster raster;
    private Point seed;
    private int backgroundColor;
    private int fillColor = 0xffff00; // default color
    private int choice = 1; // default choice
    private PatternFill pattern = new PatternFillConstant(fillColor);

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    public void setSeed(Point seed) {
        this.seed = seed;
        backgroundColor = raster.getPixel(seed.getX(), seed.getY());
    }

    public void setFillColor(int color) {
        fillColor = color;
    }

    public void setChoice(int choice) {
        switch (choice) {
            case 1:
                pattern = new PatternFillConstant(this.fillColor);
                this.choice = choice;
                break;
            case 2:
                pattern = new PatternFillCircle();
                this.choice = choice;
                break;
            case 3:
                pattern = new PatternFillChessboard();
                this.choice = choice;
                break;
            case 4:
                pattern = new PatternFillStripes();
                this.choice = choice;
                break;
        }
    }

    @Override
    public void fill() {
        seedFill(seed);
    }

    private void seedFill(Point seed) {
        if (seed.getX() >= 0 && seed.getY() >= 0 && seed.getX() < raster.getWidth() && seed.getY() < raster.getHeight()) {
            if (raster.getPixel(seed.getX(), seed.getY()) == backgroundColor && raster.getPixel(seed.getX(), seed.getY()) != fillColor) {

                if (choice == 1) {
                    raster.setPixel(seed.getX(), seed.getY(), fillColor);
                } else {
                    raster.setPixel(seed.getX(), seed.getY(), pattern.paint(seed.getX(), seed.getY()));
                }

                seedFill(new Point(seed.getX() + 1, seed.getY()));
                seedFill(new Point(seed.getX() - 1, seed.getY()));
                seedFill(new Point(seed.getX(), seed.getY() + 1));
                seedFill(new Point(seed.getX(), seed.getY() - 1));
            }
        }
    }
}
