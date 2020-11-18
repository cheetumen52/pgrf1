package fill;

import java.awt.*;

public class PatternFillStripes implements PatternFill {
    @Override
    public int paint(int x, int y) {
        if ((x % 4) == (y % 4))
            return (Color.blue).getRGB();
        return (Color.yellow).getRGB();
    }
}
