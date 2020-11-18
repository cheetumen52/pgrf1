package fill;

public class PatternFillCircle implements PatternFill {


    @Override
    public int paint(int x, int y) { //vykreslí kolečka
        int radius = 2;
        int sx = x % (2 * radius) - radius;
        int sy = y % (2 * radius) - radius;
        if (Math.sqrt(sx * sx + sy * sy) < radius) {
            return 0xFF0000;
        }

        return 0xFF;
    }
}
