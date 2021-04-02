package chess.game;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    public int x;
    public int y;
    public boolean selected;
    public List<int[]> possibleMoves;

    public Selection(int x, int y, boolean selected) {
        this.x = x;
        this.y = y;
        this.selected = selected;
        possibleMoves = new ArrayList<>();
    }
}
