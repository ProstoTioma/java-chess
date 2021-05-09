package chess.game.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static chess.game.chess.FigureUtils.*;

public class ChessBoard {

    public Integer[][] field = new Integer[8][8];
    public String currentColor = "WHITE";
    public List<Integer[]> history = Collections.synchronizedList(new ArrayList<>());
    public List<Move> movesHistory = Collections.synchronizedList(new ArrayList<>());

    public ChessBoard() {
        initField();
    }

    public ChessBoard(Integer[][] field) {
        this.field = field;
    }

    private void initField() {
        // 11/21 - pawn
        // 12/22 - rook
        // 13/23 - knight
        // 14/24 - bishop
        // 15/25 - queen
        // 16/26 - king
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 1) field[j][i] = 11;
                else if (i == 6) field[j][i] = 21;
                else if (i == 0) {
                    if (j == 0 || j == 7) field[j][i] = 12;
                    if (j == 1 || j == 6) field[j][i] = 13;
                    if (j == 2 || j == 5) field[j][i] = 14;
                    if (j == 3) field[j][i] = 15;
                    if (j == 4) field[j][i] = 16;
                } else if (i == 7) {
                    if (j == 0 || j == 7) field[j][i] = 22;
                    if (j == 1 || j == 6) field[j][i] = 23;
                    if (j == 2 || j == 5) field[j][i] = 24;
                    if (j == 3) field[j][i] = 25;
                    if (j == 4) field[j][i] = 26;
                } else {
                    field[j][i] = 10;
                }

            }
        }
    }

    public static String getFiguresColor(Integer code) {
        if (code < 17 && code > 10) return "BLACK";
        else if (code > 17) return "WHITE";
        else return "VOID";
    }

    public void moveFigure(Integer cellX, Integer cellY, Integer figureX, Integer figureY, Integer promotionCode) {
        Cell from = new Cell(figureX, figureY, field[figureX][figureY]);
        Cell to = new Cell(cellX, cellY, field[cellX][cellY]);
        Move move = new Move(from, to);
        field[cellX][cellY] = field[figureX][figureY];
        if (isLongCastlingMove(cellX, figureX, figureY)) {
            field[cellX + 1][cellY] = field[0][cellY];
            field[0][cellY] = 10;
            move.setLongCastling(true);
        } else if (isShortCastlingMove(cellX, figureX, figureY)) {
            field[cellX - 1][cellY] = field[7][cellY];
            field[7][cellY] = 10;
            move.setShortCastling(true);
        }
        field[figureX][figureY] = 10;
        var pawn = getPawnForPromotion();

        if (pawn != null ) {
            //BOT ONLY
            if (promotionCode != null) {
                //changePawnToQueen(pawn[0], pawn[1]);
                promotePawnWithSelectedFigure(promotionCode, cellX, cellY);

                history.add(new Integer[]{figureX, figureY, cellX, cellY});
                move.setPromotionCode(promotionCode);
                nextColor();
            } else {
                history.add(new Integer[]{figureX, figureY, cellX, cellY});
            }
            movesHistory.add(move);
            return;
            //todo change to promotion code instead of queen
        }

        history.add(new Integer[]{figureX, figureY, cellX, cellY});
        movesHistory.add(move);
        nextColor();
    }

    public Integer getCell(Integer x, Integer y) {
        return field[x][y];
    }

    public List<Integer[]> getAllFiguresByColor(String color) {
        return getAllFigures().stream()
                .filter(f -> getFiguresColor(field[f[0]][f[1]]).equals(color))
                .collect(Collectors.toList());
    }

    public List<Integer[]> getAllFigures() {
        var figuresList = new ArrayList<Integer[]>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                var figure = field[j][i];
                if (figure != 10) {
                    figuresList.add(new Integer[]{j, i});
                }
            }
        }
        return figuresList;
    }

    public ArrayList<Integer[]> getPossibleMoves(Integer x, Integer y, Integer[][] chessField) {
        var possibleMoves = new ArrayList<Integer[]>();
        if (chessField[x][y] == 11 || chessField[x][y] == 21) possibleMoves = getPossibleMovesPawn(x, y, chessField);
        if (chessField[x][y] == 13 || chessField[x][y] == 23) possibleMoves = getPossibleMovesKnight(x, y, chessField);
        if (chessField[x][y] == 14 || chessField[x][y] == 24) possibleMoves = getPossibleMovesBishop(x, y, chessField);
        if (chessField[x][y] == 12 || chessField[x][y] == 22) possibleMoves = getPossibleMovesRook(x, y, chessField);
        if (chessField[x][y] == 15 || chessField[x][y] == 25) possibleMoves = getPossibleMovesQueen(x, y, chessField);
        if (chessField[x][y] == 16 || chessField[x][y] == 26)
            possibleMoves = getPossibleMovesKing(x, y, chessField, canCastling(true), canCastling(false));

        return possibleMoves;
    }

    public List<Integer[]> getValidPossibleMoves(Integer x, Integer y) {
        var possibleMoves = getPossibleMoves(x, y, field);
        List<Integer[]> validMoves = new ArrayList<>();

        for (Integer[] move : possibleMoves) {
            Integer[][] copyField = deepCopy(field);
            copyField[move[0]][move[1]] = copyField[x][y];
            copyField[x][y] = 10;
            //var nextColor = (currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
            if (!isCheck(currentColor, copyField)) {
                validMoves.add(move);
            }

        }

        if ((field[x][y] == 16 || field[x][y] == 26) && canCastling(false)) {
            validMoves = validateLongCastling(validMoves);
        }
        if ((field[x][y] == 16 || field[x][y] == 26) && canCastling(true)) {
            validMoves = validateShortCastling(validMoves);
        }

        return validMoves;
    }

    public void nextColor() {
        if (currentColor.equals("WHITE")) {
            currentColor = "BLACK";
        } else {
            currentColor = "WHITE";
        }
    }

    public boolean isCheck(String color, Integer[][] chessField) {
        Integer kingX = 0, kingY = 0;
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
                if (color.equals(getFiguresColor(chessField[j][i]))) {
                    if (chessField[j][i] == 16 || chessField[j][i] == 26) {
                        kingX = j;
                        kingY = i;
                    }
                }
            }
        }
        //check
        boolean check = false;
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
                if (!getFiguresColor(chessField[j][i]).equals(color)) {
                    var moves = getPossibleMoves(j, i, chessField);
                    for (Integer[] move : moves) {
                        if (move[0].equals(kingX) && move[1].equals(kingY)) {
                            check = true;
                            //System.out.println("Check " + color);
                        }
                    }
                }
            }
        }

        return check;
    }

    public boolean isMate() {
        boolean mate = true;
        if (isCheck(currentColor, field)) {
            for (Integer i = 0; i < 8; i++) {
                for (Integer j = 0; j < 8; j++) {
                    if (currentColor.equals(getFiguresColor(field[j][i]))) {
                        var moves = getValidPossibleMoves(j, i);
                        if (!moves.isEmpty()) {
                            mate = false;
                            break;
                        }
                    }
                }
            }
            //if(getValidPossibleMoves(getPossibleMovesKing(x, y, field)).size() == 0) mate = true;
        } else {
            mate = false;
        }
        return mate;
    }

    private boolean isLongCastlingMove(Integer moveX, Integer figureX, Integer figureY) {
        if (field[figureX][figureY] == 16 || field[figureX][figureY] == 26) {
            return figureX - moveX == 2;
        }
        return false;
    }

    private boolean isShortCastlingMove(Integer moveX, Integer figureX, Integer figureY) {
        if (field[figureX][figureY] == 16 || field[figureX][figureY] == 26) {
            return figureX - moveX == -2;
        }
        return false;
    }

    private List<Integer[]> validateLongCastling(List<Integer[]> possibleMoves) {
        Integer y = (currentColor.equals("WHITE")) ? 7 : 0;
        if (isCheck(currentColor, field)) {
            possibleMoves = possibleMoves.stream().filter(m -> !(m[0] == 2 && m[1].equals(y))).collect(Collectors.toList());
        }
        var copy = deepCopy(field);
        copy[3][y] = copy[4][y];
        copy[4][y] = 10;
        if (isCheck(currentColor, copy)) {
            possibleMoves = possibleMoves.stream().filter(m -> !(m[0] == 2 && m[1].equals(y))).collect(Collectors.toList());
        }
        return possibleMoves;
    }

    private List<Integer[]> validateShortCastling(List<Integer[]> possibleMoves) {
        int y = (currentColor.equals("WHITE")) ? 7 : 0;
        if (isCheck(currentColor, field)) {
            possibleMoves = possibleMoves.stream().filter(m -> !(m[0] == 6 && m[1] == y)).collect(Collectors.toList());
        }
        var copy = deepCopy(field);
        copy[5][y] = copy[4][y];
        copy[4][y] = 10;
        if (isCheck(currentColor, copy)) {
            possibleMoves = possibleMoves.stream().filter(m -> !(m[0] == 6 && m[1] == y)).collect(Collectors.toList());
        }
        return possibleMoves;
    }

    public Integer[] getPawnForPromotion() {
        int pawnCode = (currentColor.equals("WHITE")) ? 21 : 11;
        int y = (currentColor.equals("WHITE")) ? 0 : 7;
        for (Integer[] figure : getAllFiguresByColor(currentColor)) {
            if (field[figure[0]][figure[1]] == pawnCode && figure[1] == y) {
                return figure;
            }
        }
        return null;
    }

    private boolean canCastling(boolean isShort) {
        boolean kingMoved = false;
        boolean rockMoved = false;
        Integer y = (currentColor.equals("WHITE")) ? 7 : 0;
        Integer rockX = (isShort) ? 7 : 0;
        for (Integer[] entry : history) {
            if (entry[0] == 4 && entry[1].equals(y)) {
                kingMoved = true;
            } else if (entry[0].equals(rockX) && entry[1].equals(y)) {
                rockMoved = true;
            }
        }
        if (!isShort) {
            return !kingMoved && !rockMoved && field[1][y] == 10 && field[2][y] == 10 && field[3][y] == 10;
        } else {
            return !kingMoved && !rockMoved && field[5][y] == 10 && field[6][y] == 10;
        }
    }

    public static Integer[][] deepCopy(Integer[][] org) {
        if (org == null) {
            return null;
        }

        final Integer[][] res = new Integer[org.length][];
        for (int i = 0; i < org.length; i++) {
            res[i] = Arrays.copyOf(org[i], org[i].length);
        }
        return res;
    }

    public boolean isDraw() {
        if (history.size() > 9) {
            var moveL = history.get(history.size() - 1);
            var moveP = history.get(history.size() - 2);
            if (Arrays.equals(moveL, history.get(history.size() - 5)) && Arrays.equals(moveL, history.get(history.size() - 9))
                    && Arrays.equals(moveP, history.get(history.size() - 6)) && Arrays.equals(moveP, history.get(history.size() - 10))) {
                return true;
            }
        }
        var bFigures = getAllFiguresByColor("BLACK");
        var wFigures = getAllFiguresByColor("WHITE");
        return bFigures.size() == 1 && wFigures.size() == 1;
    }

    public boolean isStaleMate() {
        if (!isMate()) {
            var figures = getAllFiguresByColor(currentColor);
            int count = (int) figures.stream().filter(move -> getValidPossibleMoves(move[0], move[1]).size() != 0).count();
            return count == 0;
        }
        return false;
    }

    public void promotePawnWithSelectedFigure(Integer figureCode, Integer pawnX, Integer pawnY) {
        field[pawnX][pawnY] = figureCode;
        nextColor();
    }

    public ChessBoard copy() {
        var copyField = deepCopy(field);
        var chessFieldCopy = new ChessBoard(copyField);
        chessFieldCopy.currentColor = currentColor;
        chessFieldCopy.history = new ArrayList<>(this.history);
        return chessFieldCopy;
    }

    public void undo() {
        var movesCount = movesHistory.size();
        int y = currentColor.equals("WHITE") ? 0 : 7;
        if (movesCount > 0) {
            var lastMove = movesHistory.get(movesCount - 1);
            var from = lastMove.from;
            var to = lastMove.to;


            field[from.x][from.y] = from.code;
            field[to.x][to.y] = to.code;

            if (lastMove.isLongCastling()) {
                field[3][y] = 10;
                field[0][y] = (y == 0) ? 12 : 22;
            } else if (lastMove.isShortCastling()) {
                field[5][y] = 10;
                field[7][y] = (y == 0) ? 12 : 22;
            }

            history.remove(history.size() - 1);
            movesHistory.remove(movesHistory.size() - 1);
        }
        nextColor();
    }

}
