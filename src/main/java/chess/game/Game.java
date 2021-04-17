package chess.game;

import chess.game.player.BotPlayer;
import chess.game.player.Player;
import chess.game.player.PlayerType;
import chess.input.MouseHandler;

import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

import static chess.game.FigureUtils.*;

public class Game implements Runnable{

    public final Integer[][] field = new Integer[8][8];
    public final Selection selection = new Selection(0, 0, false);
    private final MouseHandler mouseHandler = new MouseHandler();
    public String currentColor = "WHITE";
    public List<Integer[]> history = new ArrayList<>();
    public List<Player> players = new ArrayList<>();


    public Game() {
        initField();

        players.add(new Player("Player"));
        players.add(new Player("Player2"));
        //players.add(new BotPlayer("botPlayer", this));
        //players.add(new BotPlayer("botPlayer2", this));


        mouseHandler.addOnPressedListener((MouseEvent event) -> {
            if (getCurrentPlayer().type == PlayerType.LOCAL) {
                localPlayerMove(event.getX(), event.getY());
            }
        });
        mouseHandler.addOnReleasedListener((MouseEvent event) -> {
            if (getCurrentPlayer().type == PlayerType.LOCAL) {
                localPlayerMove(event.getX(), event.getY());
            }
        });
        mouseHandler.addOnDraggedListener((MouseEvent event) -> {
            selection.mouseX = event.getX();
            selection.mouseY = event.getY();
        });
        new Thread(this).start();
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

    @Override
    public void run() {
        while (true) {
            try {
                var currentPlayer = getCurrentPlayer();
                if (currentPlayer instanceof BotPlayer) {
                    ((BotPlayer) currentPlayer).makeMove();
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Player getCurrentPlayer() {
        return currentColor.equals("WHITE") ? players.get(0) : players.get(1);
    }

    private void initField() {
        // 11/21 - pawn
        // 12/22 - rook
        // 13/23 - knight
        // 14/24 - bishop
        // 15/25 - queen
        // 16/26 - king
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
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

    private void moveSelectedFigure(Integer cellX, Integer cellY) {
        field[cellX][cellY] = field[selection.x][selection.y];
        if (isLongCastlingMove(cellX)) {
            field[cellX + 1][cellY] = field[0][cellY];
            field[0][cellY] = 10;
        } else if (isShortCastlingMove(cellX)) {
            field[cellX - 1][cellY] = field[7][cellY];
            field[7][cellY] = 10;
        }
        field[selection.x][selection.y] = 10;

        selection.pawnForPromotion = getPawnForPromotion(field);

        nextColor();
        //isCheck(currentColor, field);
        history.add(new Integer[]{selection.x, selection.y, cellX, cellY});
        System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(selection.x), nameOfLettersY.get(selection.y), nameOfLettersX.get(cellX), nameOfLettersY.get(cellY));
        selection.selected = false;
        var nextColor = (currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        if (isMate(currentColor, field)) System.out.println("Mate! Winner: " + nextColor);

    }

    public void localPlayerMove(Integer x, Integer y) {
        // read input (mouse)
        var coords = getCellCoordinates(x, y);
        Integer cellX = coords[0];
        Integer cellY = coords[1];
        var cell = field[cellX][cellY];

        if (selection.pawnForPromotion != null) {
            // check selected figure
            var figure = getSelectedFigure(selection.pawnForPromotion, cellX, cellY);
            field[selection.pawnForPromotion[0]][selection.pawnForPromotion[1]] = figure;
            selection.pawnForPromotion = null;
            return;
        }


        if (selection.isDragAndDrop) {
            try {
                selection.possibleMoves.forEach((move) -> {
                    if (move[0] == cellX && move[1] == cellY) {
                        moveSelectedFigure(cellX, cellY);

                    }
                });

            } finally {
                selection.isDragAndDrop = false;
            }
        } else if (selection.selected) {
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
                if (currentColor.equals(getFiguresColor(field[selection.x][selection.y]))) {
                    selection.possibleMoves = getValidPossibleMoves(selection.x, selection.y, getPossibleMoves(selection.x, selection.y, field));
                    if ((cell == 16 || cell == 26) && canCastling(false)) {
                        validateLongCastling();
                    }
                    if ((cell == 16 || cell == 26) && canCastling(true)) {
                        validateShortCastling();
                    }
                } else {
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
                if ((cell == 16 || cell == 26) && canCastling(false)) {
                    validateLongCastling();
                }
                if ((cell == 16 || cell == 26) && canCastling(true)) {
                    validateShortCastling();
                }
                selection.isDragAndDrop = true;
            } else {
                selection.possibleMoves = Collections.emptyList();
            }
            selection.selected = true;
        }

    }

    public List<Integer[]> getAllFiguresByColor(String color) {
        var figuresList = new ArrayList<Integer[]>();
        for (Integer i = 0; i < 8; i++) {
            for (Integer j = 0; j < 8; j++) {
                var figure = field[j][i];
                if (getFiguresColor(figure).equals(color)) {
                    figuresList.add(new Integer[]{j, i});
                }
            }
        }
        return figuresList;
    }

    public static String getFiguresColor(Integer code) {
        if(code < 17 && code > 10) return "BLACK";
        else if(code > 17) return "WHITE";
        else return "VOID";
    }

    public Integer[] getCellCoordinates(Integer x, Integer y) {
        Integer cellX = (x - 50) / 100;
        Integer cellY = (y - 50) / 100;
        if (x < 850 && x > 50 && y > 50 && y < 850) {
//            System.out.printf("cell coordinates: %d %d \n", cellX, cellY);
            return new Integer[]{cellX, cellY};
        }
        return null;
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

    public ArrayList<Integer[]> getValidPossibleMoves(Integer x, Integer y, ArrayList<Integer[]> possibleMoves) {
        ArrayList<Integer[]> validMoves = new ArrayList<>();

        possibleMoves.forEach((move) -> {
            Integer[][] copyField = deepCopy(field);
            copyField[move[0]][move[1]] = copyField[x][y];
            copyField[x][y] = 10;
            //var nextColor = (currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
            if (!isCheck(currentColor, copyField)) {
                validMoves.add(move);
            }

        });

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

    public boolean isMate(String color, Integer[][] field) {
        boolean mate = true;
        if (isCheck(color, field)) {
            for (Integer i = 0; i < 8; i++) {
                for (Integer j = 0; j < 8; j++) {
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

    private boolean canCastling(boolean isShort) {
        boolean kingMoved = false;
        boolean rockMoved = false;
        Integer y = (currentColor.equals("WHITE")) ? 7 : 0;
        Integer rockX = (isShort) ? 7 : 0;
        for (Integer[] entry : history) {
            if (entry[0] == 4 && entry[1] == y) {
                kingMoved = true;
            } else if (entry[0] == rockX && entry[1] == y) {
                rockMoved = true;
            }
        }
        if (!isShort) {
            return !kingMoved && !rockMoved && field[1][y] == 10 && field[2][y] == 10 && field[3][y] == 10;
        } else {
            return !kingMoved && !rockMoved && field[5][y] == 10 && field[6][y] == 10;
        }
    }

    private boolean isLongCastlingMove(Integer x) {
        if (field[selection.x][selection.y] == 16 || field[selection.x][selection.y] == 26) {
            return selection.x - x == 2;
        }
        return false;
    }

    private boolean isShortCastlingMove(Integer x) {
        if (field[selection.x][selection.y] == 16 || field[selection.x][selection.y] == 26) {
            return selection.x - x == -2;
        }
        return false;
    }

    private void validateLongCastling() {
        Integer y = (currentColor.equals("WHITE")) ? 7 : 0;
        if (isCheck(currentColor, field)) {
            selection.possibleMoves = selection.possibleMoves.stream().filter(m -> !(m[0] == 2 && m[1] == y)).collect(Collectors.toList());
        }
        var copy = deepCopy(field);
        copy[3][y] = copy[4][y];
        copy[4][y] = 10;
        if (isCheck(currentColor, copy)) {
            selection.possibleMoves = selection.possibleMoves.stream().filter(m -> !(m[0] == 2 && m[1] == y)).collect(Collectors.toList());
        }
    }

    private void validateShortCastling() {
        int y = (currentColor.equals("WHITE")) ? 7 : 0;
        if (isCheck(currentColor, field)) {
            selection.possibleMoves = selection.possibleMoves.stream().filter(m -> !(m[0] == 6 && m[1] == y)).collect(Collectors.toList());
        }
        var copy = deepCopy(field);
        copy[5][y] = copy[4][y];
        copy[4][y] = 10;
        if (isCheck(currentColor, copy)) {
            selection.possibleMoves = selection.possibleMoves.stream().filter(m -> !(m[0] == 6 && m[1] == y)).collect(Collectors.toList());
        }
    }

    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public void changePawnToQueen(Integer x, Integer y) {
        int min, max;
        if (field[x][y] == 21 && y == 0) {
            min = 22;
            max = 25;
            int randomFigure = (int) Math.floor(Math.random() * (max - min + 1) + min);
            field[x][y] = randomFigure;

        } else if (field[x][y] == 11 && y == 7) {
            min = 12;
            max = 15;
            int randomFigure = (int) Math.floor(Math.random() * (max - min + 1) + min);
            field[x][y] = randomFigure;
        }
    }

    public Integer[] getPawnForPromotion(Integer[][] chessField) {
        int pawnCode = (currentColor.equals("WHITE")) ? 21 : 11;
        int y = (currentColor.equals("WHITE")) ? 0 : 7;
        for (Integer[] figure:  getAllFiguresByColor(currentColor)) {
            if(chessField[figure[0]][figure[1]] == pawnCode && figure[1] == y) {
                return figure;
            }
        }
        return null;
    }

    public Integer getSelectedFigure(Integer[] pawn, int x, int y) {
        if (pawn[0] == x) {
            switch (y) {
                case 0:
                    return 25;
                case 1:
                    return 23;
                case 2:
                    return 22;
                case 3:
                    return 24;
                case 7:
                    return 15;
                case 6:
                    return 13;
                case 5:
                    return 12;
                case 4:
                    return 14;
            }
        }
        return null;
    }


    public void printFiled() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.printf(" %d ", field[j][i]);
            }

            System.out.println();
        }
    }


}
