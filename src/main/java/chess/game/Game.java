package chess.game;

import chess.game.chess.ChessBoard;
import chess.game.player.BotPlayer;
import chess.game.player.Player;
import chess.game.player.PlayerType;
import chess.input.MouseHandler;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static chess.game.chess.FigureUtils.*;

public class Game implements Runnable{

    public final Selection selection = new Selection(0, 0, false);
    private final MouseHandler mouseHandler = new MouseHandler();
    public List<Player> players = new ArrayList<>();
    public ChessBoard board;


    public Game() {

        board = new ChessBoard();

        players.add(new Player("Player"));
        players.add(new Player("Player2"));
        //players.add(new BotPlayer2("botPlayer", this));
        //players.add(new BotPlayer2("botPlayer2", this));


        mouseHandler.addOnPressedListener((MouseEvent event) -> {
            if (getCurrentPlayer().type == PlayerType.LOCAL) {
                localPlayerMove(event.getX(), event.getY());
            }
        });
        mouseHandler.addOnReleasedListener((MouseEvent event) -> {
            if (getCurrentPlayer().type == PlayerType.LOCAL) {
                localPlayerMove(event.getX(), event.getY());
                selection.isDragAndDrop = false;
            }
        });
        mouseHandler.addOnDraggedListener((MouseEvent event) -> {
            selection.mouseX = event.getX();
            selection.mouseY = event.getY();
        });
        new Thread(this).start();
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
        return board.currentColor.equals("WHITE") ? players.get(0) : players.get(1);
    }


    private void moveSelectedFigure(Integer cellX, Integer cellY, Integer figureX, Integer figureY) {
        board.moveFigure(cellX, cellY, figureX, figureY, null);
        //TODO
        selection.pawnForPromotion = board.getPawnForPromotion();

        System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(selection.x), nameOfLettersY.get(selection.y), nameOfLettersX.get(cellX), nameOfLettersY.get(cellY));
        selection.selected = false;
        var nextColor = (board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        if (board.isMate()) System.out.println("Mate! Winner: " + nextColor);
    }

    public void localPlayerMove(Integer x, Integer y) {
        // read input (mouse)
        var coords = getCellCoordinates(x, y);
        Integer cellX = coords[0];
        Integer cellY = coords[1];
        var cell = board.getCell(cellX, cellY);

        if (selection.pawnForPromotion != null) {
            // check selected figure
            var figure = getSelectedFigure(selection.pawnForPromotion, cellX, cellY);
            //TODO
            board.promotePawnWithSelectedFigure(figure, selection.pawnForPromotion[0], selection.pawnForPromotion[1]);
            selection.pawnForPromotion = null;
            return;
        }


        if (selection.isDragAndDrop) {
            try {
                selection.possibleMoves.forEach((move) -> {
                    if (move[0].equals(cellX) && move[1].equals(cellY)) {
                        moveSelectedFigure(cellX, cellY, selection.x, selection.y);
                    }
                });

            } finally {
                selection.isDragAndDrop = false;
            }
        } else if (selection.selected) {
            boolean actionMade = false;
            for (int i = 0; i < selection.possibleMoves.size(); i++) {
                var v = selection.possibleMoves.get(i);
                if (cellX.equals(v[0]) && cellY.equals(v[1])) {
                    var selected = board.getCell(selection.x, selection.y);
                    if (!isSameColor(cell, selected)) {
                        if (getFiguresColor(selected).equals(board.currentColor)) {
                            moveSelectedFigure(cellX, cellY, selection.x, selection.y);
                            actionMade = true;
                        }
                    }
                }
            }
            selection.selected = false;

            if (!actionMade && board.getCell(cellX, cellY) != 10) {
                selectFigure(cellX, cellY, cell);
                selection.isDragAndDrop = true;
            } else {
                selection.isDragAndDrop = false;
            }

        } else if (cell != 10) {
            selectFigure(cellX, cellY, cell);
        }

    }

    public void selectFigure(int cellX, int cellY, int cell) {
        selection.x = cellX;
        selection.y = cellY;
        if (getFiguresColor(cell).equals(board.currentColor)) {
            selection.possibleMoves = board.getValidPossibleMoves(selection.x, selection.y);
            selection.isDragAndDrop = true;
        } else {
            selection.possibleMoves = Collections.emptyList();
            selection.isDragAndDrop = false;
        }
        selection.selected = true;
    }

    public Integer[] getCellCoordinates(Integer x, Integer y) {
        int cellX = (x - 50) / 100;
        int cellY = (y - 50) / 100;
        if (x < 850 && x > 50 && y > 50 && y < 850) {
            return new Integer[]{cellX, cellY};
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

    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

}
