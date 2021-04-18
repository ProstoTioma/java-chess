package chess.game.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FigureUtils {

    public static Map<Integer, String> figuresMap = new HashMap<>();
    public static Map<Integer, Integer> figuresValue = new HashMap<>();
    public static Map<Integer, String> nameOfLettersX = new HashMap<>();
    public static Map<Integer, String> nameOfLettersY = new HashMap<>();


    static {
        figuresValue.put(10, 0);
        figuresValue.put(11, 2);
        figuresValue.put(12, 5);
        figuresValue.put(13, 3);
        figuresValue.put(14, 3);
        figuresValue.put(15, 9);
        figuresValue.put(16, 10);
        figuresValue.put(21, 2);
        figuresValue.put(22, 5);
        figuresValue.put(23, 3);
        figuresValue.put(24, 3);
        figuresValue.put(25, 9);
        figuresValue.put(26, 10);

        figuresMap.put(11, "bp");
        figuresMap.put(12, "br");
        figuresMap.put(13, "bn");
        figuresMap.put(14, "bb");
        figuresMap.put(15, "bq");
        figuresMap.put(16, "bk");
        figuresMap.put(21, "wp");
        figuresMap.put(22, "wr");
        figuresMap.put(23, "wn");
        figuresMap.put(24, "wb");
        figuresMap.put(25, "wq");
        figuresMap.put(26, "wk");

        nameOfLettersX.put(0, "a");
        nameOfLettersX.put(1, "b");
        nameOfLettersX.put(2, "c");
        nameOfLettersX.put(3, "d");
        nameOfLettersX.put(4, "e");
        nameOfLettersX.put(5, "f");
        nameOfLettersX.put(6, "g");
        nameOfLettersX.put(7, "h");

        nameOfLettersY.put(0, "8");
        nameOfLettersY.put(1, "7");
        nameOfLettersY.put(2, "6");
        nameOfLettersY.put(3, "5");
        nameOfLettersY.put(4, "4");
        nameOfLettersY.put(5, "3");
        nameOfLettersY.put(6, "2");
        nameOfLettersY.put(7, "1");
    }

    public static boolean isSameColor(Integer firstFigure, Integer secondFigure) {
        return firstFigure < 17 && firstFigure > 10 && secondFigure < 17 && secondFigure > 10 || firstFigure > 17 && secondFigure > 17;
    }

    public static boolean isPawn(int code) {
        return code == 11 || code == 21;
    }

    public static ArrayList<Integer[]> getPossibleMovesPawn(Integer x, Integer y, Integer[][] chessField) {
        var possibleMoves = new ArrayList<Integer[]>();
        if (chessField[x][y] == 11 && y != 7) {
            if (chessField[x][y + 1] == 10) possibleMoves.add(new Integer[]{x, y + 1});
            if (x > 0 && (!isSameColor(chessField[x - 1][y + 1], chessField[x][y]) && chessField[x - 1][y + 1] != 10))
                possibleMoves.add(new Integer[]{x - 1, y + 1});
            if (x < 7 && (!isSameColor(chessField[x + 1][y + 1], chessField[x][y]) && chessField[x + 1][y + 1] != 10))
                possibleMoves.add(new Integer[]{x + 1, y + 1});
            if (y == 1 && chessField[x][y + 1] == 10 && chessField[x][y + 2] == 10)
                possibleMoves.add(new Integer[]{x, y + 2});
        } else if(y != 0){
            if (chessField[x][y - 1] == 10) possibleMoves.add(new Integer[]{x, y - 1});
            if (x > 0 && (!isSameColor(chessField[x - 1][y - 1], chessField[x][y]) && chessField[x - 1][y - 1] != 10))
                possibleMoves.add(new Integer[]{x - 1, y - 1});
            if (x < 7 && (!isSameColor(chessField[x + 1][y - 1], chessField[x][y]) && chessField[x + 1][y - 1] != 10))
                possibleMoves.add(new Integer[]{x + 1, y - 1});
            if (y == 6 && chessField[x][y - 1] == 10 && chessField[x][y - 2] == 10)
                possibleMoves.add(new Integer[]{x, y - 2});
        }

        return possibleMoves;
    }

    public static ArrayList<Integer[]> getPossibleMovesBishop(Integer x, Integer y, Integer[][] chessField) {
        var possibleMoves = new ArrayList<Integer[]>();
        //botom right
        for (Integer j = x + 1, i = y + 1; i < 8 && j < 8; i++, j++) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new Integer[]{j, i});
                break;

            } else {
                break;
            }
        }
        //top left
        for (Integer j = x - 1, i = y - 1; i >= 0 && j >= 0; i--, j--) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new Integer[]{j, i});
                break;
            } else {
                break;
            }
        }

        //top right
        for (Integer j = x + 1, i = y - 1; i >= 0 && j < 8; i--, j++) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new Integer[]{j, i});
                break;

            } else {
                break;
            }
        }

        //bottom left
        for (Integer j = x - 1, i = y + 1; i < 8 && j >= 0; i++, j--) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new Integer[]{j, i});
                break;

            } else {
                break;
            }
        }

        return possibleMoves;
    }

    public static ArrayList<Integer[]> getPossibleMovesKnight(Integer x, Integer y, Integer[][] chessField) {
        var possibleMoves = new ArrayList<Integer[]>();
        if (y + 2 < 8 && x + 1 < 8 && chessField[x + 1][y + 2] == 10) possibleMoves.add(new Integer[]{x + 1, y + 2});
        else if (y + 2 < 8 && x + 1 < 8 && !isSameColor(chessField[x][y], chessField[x + 1][y + 2]))
            possibleMoves.add(new Integer[]{x + 1, y + 2});

        if (y + 2 < 8 && x - 1 >= 0 && chessField[x - 1][y + 2] == 10) possibleMoves.add(new Integer[]{x - 1, y + 2});
        else if (y + 2 < 8 && x - 1 >= 0 && !isSameColor(chessField[x][y], chessField[x - 1][y + 2]))
            possibleMoves.add(new Integer[]{x - 1, y + 2});

        if (x + 2 < 8 && y + 1 < 8 && chessField[x + 2][y + 1] == 10) possibleMoves.add(new Integer[]{x + 2, y + 1});
        else if (x + 2 < 8 && y + 1 < 8 && !isSameColor(chessField[x][y], chessField[x + 2][y + 1]))
            possibleMoves.add(new Integer[]{x + 2, y + 1});
        if (x - 2 >= 0 && y + 1 < 8 && chessField[x - 2][y + 1] == 10) possibleMoves.add(new Integer[]{x - 2, y + 1});
        else if (x - 2 >= 0 && y + 1 < 8 && !isSameColor(chessField[x][y], chessField[x - 2][y + 1]))
            possibleMoves.add(new Integer[]{x - 2, y + 1});
        if (y - 2 >= 0 && x + 1 < 8 && chessField[x + 1][y - 2] == 10) possibleMoves.add(new Integer[]{x + 1, y - 2});
        else if (y - 2 >= 0 && x + 1 < 8 && !isSameColor(chessField[x][y], chessField[x + 1][y - 2]))
            possibleMoves.add(new Integer[]{x + 1, y - 2});

        if (y - 2 >= 0 && x - 1 >= 0 && chessField[x - 1][y - 2] == 10) possibleMoves.add(new Integer[]{x - 1, y - 2});
        else if (y - 2 >= 0 && x - 1 >= 0 && !isSameColor(chessField[x][y], chessField[x - 1][y - 2]))
            possibleMoves.add(new Integer[]{x - 1, y - 2});

        if (y - 1 >= 0 && x + 2 < 8 && chessField[x + 2][y - 1] == 10) possibleMoves.add(new Integer[]{x + 2, y - 1});
        else if (y - 1 >= 0 && x + 2 < 8 && !isSameColor(chessField[x][y], chessField[x + 2][y - 1]))
            possibleMoves.add(new Integer[]{x + 2, y - 1});

        if (y - 1 >= 0 && x - 2 >= 0 && chessField[x - 2][y - 1] == 10) possibleMoves.add(new Integer[]{x - 2, y - 1});
        else if (y - 1 >= 0 && x - 2 >= 0 && !isSameColor(chessField[x][y], chessField[x - 2][y - 1]))
            possibleMoves.add(new Integer[]{x - 2, y - 1});
        return possibleMoves;
    }

    public static ArrayList<Integer[]> getPossibleMovesRook(Integer x, Integer y, Integer[][] chessField) {
        var possibleMoves = new ArrayList<Integer[]>();
        //top
        for (Integer i = y - 1; i >= 0; i--) {
            var nextCell = chessField[x][i];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{x, i});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new Integer[]{x, i});
                break;
            } else {
                break;
            }
        }
        //bot
        for (Integer i = y + 1; i < 8; i++) {
            var nextCell = chessField[x][i];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{x, i});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new Integer[]{x, i});
                break;
            } else {
                break;
            }
        }
        //left
        for (Integer i = x - 1; i >= 0; i--) {
            var nextCell = chessField[i][y];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{i, y});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new Integer[]{i, y});
                break;
            } else {
                break;
            }
        }
        //right
        for (Integer i = x + 1; i < 8; i++) {
            var nextCell = chessField[i][y];
            if (nextCell == 10) {
                possibleMoves.add(new Integer[]{i, y});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new Integer[]{i, y});
                break;
            } else {
                break;
            }
        }
        return possibleMoves;
    }

    public static ArrayList<Integer[]> getPossibleMovesQueen(Integer x, Integer y, Integer[][] chessField) {
        var possibleMoves = new ArrayList<Integer[]>();
        possibleMoves.addAll(getPossibleMovesRook(x, y, chessField));
        possibleMoves.addAll(getPossibleMovesBishop(x, y, chessField));
        return possibleMoves;
    }

    public static ArrayList<Integer[]> getPossibleMovesKing(Integer x, Integer y, Integer[][] chessField, boolean canShortCastling, boolean canLongCastling) {
        var possibleMoves = new ArrayList<Integer[]>();


        if (y < 7 && (chessField[x][y + 1] == 10 || !isSameColor(chessField[x][y + 1], chessField[x][y])))
            possibleMoves.add(new Integer[]{x, y + 1});

        if (y > 0 && (chessField[x][y - 1] == 10 || !isSameColor(chessField[x][y - 1], chessField[x][y])))
            possibleMoves.add(new Integer[]{x, y - 1});

        if (x < 7 && (chessField[x + 1][y] == 10 || !isSameColor(chessField[x + 1][y], chessField[x][y])))
            possibleMoves.add(new Integer[]{x + 1, y});

        if (x > 0 && (chessField[x - 1][y] == 10 || !isSameColor(chessField[x - 1][y], chessField[x][y])))
            possibleMoves.add(new Integer[]{x - 1, y});

        //top right
        if (y > 0 && x < 7 && (chessField[x + 1][y - 1] == 10 || !isSameColor(chessField[x + 1][y - 1], chessField[x][y])))
            possibleMoves.add(new Integer[]{x + 1, y - 1});
        //top left
        if (y > 0 && x > 0 && (chessField[x - 1][y - 1] == 10 || !isSameColor(chessField[x - 1][y - 1], chessField[x][y])))
            possibleMoves.add(new Integer[]{x - 1, y - 1});
        //bottom left
        if (y < 7 && x > 0 && (chessField[x - 1][y + 1] == 10 || !isSameColor(chessField[x - 1][y + 1], chessField[x][y])))
            possibleMoves.add(new Integer[]{x - 1, y + 1});
        //bottom right
        if (y < 7 && x < 7 && (chessField[x + 1][y + 1] == 10 || !isSameColor(chessField[x + 1][y + 1], chessField[x][y])))
            possibleMoves.add(new Integer[]{x + 1, y + 1});

        if (canLongCastling) {
            if (getFiguresColor(chessField[x][y]).equals("WHITE")) {
                possibleMoves.add(new Integer[]{2, 7});
            } else {
                possibleMoves.add(new Integer[]{2, 0});
            }
        }
        if (canShortCastling) {
            if (getFiguresColor(chessField[x][y]).equals("WHITE")) {
                possibleMoves.add(new Integer[]{6, 7});
            } else {
                possibleMoves.add(new Integer[]{6, 0});
            }
        }

        return possibleMoves;
    }

    public static String getFiguresColor(Integer code) {
        if(code < 17 && code > 10) return "BLACK";
        else if(code > 17) return "WHITE";
        else return "VOID";
    }

}
