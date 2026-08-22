import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 30;
    private static final int SIDE_PANEL_WIDTH = 160;

    private final Board board = new Board();
    private Tetromino current;
    private int nextType;

    private int score = 0;
    private int linesCleared = 0;
    private int level = 1;
    private boolean gameOver = false;
    private boolean paused = false;

    private boolean spaceHeld = false;
    private boolean pHeld = false;

    private final Timer timer;
    private final Random random = new Random();

    public GamePanel() {
        setPreferredSize(new Dimension(Board.WIDTH * TILE_SIZE + SIDE_PANEL_WIDTH, Board.HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e.getKeyCode());
            }
        });

        initGame();

        timer = new Timer(currentSpeedMs(), this);
        timer.start();
    }

    private int currentSpeedMs() {
        return Math.max(100, 500 - (level - 1) * 40);
    }

    private void initGame() {
        board.clear();
        score = 0;
        linesCleared = 0;
        level = 1;
        gameOver = false;
        paused = false;

        nextType = random.nextInt(Tetromino.TYPE_COUNT);
        spawnPiece();
    }

    private void spawnPiece() {
        int type = nextType;
        nextType = random.nextInt(Tetromino.TYPE_COUNT);

        int startX = Board.WIDTH / 2 - 2;
        current = new Tetromino(type, startX, 0);

        if (board.collides(current.getType(), current.getRotation(), current.getX(), current.getY())) {
            gameOver = true;
            timer.stop();
        }
    }

    private boolean tryMoveDown() {
        if (!board.collides(current.getType(), current.getRotation(), current.getX(), current.getY() + 1)) {
            current.moveBy(0, 1);
            return true;
        }
        return false;
    }

    private void lockPieceAndContinue() {
        boolean lockedAboveBoard = board.lock(current);
        applyLineClearScore(board.clearFullLines());

        if (lockedAboveBoard) {
            gameOver = true;
            timer.stop();
            return;
        }

        spawnPiece();
    }

    private void applyLineClearScore(int cleared) {
        if (cleared <= 0) return;

        linesCleared += cleared;
        int[] lineScores = {0, 100, 300, 500, 800};
        score += lineScores[Math.min(cleared, 4)] * level;

        int newLevel = linesCleared / 10 + 1;
        if (newLevel != level) {
            level = newLevel;
            timer.setDelay(currentSpeedMs());
        }
    }

    private void hardDrop() {
        int cellsDropped = 0;
        while (!board.collides(current.getType(), current.getRotation(), current.getX(), current.getY() + 1)) {
            current.moveBy(0, 1);
            cellsDropped++;
        }
        score += cellsDropped * 2;
        lockPieceAndContinue();
    }

    private void rotate() {
        int newRotation = current.nextRotation();

        int[] kicks = {0, -1, 1, -2, 2};
        for (int kick : kicks) {
            if (!board.collides(current.getType(), newRotation, current.getX() + kick, current.getY())) {
                current.setRotation(newRotation);
                current.moveBy(kick, 0);
                return;
            }
        }
    }

    private void handleKeyPress(int keyCode) {
        if (gameOver) {
            if (keyCode == KeyEvent.VK_SPACE && !spaceHeld) {
                spaceHeld = true;
                initGame();
                timer.setDelay(currentSpeedMs());
                timer.start();
            }
            return;
        }

        if (keyCode == KeyEvent.VK_P) {
            if (!pHeld) {
                pHeld = true;
                paused = !paused;
            }
            return;
        }
        if (paused) return;

        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                if (!board.collides(current.getType(), current.getRotation(), current.getX() - 1, current.getY())) {
                    current.moveBy(-1, 0);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (!board.collides(current.getType(), current.getRotation(), current.getX() + 1, current.getY())) {
                    current.moveBy(1, 0);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (tryMoveDown()) score += 1;
                break;
            case KeyEvent.VK_UP:
                rotate();
                break;
            case KeyEvent.VK_SPACE:
                if (!spaceHeld) {
                    spaceHeld = true;
                    hardDrop();
                }
                break;
        }
        repaint();
    }

    private void handleKeyRelease(int keyCode) {
        if (keyCode == KeyEvent.VK_SPACE) {
            spaceHeld = false;
        } else if (keyCode == KeyEvent.VK_P) {
            pHeld = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || paused) return;

        if (!tryMoveDown()) {
            lockPieceAndContinue();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawBoard(g2d);
        drawCurrentPiece(g2d);
        drawSidePanel(g2d);

        if (paused && !gameOver) {
            drawCenteredMessage(g2d, "PAUSED", "Press P to continue");
        }
        if (gameOver) {
            drawCenteredMessage(g2d, "GAME OVER", "Press SPACE to restart");
        }
    }

    private void drawBoard(Graphics2D g2d) {
        g2d.setColor(new Color(40, 40, 40));
        for (int c = 0; c <= Board.WIDTH; c++) {
            g2d.drawLine(c * TILE_SIZE, 0, c * TILE_SIZE, Board.HEIGHT * TILE_SIZE);
        }
        for (int r = 0; r <= Board.HEIGHT; r++) {
            g2d.drawLine(0, r * TILE_SIZE, Board.WIDTH * TILE_SIZE, r * TILE_SIZE);
        }

        for (int r = 0; r < Board.HEIGHT; r++) {
            for (int c = 0; c < Board.WIDTH; c++) {
                int cell = board.getCell(r, c);
                if (cell != 0) {
                    drawTile(g2d, c, r, Tetromino.COLORS[cell - 1]);
                }
            }
        }
    }

    private void drawCurrentPiece(Graphics2D g2d) {
        if (gameOver) return;
        String shape = current.getShape();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape.charAt(r * 4 + c) == '1') {
                    int boardR = current.getY() + r;
                    int boardC = current.getX() + c;
                    if (boardR >= 0) {
                        drawTile(g2d, boardC, boardR, current.getColor());
                    }
                }
            }
        }
    }

    private void drawTile(Graphics2D g2d, int col, int row, Color color) {
        int x = col * TILE_SIZE;
        int y = row * TILE_SIZE;
        g2d.setColor(color);
        g2d.fillRect(x, y, TILE_SIZE - 1, TILE_SIZE - 1);
        g2d.setColor(color.darker());
        g2d.drawRect(x, y, TILE_SIZE - 1, TILE_SIZE - 1);
    }

    private void drawSidePanel(Graphics2D g2d) {
        int panelX = Board.WIDTH * TILE_SIZE + 15;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("SCORE", panelX, 30);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(String.valueOf(score), panelX, 55);

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("LEVEL", panelX, 95);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(String.valueOf(level), panelX, 120);

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("LINES", panelX, 160);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(String.valueOf(linesCleared), panelX, 185);

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("NEXT", panelX, 225);

        String nextShape = Tetromino.SHAPES[nextType][0];
        int previewTile = 18;
        int previewX = panelX;
        int previewY = 240;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (nextShape.charAt(r * 4 + c) == '1') {
                    g2d.setColor(Tetromino.COLORS[nextType]);
                    g2d.fillRect(previewX + c * previewTile, previewY + r * previewTile,
                            previewTile - 1, previewTile - 1);
                }
            }
        }
    }

    private void drawCenteredMessage(Graphics2D g2d, String title, String subtitle) {
        int boardPixelWidth = Board.WIDTH * TILE_SIZE;
        int boardPixelHeight = Board.HEIGHT * TILE_SIZE;

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, boardPixelWidth, boardPixelHeight);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (boardPixelWidth - fm.stringWidth(title)) / 2;
        g2d.drawString(title, x, boardPixelHeight / 2);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g2d.getFontMetrics();
        x = (boardPixelWidth - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, x, boardPixelHeight / 2 + 25);
    }
}