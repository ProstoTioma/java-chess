package chess.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private Consumer<MouseEvent> onPressed;
    private Consumer<MouseEvent> onReleased;
    private Consumer<MouseEvent> onDragged;

    public void addOnPressedListener(Consumer<MouseEvent> listener) {
        this.onPressed = listener;
    }

    public void addOnReleasedListener(Consumer<MouseEvent> listener) {
        this.onReleased = listener;
    }

    public void addOnDraggedListener(Consumer<MouseEvent> listener) {
        this.onDragged = listener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onPressed.accept(e);
    }

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
        onDragged.accept(e);
    }
}
