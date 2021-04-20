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
import java.util.HashMap;
import java.util.Map;

import static chess.game.chess.FigureUtils.figuresMap;

public class Screen extends Canvas {

    private final BufferStrategy strategy;

    private final Integer width;
    private final Integer height;

    public static Map<String, BufferedImage> imageMap = new HashMap<>();

    private final Game game = new Game();

    static {
        figuresMap.forEach((k, v) -> {
            try {
                imageMap.put(v, ImageIO.read(getImageByName(v + ".png")));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }


    public Screen(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        JFrame container = new JFrame("Chess");

        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(width, height));
        panel.setLayout(null);

        setBounds(0, 0, width, height);
        this.addMouseListener(game.getMouseHandler());
        this.addMouseMotionListener(game.getMouseHandler());
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

        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    public static BufferedImage resize(BufferedImage img, Integer newW, Integer newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static Shape createRingShape(
            double centerX, double centerY, double outerRadius, double thickness) {
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

    public void gameLoop() throws InterruptedException, IOException {
        while (true) {
            Thread.sleep(20);
            draw();
        }
    }

    private void draw() {
        var g = getGameGraphics();

        drawBackground(g);
        drawLetters(g);
        drawChessBoard(g);
        drawSelection(g);
        highlightLastMove(g);
        drawPossibleMoves(g);
        drawCellBorder(g);
        drawChessField(g);
        //drawSideBar(g);
        drawDragAndDrop(g);

        drawFigureSelectionPopup(g);

        g.dispose();
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

    private void drawChessField(Graphics2D g) {
        int x = 50, y = 50;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String image = figuresMap.get(game.board.getCell(j, i));
                if (image != null) {
                    if (!(game.selection.isDragAndDrop && j == game.selection.x && i == game.selection.y)) {
                        drawImage(g, image, x, y);
                    }
                }
                x += 100;
            }
            x = 50;
            y += 100;
        }
    }

    private void drawImage(Graphics2D g, String figureName, Integer x, Integer y) {
        var bi = imageMap.get(figureName);
        g.drawImage(bi, null, x, y);
    }

    private static InputStream getImageByName(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream("image/" + name);
    }

    private void drawSelection(Graphics2D g) {
        var isSelected = game.selection.selected;
        if (isSelected) {
            highlightCell(g, game.selection.x, game.selection.y);
        }
    }

    private void drawPossibleMoves(Graphics2D g) {
        if (game.selection.selected) {
            var moves = game.selection.possibleMoves;
            moves.forEach(move -> {
                g.setColor(new Color(20, 20, 20, 50));
                if (game.board.getCell(move[0], move[1]) == 10) {
                    g.fillOval((move[0] * 100) + 80, (move[1] * 100) + 80, 35, 35);
                } else {
                    var ring = createRingShape((move[0] * 100) + 100, (move[1] * 100) + 100, 50, 9);
                    g.fill(ring);
                }

            });
        }
    }

    private void drawSideBar(Graphics2D g) {
        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(900, 50, 280, 800, 10, 10);
    }


    private void drawDragAndDrop(Graphics2D g) {
        if (game.selection.isDragAndDrop) {
            var image = figuresMap.get(game.board.getCell(game.selection.x, game.selection.y));
            if (image != null) drawImage(g, image, game.selection.mouseX - 50, game.selection.mouseY - 50);
        }
    }

    private void drawCellBorder(Graphics2D g) {
        var stroke = new BasicStroke(7);
        g.setStroke(stroke);
        g.setColor(new Color(40, 40, 40, 40));
        if ((game.selection.isDragAndDrop)) {
            var coords = game.getCellCoordinates(game.selection.mouseX, game.selection.mouseY);
            if (coords != null) g.drawRoundRect((coords[0] * 100) + 50, (coords[1] * 100) + 50, 100, 100, 5, 5);
        }
    }

    private void highlightCell(Graphics2D g, Integer x, Integer y) {
        if (y % 2 == 0) {
            if (x % 2 != 0) {
                g.setColor(new Color(130, 146, 192));
            } else {
                g.setColor(new Color(182, 206, 220));
            }
        } else {
            if (x % 2 == 0) {
                g.setColor(new Color(130, 146, 192));
            } else {
                g.setColor(new Color(182, 206, 220));
            }
        }
        g.fillRect((x * 100) + 50, (y * 100) + 50, 100, 100);
    }

    private void highlightLastMove(Graphics2D g) {
        if (!game.board.history.isEmpty()) {
            var lastMove = game.board.history.get(game.board.history.size() - 1);
            highlightCell(g, lastMove[0], lastMove[1]);
            highlightCell(g, lastMove[2], lastMove[3]);

        }
    }

    private void drawFigureSelectionPopup(Graphics2D g) {
        var pawn = game.selection.pawnForPromotion;
        if (pawn != null) {
            if(pawn[1] == 0) {
                g.setColor(Color.WHITE);
                g.fillRoundRect((pawn[0] * 100) + 50, 50, 100, 400, 10, 10);
                drawImage(g, "wq", (pawn[0] * 100) + 50, 50);
                drawImage(g, "wn", (pawn[0] * 100) + 50, 150);
                drawImage(g, "wr", (pawn[0] * 100) + 50, 250);
                drawImage(g, "wb", (pawn[0] * 100) + 50, 350);
            } else if(pawn[1] == 7) {
                g.setColor(Color.WHITE);
                g.fillRoundRect((pawn[0] * 100) + 50, 450, 100, 400, 10, 10);
                drawImage(g, "bb", (pawn[0] * 100) + 50, 450);
                drawImage(g, "br", (pawn[0] * 100) + 50, 550);
                drawImage(g, "bn", (pawn[0] * 100) + 50, 650);
                drawImage(g, "bq", (pawn[0] * 100) + 50, 750);
            }
        }
    }

}

