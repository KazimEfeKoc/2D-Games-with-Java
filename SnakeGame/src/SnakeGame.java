import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 25;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int GAME_SPEED_MS = 120;

    private final LinkedList<Point> snake = new LinkedList<>(); // Yılanın gövde parçaları
    private Point food;
    private char direction = 'R';        // Yön: U(p), D(own), L(eft), R(ight)
    private boolean gameOver = false;
    private int score = 0;

    private final Timer timer;
    private final Random random = new Random();

    public SnakeGame() {
        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        initGame();

        // Timer her GAME_SPEED_MS'de bir actionPerformed'i çağırır -> bu bizim "game loop"umuz
        timer = new Timer(GAME_SPEED_MS, this);
        timer.start();
    }

    private void initGame() {
        snake.clear();
        // Yılan başlangıçta ortada, 3 parçadan oluşan yatay bir çizgi
        int startX = GRID_WIDTH / 2;
        int startY = GRID_HEIGHT / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        direction = 'R';
        score = 0;
        gameOver = false;
        spawnFood();
    }

    private void spawnFood() {
        Point newFood;
        do {
            newFood = new Point(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));
        } while (snake.contains(newFood)); // Yemin yılanın üstüne düşmemesini garanti et
        food = newFood;
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_SPACE:
                if (gameOver) initGame();
                break;
        }
    }

    private void move() {
        if (gameOver) return;

        Point head = snake.getFirst();
        Point newHead = switch (direction) {
            case 'U' -> new Point(head.x, head.y - 1);
            case 'D' -> new Point(head.x, head.y + 1);
            case 'L' -> new Point(head.x - 1, head.y);
            default  -> new Point(head.x + 1, head.y); // 'R'
        };

        // Duvardan çıkınca diğer taraftan devam et (wrap-around).
        // Java'da % operatörü negatif sayılarda negatif sonuç verebildiği için
        // (örn. -1 % 20 = -1), + GRID_WIDTH ekleyip tekrar % alarak pozitif tutuyoruz.
        newHead.x = ((newHead.x % GRID_WIDTH) + GRID_WIDTH) % GRID_WIDTH;
        newHead.y = ((newHead.y % GRID_HEIGHT) + GRID_HEIGHT) % GRID_HEIGHT;

        // Kendi kendine çarpma kontrolü
        if (snake.contains(newHead)) {
            gameOver = true;
            return;
        }

        snake.addFirst(newHead);

        if (newHead.equals(food)) {
            score++;
            spawnFood(); // Yem yendi, kuyruğu kesme -> yılan uzar
        } else {
            snake.removeLast(); // Yem yenmediyse kuyruğu kes -> yılan aynı boyda kalır
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //Food - bait
        g2d.setColor(Color.RED);
        g2d.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            g2d.setColor(i == 0 ? new Color(50, 220, 50) : new Color(30, 160, 30)); // Baş daha açık renk
            g2d.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);
        }

        // Score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Skor: " + score, 10, 20);

        if (gameOver) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            String msg = "OYUN BİTTİ";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            g2d.drawString(msg, x, getHeight() / 2);

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            String restartMsg = "Yeniden başlamak için SPACE'e bas";
            fm = g2d.getFontMetrics();
            x = (getWidth() - fm.stringWidth(restartMsg)) / 2;
            g2d.drawString(restartMsg, x, getHeight() / 2 + 30);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            SnakeGame game = new SnakeGame();

            frame.add(game);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            game.requestFocusInWindow();
        });
    }
}