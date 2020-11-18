package fill;

public class PatternFillConstant implements PatternFill {
    private int color;

    public PatternFillConstant(int color) {
        this.color = color;
    } // konstantní barva

    @Override
    public int paint(int x, int y) {
        return color;
    }
}
