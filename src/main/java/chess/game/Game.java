package chess.game;

import chess.input.MouseHandler;

import java.awt.event.MouseEvent;
import java.util.*;

public class Game {

    public static Map<Integer, String> figuresMap = new HashMap<>();
    public static Map<Integer, String> nameOfLettersX = new HashMap<>();
    public static Map<Integer, String> nameOfLettersY = new HashMap<>();

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

    public final int[][] field = new int[8][8];
    public final Selection selection = new Selection(0, 0, false);
    private final MouseHandler mouseHandler;
    public String currentColor = "WHITE";
    public List<int[]> history;

    public Game() {
        history = new ArrayList<>();
        initField();
        mouseHandler = new MouseHandler((MouseEvent event) -> {
            move(event.getX(), event.getY());
        }, (MouseEvent event) -> {
            var coords = getCellCoordinates(event.getX(), event.getY());
            int cellX = coords[0];
            int cellY = coords[1];

            if (selection.isDragAndDrop) {
                selection.possibleMoves.forEach((move) -> {
                    if (move[0] == cellX && move[1] == cellY) {
                        moveSelectedFigure(cellX, cellY);
                    }
                });

            }
            selection.isDragAndDrop = false;
        }, (MouseEvent event) -> {
            selection.mouseX = event.getX();
            selection.mouseY = event.getY();
        });
    }


    public static void main(String[] args) {
        new Game().printFiled();
    }

