package chess.game.bot;

import chess.game.Game;
import chess.game.chess.ChessBoard;
import chess.game.chess.FigureUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static chess.game.chess.FigureUtils.*;

public class Bot1 implements Bot {

    Game game;
    int deep;

    public Bot1(Game game, int deep) {
        this.game = game;
        this.deep = deep;
    }

    public void olejBotMove() {

    }


    public void makeBotMove() {
        var nextColor = (game.board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        var bestMoveInfo = getBestMove(game.board, deep);

        var bestMove = bestMoveInfo.getKey();

        if (bestMove != null) {
            game.board.moveFigure(bestMove[2], bestMove[3], bestMove[0], bestMove[1], getPromotionCode(nextColor));
        }
    }

    public Map.Entry<Integer[], Integer> getBestMove(ChessBoard board, int deep) {
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

                if (isPawn(board.getCell(figure[0], figure[1])) && onOppositeSide(nextColor, move[1])) {
                    if (move[1] == 7 || move[1] == 0) {
                        score += 9;
                    }
                }

                var copyBoard = board.copy();
                copyBoard.moveFigure(move[0], move[1], figure[0], figure[1], getPromotionCode(nextColor));

                if (copyBoard.isCheck(nextColor, copyBoard.field)) {
                    if (score == 0)
                        score = 1;
                }
                if (copyBoard.isMate()) {
                    score = 100;
                }

                movesValues.put(new Integer[]{figure[0], figure[1], move[0], move[1]}, score);
            }
        });

        var movesValuesList = movesValues.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

        if (!movesValuesList.isEmpty()) {
            if (deep > 1) {
                for (var moveInfo : movesValuesList) {

                    var nextMovesBoard = board.copy();
                    var currentBestMove = moveInfo.getKey();

                    if (currentBestMove != null) {
                        nextMovesBoard.moveFigure(currentBestMove[2], currentBestMove[3], currentBestMove[0], currentBestMove[1], getPromotionCode(nextColor));
                        var nextMove = getBestMove(nextMovesBoard, deep - 1);

                        if (nextMovesBoard.isCheck(nextColor, nextMovesBoard.field)) {
                            if (moveInfo.getValue() <= 0)
                                moveInfo.setValue(moveInfo.getValue() + 1);
                        }
                        if (nextMovesBoard.isMate()) {
                            moveInfo.setValue(moveInfo.getValue() + 100);
                        }
                        if (nextMove != null) {
                            moveInfo.setValue(moveInfo.getValue() - nextMove.getValue());
                        }
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

    private boolean onOppositeSide(String color, Integer y) {
        return color.equals("WHITE") ? (y > 5) : (y < 3);
    }

    //always promote to queen
    private Integer getPromotionCode(String color) {
        return color.equals("WHITE") ? 15 : 25;
    }
}
