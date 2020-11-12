package fill;

public class PatternFillCircle implements PatternFill {
    private int radius = 5;


    @Override
    public int paint(int x, int y) {
        int sx = x % (2 * radius) - radius;
        int sy = y % (2 * radius) - radius;
        if (Math.sqrt(sx * sx + sy * sy) < radius) {
            return 0xFF0000;
        }

        return 0xFF;
    }
}
