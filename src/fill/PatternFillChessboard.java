package fill;

import java.awt.*;

public class PatternFillChessboard implements PatternFill {
    @Override
    public int paint(int x, int y) { //vykreslí šachovnici
        if ((x % 2 == 0) == (y % 2 == 0))
            return (Color.BLACK).getRGB();
        else
            return (Color.WHITE).getRGB();
    }
}
