import javax.swing.*;

public class TetrisGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris Game");
            GamePanel game = new GamePanel();

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