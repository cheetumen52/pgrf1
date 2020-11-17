package fill;

import java.awt.*;

public class PatternFillChessboard implements PatternFill {
    @Override
    public int paint(int x, int y) {

        // Check whether row and column
        // are in even position
        // If it is true set Black color
        if ((x % 2 == 0) == (y % 2 == 0))
            return (Color.BLACK).getRGB();
        else
            return (Color.WHITE).getRGB();

    }
}
