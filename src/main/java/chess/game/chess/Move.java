package chess.game.chess;

public class Move {

    Cell from;
    Cell to;

    private boolean isLongCastling;
    private boolean isShortCastling;
    private int promotionCode;

    public Cell getFrom() {
        return from;
    }

    public Cell getTo() {
        return to;
    }

    public Move(Cell from, Cell to) {
        this.from = from;
        this.to = to;
    }

    public boolean isLongCastling() {
        return isLongCastling;
    }

    public void setLongCastling(boolean longCastling) {
        isLongCastling = longCastling;
    }

    public boolean isShortCastling() {
        return isShortCastling;
    }

    public void setShortCastling(boolean shortCastling) {
        isShortCastling = shortCastling;
    }

    public int getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(int promotionCode) {
        this.promotionCode = promotionCode;
    }
}
