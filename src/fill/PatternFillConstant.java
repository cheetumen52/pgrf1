package fill;

public class PatternFillConstant implements PatternFill {
    private int color;

    public PatternFillConstant(int color) {
        this.color = color;
    }

    @Override
    public int paint(int x, int y) {
        return color;
    }
}
