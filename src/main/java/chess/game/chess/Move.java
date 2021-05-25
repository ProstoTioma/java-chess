package chess.game.chess;

public class Move {

    Cell from;
    Cell to;

    private boolean isLongCastlingWhite;
    private boolean isLongCastlingBlack;
    private boolean isShortCastlingWhite;
    private boolean isShortCastlingBlack;
    private int promotionCode;
    private String color;

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

    public boolean isLongCastlingBlack() {
        return isLongCastlingBlack;
    }

    public boolean isLongCastlingWhite() {
        return isLongCastlingWhite;
    }

    public void setLongCastling(boolean longCastling, String color) {
        if(color.equals("WHITE")) {
            isLongCastlingWhite = longCastling;
        } else isLongCastlingBlack = longCastling;
    }

    public boolean isShortCastlingBlack() {
        return isShortCastlingBlack;
    }

    public boolean isShortCastlingWhite() {
        return isShortCastlingWhite;
    }

    public void setShortCastling(boolean shortCastling, String color) {
        if(color.equals("WHITE")) {
            isShortCastlingWhite = shortCastling;
        } else isShortCastlingBlack = shortCastling;
    }

    public int getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(int promotionCode) {
        this.promotionCode = promotionCode;
    }
}
