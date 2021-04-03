package chess.game;

import chess.input.MouseHandler;

import java.awt.event.MouseEvent;
import java.util.*;

public class Game {

    public static Map<Integer, String> figuresMap = new HashMap<>();
    public final int[][] field = new int[8][8];
    public final Selection selection = new Selection(0, 0, false);
    public String currentColor = "WHITE";

    private final MouseHandler mouseHandler = new MouseHandler((Object e) -> {
        MouseEvent event = (MouseEvent) e;
        move(event.getX(), event.getY());
        System.out.println(event.getX() + " " + event.getY());
    });

    static {
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
    }

    public Game() {
        initField();
    }

    public static void main(String[] args) {
        new Game().printFiled();
    }

    public MouseHandler getMouseHandler() {
        return mouseHandler;
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

    public int[] getCellCoordinates(int x, int y) {
        int cellX = (x - 50) / 100;
        int cellY = (y - 50) / 100;
        if (x < 850 && x > 50 && y > 50 && y < 850) {
            System.out.printf("cell coordinates: %d %d \n", cellX, cellY);
            return new int[]{cellX, cellY};
        }
        return null;
    }

    public void move(int x, int y) {
        // read input (mouse)
        var coords = getCellCoordinates(x, y);
        int cellX = coords[0];
        int cellY = coords[1];
        var cell = field[cellX][cellY];

        if (selection.selected) {
                for (int i = 0; i < selection.possibleMoves.size(); i++) {
                    var v = selection.possibleMoves.get(i);
                    if (cellX == v[0] && cellY == v[1]) {
                        var selected = field[selection.x][selection.y];
                        if (!isSameColor(cell, selected)) {
                            if (getFiguresColor(selected).equals(currentColor)) {
                                field[cellX][cellY] = field[selection.x][selection.y];
                                field[selection.x][selection.y] = 10;
                                nextColor();
                            }
                            //action
                        }
                    }

                }
                selection.selected = false;

        } else if (cell != 10) {
            selection.x = cellX;
            selection.y = cellY;
            if (getFiguresColor(cell).equals(currentColor)) {
                selection.possibleMoves = getPossibleMoves(cellX, cellY);
            } else {
                selection.possibleMoves = Collections.emptyList();
            }
            selection.selected = true;
            System.out.printf("Selected %d %d\n", selection.x, selection.y);
        }

    }

    public void printFiled() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.printf(" %d ", field[j][i]);
            }

