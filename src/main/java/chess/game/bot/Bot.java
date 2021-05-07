package chess.game.bot;

import chess.game.chess.ChessBoard;
import chess.game.chess.FigureUtils;
import chess.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static chess.game.chess.FigureUtils.*;

public class Bot {

    Game game;

    public Bot(Game game) {
        this.game = game;
    }

    public void olejBotMove() {

    }


    public void makeBotMove() {
        var nextColor = (game.board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        var bestMoveInfo = getBestMove(game.board,  1);

        var bestMove = bestMoveInfo.getKey();

        if (bestMove != null) {
            game.board.moveFigure(bestMove[2], bestMove[3], bestMove[0], bestMove[1], getPromotionCode(nextColor));
        }
    }

    public Map.Entry<Integer[], Integer> getBestMove(ChessBoard board, int deep) {
        var nextColor = (board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        var movesMap = new HashMap<Integer[], List<Integer[]>>();
        // Integer[]{x, y, xx, yy}, score
        var bestMovesMap = new HashMap<Integer[], Integer>();
        board.getAllFiguresByColor(board.currentColor).forEach(figure -> {
            var moves = board.getValidPossibleMoves(figure[0], figure[1]);
            if (moves.size() > 0) {
                movesMap.put(figure, moves);
            }
        });
        if (board.isMate()) {
            System.out.println("Mate! Winner: " + nextColor);
            return null;
        }
        movesMap.forEach((figure, moves) -> {
            Integer[] bestMove = null;
            var bestMoveScore = -10;
            for (Integer[] move : moves) {
                Integer score = FigureUtils.figuresValue.get(board.getCell(move[0], move[1]));

                if (isPawn(board.getCell(figure[0], figure[1])) && onOppositeSide(nextColor, move[1])) {
//                    score += 1;
                    if (move[1] == 7 || move[1] == 0) {
                        score += 9;
                    }
                }
                var copyBoard = board.copy();
                copyBoard.moveFigure(move[0], move[1], figure[0], figure[1], getPromotionCode(nextColor));
                //get enemy moves
                var maxEnemyScore = copyBoard.getAllFiguresByColor(nextColor)
                        .stream().map(enemyFigure -> {
                            var enemyMoves = copyBoard.getValidPossibleMoves(enemyFigure[0], enemyFigure[1]);
                            if (enemyMoves.size() > 0) {
                                var max = enemyMoves.stream()
                                        .map(em -> FigureUtils.figuresValue.get(copyBoard.getCell(em[0], em[1])))
                                        .max(Integer::compareTo).get();
                                return max;
                            }
                            return 0;
                        }).max(Integer::compareTo).get();
                //TODO pawn to queen priority && castling && check && mate
                //TODO don't move a king w/out reason

                if (copyBoard.isCheck(nextColor, copyBoard.field)) {
                    if(score <= 0)
                    score = 1;
                }
                if (copyBoard.isMate()) {
                    score = 100;
                }
                var totalScoreOfMove = score - maxEnemyScore;
                if (totalScoreOfMove >= bestMoveScore) {
                    bestMove = move;
                    bestMoveScore = totalScoreOfMove;
                }
            }
            if (bestMove != null) {
                bestMovesMap.put(new Integer[]{figure[0], figure[1], bestMove[0], bestMove[1]}, bestMoveScore);
            }
        });

        var bestMoveList = bestMovesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

        var minvalue = bestMoveList.get(bestMoveList.size() - 1).getValue();
        bestMoveList = bestMoveList.stream().filter(bm -> bm.getValue().equals(minvalue)).collect(Collectors.toList());



        var randomBestMoveIndex =  ThreadLocalRandom.current().nextInt(0, bestMoveList.size());


        var bestMoveInfo = bestMoveList.get(randomBestMoveIndex);
        return bestMoveInfo;
    }

    private boolean onOppositeSide(String color, Integer y) {
        return color.equals("WHITE") ?  (y > 5) : (y < 3);
    }

    //always promote to queen
    private Integer getPromotionCode(String color) {
        return color.equals("WHITE") ? 15 : 25;
    }

    public void makeBotRandomMove() {
        var figures = game.board.getAllFiguresByColor(game.board.currentColor).stream().filter(f -> game.board.getValidPossibleMoves(f[0], f[1]).size() > 0).collect(Collectors.toList());
        if (figures.size() > 0) {
            var randomFigureIndex = ThreadLocalRandom.current().nextInt(0, figures.size());
            var selectedFigure = figures.get(randomFigureIndex);
            var moves = game.board.getValidPossibleMoves(selectedFigure[0], selectedFigure[1]);
            var randomMoveIndex = ThreadLocalRandom.current().nextInt(0, moves.size());
            var randomMove = moves.get(randomMoveIndex);
            game.selection.x = selectedFigure[0];
            game.selection.y = selectedFigure[1];
            System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(game.selection.x), nameOfLettersY.get(game.selection.y), nameOfLettersX.get(randomMove[0]), nameOfLettersY.get(randomMove[1]));

            game.board.moveFigure(randomMove[0], randomMove[1], selectedFigure[0], selectedFigure[1], getPromotionCode(game.board.currentColor));
        } else {
            System.out.println("no moves found");
        }

    }
}
