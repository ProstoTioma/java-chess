package chess.game.bot;

import chess.game.chess.FigureUtils;
import chess.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static chess.game.chess.FigureUtils.nameOfLettersX;
import static chess.game.chess.FigureUtils.nameOfLettersY;

public class Bot {

    Game game;

    public Bot(Game game) {
        this.game = game;
    }


    public void makeBotMove() {
        var nextColor = (game.board.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
        var movesMap = new HashMap<Integer[], List<Integer[]>>();
        // Integer[]{x, y, xx, yy}, score
        var bestMovesMap = new HashMap<Integer[], Integer>();
        game.board.getAllFiguresByColor(game.board.currentColor).forEach(figure -> {
            var moves = game.board.getValidPossibleMoves(figure[0], figure[1]);
            if (moves.size() > 0) {
                movesMap.put(figure, moves);
            }
        });
        if (game.board.isMate()) {
            System.out.println("Mate! Winner: " + nextColor);
            return;
        }
        movesMap.forEach((figure, moves) -> {
            Integer[] bestMove = moves.get(0);
            var bestMoveScore = -10;
            for (Integer[] move : moves) {
                Integer score = FigureUtils.figuresValue.get(game.board.getCell(move[0], move[1]));
                var copyBoard = game.board.copy();
                copyBoard.moveFigure(move[0], move[1], figure[0], figure[1], getPromotionCode(nextColor));
                //get enemy moves
                var maxEnemyScore = game.board.getAllFiguresByColor(nextColor)
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
            if (bestMove != null)
                bestMovesMap.put(new Integer[]{figure[0], figure[1], bestMove[0], bestMove[1]}, bestMoveScore);
        });

        var bestMoveList = bestMovesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        var bestMove = bestMoveList.get(bestMoveList.size() - 1).getKey();

        game.board.moveFigure(bestMove[2], bestMove[3], bestMove[0], bestMove[1], getPromotionCode(nextColor));
    }

    //always promote to queen
    private Integer getPromotionCode(String color) {
        return color.equals("WHITE") ? 25 : 15;
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
