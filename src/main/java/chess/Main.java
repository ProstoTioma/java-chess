package chess;

import chess.graphics.Screen;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        new Screen(1200, 900).gameLoop();
    }
}
