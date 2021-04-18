package chess.game.player;

import chess.game.Game;
import chess.game.bot.Bot;

public class BotPlayer extends Player {
    Bot bot;

    public BotPlayer(String name, Game g) {
        super(name);
        type = PlayerType.BOT;
        bot = new Bot(g);
    }

    public void makeMove() {
        bot.makeBotMove();
    }
}
