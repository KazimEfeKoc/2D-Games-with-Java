import java.util.Arrays;

public class Board {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private final int[][] grid = new int[HEIGHT][WIDTH];

    public void clear() {
        for (int[] row : grid) {
            Arrays.fill(row, 0);
        }
    }

    public int getCell(int row, int col) {
        return grid[row][col];
    }

    public boolean collides(int type, int rotation, int x, int y) {
        String shape = Tetromino.SHAPES[type][rotation];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape.charAt(r * 4 + c) == '1') {
                    int boardR = y + r;
                    int boardC = x + c;

                    if (boardC < 0 || boardC >= WIDTH || boardR >= HEIGHT) {
                        return true;
                    }
                    if (boardR >= 0 && grid[boardR][boardC] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean lock(Tetromino piece) {
        String shape = piece.getShape();
        boolean lockedAboveBoard = false;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape.charAt(r * 4 + c) == '1') {
                    int boardR = piece.getY() + r;
                    int boardC = piece.getX() + c;
                    if (boardR < 0) {
                        lockedAboveBoard = true;
                    } else {
                        grid[boardR][boardC] = piece.getType() + 1;
                    }
                }
            }
        }

        return lockedAboveBoard;
    }

    public int clearFullLines() {
        int cleared = 0;

        for (int r = HEIGHT - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < WIDTH; c++) {
                if (grid[r][c] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {
                cleared++;
                for (int rr = r; rr > 0; rr--) {
                    grid[rr] = grid[rr - 1].clone();
                }
                Arrays.fill(grid[0], 0);
                r++;
            }
        }

        return cleared;
    }
}