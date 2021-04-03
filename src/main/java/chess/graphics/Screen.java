package chess.graphics;

import chess.game.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class Screen extends Canvas {

    private final BufferStrategy strategy;

    private final int width;
    private final int height;


    private final Game game = new Game();


    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        JFrame container = new JFrame("Chess");

        JPanel panel = (JPanel) container.getContentPane();
        //panel.addMouseListener(new MouseHandler());
        panel.setPreferredSize(new Dimension(width, height));
        panel.setLayout(null);

        setBounds(0, 0, width, height);
        this.addMouseListener(game.getMouseHandler());
        panel.add(this);
        setIgnoreRepaint(true);

        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        container.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //  container.addMouseListener(new MouseHandler());

        //    requestFocus();

        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public void gameLoop() throws InterruptedException, IOException {
        while (true) {
            Thread.sleep(25);
            draw();
            // wait for input
        }
    }

    private void draw() throws IOException {
        var g = getGameGraphics();
        drawBackground(g);
        drawLetters(g);
        drawChessBoard(g);
        drawSelection(g);
        drawPossibleMoves(g);
        drawChessField(g);
        drawSideBar(g);

        g.dispose(); // ?
        strategy.show();
    }

    private Graphics2D getGameGraphics() {
        return (Graphics2D) strategy.getDrawGraphics();
    }

    private void drawLetters(Graphics2D g) {
        int x = 20, y = 100;
        for (int i = 8; i > 0; i--) {
            g.setColor(new Color(152, 151, 139));
            g.setFont(new Font("O", Font.BOLD, 20));
            g.drawString("" + i, x, y);
            y += 100;
        }
        y = height - 20;
        x = 90;
        for (int i = 97; i < 105; i++) {
            g.setColor(new Color(152, 151, 139));
            g.setFont(new Font("O", Font.BOLD, 20));
            g.drawString("" + (char) i, x, y);
            x += 100;
        }
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(new Color(49, 46, 43));
        g.fillRect(0, 0, width, height);
    }

    private void drawChessBoard(Graphics2D g) {
        int x = 50, y = 50;
        final int cellWidth = 100, cellHeight = 100;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                var startColor = (i % 2 == 0) ? Color.WHITE : new Color(136, 119, 183);
                var nexColor = (startColor.equals(Color.WHITE)) ? new Color(136, 119, 183) : Color.WHITE;
                g.setColor(startColor);
                g.fillRect(x, y, cellWidth, cellHeight);
                g.setColor(nexColor);
                g.fillRect(x + 100, y, cellWidth, cellHeight);
                x += 200;
            }
            y += 100;
            x = 50;
        }
    }

    private void drawChessField(Graphics2D g) throws IOException {
        int x = 50, y = 50;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String image = Game.figuresMap.get(game.field[j][i]);
                if (image != null) drawImage(g, image, x, y);
                x += 100;
            }
            x = 50;
            y += 100;
        }
    }


    private void drawImage(Graphics2D g, String figureName, int x, int y) throws IOException {
        var bi = ImageIO.read(getImageByName(figureName + ".png"));
        g.drawImage(bi, null, x, y);
    }

    private InputStream getImageByName(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream("image/" + name);
    }

    private void drawSelection(Graphics2D g) {
        var isSelected = game.selection.selected;
        if (isSelected) {
            if (game.selection.y % 2 == 0) {
                if (game.selection.x % 2 != 0) {
                    g.setColor(new Color(130, 146, 192));
                } else {
                    g.setColor(new Color(182, 206, 220));
                }
            } else {
                if (game.selection.x % 2 == 0) {
                    g.setColor(new Color(130, 146, 192));
                } else {
                    g.setColor(new Color(182, 206, 220));
                }
            }

            g.fillRect((game.selection.x * 100) + 50, (game.selection.y * 100) + 50, 100, 100);
        }
    }

    private void drawPossibleMoves(Graphics2D g) {
        if (game.selection.selected) {
            var moves = game.selection.possibleMoves;
            moves.forEach(move -> {
                g.setColor(new Color(20, 20, 20, 50));
                if (game.field[move[0]][move[1]] == 10) {
                    g.fillOval((move[0] * 100) + 80, (move[1] * 100) + 80, 35, 35);
                } else {
                    var ring = createRingShape((move[0] * 100) + 100, (move[1] * 100) + 100, 50, 9 );
                    g.fill(ring);
                }

            });
        }
    }

    private static Shape createRingShape(
            double centerX, double centerY, double outerRadius, double thickness)
    {
        Ellipse2D outer = new Ellipse2D.Double(
                centerX - outerRadius,
                centerY - outerRadius,
                outerRadius + outerRadius,
                outerRadius + outerRadius);
        Ellipse2D inner = new Ellipse2D.Double(
                centerX - outerRadius + thickness,
                centerY - outerRadius + thickness,
                outerRadius + outerRadius - thickness - thickness,
                outerRadius + outerRadius - thickness - thickness);
        Area area = new Area(outer);
        area.subtract(new Area(inner));
        return area;
    }

    private void drawSideBar(Graphics2D g) {
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(900, 50, 280, 800, 10, 10);
    }

    static class Button {
        int width;
        int height;
        Consumer onClick;

        public Button(int width, int height, Consumer onClick) {
            this.width = width;
            this.height = height;
        }

        public void draw(Graphics2D g, int x, int y) {

        }
    }

}

