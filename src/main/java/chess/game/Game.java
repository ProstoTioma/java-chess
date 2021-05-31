package chess.game;

import chess.game.bot.Bot1;
import chess.game.bot.Bot2;
import chess.game.bot.Bot3;
import chess.game.chess.ChessBoard;
import chess.game.player.BotPlayer;
import chess.game.player.Player;
import chess.game.player.PlayerType;
import chess.input.MouseHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static chess.game.chess.FigureUtils.*;

public class Game implements Runnable {

    public final Selection selection = new Selection(0, 0, false);
    private final MouseHandler mouseHandler = new MouseHandler();
    public List<Player> players = new ArrayList<>();
    public ChessBoard board;
    public boolean isGameOver = false;
    public int redX;
    public int redY;
    public boolean isRed;


    public Game() {

        board = new ChessBoard();
        game = this;

        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
        /*players.add(new BotPlayer("botPlayer", this, new Bot1(this, 3)));
        players.add(new BotPlayer("botPlayer2", this, new Bot1(this, 3)));*/


        //        players.add(new BotPlayer("botPlayer", this, new Bot1(this, 1)));
//        players.add(new BotPlayer("botPlayer", this, new Bot3(this, 3)));
//        players.add(new Player("Player"));
//        players.add(new BotPlayer("botPlayer2", this, new Bot2(this)));


        mouseHandler.addOnPressedListener((MouseEvent event) -> {
            if (event.getButton() != 3) {
                isRed = false;
                if (event.getX() < 50 && event.getY() < 50) {
                    board.undo();
                    //getBestMove();
                } else if (event.getX() > 850 && event.getY() < 50) {
                    getBestMove(3);
                } else if (event.getX() > 850 && event.getY() > 850) {
                    getBestMove(4);
                } else if (getCurrentPlayer().type == PlayerType.LOCAL) {
                    localPlayerMove(event.getX(), event.getY());
                }
            } else {
                if(event.getX() > 50 && event.getX() < 850 && event.getY() > 50 && event.getY() < 850) {
                    if(isRed && redX == ((getCellCoordinates(event.getX(), event.getY())[0] * 100) + 50) && redY == ((getCellCoordinates(event.getX(), event.getY())[1] * 100) + 50)) {
                        isRed = false;
                    }
                    else {
                        isRed = true;
                        var cell = getCellCoordinates(event.getX(), event.getY());
                        redX = (cell[0] * 100) + 50;
                        redY = (cell[1] * 100) + 50;
                    }

                }
            }
        });
        mouseHandler.addOnReleasedListener((MouseEvent event) -> {
            if (event.getButton() != 3) {
                isRed = false;
                if (getCurrentPlayer().type == PlayerType.LOCAL) {
                    localPlayerMove(event.getX(), event.getY());
                    selection.isDragAndDrop = false;
                }
            }
        });
        mouseHandler.addOnDraggedListener((MouseEvent event) -> {
            selection.mouseX = event.getX();
            selection.mouseY = event.getY();
        });
        new Thread(this).start();
    }

    void getBestMove(int deep) {
        var bestMove = new Bot1(this, deep).getBestMove(board.copy(), deep);
        var move = bestMove.getKey();
        Bot1.i = 0;
        System.out.println(nameOfLettersX.get(move[0]) + nameOfLettersY.get(move[1]) + " " + nameOfLettersX.get(move[2]) + nameOfLettersY.get(move[3]) + " Value: " + bestMove.getValue());
    }

    @Override
    public void run() {
        while (true) {
            try {
                var currentPlayer = getCurrentPlayer();
                if (currentPlayer instanceof BotPlayer) {
                    if (!isGameOver) {
                        ((BotPlayer) currentPlayer).makeMove();
                    }
                }


                if (board.isDraw()) {
                    isGameOver = true;
                    System.out.println("Draw! " + board.history.size() / 2);
                    return;
                }
                if (board.isStaleMate()) {
                    isGameOver = true;
                    System.out.println("Stale Mate! " + board.history.size() / 2);
                    return;
                }

                if (board.isMate()) {
                    isGameOver = true;
                    var nextColor = (board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
                    System.out.printf("Mate! Winner: %s. %d moves", nextColor, board.history.size() / 2);
                    return;
                }
                Thread.sleep(50);
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
        var coords = getCellCoordinates(x, y);
        if (x > 50 && y > 50 && x < 850 && y < 850) {
            Integer cellX = coords[0];
            Integer cellY = coords[1];
            var cell = board.getCell(cellX, cellY);

            if (!isGameOver) {
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
                                if (isPassage && ((board.field[move[0]][(board.history.get(board.history.size() - 1)[3])] == 11 && board.field[selection.x][selection.y] == 21
                                        && move[0].equals(board.history.get(board.history.size() - 1)[2]) && move[1].equals(board.history.get(board.history.size() - 1)[3] - 1))
                                        || (board.field[move[0]][(board.history.get(board.history.size() - 1)[3])] == 21 && board.field[selection.x][selection.y] == 11
                                        && move[0].equals(board.history.get(board.history.size() - 1)[2]) && move[1].equals(board.history.get(board.history.size() - 1)[3] + 1)))) {

                                    game.board.field[board.history.get(board.history.size() - 1)[2]][board.history.get(board.history.size() - 1)[3]] = 10;
                                    isPassage = false;
                                }
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
                                    if (isPassage && ((board.field[cellX][(board.history.get(board.history.size() - 1)[3])] == 11 && board.field[selection.x][selection.y] == 21
                                            && cellX.equals(board.history.get(board.history.size() - 1)[2]) && cellY.equals(board.history.get(board.history.size() - 1)[3] - 1))
                                            || (board.field[cellX][(board.history.get(board.history.size() - 1)[3])] == 21 && board.field[selection.x][selection.y] == 11
                                            && cellX.equals(board.history.get(board.history.size() - 1)[2]) && cellY.equals(board.history.get(board.history.size() - 1)[3] + 1)))) {
                                        game.board.field[board.history.get(board.history.size() - 1)[2]][board.history.get(board.history.size() - 1)[3]] = 10;
                                        isPassage = false;
                                    }
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

    public boolean wasCastling(String color) {
        for (var v : board.movesHistory) {
            if ((color.equals("BLACK") && (v.isLongCastlingBlack() || v.isShortCastlingBlack())) || (color.equals("WHITE") && (v.isLongCastlingWhite() || v.isShortCastlingWhite()))) {
                return true;

            }
        }
        return false;
    }

    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

}