            System.out.println();
        }
    }

    public boolean isSameColor(int firstFigure, int secondFigure) {
        return firstFigure < 17 && firstFigure > 10 && secondFigure < 17 && secondFigure > 10 || firstFigure > 17 && secondFigure > 17;
    }

    public List<int[]> getPossibleMoves(int x, int y) {
        if (field[x][y] == 11 || field[x][y] == 21) return getPossibleMovesPawn(x, y);
        if (field[x][y] == 13 || field[x][y] == 23) return getPossibleMovesKnight(x, y);
        if (field[x][y] == 14 || field[x][y] == 24) return getPossibleMovesBishop(x, y);
        if (field[x][y] == 12 || field[x][y] == 22) return getPossibleMovesRook(x, y);
        if (field[x][y] == 15 || field[x][y] == 25) return getPossibleMovesQueen(x, y);
        if (field[x][y] == 16 || field[x][y] == 26) return getPossibleMovesKing(x, y);

        else return new ArrayList<>();
    }

    public List<int[]> getPossibleMovesPawn(int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        if (field[x][y] == 11) {
            if (field[x][y + 1] == 10) possibleMoves.add(new int[]{x, y + 1});
            if (x > 0 && (!isSameColor(field[x - 1][y + 1], field[x][y]) && field[x - 1][y + 1] != 10)) possibleMoves.add(new int[]{x - 1, y + 1});
            if (x < 7 && (!isSameColor(field[x + 1][y + 1], field[x][y]) && field[x + 1][y + 1] != 10)) possibleMoves.add(new int[]{x + 1, y + 1});
            if (y == 1 && field[x][y + 1] == 10 && field[x][y + 2] == 10) possibleMoves.add(new int[]{x, y + 2});
        } else {
            if (field[x][y - 1] == 10) possibleMoves.add(new int[]{x, y - 1});
            if (x > 0 && (!isSameColor(field[x - 1][y - 1], field[x][y]) && field[x - 1][y - 1] != 10)) possibleMoves.add(new int[]{x - 1, y - 1});
            if (x < 7 && (!isSameColor(field[x + 1][y - 1], field[x][y]) && field[x + 1][y - 1] != 10)) possibleMoves.add(new int[]{x + 1, y - 1});
            if (y == 6 && field[x][y - 1] == 10 && field[x][y - 2] == 10) possibleMoves.add(new int[]{x, y - 2});
        }

        return possibleMoves;
    }

    public List<int[]> getPossibleMovesBishop(int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        //botom right
        for (int j = x + 1, i = y + 1; i < 8 && j < 8; i++, j++) {
            var nextCell = field[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, field[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;

            } else {
                break;
            }
        }
        //top left
        for (int j = x - 1, i = y - 1; i >= 0 && j >= 0; i--, j--) {
            var nextCell = field[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, field[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;
            } else {
                break;
            }
        }

        //top right
        for (int j = x + 1, i = y - 1; i >= 0 && j < 8; i--, j++) {
            var nextCell = field[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, field[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;

            } else {
                break;
            }
        }

        //bottom left
        for (int j = x - 1, i = y + 1; i < 8 && j >= 0; i++, j--) {
            var nextCell = field[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, field[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;

            } else {
                break;
            }
        }

        return possibleMoves;
    }

    public List<int[]> getPossibleMovesKnight(int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        if (y + 2 < 8 && x + 1 < 8 && field[x + 1][y + 2] == 10) possibleMoves.add(new int[]{x + 1, y + 2});
        else if (y + 2 < 8 && x + 1 < 8 && !isSameColor(field[x][y], field[x + 1][y + 2]))
            possibleMoves.add(new int[]{x + 1, y + 2});

        if (y + 2 < 8 && x - 1 >= 0 && field[x - 1][y + 2] == 10) possibleMoves.add(new int[]{x - 1, y + 2});
        else if (y + 2 < 8 && x - 1 >= 0 && !isSameColor(field[x][y], field[x - 1][y + 2]))
            possibleMoves.add(new int[]{x - 1, y + 2});

        if (x + 2 < 8 && y + 1 < 8 && field[x + 2][y + 1] == 10) possibleMoves.add(new int[]{x + 2, y + 1});
        else if (x + 2 < 8 && y + 1 < 8 && !isSameColor(field[x][y], field[x + 2][y + 1]))
            possibleMoves.add(new int[]{x + 2, y + 1});
        if (x - 2 >= 0 && y + 1 < 8 && field[x - 2][y + 1] == 10) possibleMoves.add(new int[]{x - 2, y + 1});
        else if (x - 2 >= 0 && y + 1 < 8 && !isSameColor(field[x][y], field[x - 2][y + 1]))
            possibleMoves.add(new int[]{x - 2, y + 1});
        if (y - 2 >= 0 && x + 1 < 8 && field[x + 1][y - 2] == 10) possibleMoves.add(new int[]{x + 1, y - 2});
        else if (y - 2 >= 0 && x + 1 < 8 && !isSameColor(field[x][y], field[x + 1][y - 2]))
            possibleMoves.add(new int[]{x + 1, y - 2});

        if (y - 2 >= 0 && x - 1 >= 0 && field[x - 1][y - 2] == 10) possibleMoves.add(new int[]{x - 1, y - 2});
        else if (y - 2 >= 0 && x - 1 >= 0 && !isSameColor(field[x][y], field[x - 1][y - 2]))
            possibleMoves.add(new int[]{x - 1, y - 2});

        if (y - 1 >= 0 && x + 2 < 8 && field[x + 2][y - 1] == 10) possibleMoves.add(new int[]{x + 2, y - 1});
        else if (y - 1 >= 0 && x + 2 < 8 && !isSameColor(field[x][y], field[x + 2][y - 1]))
            possibleMoves.add(new int[]{x + 2, y - 1});

        if (y - 1 >= 0 && x - 2 >= 0 && field[x - 2][y - 1] == 10) possibleMoves.add(new int[]{x - 2, y - 1});
        else if (y - 1 >= 0 && x - 2 >= 0 && !isSameColor(field[x][y], field[x - 2][y - 1]))
            possibleMoves.add(new int[]{x - 2, y - 1});
        return possibleMoves;
    }

    public List<int[]> getPossibleMovesRook(int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        //top
        for (int i = y - 1; i >= 0; i--) {
            var nextCell = field[x][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{x, i});
            } else if (!isSameColor(field[x][y], nextCell)) {
                possibleMoves.add(new int[]{x, i});
                break;
            } else {
                break;
            }
        }
        //bot
        for (int i = y + 1; i < 8; i++) {
            var nextCell = field[x][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{x, i});
            } else if (!isSameColor(field[x][y], nextCell)) {
                possibleMoves.add(new int[]{x, i});
                break;
            } else {
                break;
            }
        }
        //left
        for (int i = x - 1; i >= 0; i--) {
            var nextCell = field[i][y];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{i, y});
            } else if (!isSameColor(field[x][y], nextCell)) {
                possibleMoves.add(new int[]{i, y});
                break;
            } else {
                break;
            }
        }
        //right
        for (int i = x + 1; i < 8; i++) {
            var nextCell = field[i][y];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{i, y});
            } else if (!isSameColor(field[x][y], nextCell)) {
                possibleMoves.add(new int[]{i, y});
                break;
            } else {
                break;
            }
        }
        return possibleMoves;
    }

    public List<int[]> getPossibleMovesQueen(int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(getPossibleMovesRook(x, y));
        possibleMoves.addAll(getPossibleMovesBishop(x, y));
        return possibleMoves;
    }

    public List<int[]> getPossibleMovesKing(int x, int y) {
        List<int[]> possibleMoves = new ArrayList<>();

        if (y < 7 && (field[x][y + 1] == 10 || !isSameColor(field[x][y + 1], field[x][y])))
            possibleMoves.add(new int[]{x, y + 1});

        if (y > 0 && (field[x][y - 1] == 10 || !isSameColor(field[x][y - 1], field[x][y])))
            possibleMoves.add(new int[]{x, y - 1});

        if (x < 7 && (field[x + 1][y] == 10 || !isSameColor(field[x + 1][y], field[x][y])))
            possibleMoves.add(new int[]{x + 1, y});

        if (x > 0 && (field[x - 1][y] == 10 || !isSameColor(field[x - 1][y], field[x][y])))
            possibleMoves.add(new int[]{x - 1, y});

        //top right
        if (y > 0 && x < 7 && (field[x + 1][y - 1] == 10 || !isSameColor(field[x + 1][y - 1], field[x][y])))
            possibleMoves.add(new int[]{x + 1, y - 1});
        //top left
        if (y > 0 && x > 0 && (field[x - 1][y - 1] == 10 || !isSameColor(field[x - 1][y - 1], field[x][y])))
            possibleMoves.add(new int[]{x - 1, y - 1});
        //bottom left
        if (y < 7 && x > 0 && (field[x - 1][y + 1] == 10 || !isSameColor(field[x - 1][y + 1], field[x][y])))
            possibleMoves.add(new int[]{x - 1, y + 1});
        //bottom right
        if (y < 7 && x < 7 && (field[x + 1][y + 1] == 10 || !isSameColor(field[x + 1][y + 1], field[x][y])))
            possibleMoves.add(new int[]{x + 1, y + 1});

        return possibleMoves;
    }

    private void nextColor() {
        if (currentColor.equals("WHITE")) {
            currentColor = "BLACK";
        } else {
            currentColor = "WHITE";
        }
        System.out.printf("%s players move", currentColor);
    }

    private String getFiguresColor(int code) {
        return (code < 17 && code > 10) ? "BLACK" : "WHITE";
    }


}
