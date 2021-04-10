package chess.game.player;

public class Player {

    public String name;
    public PlayerType type;

    public Player(String name) {
        this.name = name;
        this.type = PlayerType.LOCAL;
    }

}
