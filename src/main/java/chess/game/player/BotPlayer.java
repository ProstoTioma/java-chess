package chess.game.player;

import chess.game.Game;
import chess.game.bot.Bot;

public class BotPlayer extends Player {
    Bot bot;

    public BotPlayer(String name, Game g, Bot bot) {
        super(name);
        type = PlayerType.BOT;
        this.bot = bot;
    }

    public void makeMove() {
        bot.makeBotMove();
    }
}
