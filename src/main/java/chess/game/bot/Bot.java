package chess.game.bot;

import chess.game.Game;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Bot {

    Game game;

    public Bot(Game game) {
        this.game = game;
    }

    //TODO
    public void makeBotMove() {

        //Random figure and random move
        var figures = game.getAllFiguresByColor(game.currentColor)
                .stream()
                .filter(figure -> game.getValidPossibleMoves(figure[0], figure[1], game.getPossibleMoves(figure[0], figure[1], game.field)).size() > 0)
                .collect(Collectors.toList());

        var randomFigureIndex = ThreadLocalRandom.current().nextInt(0, figures.size());
        var selectedFigure = figures.get(randomFigureIndex);
        var moves = game.getValidPossibleMoves(selectedFigure[0], selectedFigure[1], game.getPossibleMoves(selectedFigure[0], selectedFigure[1], game.field));
        var randomMoveIndex = ThreadLocalRandom.current().nextInt(0, moves.size());
        var randomMove = moves.get(randomMoveIndex);

        game.field[randomMove[0]][randomMove[1]] = game.field[selectedFigure[0]][selectedFigure[1]];
        game.field[selectedFigure[0]][selectedFigure[1]] = 10;
        game.history.add(new int[]{randomMove[0], randomMove[1], selectedFigure[0], selectedFigure[1]});
        game.nextColor();
    }

}
