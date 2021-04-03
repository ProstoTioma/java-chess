package chess.input;

import chess.game.Game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;
import java.util.function.Function;

public class MouseHandler implements MouseListener {

    //private final Game game;
    private Consumer onPressed;

    public MouseHandler(Consumer onPressed) {
        this.onPressed = onPressed;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        game.move(e.getX(), e.getY());
//        System.out.println(e.getX() + " " + e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onPressed.accept(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
