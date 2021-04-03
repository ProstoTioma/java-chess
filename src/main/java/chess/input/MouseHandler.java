package chess.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private Consumer<MouseEvent> onPressed;
    private Consumer<MouseEvent> onReleased;
    private Consumer<MouseEvent> onDragged;

    public MouseHandler(Consumer<MouseEvent> onPressed, Consumer<MouseEvent> onReleased, Consumer<MouseEvent> onDragged) {
        this.onPressed = onPressed;
        this.onReleased = onReleased;
        this.onDragged = onDragged;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onPressed.accept(e);
    }

//    @Override
//    public void mousePressed(MouseEvent e) {
//
//        game.move(e.x, e.y)
//
//    }


    @Override
    public void mouseReleased(MouseEvent e) {
        onReleased.accept(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        onDragged.accept(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
