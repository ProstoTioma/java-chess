package chess.game.bot;

import chess.game.Game;
import chess.game.chess.ChessBoard;
import chess.game.chess.FigureUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static chess.game.chess.FigureUtils.isPawn;

public class Bot1 implements Bot {

    public static int i = 0;

    Game game;
    int deep;

    public Bot1(Game game, int deep) {
        this.game = game;
        this.deep = deep;
    }


    public void makeBotMove() {
        var nextColor = (game.board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        var bestMoveInfo = getBestMove(game.board.copy(), deep);
        i = 0;
        System.out.println("--------------------");
        var bestMove = bestMoveInfo.getKey();

        if (bestMove != null) {
            game.board.moveFigure(bestMove[2], bestMove[3], bestMove[0], bestMove[1], getPromotionCode(nextColor));
        }
    }

    public Map.Entry<Integer[], Integer> getBestMove(ChessBoard board, int deep) {
        var limit = getLimit(deep);
        System.out.println(++i);
        var nextColor = (board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        var movesMap = new HashMap<Integer[], List<Integer[]>>();
        var movesValues = new HashMap<Integer[], Integer>();
        board.getAllFiguresByColor(board.currentColor).forEach(figure -> {
            var moves = board.getValidPossibleMoves(figure[0], figure[1]);
            if (moves.size() > 0) {
                movesMap.put(figure, moves);
            }
        });

        movesMap.forEach((figure, moves) -> {
            for (Integer[] move : moves) {
                Integer score = FigureUtils.figuresValue.get(board.getCell(move[0], move[1]));

                if ((board.field[figure[0]][figure[1]] == 16 || board.field[figure[0]][figure[1]] == 26) && (board.canCastling(false) || board.canCastling(true))
                        && ((move[0] == 2 && (move[1] == 7 || move[1] == 0)) || (move[0] == 6 && (move[1] == 7 || move[1] == 0)))) {
                    if (score == 0) score = 1;
                }


                if (!game.wasCastling(board.currentColor) && (board.field[figure[0]][figure[1]] == 22 || board.field[figure[0]][figure[1]] == 12)) {
                    if (score == 0) score = -1;
                }
                /*else if((board.field[figure[0]][figure[1]] == 12 || board.field[figure[0]][figure[1]] == 22)){
                    if(!board.movesHistory.get(board.movesHistory.size() - 1).isShortCastling() && !board.movesHistory.get(board.movesHistory.size() - 1).isLongCastling())
                    if(score == 0) score = -1;
                }
*/
                if (isPawn(board.getCell(figure[0], figure[1]))) {
                    if (move[1] == 7 || move[1] == 0) {
                        score += 9;
                    }
                }

                //var copyBoard = board.copy();
                board.moveFigure(move[0], move[1], figure[0], figure[1], getPromotionCode(nextColor));

                if (board.isCheck(nextColor, board.field)) {
                    if (score == 0)
                        score = 1;
                }
                if (board.isMate() && this.deep - deep < 2) {
                    score = 100;
                }
                movesValues.put(new Integer[]{figure[0], figure[1], move[0], move[1]}, score);
                board.undo();
            }
        });

        var movesValuesList = movesValues.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Collections.reverse(movesValuesList);
        movesValuesList = movesValuesList.stream().limit(limit).collect(Collectors.toList());

        if (!movesValuesList.isEmpty()) {
            if (deep > 1) {
                for (var moveInfo : movesValuesList) {

                    //var nextMovesBoard = board.copy();
                    var currentBestMove = moveInfo.getKey();

                    if (currentBestMove != null) {
                        board.moveFigure(currentBestMove[2], currentBestMove[3], currentBestMove[0], currentBestMove[1], getPromotionCode(nextColor));
                        var nextMove = getBestMove(board, deep - 1);

//                        if (board.isCheck(nextColor, board.field)) {
//                            if (moveInfo.getValue() == 0) {
//                                moveInfo.setValue(1);
//                            }
//                        }
//                        if (board.isMate()) {
//                            moveInfo.setValue(moveInfo.getValue() + 100);
//                        }
                        if (nextMove != null) {
                            moveInfo.setValue(moveInfo.getValue() - nextMove.getValue());
                        }
                        board.undo();
                    }
                }
            }

            var values = movesValuesList.stream().map(Map.Entry::getValue).sorted().collect(Collectors.toList());
            var bestMovesValuesList = movesValuesList.stream().filter(bm -> bm.getValue().equals(values.get(values.size() - 1))).collect(Collectors.toList());
            var randomBestMoveIndex = ThreadLocalRandom.current().nextInt(0, bestMovesValuesList.size());
            var bestMoveInfo = bestMovesValuesList.get(randomBestMoveIndex);
            return bestMoveInfo;
        } else {
            return null;
        }

    }

    private int getLimit(int deep) {
        if (this.deep - deep == 0) {
            return 50;
        } else if (this.deep - deep == 1) {
            return 50;
        } else if (this.deep - deep == 2) {
            return 50;
        } else if (this.deep - deep == 3) {
            return 10;
        } else if (this.deep - deep == 4) {
            return 8;
        } else {
            return 1;
        }
    }
//
//    private int getLimit(int deep) {
//
//    }

    private boolean onOppositeSide(String color, Integer y) {
        return color.equals("WHITE") ? (y > 5) : (y < 3);
    }

    //always promote to queen
    private Integer getPromotionCode(String color) {
        return color.equals("WHITE") ? 15 : 25;
    }
}
