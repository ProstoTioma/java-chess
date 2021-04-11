package chess.game;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    public Integer x;
    public Integer y;
    public boolean selected;
    public List<Integer[]> possibleMoves;
    public boolean isDragAndDrop;
    public Integer mouseX;
    public Integer mouseY;

    public Selection(Integer x, Integer y, boolean selected) {
        this.x = x;
        this.y = y;
        this.selected = selected;
        possibleMoves = new ArrayList<>();
    }
}
