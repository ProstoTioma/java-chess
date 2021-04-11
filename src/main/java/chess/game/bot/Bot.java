package chess.game.bot;

import chess.game.FigureUtils;
import chess.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static chess.game.FigureUtils.nameOfLettersX;
import static chess.game.FigureUtils.nameOfLettersY;

public class Bot {

    Game game;

    public Bot(Game game) {
        this.game = game;
    }

    public void makeBotMove() throws InterruptedException {
        //Random figure and random move
        var figures = game.getAllFiguresByColor(game.currentColor)
                .stream()
                .filter(figure -> game.getValidPossibleMoves(figure[0], figure[1], game.getPossibleMoves(figure[0], figure[1], game.field)).size() > 0)
                .collect(Collectors.toList());
        //var figures = game.getAllFiguresByColor(game.currentColor);

        int bestScore = -1;
        int bestEnemyScore = -1;
        int bestEnemyScore2 = -1;
        Integer[] bestFigure = new Integer[2];
        int[] bestMove = new int[2];
        for (Integer[] integers : figures) {
            var moves = game.getValidPossibleMoves(integers[0], integers[1], game.getPossibleMoves(integers[0], integers[1], game.field));
            for (int[] move : moves) {
                int score = FigureUtils.figuresValue.get(game.field[move[0]][move[1]]);
                if (score > bestScore) {
                    bestScore = score;
                    bestFigure = integers;
                    bestMove = move;
                    game.selection.x = bestFigure[0];
                    game.selection.y = bestFigure[1];
                }
            }
        }

        //if (bestScore > 0) {
            int[][] copyField = Game.deepCopy(game.field);
            copyField[bestMove[0]][bestMove[1]] = copyField[bestFigure[0]][bestFigure[1]];
            copyField[bestFigure[0]][bestFigure[1]] = 10;
            var nextColor = (game.currentColor.equals("WHITE")) ? "BLACK" : "WHITE";

            var figuresEnemy = game.getAllFiguresByColor(nextColor)
                    .stream()
                    .filter(figure -> game.getValidPossibleMoves(figure[0], figure[1], game.getPossibleMoves(figure[0], figure[1], copyField)).size() > 0)
                    .collect(Collectors.toList());

            for (Integer[] integers : figuresEnemy) {
                var moves = game.getValidPossibleMoves(integers[0], integers[1], game.getPossibleMoves(integers[0], integers[1], copyField));
                for (int[] move : moves) {
                    int score = FigureUtils.figuresValue.get(copyField[move[0]][move[1]]);
                    if (score > bestEnemyScore) {
                        bestEnemyScore = score;
                    }
                }
            }

            if (bestScore >= bestEnemyScore) {
                System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(game.selection.x), nameOfLettersY.get(game.selection.y), nameOfLettersX.get(bestMove[0]), nameOfLettersY.get(bestMove[1]));
                game.field[bestMove[0]][bestMove[1]] = game.field[bestFigure[0]][bestFigure[1]];
                game.field[bestFigure[0]][bestFigure[1]] = 10;
                game.history.add(new int[]{bestMove[0], bestMove[1], bestFigure[0], bestFigure[1]});
                //game.changePawnToQueen(bestMove[0], bestMove[1]);
            } else {
                //найти ход, в котором енеми скор меньше всего
                makeBotRandomMove(figures);
                /*for (Integer[] integers : figures) {
                    var moves = game.getValidPossibleMoves(integers[0], integers[1], game.getPossibleMoves(integers[0], integers[1], game.field));
                    for (int[] move : moves) {
                        copyField[move[0]][move[1]] = copyField[integers[0]][integers[1]];
                        copyField[bestFigure[0]][bestFigure[1]] = 10;

                        for (Integer[] enemyFigure : figuresEnemy) {
                            var movesEnemy = game.getValidPossibleMoves(enemyFigure[0], enemyFigure[1], game.getPossibleMoves(enemyFigure[0], enemyFigure[1], copyField));
                            for (int[] moveEnemy : movesEnemy) {
                                int score = FigureUtils.figuresValue.get(copyField[moveEnemy[0]][moveEnemy[1]]);
                                if (score > bestEnemyScore2) {
                                    bestEnemyScore2 = score;
                                }
                            }
                        }
                        if(bestEnemyScore2 < bestEnemyScore) {
                            bestFigure = integers;
                            bestMove = move;
                            game.selection.x = bestFigure[0];
                            game.selection.y = bestFigure[1];
                        }
                    }
                }*/
            }


        /*} else {
            makeBotRandomMove(figures);
            //game.changePawnToQueen(bestMove[0], bestMove[1]);
        }*/
        game.nextColor();




        /*game.field[randomMove[0]][randomMove[1]] = game.field[selectedFigure[0]][selectedFigure[1]];
        game.field[selectedFigure[0]][selectedFigure[1]] = 10;
        game.history.add(new int[]{randomMove[0], randomMove[1], selectedFigure[0], selectedFigure[1]});
        game.nextColor();*/

        /*var randomFigureIndex = ThreadLocalRandom.current().nextInt(0, figures.size());
        var selectedFigure = figures.get(randomFigureIndex);
        var moves = game.getValidPossibleMoves(selectedFigure[0], selectedFigure[1], game.getPossibleMoves(selectedFigure[0], selectedFigure[1], game.field));
        var randomMoveIndex = ThreadLocalRandom.current().nextInt(0, moves.size());
        var randomMove = moves.get(randomMoveIndex);*/


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
        game.history.add(new int[]{randomMove[0], randomMove[1], selectedFigure[0], selectedFigure[1]});
    }

}
