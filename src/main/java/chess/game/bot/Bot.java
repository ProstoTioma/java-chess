package chess.game.bot;

import chess.game.FigureUtils;
import chess.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static chess.game.FigureUtils.nameOfLettersX;
import static chess.game.FigureUtils.nameOfLettersY;

public class Bot {

    Game game;

    public Bot(Game game) {
        this.game = game;
    }

    public void makeBotMove() {
        var movesMap = new HashMap<Integer[], ArrayList<Integer[]>>();
        // Integer[]{x, y, xx, yy}, score
        var bestMovesMap = new HashMap<Integer[], Integer>();
        game.getAllFiguresByColor(game.currentColor)
                .stream().forEach(figure -> {
            var moves = game.getValidPossibleMoves(figure[0], figure[1], game.getPossibleMoves(figure[0], figure[1], game.field));
            if (moves.size() > 0) {
                movesMap.put(figure, moves);
            }
        });
        movesMap.forEach((figure, moves) -> {
            Integer[] bestMove = moves.get(0);
            var bestMoveScore = -10;
            for (Integer[] move : moves) {
                Integer score = FigureUtils.figuresValue.get(game.field[move[0]][move[1]]);
                Integer[][] copyField = Game.deepCopy(game.field);
                copyField[move[0]][move[1]] = copyField[figure[0]][figure[1]];
                copyField[figure[0]][figure[1]] = 10;
                var nextColor = (game.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";
                //get enemy moves
                //var enemyMovesMap = new HashMap<Integer[], ArrayList<Integer[]>>();
                var maxEnemyScore = game.getAllFiguresByColor(nextColor)
                        .stream().map(enemyFigure -> {
                            var enemyMoves = game.getValidPossibleMoves(enemyFigure[0], enemyFigure[1], game.getPossibleMoves(enemyFigure[0], enemyFigure[1], copyField));
                            if (enemyMoves.size() > 0) {
                                var max = enemyMoves.stream()
                                        .map(em -> FigureUtils.figuresValue.get(copyField[em[0]][em[1]]))
                                        .max(Integer::compareTo).get();
                                return max;

                            }
                            return 0;
                        }).max(Integer::compareTo).get();
                //TODO pawn to queen priority && castling && check && mate
                //TODO don't move a king w/out reason
                //NEW HERE:
                if(score == 0 && (copyField[figure[0]][figure[1]] == 16 || copyField[figure[0]][figure[1]] == 26) && maxEnemyScore == 0) continue;
                if(game.isCheck(nextColor, copyField)) {
                    score = 2;
                }
                if(game.isMate(nextColor, copyField)) {
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


        game.field[bestMove[2]][bestMove[3]] = game.field[bestMove[0]][bestMove[1]];
        game.field[bestMove[0]][bestMove[1]] = 10;
        game.changePawnToQueen(bestMove[2], bestMove[3]);
        game.history.add(bestMove);
        game.nextColor();
    }

    private void makeBotRandomMove(List<Integer[]> figures) {
        var randomFigureIndex = ThreadLocalRandom.current().nextInt(0, figures.size());
        var selectedFigure = figures.get(randomFigureIndex);
        var moves = game.getValidPossibleMoves(selectedFigure[0], selectedFigure[1], game.getPossibleMoves(selectedFigure[0], selectedFigure[1], game.field));
        var randomMoveIndex = ThreadLocalRandom.current().nextInt(0, moves.size());
        var randomMove = moves.get(randomMoveIndex);
        game.selection.x = selectedFigure[0];
        game.selection.y = selectedFigure[1];
        System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(game.selection.x), nameOfLettersY.get(game.selection.y), nameOfLettersX.get(randomMove[0]), nameOfLettersY.get(randomMove[1]));
        game.field[randomMove[0]][randomMove[1]] = game.field[selectedFigure[0]][selectedFigure[1]];
        game.field[selectedFigure[0]][selectedFigure[1]] = 10;
        game.history.add(new Integer[]{randomMove[0], randomMove[1], selectedFigure[0], selectedFigure[1]});
    }

}
