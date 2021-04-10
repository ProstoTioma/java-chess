package chess.game.bot;

import chess.game.FigureUtils;
import chess.game.Game;

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

        int bestScore = 0;
        Integer[] bestFigure = new Integer[2];
        int[] bestMove = new int[2];
        for (int i = 0; i < figures.size(); i++) {
            var moves = game.getValidPossibleMoves(figures.get(i)[0], figures.get(i)[1], game.getPossibleMoves(figures.get(i)[0], figures.get(i)[1], game.field));
            for (int j = 0; j < moves.size(); j++) {
                int score = FigureUtils.figuresValue.get(game.field[moves.get(j)[0]][moves.get(j)[1]]);
                if (score > bestScore) {
                    bestScore = score;
                    bestFigure = figures.get(i);
                    bestMove = moves.get(j);
                    game.selection.x = bestFigure[0];
                    game.selection.y = bestFigure[1];
                }
            }
        }

        if(bestScore > 0) {
            System.out.printf("Figure from %s%s to %s%s\n", nameOfLettersX.get(game.selection.x), nameOfLettersY.get(game.selection.y), nameOfLettersX.get(bestMove[0]), nameOfLettersY.get(bestMove[1]));
            game.field[bestMove[0]][bestMove[1]] = game.field[bestFigure[0]][bestFigure[1]];
            game.field[bestFigure[0]][bestFigure[1]] = 10;
            game.history.add(new int[]{bestMove[0], bestMove[1], bestFigure[0], bestFigure[1]});
        } else {
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

}