    public static int[][] deepCopy(int[][] org) {
        if (org == null) {
            return null;
        }

        final int[][] res = new int[org.length][];
        for (int i = 0; i < org.length; i++) {
            res[i] = Arrays.copyOf(org[i], org[i].length);
        }
        return res;
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
//            System.out.printf("cell coordinates: %d %d \n", cellX, cellY);
            return new int[]{cellX, cellY};
        }
        return null;
    }

    private void moveSelectedFigure(int cellX, int cellY) {
        field[cellX][cellY] = field[selection.x][selection.y];
        field[selection.x][selection.y] = 10;
        nextColor();
        //isCheck(currentColor, field);
        history.add(new int[]{selection.x, selection.y, cellX, cellY});
        System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(selection.x), nameOfLettersY.get(selection.y), nameOfLettersX.get(cellX), nameOfLettersY.get(cellY));
        selection.selected = false;
        var nextColor = (currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        if(isMate()) System.out.println("Mate! Winner: " + nextColor);

    }

    public void move(int x, int y) {
        // read input (mouse)
        var coords = getCellCoordinates(x, y);
        int cellX = coords[0];
        int cellY = coords[1];
        var cell = field[cellX][cellY];

        if (selection.selected) {
            boolean actionMade = false;
            for (int i = 0; i < selection.possibleMoves.size(); i++) {
                var v = selection.possibleMoves.get(i);
                if (cellX == v[0] && cellY == v[1]) {
                    var selected = field[selection.x][selection.y];
                    if (!isSameColor(cell, selected)) {
                        if (getFiguresColor(selected).equals(currentColor)) {
                            moveSelectedFigure(cellX, cellY);
                            actionMade = true;
                        }
                    }
                }
            }
            selection.selected = false;

            if (!actionMade && field[cellX][cellY] != 10) {
                selection.x = cellX;
                selection.y = cellY;
                if (currentColor.equals(getFiguresColor(field[selection.x][selection.y])))
                    selection.possibleMoves = getValidPossibleMoves(selection.x, selection.y, getPossibleMoves(selection.x, selection.y, field));
                else {
                    selection.possibleMoves = Collections.emptyList();
                }
                selection.selected = true;
                selection.isDragAndDrop = true;
            }


        } else if (cell != 10) {
            selection.x = cellX;
            selection.y = cellY;
            if (getFiguresColor(cell).equals(currentColor)) {
                selection.possibleMoves = getValidPossibleMoves(selection.x, selection.y, getPossibleMoves(cellX, cellY, field));
                selection.isDragAndDrop = true;
            } else {
                selection.possibleMoves = Collections.emptyList();
            }
            selection.selected = true;
//            System.out.printf("Selected %d %d\n", selection.x, selection.y);
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

    public ArrayList<int[]> getPossibleMoves(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();
        if (chessField[x][y] == 11 || chessField[x][y] == 21) possibleMoves = getPossibleMovesPawn(x, y, chessField);
        if (chessField[x][y] == 13 || chessField[x][y] == 23) possibleMoves = getPossibleMovesKnight(x, y, chessField);
        if (chessField[x][y] == 14 || chessField[x][y] == 24) possibleMoves = getPossibleMovesBishop(x, y, chessField);
        if (chessField[x][y] == 12 || chessField[x][y] == 22) possibleMoves = getPossibleMovesRook(x, y,chessField);
        if (chessField[x][y] == 15 || chessField[x][y] == 25) possibleMoves = getPossibleMovesQueen(x, y, chessField);
        if (chessField[x][y] == 16 || chessField[x][y] == 26) possibleMoves = getPossibleMovesKing(x, y, chessField);

        return possibleMoves;
    }

    public ArrayList<int[]> getValidPossibleMoves(int x, int y, ArrayList<int[]> possibleMoves) {
        ArrayList<int[]> validMoves = new ArrayList<>();

        possibleMoves.forEach((move) -> {
            int[][] copyField = deepCopy(field);
            copyField[move[0]][move[1]] = copyField[x][y];
            copyField[x][y] = 10;
            var nextColor = (currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
            if (!isCheck(currentColor, copyField)) {
                validMoves.add(move);
            }

        });

        return validMoves;
    }

    public ArrayList<int[]> getPossibleMovesPawn(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();
        if (chessField[x][y] == 11) {
            if (chessField[x][y + 1] == 10) possibleMoves.add(new int[]{x, y + 1});
            if (x > 0 && (!isSameColor(chessField[x - 1][y + 1], chessField[x][y]) && chessField[x - 1][y + 1] != 10))
                possibleMoves.add(new int[]{x - 1, y + 1});
            if (x < 7 && (!isSameColor(chessField[x + 1][y + 1], chessField[x][y]) && chessField[x + 1][y + 1] != 10))
                possibleMoves.add(new int[]{x + 1, y + 1});
            if (y == 1 && chessField[x][y + 1] == 10 && chessField[x][y + 2] == 10) possibleMoves.add(new int[]{x, y + 2});
        } else {
            if (chessField[x][y - 1] == 10) possibleMoves.add(new int[]{x, y - 1});
            if (x > 0 && (!isSameColor(chessField[x - 1][y - 1], chessField[x][y]) && chessField[x - 1][y - 1] != 10))
                possibleMoves.add(new int[]{x - 1, y - 1});
            if (x < 7 && (!isSameColor(chessField[x + 1][y - 1], chessField[x][y]) && chessField[x + 1][y - 1] != 10))
                possibleMoves.add(new int[]{x + 1, y - 1});
            if (y == 6 && chessField[x][y - 1] == 10 && chessField[x][y - 2] == 10) possibleMoves.add(new int[]{x, y - 2});
        }

        return possibleMoves;
    }

    public ArrayList<int[]> getPossibleMovesBishop(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();
        //botom right
        for (int j = x + 1, i = y + 1; i < 8 && j < 8; i++, j++) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;

            } else {
                break;
            }
        }
        //top left
        for (int j = x - 1, i = y - 1; i >= 0 && j >= 0; i--, j--) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;
            } else {
                break;
            }
        }

        //top right
        for (int j = x + 1, i = y - 1; i >= 0 && j < 8; i--, j++) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;

            } else {
                break;
            }
        }

        //bottom left
        for (int j = x - 1, i = y + 1; i < 8 && j >= 0; i++, j--) {
            var nextCell = chessField[j][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{j, i});
            } else if (!isSameColor(nextCell, chessField[x][y])) {
                possibleMoves.add(new int[]{j, i});
                break;

            } else {
                break;
            }
        }

        return possibleMoves;
    }

    public ArrayList<int[]> getPossibleMovesKnight(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();
        if (y + 2 < 8 && x + 1 < 8 && chessField[x + 1][y + 2] == 10) possibleMoves.add(new int[]{x + 1, y + 2});
        else if (y + 2 < 8 && x + 1 < 8 && !isSameColor(chessField[x][y], chessField[x + 1][y + 2]))
            possibleMoves.add(new int[]{x + 1, y + 2});

        if (y + 2 < 8 && x - 1 >= 0 && chessField[x - 1][y + 2] == 10) possibleMoves.add(new int[]{x - 1, y + 2});
        else if (y + 2 < 8 && x - 1 >= 0 && !isSameColor(chessField[x][y], chessField[x - 1][y + 2]))
            possibleMoves.add(new int[]{x - 1, y + 2});

        if (x + 2 < 8 && y + 1 < 8 && chessField[x + 2][y + 1] == 10) possibleMoves.add(new int[]{x + 2, y + 1});
        else if (x + 2 < 8 && y + 1 < 8 && !isSameColor(chessField[x][y], chessField[x + 2][y + 1]))
            possibleMoves.add(new int[]{x + 2, y + 1});
        if (x - 2 >= 0 && y + 1 < 8 && chessField[x - 2][y + 1] == 10) possibleMoves.add(new int[]{x - 2, y + 1});
        else if (x - 2 >= 0 && y + 1 < 8 && !isSameColor(chessField[x][y], chessField[x - 2][y + 1]))
            possibleMoves.add(new int[]{x - 2, y + 1});
        if (y - 2 >= 0 && x + 1 < 8 && chessField[x + 1][y - 2] == 10) possibleMoves.add(new int[]{x + 1, y - 2});
        else if (y - 2 >= 0 && x + 1 < 8 && !isSameColor(chessField[x][y], chessField[x + 1][y - 2]))
            possibleMoves.add(new int[]{x + 1, y - 2});

        if (y - 2 >= 0 && x - 1 >= 0 && chessField[x - 1][y - 2] == 10) possibleMoves.add(new int[]{x - 1, y - 2});
        else if (y - 2 >= 0 && x - 1 >= 0 && !isSameColor(chessField[x][y], chessField[x - 1][y - 2]))
            possibleMoves.add(new int[]{x - 1, y - 2});

        if (y - 1 >= 0 && x + 2 < 8 && chessField[x + 2][y - 1] == 10) possibleMoves.add(new int[]{x + 2, y - 1});
        else if (y - 1 >= 0 && x + 2 < 8 && !isSameColor(chessField[x][y], chessField[x + 2][y - 1]))
            possibleMoves.add(new int[]{x + 2, y - 1});

        if (y - 1 >= 0 && x - 2 >= 0 && chessField[x - 2][y - 1] == 10) possibleMoves.add(new int[]{x - 2, y - 1});
        else if (y - 1 >= 0 && x - 2 >= 0 && !isSameColor(chessField[x][y], chessField[x - 2][y - 1]))
            possibleMoves.add(new int[]{x - 2, y - 1});
        return possibleMoves;
    }

    public ArrayList<int[]> getPossibleMovesRook(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();
        //top
        for (int i = y - 1; i >= 0; i--) {
            var nextCell = chessField[x][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{x, i});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new int[]{x, i});
                break;
            } else {
                break;
            }
        }
        //bot
        for (int i = y + 1; i < 8; i++) {
            var nextCell = chessField[x][i];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{x, i});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new int[]{x, i});
                break;
            } else {
                break;
            }
        }
        //left
        for (int i = x - 1; i >= 0; i--) {
            var nextCell = chessField[i][y];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{i, y});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new int[]{i, y});
                break;
            } else {
                break;
            }
        }
        //right
        for (int i = x + 1; i < 8; i++) {
            var nextCell = chessField[i][y];
            if (nextCell == 10) {
                possibleMoves.add(new int[]{i, y});
            } else if (!isSameColor(chessField[x][y], nextCell)) {
                possibleMoves.add(new int[]{i, y});
                break;
            } else {
                break;
            }
        }
        return possibleMoves;
    }

    public ArrayList<int[]> getPossibleMovesQueen(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();
        possibleMoves.addAll(getPossibleMovesRook(x, y, chessField));
        possibleMoves.addAll(getPossibleMovesBishop(x, y, chessField));
        return possibleMoves;
    }

    public ArrayList<int[]> getPossibleMovesKing(int x, int y, int[][] chessField) {
        var possibleMoves = new ArrayList<int[]>();


            if (y < 7 && (chessField[x][y + 1] == 10 || !isSameColor(chessField[x][y + 1], chessField[x][y])))
                possibleMoves.add(new int[]{x, y + 1});

            if (y > 0 && (chessField[x][y - 1] == 10 || !isSameColor(chessField[x][y - 1], chessField[x][y])))
                possibleMoves.add(new int[]{x, y - 1});

            if (x < 7 && (chessField[x + 1][y] == 10 || !isSameColor(chessField[x + 1][y], chessField[x][y])))
                possibleMoves.add(new int[]{x + 1, y});

            if (x > 0 && (chessField[x - 1][y] == 10 || !isSameColor(chessField[x - 1][y], chessField[x][y])))
                possibleMoves.add(new int[]{x - 1, y});

            //top right
            if (y > 0 && x < 7 && (chessField[x + 1][y - 1] == 10 || !isSameColor(chessField[x + 1][y - 1], chessField[x][y])))
                possibleMoves.add(new int[]{x + 1, y - 1});
            //top left
            if (y > 0 && x > 0 && (chessField[x - 1][y - 1] == 10 || !isSameColor(chessField[x - 1][y - 1], chessField[x][y])))
                possibleMoves.add(new int[]{x - 1, y - 1});
            //bottom left
            if (y < 7 && x > 0 && (chessField[x - 1][y + 1] == 10 || !isSameColor(chessField[x - 1][y + 1], chessField[x][y])))
                possibleMoves.add(new int[]{x - 1, y + 1});
            //bottom right
            if (y < 7 && x < 7 && (chessField[x + 1][y + 1] == 10 || !isSameColor(chessField[x + 1][y + 1], chessField[x][y])))
                possibleMoves.add(new int[]{x + 1, y + 1});

        return possibleMoves;
    }

    private void nextColor() {
        if (currentColor.equals("WHITE")) {
            currentColor = "BLACK";
        } else {
            currentColor = "WHITE";
        }
        //System.out.printf("%s players move\n", currentColor);
    }

    private String getFiguresColor(int code) {
        return (code < 17 && code > 10) ? "BLACK" : "WHITE";
    }

    private boolean isCheck(String color, int[][] chessField) {
        int kingX = 0, kingY = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!getFiguresColor(chessField[j][i]).equals(color)) {
                    var moves = getPossibleMoves(j, i, chessField);
                    for (int[] move : moves) {
                        if (move[0] == kingX && move[1] == kingY) {
                            check = true;
                            //System.out.println("Check " + color);
                        }
                    }
                }
            }
        }

        return check;
    }

    private boolean isMate() {
        boolean mate = true;
        if (isCheck(currentColor, field)) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (currentColor.equals(getFiguresColor(field[j][i]))) {
                        var moves = getValidPossibleMoves(j, i, getPossibleMoves(j, i, field));
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


}
