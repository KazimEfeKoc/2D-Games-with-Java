import java.awt.Color;

public class Tetromino {

    public static final String[][] SHAPES = {
            {
                    "0000111100000000",
                    "0010001000100010",
                    "0000111100000000",
                    "0010001000100010"
            },
            {
                    "0000011001100000",
                    "0000011001100000",
                    "0000011001100000",
                    "0000011001100000"
            },
            {
                    "0100111000000000",
                    "0100011001000000",
                    "0000111001000000",
                    "0100110001000000"
            },
            {
                    "0000011011000000",
                    "0100011000100000",
                    "0000011011000000",
                    "0100011000100000"
            },
            {
                    "0000110001100000",
                    "0010011001000000",
                    "0000110001100000",
                    "0010011001000000"
            },
            {
                    "1000111000000000",
                    "0110010001000000",
                    "0000111000100000",
                    "0100010011000000"
            },
            {
                    "0010111000000000",
                    "0100010001100000",
                    "0000111010000000",
                    "1100010001000000"
            }
    };

    public static final Color[] COLORS = {
            Color.CYAN,
            Color.YELLOW,
            new Color(160, 32, 240),
            Color.GREEN,
            Color.RED,
            Color.BLUE,
            new Color(255, 140, 0)
    };

    public static final int TYPE_COUNT = SHAPES.length;

    private final int type;
    private int rotation;
    private int x, y;

    public Tetromino(int type, int startX, int startY) {
        this.type = type;
        this.rotation = 0;
        this.x = startX;
        this.y = startY;
    }

    public int getType() {
        return type;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int nextRotation() {
        return (rotation + 1) % 4;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Color getColor() {
        return COLORS[type];
    }

    public String getShape() {
        return SHAPES[type][rotation];
    }
}